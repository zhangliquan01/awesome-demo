cmake_minimum_required(VERSION 3.16)
project(grpc_hello_world)

# 设置C++标准
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 查找必要的包
find_package(Threads REQUIRED)
find_package(PkgConfig REQUIRED)

# 查找 Protobuf
find_package(Protobuf REQUIRED)

# 查找 gRPC
find_package(gRPC CONFIG REQUIRED)

# 设置proto文件路径
set(PROTO_PATH "${CMAKE_CURRENT_SOURCE_DIR}/proto")
set(PROTO_FILE "${PROTO_PATH}/hello.proto")
set(GENERATED_PROTOBUF_PATH "${CMAKE_CURRENT_BINARY_DIR}/generated")

# 创建生成文件目录
file(MAKE_DIRECTORY ${GENERATED_PROTOBUF_PATH})

# 生成protobuf和gRPC文件
set(PROTO_SRCS "${GENERATED_PROTOBUF_PATH}/hello.pb.cc")
set(PROTO_HDRS "${GENERATED_PROTOBUF_PATH}/hello.pb.h")
set(GRPC_SRCS "${GENERATED_PROTOBUF_PATH}/hello.grpc.pb.cc")
set(GRPC_HDRS "${GENERATED_PROTOBUF_PATH}/hello.grpc.pb.h")

# 生成protobuf代码
add_custom_command(
    OUTPUT "${PROTO_SRCS}" "${PROTO_HDRS}"
    COMMAND ${Protobuf_PROTOC_EXECUTABLE}
    ARGS --proto_path=${PROTO_PATH}
         --cpp_out=${GENERATED_PROTOBUF_PATH}
         ${PROTO_FILE}
    DEPENDS ${PROTO_FILE}
    COMMENT "生成protobuf代码"
)

# 生成gRPC代码
add_custom_command(
    OUTPUT "${GRPC_SRCS}" "${GRPC_HDRS}"
    COMMAND ${Protobuf_PROTOC_EXECUTABLE}
    ARGS --proto_path=${PROTO_PATH}
         --grpc_out=${GENERATED_PROTOBUF_PATH}
         --plugin=protoc-gen-grpc=$<TARGET_FILE:gRPC::grpc_cpp_plugin>
         ${PROTO_FILE}
    DEPENDS ${PROTO_FILE}
    COMMENT "生成gRPC代码"
)

# 创建proto库
add_library(hello_proto
    ${PROTO_SRCS}
    ${GRPC_SRCS}
)

target_link_libraries(hello_proto
    gRPC::grpc++
    gRPC::grpc++_reflection
    protobuf::libprotobuf
)

target_include_directories(hello_proto PUBLIC ${GENERATED_PROTOBUF_PATH})

# 创建服务器可执行文件
add_executable(hello_server
    src/server.cpp
)

target_link_libraries(hello_server
    hello_proto
    gRPC::grpc++
    gRPC::grpc++_reflection
    protobuf::libprotobuf
    Threads::Threads
)

target_include_directories(hello_server PRIVATE ${GENERATED_PROTOBUF_PATH})

# 创建客户端可执行文件
add_executable(hello_client
    src/client.cpp
)

target_link_libraries(hello_client
    hello_proto
    gRPC::grpc++
    gRPC::grpc++_reflection
    protobuf::libprotobuf
    Threads::Threads
)

target_include_directories(hello_client PRIVATE ${GENERATED_PROTOBUF_PATH})

# 安装目标
install(TARGETS hello_server hello_client
    RUNTIME DESTINATION bin
)

# 为开发者提供的方便脚本
configure_file(
    "${CMAKE_CURRENT_SOURCE_DIR}/scripts/run_example.sh.in"
    "${CMAKE_CURRENT_BINARY_DIR}/run_example.sh"
    @ONLY
)