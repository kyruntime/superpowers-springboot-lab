# 根据 ID 查询单本图书 API Implementation Plan

> **面向代理式执行者：** 必须使用子技能 `superpowers:subagent-driven-development`（推荐）或 `superpowers:executing-plans`，按任务逐项落实本计划。步骤使用复选框（`- [ ]`）语法以便跟踪进度。

**Goal:** 在现有图书收藏 API 上新增 `GET /api/books/{id}`，支持根据 id 查询单本图书。

**Architecture:** 继续使用现有 `BookController -> BookService -> BookRepository -> H2 Database` 分层结构。成功响应复用 `BookResponse`，不存在时复用 `BookNotFoundException` 和 `GlobalExceptionHandler` 返回简单 JSON 错误响应。

**Tech Stack:** Spring Boot 3、JDK 17、Maven、Spring Web、Spring Data JPA、H2、JUnit 5、MockMvc。

---

## 文件结构

- Modify: `src/main/java/com/example/superpowers/book/BookService.java`  
  增加 `getBook(Long id)` 用例，通过 `bookRepository.findById(id)` 查询，查不到时抛出 `BookNotFoundException`。
- Modify: `src/main/java/com/example/superpowers/book/BookController.java`  
  增加 `GET /api/books/{id}` 路由，调用 `BookService#getBook`。
- Modify: `src/test/java/com/example/superpowers/book/BookApiTest.java`  
  增加 MockMvc 集成测试，覆盖 id 存在和不存在两种场景。

`BookRepository` 不需要修改，因为 Spring Data JPA 已经提供 `findById`。

---

### Task 1: 实现单本图书查询用例

**Files:**
- Modify: `src/main/java/com/example/superpowers/book/BookService.java`
- Modify: `src/main/java/com/example/superpowers/book/BookController.java`

- [ ] **Step 1: 修改 `BookService`**

在 `src/main/java/com/example/superpowers/book/BookService.java` 中，在 `listBooks()` 方法后添加：

```java
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return BookResponse.from(book);
    }
```

修改后的 `BookService.java` 应为：

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

    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return BookResponse.from(book);
    }

    @Transactional
    public BookResponse markAsRead(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        book.markAsRead();
        return BookResponse.from(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }
}
```

- [ ] **Step 2: 修改 `BookController`**

在 `src/main/java/com/example/superpowers/book/BookController.java` 中，在 `listBooks()` 方法后添加：

```java
    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }
```

修改后的 `BookController.java` 应为：

```java
package com.example.superpowers.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @PatchMapping("/{id}/read")
    public BookResponse markAsRead(@PathVariable Long id) {
        return bookService.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
```

- [ ] **Step 3: 编译验证**

Run:

```bash
mvn test -DskipTests
```

Expected: `BUILD SUCCESS`，确认新增方法和路由可以编译。

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/superpowers/book/BookService.java src/main/java/com/example/superpowers/book/BookController.java
git commit -m "feat: get book by id"
```

---

### Task 2: 补充单本查询集成测试

**Files:**
- Modify: `src/test/java/com/example/superpowers/book/BookApiTest.java`

- [ ] **Step 1: 添加 id 存在时返回图书详情的测试**

在 `src/test/java/com/example/superpowers/book/BookApiTest.java` 中，追加以下测试方法：

```java
    @Test
    void getExistingBookReturnsBookDetails() throws Exception {
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

        Number id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.longValue()))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.read").value(false))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }
```

- [ ] **Step 2: 添加 id 不存在时返回 404 的测试**

继续在 `BookApiTest.java` 中追加：

```java
    @Test
    void getMissingBookReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }
```

- [ ] **Step 3: 运行单本查询相关测试**

Run:

```bash
mvn test -Dtest=BookApiTest#getExistingBookReturnsBookDetails,BookApiTest#getMissingBookReturnsNotFound
```

Expected: `BUILD SUCCESS`，新增 2 个测试通过。

- [ ] **Step 4: 运行全量测试**

Run:

```bash
mvn test
```

Expected: `BUILD SUCCESS`，所有测试通过，已有接口行为没有回归。

- [ ] **Step 5: Commit**

```bash
git add src/test/java/com/example/superpowers/book/BookApiTest.java
git commit -m "test: cover get book by id"
```

---

## 自检结果

- Spec coverage: 计划覆盖 `GET /api/books/{id}` 成功返回、id 不存在返回 404、复用 `BookResponse`、复用现有错误处理，以及不修改已有接口行为。
- Placeholder scan: 未发现占位式步骤或空泛实现描述。
- Type consistency: `BookService#getBook(Long id)`、`BookController#getBook(@PathVariable Long id)`、`BookResponse`、`BookNotFoundException` 命名和现有代码保持一致。

---

## 执行选项

计划已完成并保存至 `docs/superpowers/plans/2026-05-05-get-book-by-id-api-implementation.md`。有两种执行方式：

1. **子代理驱动（推荐）** —— 每个任务派生子代理从头处理，任务之间做评审，迭代更快。
2. **本会话内联执行** —— 在本对话中用 `executing-plans` 按检查点批量执行任务。

选用哪一种？
