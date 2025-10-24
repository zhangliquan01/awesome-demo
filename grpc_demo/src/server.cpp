#include <iostream>
#include <memory>
#include <string>
#include <thread>
#include <chrono>

#include <grpcpp/grpcpp.h>
#include "hello.grpc.pb.h"

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;
using grpc::ServerWriter;

using hello::HelloService;
using hello::HelloRequest;
using hello::HelloReply;

// HelloService的实现
class HelloServiceImpl final : public HelloService::Service {
  Status SayHello(ServerContext* context, const HelloRequest* request,
                  HelloReply* reply) override {
    std::string prefix("Hello ");
    reply->set_message(prefix + request->name() + "!");
    std::cout << "收到请求: " << request->name() << std::endl;
    return Status::OK;
  }

  Status SayHelloStream(ServerContext* context, const HelloRequest* request,
                       ServerWriter<HelloReply>* writer) override {
    std::cout << "收到流式请求: " << request->name() << std::endl;
    
    for (int i = 1; i <= 5; ++i) {
      HelloReply reply;
      reply.set_message("Hello " + request->name() + " #" + std::to_string(i));
      
      if (!writer->Write(reply)) {
        break;
      }
      
      // 模拟一些处理时间
      std::this_thread::sleep_for(std::chrono::milliseconds(500));
    }
    
    return Status::OK;
  }
};

void RunServer() {
  std::string server_address("0.0.0.0:50051");
  HelloServiceImpl service;

  ServerBuilder builder;
  
  // 监听指定地址，不使用认证
  builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
  
  // 注册服务
  builder.RegisterService(&service);
  
  // 构建并启动服务器
  std::unique_ptr<Server> server(builder.BuildAndStart());
  std::cout << "服务器监听地址: " << server_address << std::endl;

  // 等待服务器关闭
  server->Wait();
}

int main(int argc, char** argv) {
  std::cout << "启动 gRPC Hello World 服务器..." << std::endl;
  RunServer();
  return 0;
}