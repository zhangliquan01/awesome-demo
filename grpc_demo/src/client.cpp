#include <iostream>
#include <memory>
#include <string>
#include <thread>
#include <mutex>

#include <grpcpp/grpcpp.h>
#include "hello.grpc.pb.h"

// 简单的HTTP服务器库（使用基础socket实现）
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sstream>

using grpc::Channel;
using grpc::ClientContext;
using grpc::ClientReader;
using grpc::Status;

using hello::HelloService;
using hello::HelloRequest;
using hello::HelloReply;

class HelloClient {
 public:
  HelloClient(std::shared_ptr<Channel> channel)
      : stub_(HelloService::NewStub(channel)) {}

  // 调用简单的SayHello方法
  std::string SayHello(const std::string& name) {
    HelloRequest request;
    request.set_name(name);

    HelloReply reply;
    ClientContext context;

    Status status = stub_->SayHello(&context, request, &reply);

    if (status.ok()) {
      return reply.message();
    } else {
      std::cout << "RPC调用失败: " << status.error_code() << ": " << status.error_message() << std::endl;
      return "RPC调用失败";
    }
  }

  // 调用流式SayHelloStream方法
  std::vector<std::string> SayHelloStream(const std::string& name) {
    HelloRequest request;
    request.set_name(name);

    ClientContext context;
    std::unique_ptr<ClientReader<HelloReply>> reader(
        stub_->SayHelloStream(&context, request));

    std::vector<std::string> responses;
    HelloReply reply;
    while (reader->Read(&reply)) {
      responses.push_back(reply.message());
    }

    Status status = reader->Finish();
    if (!status.ok()) {
      std::cout << "流式RPC调用失败: " << status.error_code() << ": " << status.error_message() << std::endl;
    }

    return responses;
  }

 private:
  std::unique_ptr<HelloService::Stub> stub_;
};

class SimpleHttpServer {
private:
  HelloClient* grpc_client_;
  int server_fd_;
  int port_;
  std::mutex client_mutex_;

public:
  SimpleHttpServer(HelloClient* client, int port) 
    : grpc_client_(client), port_(port), server_fd_(-1) {}

  ~SimpleHttpServer() {
    if (server_fd_ != -1) {
      close(server_fd_);
    }
  }

  bool Start() {
    server_fd_ = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd_ == -1) {
      std::cerr << "创建socket失败" << std::endl;
      return false;
    }

    int opt = 1;
    setsockopt(server_fd_, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    struct sockaddr_in address;
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(port_);

    if (bind(server_fd_, (struct sockaddr*)&address, sizeof(address)) < 0) {
      std::cerr << "绑定端口失败" << std::endl;
      return false;
    }

    if (listen(server_fd_, 3) < 0) {
      std::cerr << "监听失败" << std::endl;
      return false;
    }

    std::cout << "HTTP服务器启动，监听端口: " << port_ << std::endl;
    return true;
  }

  void Run() {
    while (true) {
      struct sockaddr_in client_addr;
      socklen_t client_len = sizeof(client_addr);
      int client_fd = accept(server_fd_, (struct sockaddr*)&client_addr, &client_len);
      
      if (client_fd < 0) {
        continue;
      }

      // 在新线程中处理请求
      std::thread(&SimpleHttpServer::HandleRequest, this, client_fd).detach();
    }
  }

private:
  void HandleRequest(int client_fd) {
    char buffer[4096] = {0};
    read(client_fd, buffer, sizeof(buffer));
    
    std::string request(buffer);
    std::string response;

    // 解析HTTP请求
    if (request.find("GET /hello") == 0) {
      // 提取name参数
      std::string name = "World";
      size_t name_pos = request.find("name=");
      if (name_pos != std::string::npos) {
        size_t start = name_pos + 5;
        size_t end = request.find("&", start);
        if (end == std::string::npos) {
          end = request.find(" ", start);
        }
        if (end != std::string::npos) {
          name = request.substr(start, end - start);
        }
      }

      std::lock_guard<std::mutex> lock(client_mutex_);
      std::string grpc_response = grpc_client_->SayHello(name);
      
      response = CreateHttpResponse(200, "application/json", 
        "{\"message\": \"" + grpc_response + "\", \"method\": \"simple\"}");

    } else if (request.find("GET /hello-stream") == 0) {
      // 流式接口
      std::string name = "World";
      size_t name_pos = request.find("name=");
      if (name_pos != std::string::npos) {
        size_t start = name_pos + 5;
        size_t end = request.find("&", start);
        if (end == std::string::npos) {
          end = request.find(" ", start);
        }
        if (end != std::string::npos) {
          name = request.substr(start, end - start);
        }
      }

      std::lock_guard<std::mutex> lock(client_mutex_);
      std::vector<std::string> grpc_responses = grpc_client_->SayHelloStream(name);
      
      std::string json_array = "[";
      for (size_t i = 0; i < grpc_responses.size(); ++i) {
        if (i > 0) json_array += ",";
        json_array += "\"" + grpc_responses[i] + "\"";
      }
      json_array += "]";

      response = CreateHttpResponse(200, "application/json", 
        "{\"messages\": " + json_array + ", \"method\": \"stream\"}");

    } else if (request.find("GET /") == 0) {
      // 主页
      std::string html = R"(
<!DOCTYPE html>
<html>
<head>
    <title>gRPC Hello World Client</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 600px; }
        .endpoint { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }
        .response { margin-top: 10px; padding: 10px; background-color: #f5f5f5; border-radius: 3px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>gRPC Hello World HTTP Client</h1>
        <p>这个HTTP服务器作为gRPC客户端，将HTTP请求转发到gRPC服务器。</p>
        
        <div class="endpoint">
            <h3>简单问候接口</h3>
            <p><strong>GET /hello?name=你的名字</strong></p>
            <p>示例: <a href="/hello?name=张三">/hello?name=张三</a></p>
        </div>
        
        <div class="endpoint">
            <h3>流式问候接口</h3>
            <p><strong>GET /hello-stream?name=你的名字</strong></p>
            <p>示例: <a href="/hello-stream?name=李四">/hello-stream?name=李四</a></p>
        </div>
    </div>
</body>
</html>
      )";
      response = CreateHttpResponse(200, "text/html", html);
    } else {
      response = CreateHttpResponse(404, "text/plain", "Not Found");
    }

    send(client_fd, response.c_str(), response.length(), 0);
    close(client_fd);
  }

  std::string CreateHttpResponse(int status_code, const std::string& content_type, const std::string& body) {
    std::ostringstream response;
    response << "HTTP/1.1 " << status_code << " ";
    
    switch (status_code) {
      case 200: response << "OK"; break;
      case 404: response << "Not Found"; break;
      default: response << "Unknown"; break;
    }
    
    response << "\r\n";
    response << "Content-Type: " << content_type << "; charset=utf-8\r\n";
    response << "Content-Length: " << body.length() << "\r\n";
    response << "Connection: close\r\n";
    response << "\r\n";
    response << body;
    
    return response.str();
  }
};

int main(int argc, char** argv) {
  std::string grpc_server_address = "localhost:50051";
  int http_port = 8080;

  if (argc >= 2) {
    grpc_server_address = argv[1];
  }
  if (argc >= 3) {
    http_port = std::stoi(argv[2]);
  }

  // 创建gRPC客户端
  auto channel = grpc::CreateChannel(grpc_server_address, grpc::InsecureChannelCredentials());
  HelloClient grpc_client(channel);

  std::cout << "连接到gRPC服务器: " << grpc_server_address << std::endl;

  // 测试gRPC连接
  std::string test_response = grpc_client.SayHello("测试");
  std::cout << "gRPC测试响应: " << test_response << std::endl;

  // 启动HTTP服务器
  SimpleHttpServer http_server(&grpc_client, http_port);
  
  if (!http_server.Start()) {
    std::cerr << "HTTP服务器启动失败" << std::endl;
    return 1;
  }

  std::cout << "HTTP服务器运行中，访问 http://localhost:" << http_port << " 查看接口" << std::endl;
  std::cout << "按 Ctrl+C 停止服务器" << std::endl;

  // 运行HTTP服务器
  http_server.Run();

  return 0;
}