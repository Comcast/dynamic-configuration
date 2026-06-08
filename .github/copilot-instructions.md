## ✅ Java Code Review Instructions

### 📋 Review Scope
- **Code Review Focus**: Java source code, configuration files, and test files
- **Documentation Exclusion**: Do NOT review markdown files in the `/docs` directory
  - Documentation files follow their own review process
  - Changes to feature documentation should be reviewed separately
  - Focus code review efforts on functional code changes only

### 🔍 General Guidelines
- Ensure code follows the **project's coding standards** and **Java best practices**
- Check for **clear and meaningful naming** of classes, methods, and variables
- Verify that the code is **modular**, **readable**, and **maintainable**
- Confirm that **comments and documentation** are present where necessary
- Follow Single Responsibility Principle (SRP) for classes and methods

### 📦 Version Management
- **Development workflow**: 
  - Start from SNAPSHOT version (e.g., `2.3.91-SNAPSHOT`)
  - Work on features/fixes with SNAPSHOT version
  - When ready to release: **Remove `-SNAPSHOT` suffix** in your PR
  - Example: `<version>2.3.91-SNAPSHOT</version>` → `<version>2.3.91</version>`
- **Release marking**: 
  - **Developer responsibility**: Remove `-SNAPSHOT` to mark code as ready for release
  - This signals that the version should be released when PR is merged
  - Example: `2.3.91-SNAPSHOT` → `2.3.91` (developer removes SNAPSHOT)
- **Post-merge automation**:
  - CI/CD pipeline automatically creates next SNAPSHOT version after merge
  - Pipeline increments version and adds `-SNAPSHOT` back
  - Example: After merging `2.3.91`, pipeline creates `2.3.92-SNAPSHOT`
- **Version increments**: 
  - **Patch** (x.x.N): Bug fixes, minor changes, backward-compatible
  - **Minor** (x.N.x): New features, backward-compatible enhancements
  - **Major** (N.x.x): Breaking changes, API incompatibilities
- **When to update version**:
  - Remove `-SNAPSHOT` when your changes are ready for release
  - Ensure version number reflects the type of changes (patch/minor/major)
  - Pipeline handles creating next SNAPSHOT version automatically
- **Code Review Check**: 
  - ✅ If `-SNAPSHOT` is removed: Verify version increment is appropriate for the changes
  - ✅ If `-SNAPSHOT` is still present: Ask if this PR is ready for release or still WIP
  - ✅ Ensure version number follows semantic versioning based on change type

### 🧪 Testing
- Ensure **unit tests** are provided for all new functionality
- Verify that tests cover **positive**, **negative**, and **edge cases**
- Confirm that all tests pass locally and in CI/CD pipelines
- Use descriptive test names following pattern: `should[ExpectedBehavior]When[Condition]`
  - Example: `shouldReturnActiveAccountWhenBillingNumberExists()`
- Use `@Test` annotation for test methods (JUnit 5)
- Validate functionality, not just line coverage
- Use mocking for external dependencies (APIs, databases, file systems)
- Avoid over-mocking; use real objects for simple POJOs and value objects
- Assertions should validate error codes and business logic, not just mocked error messages

### 📊 Code Coverage
- Maintain minimum **80% line coverage** for new code
- Aim for **90%+ branch coverage** on critical business logic
- Don't chase 100% coverage at expense of meaningful tests
- Exclude generated code, models, and configuration from coverage checks

### 🛡️ Security & Error Handling
- Validate input and handle exceptions gracefully
- Create custom exceptions for domain-specific errors
- Always log before rethrowing exceptions
- Provide meaningful error messages with context
- Don't catch generic `Exception` unless necessary; catch specific types
- Avoid exposing sensitive information in logs or error messages
- Ensure proper use of access modifiers (`private`, `protected`, etc.)
- Check for potential **null pointer exceptions** and use null-safety patterns

### 🔒 Null Safety
- Use `@NonNull` and `@Nullable` annotations to document intent
- Use `Optional<T>` for return types when absence is valid business case
- **Don't use** `Optional` for:
  - Method parameters (use overloading or builder pattern)
  - Fields in POJOs/entities
  - Collections (use empty collections instead)
- Validate parameters at method boundaries: `Objects.requireNonNull(param, "param must not be null")`
- Use `Optional.ofNullable()` when dealing with potentially null values from external sources

### 📝 Logging Standards
- Use SLF4J with appropriate log levels (ERROR, WARN, INFO, DEBUG, TRACE)
- Never log sensitive data: passwords, tokens, PII, credit cards
- Use structured logging with MDC context for distributed tracing
- Log exceptions with context: `log.error("Failed to process account {}", accountId, exception)`
- Avoid excessive logging in tight loops (performance impact)

### ⚡ Concurrency & Async Operations
- Use `CompletableFuture` for parallel, independent operations
- Always handle exceptions in async chains with `.exceptionally()` or `.handle()`
- Preserve MDC context when using `CompletableFuture.supplyAsync()` with custom context wrappers
- Avoid blocking operations in async chains
- Configure appropriate thread pools; don't rely on ForkJoinPool.commonPool() for I/O

### ⚡ Performance & Optimization
- Review loops and recursive calls for performance bottlenecks
- Avoid unnecessary object creation or memory usage
- Use efficient data structures and algorithms
- Prefer `Stream` API for readability unless performance is critical
- Use lazy evaluation where appropriate
- **String concatenation**: 
  - Use `+` operator for simple concatenations (1-2 operations)
  - Use `StringBuilder` only for loops or multiple concatenations (3+)
  - Example: `String result = prefix + value + suffix;` is fine
  - Avoid: Unnecessary StringBuilder for single concatenation

### 📦 Dependencies & Imports
- Remove unused imports and dependencies
- Ensure third-party libraries are approved and up-to-date
- Avoid tight coupling between modules
- Follow Dependency Inversion Principle (depend on abstractions)

### 🧹 Code Style & Formatting
- Ensure consistent indentation and spacing
- Follow naming conventions (`camelCase` for methods/variables, `PascalCase` for classes)
- Use annotations (`@Override`, `@Nullable`, etc.) appropriately
- Check for trailing whitespace and unnecessary comments
- Limit line length to 120 characters

### 🌐 API Design (GraphQL)
- Use descriptive query and mutation names (verbs for mutations, nouns for queries)
- Implement proper error handling with GraphQL error extensions
- Add pagination for list queries (limit, offset or cursor-based)
- Document schema with descriptions for all types and fields
- Validate input at schema level with custom scalars where appropriate
- Use DataLoader pattern to avoid N+1 query problems

### 📄 Documentation
- Public methods and classes should have Javadoc comments
- Include usage examples if the code introduces new APIs or utilities
- Update README or relevant documentation if behavior changes
- Document complex business logic and algorithms
- Add inline comments for non-obvious code decisions

### 📚 Best Practices References
- [Java Best Practices](https://blog.jetbrains.com/idea/2024/02/java-best-practices/)
- [Defensive Programming](https://www.javacodegeeks.com/2012/03/defensive-programming-being-just-enough.html)
- [JUnit Testing](https://www.geeksforgeeks.org/advance-java/introduction-of-junit/)

**JUnit test cases must serve the following:**
- Validate hypotheses (Successes and Failures)
- Serve as documentation for other developers
- Instill confidence for safe change/refactoring

### 🚦 Review Checklist for Reviewers
Before approving a PR, confirm:
- [ ] **Scope check**: Focus on code changes; skip markdown files in `/docs` directory
- [ ] Code compiles and runs as expected
- [ ] All tests pass with adequate coverage (80%+ for new code)
- [ ] Code adheres to style and design guidelines
- [ ] No obvious bugs or security issues
- [ ] No sensitive data in logs or error messages
- [ ] Proper error handling and null safety
- [ ] Documentation is updated (code-level comments, Javadoc)
- [ ] **POM version check**: 
  - If `-SNAPSHOT` removed: Verify version increment is appropriate (patch/minor/major) for the changes
  - If `-SNAPSHOT` present: Confirm if this is still WIP or ready for release
- [ ] No unnecessary dependencies added
- [ ] Performance considerations addressed

