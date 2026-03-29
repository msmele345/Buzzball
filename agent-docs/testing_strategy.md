### Backend Test Strategy

### TDD Workflow
- Always write failing tests BEFORE implementation
- Use AAA pattern: Arrange-Act-Assert
- One assertion per test when possible
- Test names describe behavior: "should_return_empty_when_no_items"

### Test-First Rules
- When I ask for a feature, write tests first
- Tests should FAIL initially (no implementation exists)
- Only after tests are written, implement minimal code to pass

- Controllers should have @Springbootest integration slice tests
- For Integration tests, Autowire the TestRestTemplate bean and use @MockBean to mock the cosmos layer.
- Controllers should also have @ExtendWith(MockitoExtension.class) unit tests with a mocked web layer.
- For unit tests annotated with @ExtendWith(MockitoExtension.class), use Mockito's @Mock and @InjectMocks.

### Frontend Test Strategy
- React Test library for component testing. Write a failing test first then make it pass when writing implementation code.
- Use Jest for unit testing of utility functions and hooks. Write a failing test first then make it pass when writing implementation code.