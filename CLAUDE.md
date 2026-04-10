## See @agent_docs/ for general guidelines on how to work with this project
    agent_docs/
    |- git_instructions.md
    |- project_details.md
    |- testing_strategy.md

## See @backend/LOCAL_DEVELOPMENT.md for instructions on how to set up the local environment and run the project

## Commands
- `npm run dev`   — start the server
- `npx vitest`  — run tests
- `npm run build` — production build

## Critical Conventions
### TDD Workflow
- Always write failing tests BEFORE implementation
- Use AAA pattern: Arrange-Act-Assert
- One assertion per test when possible
- Test names describe behavior: "should_return_empty_when_no_items"

### Test-First Rules
- When I ask for a feature, write tests first
- Tests should FAIL initially (no implementation exists)
- Only after tests are written, implement minimal code to pass

## Additional Instructions:
- Add code comments to explain complex parts of the flow.
- Ask me questions if there is anything you are not sure about the project or the requirements. 