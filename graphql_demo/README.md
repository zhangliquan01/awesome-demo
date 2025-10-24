# GraphQL Demo - Spring Boot GraphQL示例项目

这是一个基于Spring Boot和GraphQL的完整示例项目，展示了如何使用GraphQL构建现代化的API服务。

## 项目简介

本项目实现了一个图书管理系统，包含作者(Author)和图书(Book)两个核心实体，提供了完整的CRUD操作。

### 技术栈

- **Spring Boot 3.2.0** - 应用框架
- **Spring GraphQL** - GraphQL支持
- **Spring Data JPA** - 数据持久化
- **H2 Database** - 内存数据库（用于演示）
- **Lombok** - 简化Java代码
- **Maven** - 项目构建工具

## 项目结构

```
GraphQL_demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/graphql/
│   │   │   ├── config/              # 配置类
│   │   │   │   └── DataInitializer.java
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   │   ├── CreateAuthorInput.java
│   │   │   │   ├── UpdateAuthorInput.java
│   │   │   │   ├── CreateBookInput.java
│   │   │   │   └── UpdateBookInput.java
│   │   │   ├── entity/              # 实体类
│   │   │   │   ├── Author.java
│   │   │   │   └── Book.java
│   │   │   ├── repository/          # 数据访问层
│   │   │   │   ├── AuthorRepository.java
│   │   │   │   └── BookRepository.java
│   │   │   ├── resolver/            # GraphQL解析器
│   │   │   │   ├── QueryResolver.java
│   │   │   │   └── MutationResolver.java
│   │   │   ├── service/             # 业务逻辑层
│   │   │   │   ├── AuthorService.java
│   │   │   │   └── BookService.java
│   │   │   └── GraphQLDemoApplication.java
│   │   └── resources/
│   │       ├── application.yml      # 应用配置
│   │       └── graphql/
│   │           └── schema.graphqls  # GraphQL Schema定义
│   └── test/
└── pom.xml                          # Maven配置
```

## 快速开始

### 前置要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 运行项目

1. **克隆或下载项目**

2. **编译项目**
   ```bash
   mvn clean install
   ```

3. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

4. **访问应用**
   - **GraphiQL界面**: http://localhost:8080/graphiql
   - GraphQL端点: http://localhost:8080/graphql
   - H2数据库控制台: http://localhost:8080/h2-console

   > **💡 提示**: GraphiQL重定向问题已修复！可以正常使用所有功能。详见 `FIX_EXPLANATION.md`

## GraphQL API使用示例

### 查询操作 (Query)

#### 1. 获取所有作者
```graphql
query {
  allAuthors {
    id
    name
    email
    bio
    books {
      id
      title
    }
  }
}
```

#### 2. 根据ID获取作者
```graphql
query {
  authorById(id: 1) {
    id
    name
    email
    books {
      title
      price
    }
  }
}
```

#### 3. 搜索作者
```graphql
query {
  authorsByName(name: "张") {
    id
    name
    email
  }
}
```

#### 4. 获取所有图书
```graphql
query {
  allBooks {
    id
    title
    isbn
    price
    publishYear
    author {
      name
      email
    }
  }
}
```

#### 5. 根据ID获取图书
```graphql
query {
  bookById(id: 1) {
    id
    title
    description
    price
    author {
      name
    }
  }
}
```

#### 6. 搜索图书
```graphql
query {
  booksByTitle(title: "Spring") {
    id
    title
    price
  }
}
```

#### 7. 根据作者获取图书
```graphql
query {
  booksByAuthor(authorId: 1) {
    id
    title
    price
  }
}
```

### 变更操作 (Mutation)

#### 1. 创建作者
```graphql
mutation {
  createAuthor(input: {
    name: "赵六"
    email: "zhaoliu@example.com"
    bio: "资深技术专家"
  }) {
    id
    name
    email
  }
}
```

#### 2. 更新作者
```graphql
mutation {
  updateAuthor(input: {
    id: 1
    name: "张三（更新）"
    bio: "高级Java开发工程师"
  }) {
    id
    name
    bio
  }
}
```

#### 3. 删除作者
```graphql
mutation {
  deleteAuthor(id: 4)
}
```

#### 4. 创建图书
```graphql
mutation {
  createBook(input: {
    title: "Kubernetes实战"
    isbn: "978-7-111-12345-5"
    description: "容器编排技术详解"
    authorId: 3
    price: 118.00
    publishYear: 2024
  }) {
    id
    title
    isbn
    price
    author {
      name
    }
  }
}
```

#### 5. 更新图书
```graphql
mutation {
  updateBook(input: {
    id: 1
    title: "Spring Boot实战（第二版）"
    price: 99.00
  }) {
    id
    title
    price
  }
}
```

#### 6. 删除图书
```graphql
mutation {
  deleteBook(id: 5)
}
```

### 复杂查询示例

#### 查询作者及其所有图书的详细信息
```graphql
query {
  authorById(id: 1) {
    id
    name
    email
    bio
    books {
      id
      title
      isbn
      description
      price
      publishYear
    }
  }
}
```

#### 同时查询多个资源
```graphql
query {
  allAuthors {
    id
    name
  }
  allBooks {
    id
    title
    price
  }
}
```

## 数据模型

### Author (作者)
- `id`: 作者ID
- `name`: 作者姓名
- `email`: 作者邮箱（唯一）
- `bio`: 作者简介
- `books`: 作者的图书列表

### Book (图书)
- `id`: 图书ID
- `title`: 图书标题
- `isbn`: ISBN编号（唯一）
- `description`: 图书描述
- `author`: 作者信息
- `price`: 价格
- `publishYear`: 出版年份

## 特性说明

### 1. GraphQL优势
- **按需查询**: 客户端可以精确指定需要的字段
- **单一端点**: 所有操作通过 `/graphql` 端点完成
- **强类型**: Schema定义了完整的类型系统
- **关联查询**: 可以一次查询获取关联数据

### 2. 项目特点
- 完整的CRUD操作
- 实体关联关系（一对多）
- 数据验证和异常处理
- 自动初始化演示数据
- 支持GraphiQL可视化界面

### 3. 开发工具

#### GraphiQL界面
- **访问**: `/graphiql`
- **特性**:
  - ✅ 自动补全（Ctrl+Space）
  - ✅ 文档浏览器（自动从Schema生成）
  - ✅ 查询历史记录
  - ✅ 变量编辑器
  - ✅ 标准的GraphQL IDE体验
- **修复说明**: 通过添加Filter解决了重定向导致的loading问题，详见 `FIX_EXPLANATION.md`

#### H2 Console
- **访问**: `/h2-console`
- 可视化数据库管理界面

## 配置说明

主要配置在 `application.yml` 中：

- **数据库配置**: 使用H2内存数据库，应用重启后数据会重置
- **GraphQL配置**: 启用GraphiQL界面，路径为 `/graphiql`
- **JPA配置**: 自动创建表结构，显示SQL语句

## 扩展建议

1. **数据库**: 将H2替换为MySQL、PostgreSQL等生产级数据库
2. **安全性**: 添加Spring Security进行身份认证和授权
3. **分页**: 实现GraphQL分页查询
4. **订阅**: 添加GraphQL Subscription支持实时数据推送
5. **缓存**: 使用DataLoader优化N+1查询问题
6. **测试**: 添加单元测试和集成测试

## 常见问题

### 1. 如何修改端口？
在 `application.yml` 中修改 `server.port` 配置。

### 2. 如何查看数据库内容？
访问 http://localhost:8080/h2-console，使用以下配置：
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (留空)

### 3. 如何添加新的字段？
1. 在实体类中添加字段
2. 在 `schema.graphqls` 中更新类型定义
3. 重启应用

## 许可证

本项目仅用于学习和演示目的。

## 联系方式

如有问题或建议，欢迎提出Issue或Pull Request。

