# 图书收藏 API Implementation Plan

> **面向代理式执行者：** 必须使用子技能 `superpowers:subagent-driven-development`（推荐）或 `superpowers:executing-plans`，按任务逐项落实本计划。步骤使用复选框（`- [ ]`）语法以便跟踪进度。

**Goal:** 构建一个 Spring Boot 3 图书收藏 API，支持创建、查询、标记已读和删除收藏图书。

**Architecture:** 使用简单分层架构：Controller 负责 HTTP 契约，Service 负责用例编排，Repository 负责 JPA 持久化，H2 作为测试和本地运行数据库。API 使用 DTO 隔离实体，统一异常处理返回最小 JSON 错误响应。

**Tech Stack:** Spring Boot 3、JDK 17、Maven、Spring Web、Spring Data JPA、H2、Bean Validation、JUnit 5、MockMvc。

---

## 文件结构

- Create: `pom.xml`  
  Maven 项目配置，声明 Spring Boot、Web、JPA、Validation、H2 和测试依赖。
- Create: `src/main/java/com/example/superpowers/SuperpowersSpringbootLabApplication.java`  
  Spring Boot 启动类。
- Create: `src/main/java/com/example/superpowers/book/Book.java`  
  JPA 实体，保存图书数据。
- Create: `src/main/java/com/example/superpowers/book/BookRepository.java`  
  Spring Data JPA 仓储，提供按 `createdAt ASC, id ASC` 排序的查询。
- Create: `src/main/java/com/example/superpowers/book/BookService.java`  
  应用用例：创建、查询、标记已读、删除。
- Create: `src/main/java/com/example/superpowers/book/BookController.java`  
  HTTP API 控制器。
- Create: `src/main/java/com/example/superpowers/book/CreateBookRequest.java`  
  创建图书请求 DTO，校验 `title` 不能为空白。
- Create: `src/main/java/com/example/superpowers/book/BookResponse.java`  
  图书响应 DTO。
- Create: `src/main/java/com/example/superpowers/book/BookNotFoundException.java`  
  图书不存在异常。
- Create: `src/main/java/com/example/superpowers/common/ErrorResponse.java`  
  错误响应 DTO。
- Create: `src/main/java/com/example/superpowers/common/GlobalExceptionHandler.java`  
  全局异常处理。
- Create: `src/main/resources/application.properties`  
  本地 H2 和 JPA 配置。
- Create: `src/test/java/com/example/superpowers/book/BookApiTest.java`  
  MockMvc 集成测试，覆盖 spec 中所有 API 行为。

---

### Task 1: 初始化 Spring Boot Maven 项目

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/example/superpowers/SuperpowersSpringbootLabApplication.java`
- Create: `src/main/resources/application.properties`

- [ ] **Step 1: 创建 Maven 项目配置**

Create `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>superpowers-springboot-lab</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>superpowers-springboot-lab</name>
    <description>Spring Boot lab for practicing Superpowers workflow</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建启动类**

Create `src/main/java/com/example/superpowers/SuperpowersSpringbootLabApplication.java`:

```java
package com.example.superpowers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuperpowersSpringbootLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperpowersSpringbootLabApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建本地配置**

Create `src/main/resources/application.properties`:

```properties
spring.application.name=superpowers-springboot-lab
spring.datasource.url=jdbc:h2:mem:books;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.open-in-view=false
```

- [ ] **Step 4: 验证项目可以编译**

Run:

```bash
./mvnw test
```

如果没有 Maven Wrapper，先运行：

```bash
mvn test
```

Expected: Maven 能下载依赖并执行测试阶段；此时没有测试，构建应通过。

- [ ] **Step 5: Commit**

```bash
git add pom.xml src/main/java/com/example/superpowers/SuperpowersSpringbootLabApplication.java src/main/resources/application.properties
git commit -m "chore: initialize spring boot project"
```

---

### Task 2: 用 TDD 实现创建图书 API

**Files:**
- Create: `src/test/java/com/example/superpowers/book/BookApiTest.java`
- Create: `src/main/java/com/example/superpowers/book/Book.java`
- Create: `src/main/java/com/example/superpowers/book/BookRepository.java`
- Create: `src/main/java/com/example/superpowers/book/BookService.java`
- Create: `src/main/java/com/example/superpowers/book/BookController.java`
- Create: `src/main/java/com/example/superpowers/book/CreateBookRequest.java`
- Create: `src/main/java/com/example/superpowers/book/BookResponse.java`

- [ ] **Step 1: 写创建图书的失败测试**

Create `src/test/java/com/example/superpowers/book/BookApiTest.java`:

```java
package com.example.superpowers.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createBookReturnsCreatedBook() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Clean Code",
                          "author": "Robert C. Martin"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.read").value(false))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run:

```bash
mvn test -Dtest=BookApiTest#createBookReturnsCreatedBook
```

Expected: FAIL，原因是 `POST /api/books` 尚未实现，通常会得到 404 或上下文缺少相关 bean。

- [ ] **Step 3: 创建实体、仓储、DTO、服务和控制器**

Create `src/main/java/com/example/superpowers/book/Book.java`:

```java
package com.example.superpowers.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(nullable = false)
    private boolean read;

    @Column(nullable = false)
    private Instant createdAt;

    protected Book() {
    }

    public Book(String title, String author, boolean read, Instant createdAt) {
        this.title = title;
        this.author = author;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isRead() {
        return read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

Create `src/main/java/com/example/superpowers/book/BookRepository.java`:

```java
package com.example.superpowers.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
```

Create `src/main/java/com/example/superpowers/book/CreateBookRequest.java`:

```java
package com.example.superpowers.book;

import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
        @NotBlank(message = "Title must not be blank")
        String title,
        String author
) {
}
```

Create `src/main/java/com/example/superpowers/book/BookResponse.java`:

```java
package com.example.superpowers.book;

import java.time.Instant;

public record BookResponse(
        Long id,
        String title,
        String author,
        boolean read,
        Instant createdAt
) {

    static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.isRead(),
                book.getCreatedAt()
        );
    }
}
```

Create `src/main/java/com/example/superpowers/book/BookService.java`:

```java
package com.example.superpowers.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Book book = new Book(request.title(), request.author(), false, Instant.now());
        return BookResponse.from(bookRepository.save(book));
    }
}
```

Create `src/main/java/com/example/superpowers/book/BookController.java`:

```java
package com.example.superpowers.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run:

```bash
mvn test -Dtest=BookApiTest#createBookReturnsCreatedBook
```

Expected: PASS。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/superpowers/book src/test/java/com/example/superpowers/book/BookApiTest.java
git commit -m "feat: create books"
```

---

### Task 3: 用 TDD 实现创建图书校验错误

**Files:**
- Modify: `src/test/java/com/example/superpowers/book/BookApiTest.java`
- Create: `src/main/java/com/example/superpowers/common/ErrorResponse.java`
- Create: `src/main/java/com/example/superpowers/common/GlobalExceptionHandler.java`

- [ ] **Step 1: 写 title 为空白时返回 400 的失败测试**

Append to `BookApiTest` imports:

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
```

Append to `BookApiTest`:

```java
    @Test
    void createBookWithBlankTitleReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "   ",
                          "author": "Robert C. Martin"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title must not be blank"));
    }
```

- [ ] **Step 2: 运行测试确认失败**

Run:

```bash
mvn test -Dtest=BookApiTest#createBookWithBlankTitleReturnsBadRequest
```

Expected: FAIL，状态码可能已经是 400，但响应体尚不是 `{"message":"Title must not be blank"}`。

- [ ] **Step 3: 添加错误响应和全局异常处理**

Create `src/main/java/com/example/superpowers/common/ErrorResponse.java`:

```java
package com.example.superpowers.common;

public record ErrorResponse(String message) {
}
```

Create `src/main/java/com/example/superpowers/common/GlobalExceptionHandler.java`:

```java
package com.example.superpowers.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed");
        return new ErrorResponse(message);
    }
}
```

- [ ] **Step 4: 运行创建相关测试确认通过**

Run:

```bash
mvn test -Dtest=BookApiTest#createBookReturnsCreatedBook,BookApiTest#createBookWithBlankTitleReturnsBadRequest
```

Expected: PASS。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/superpowers/common src/test/java/com/example/superpowers/book/BookApiTest.java
git commit -m "feat: validate book creation"
```

---

### Task 4: 用 TDD 实现查询图书列表

**Files:**
- Modify: `src/test/java/com/example/superpowers/book/BookApiTest.java`
- Modify: `src/main/java/com/example/superpowers/book/BookRepository.java`
- Modify: `src/main/java/com/example/superpowers/book/BookService.java`
- Modify: `src/main/java/com/example/superpowers/book/BookController.java`

- [ ] **Step 1: 添加测试隔离清理和查询列表测试**

Modify `BookApiTest` imports:

```java
import org.junit.jupiter.api.BeforeEach;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
```

Add field and setup to `BookApiTest`:

```java
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
    }
```

Append to `BookApiTest`:

```java
    @Test
    void listBooksReturnsEmptyArrayWhenNoBooksExist() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void listBooksReturnsBooksInStableOrder() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Clean Code",
                          "author": "Robert C. Martin"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Refactoring",
                          "author": "Martin Fowler"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[1].title").value("Refactoring"));
    }
```

- [ ] **Step 2: 运行测试确认失败**

Run:

```bash
mvn test -Dtest=BookApiTest#listBooksReturnsEmptyArrayWhenNoBooksExist,BookApiTest#listBooksReturnsBooksInStableOrder
```

Expected: FAIL，原因是 `GET /api/books` 尚未实现。

- [ ] **Step 3: 实现排序查询**

Replace `BookRepository.java` with:

```java
package com.example.superpowers.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByCreatedAtAscIdAsc();
}
```

Replace `BookService.java` with:

```java
package com.example.superpowers.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Book book = new Book(request.title(), request.author(), false, Instant.now());
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> listBooks() {
        return bookRepository.findAllByOrderByCreatedAtAscIdAsc()
                .stream()
                .map(BookResponse::from)
                .toList();
    }
}
```

Replace `BookController.java` with:

```java
package com.example.superpowers.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @GetMapping
    public List<BookResponse> listBooks() {
        return bookService.listBooks();
    }
}
```

- [ ] **Step 4: 运行查询相关测试确认通过**

Run:

```bash
mvn test -Dtest=BookApiTest#listBooksReturnsEmptyArrayWhenNoBooksExist,BookApiTest#listBooksReturnsBooksInStableOrder
```

Expected: PASS。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/superpowers/book src/test/java/com/example/superpowers/book/BookApiTest.java
git commit -m "feat: list books"
```

---

### Task 5: 用 TDD 实现标记已读

**Files:**
- Modify: `src/test/java/com/example/superpowers/book/BookApiTest.java`
- Modify: `src/main/java/com/example/superpowers/book/Book.java`
- Create: `src/main/java/com/example/superpowers/book/BookNotFoundException.java`
- Modify: `src/main/java/com/example/superpowers/book/BookService.java`
- Modify: `src/main/java/com/example/superpowers/book/BookController.java`
- Modify: `src/main/java/com/example/superpowers/common/GlobalExceptionHandler.java`

- [ ] **Step 1: 写标记已读成功和不存在返回 404 的失败测试**

Modify `BookApiTest` imports:

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
```

Append to `BookApiTest`:

```java
    @Test
    void markExistingBookAsReadReturnsUpdatedBook() throws Exception {
        String response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Clean Code",
                          "author": "Robert C. Martin"
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(patch("/api/books/{id}/read", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    void markMissingBookAsReadReturnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/books/{id}/read", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }
```

- [ ] **Step 2: 运行测试确认失败**

Run:

```bash
mvn test -Dtest=BookApiTest#markExistingBookAsReadReturnsUpdatedBook,BookApiTest#markMissingBookAsReadReturnsNotFound
```

Expected: FAIL，原因是 `PATCH /api/books/{id}/read` 尚未实现。

- [ ] **Step 3: 实现标记已读和 404 错误**

Add to `Book.java`:

```java
    public void markAsRead() {
        this.read = true;
    }
```

Create `src/main/java/com/example/superpowers/book/BookNotFoundException.java`:

```java
package com.example.superpowers.book;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book not found: " + id);
    }
}
```

Replace `BookService.java` with:

```java
package com.example.superpowers.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Book book = new Book(request.title(), request.author(), false, Instant.now());
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> listBooks() {
        return bookRepository.findAllByOrderByCreatedAtAscIdAsc()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional
    public BookResponse markAsRead(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        book.markAsRead();
        return BookResponse.from(book);
    }
}
```

Replace `BookController.java` with:

```java
package com.example.superpowers.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @GetMapping
    public List<BookResponse> listBooks() {
        return bookService.listBooks();
    }

    @PatchMapping("/{id}/read")
    public BookResponse markAsRead(@PathVariable Long id) {
        return bookService.markAsRead(id);
    }
}
```

Add to `GlobalExceptionHandler.java`:

```java
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookNotFound(BookNotFoundException exception) {
        return new ErrorResponse("Book not found");
    }
```

Also add import:

```java
import com.example.superpowers.book.BookNotFoundException;
```

- [ ] **Step 4: 运行标记已读相关测试确认通过**

Run:

```bash
mvn test -Dtest=BookApiTest#markExistingBookAsReadReturnsUpdatedBook,BookApiTest#markMissingBookAsReadReturnsNotFound
```

Expected: PASS。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/superpowers/book src/main/java/com/example/superpowers/common/GlobalExceptionHandler.java src/test/java/com/example/superpowers/book/BookApiTest.java
git commit -m "feat: mark books as read"
```

---

### Task 6: 用 TDD 实现删除图书

**Files:**
- Modify: `src/test/java/com/example/superpowers/book/BookApiTest.java`
- Modify: `src/main/java/com/example/superpowers/book/BookService.java`
- Modify: `src/main/java/com/example/superpowers/book/BookController.java`

- [ ] **Step 1: 写删除成功和不存在返回 404 的失败测试**

Modify `BookApiTest` imports:

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
```

Append to `BookApiTest`:

```java
    @Test
    void deleteExistingBookRemovesItFromList() throws Exception {
        String response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Clean Code",
                          "author": "Robert C. Martin"
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/books/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deleteMissingBookReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }
```

- [ ] **Step 2: 运行测试确认失败**

Run:

```bash
mvn test -Dtest=BookApiTest#deleteExistingBookRemovesItFromList,BookApiTest#deleteMissingBookReturnsNotFound
```

Expected: FAIL，原因是 `DELETE /api/books/{id}` 尚未实现。

- [ ] **Step 3: 实现删除用例和端点**

Add to `BookService.java`:

```java
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }
```

Add to `BookController.java` imports:

```java
import org.springframework.web.bind.annotation.DeleteMapping;
```

Add to `BookController.java`:

```java
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
```

- [ ] **Step 4: 运行删除相关测试确认通过**

Run:

```bash
mvn test -Dtest=BookApiTest#deleteExistingBookRemovesItFromList,BookApiTest#deleteMissingBookReturnsNotFound
```

Expected: PASS。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/superpowers/book src/test/java/com/example/superpowers/book/BookApiTest.java
git commit -m "feat: delete books"
```

---

### Task 7: 全量验证和收尾

**Files:**
- Modify if needed: `src/test/java/com/example/superpowers/book/BookApiTest.java`
- Modify if needed: `src/main/java/com/example/superpowers/book/*.java`
- Modify if needed: `src/main/java/com/example/superpowers/common/*.java`

- [ ] **Step 1: 运行全量测试**

Run:

```bash
mvn test
```

Expected: PASS，所有 `BookApiTest` 测试通过。

- [ ] **Step 2: 按 spec 做覆盖核对**

确认以下行为都有 MockMvc 测试覆盖：

```text
POST /api/books
- 201 Created
- response 包含 id/title/author/read/createdAt
- read 默认为 false
- 空白 title 返回 400 和 "Title must not be blank"

GET /api/books
- 200 OK
- 无数据返回 []
- 多条数据按 createdAt ASC, id ASC 稳定排序

PATCH /api/books/{id}/read
- 已存在返回 200 和 read=true
- 不存在返回 404 和 "Book not found"
- 重复标记已读保持成功

DELETE /api/books/{id}
- 已存在返回 204
- 删除后不再出现在列表
- 不存在返回 404 和 "Book not found"
```

如果发现缺少“重复标记已读保持成功”的测试，向 `BookApiTest` 追加：

```java
    @Test
    void markAlreadyReadBookAsReadIsIdempotent() throws Exception {
        String response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Clean Code",
                          "author": "Robert C. Martin"
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(patch("/api/books/{id}/read", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));

        mockMvc.perform(patch("/api/books/{id}/read", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }
```

- [ ] **Step 3: 再次运行全量测试**

Run:

```bash
mvn test
```

Expected: PASS。

- [ ] **Step 4: Commit**

```bash
git add src/main/java src/test/java
git commit -m "test: verify book api behavior"
```

---

## 自检结果

- Spec coverage: 计划覆盖项目初始化、四个 API、数据模型、分层架构、DTO、错误处理、MockMvc 测试和 H2 持久化。
- Placeholder scan: 未发现占位式步骤或空泛实现描述。
- Type consistency: 类名、包名、路径、DTO 字段和 API 路径在各任务中保持一致。

---

## 执行选项

计划已完成并保存至 `docs/superpowers/plans/2026-05-05-book-collection-api-implementation.md`。有两种执行方式：

1. **子代理驱动（推荐）** —— 每个任务派生子代理从头处理，任务之间做评审，迭代更快。
2. **本会话内联执行** —— 在本对话中用 `executing-plans` 按检查点批量执行任务。

选用哪一种？
