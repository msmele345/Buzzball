### Backend Test Strategy
- Write a failing test first then make it pass when writing implementation code. TDD style.
- Controllers should have @WebMvcTest integration slice tests
- For @WebMvcTests, Autowire the WebTestClient bean and use @MockitoBean to mock the service layer.
- Controllers should also have @ExtendWith(MockitoExtension.class) unit tests with a mocked web layer.
- For unit tests annotated with @ExtendWith(MockitoExtension.class), use Mockito's @Mock and @InjectMocks.

### Frontend Test Strategy
- React Test library for component testing. Write a failing test first then make it pass when writing implementation code.
- Use Jest for unit testing of utility functions and hooks. Write a failing test first then make it pass when writing implementation code.