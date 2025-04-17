# prompt-based-coding

An IntelliJ IDEA plugin project that leverages OpenAI to interpret natural language prompts and automatically create or modify Java Spring Boot code within your development workspace. This repository contains the complete source code for the plugin—no marketplace distribution is provided.

## Features

- **Natural Language to Code**: Interpret developer prompts in plain English and translate them into actionable code changes.
- **Class Creation & Modification**: Automatically generate new classes or update existing ones based on structured JSON instructions.
- **Context Awareness**: Retrieve existing code context (classes, methods, imports) to ensure accurate modifications.
- **JSON-Based Instruction Extraction**: Use GPT-4o-mini to parse prompts into structured instructions (`action`, `newClasses`, `modifiedClasses`, etc.).
- **Embedded Tool Window**: A custom Tool Window in IntelliJ for prompt input and instant feedback within the IDE.

## Getting Started

### Prerequisites

- IntelliJ IDEA Community or Ultimate (version 2021.3 or newer)
- Java 11 or higher
- Gradle (wrapper included)
- An OpenAI API key

### Building the Plugin

1. Clone this repository:
   ```bash
   git clone https://github.com/ankitbanka17/prompt-based-coding.git
   cd prompt-based-coding
   ```
2. Configure your OpenAI API key (see Configuration below).
3. Build the plugin JAR:
   ```bash
   ./gradlew clean build
   ```
   The resulting plugin artifact will be at `build/distributions/prompt-based-coding.zip`.

### Installing Locally

1. In IntelliJ IDEA, go to **Settings** ➔ **Plugins**.
2. Click the gear icon and choose **Install Plugin from Disk...**
3. Select the generated ZIP (`build/distributions/prompt-based-coding.zip`).
4. Restart the IDE to activate the plugin.

### Running in Dev Mode

For iterative development, you can launch a sandbox instance of IntelliJ:
```bash
./gradlew runIde
```
This opens a fresh IDE with your plugin loaded, isolating it from your main IDE settings.

## Usage

1. Open the **Prompt-Based Coding** tool window: `View` ➔ `Tool Windows` ➔ **Code Assistant**.
2. Enter a natural language instruction, for example:
   ```text
   Add a REST controller for user management with CRUD endpoints.
   ```
3. Press **Submit**. The plugin will:
   - Send the prompt to `LLMAdapter` for instruction extraction.
   - Locate or create target code files via `PromptProcessor`.
   - Apply modifications or generate new code using `CodeModifier`.
   - Write changes to your project and display results in the tool window.

## Configuration

All settings are under **File** ➔ **Settings** ➔ **Other Settings** ➔ **Prompt-Based Coding**:

- **OpenAI API Key**: Your API key (e.g., `sk-...`).
- **Instruction Model**: Model for extracting JSON instructions (default: `gpt-4o-mini`).
- **Modification Model**: Model for code changes (default: `gpt-4`).
- **Prompt Templates**: Editable templates located in `src/main/resources/prompts/`.

## Project Structure

```
prompt-based-coding/
├── src/
│   ├── main/
│   │   ├── kotlin/com/github/ankitbanka17/promptbasedcoding/
│   │   │   ├── adapter/LLMAdapter.kt
│   │   │   ├── handlers/PromptProcessor.kt
│   │   │   ├── services/CodeModifier.kt
│   │   │   ├── tools/MyToolWindow.kt
│   │   │   └── util/PromptBuilder.kt
│   │   └── resources/prompts/
│   │       └── code_instructions.txt
│   └── test/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
└── gradlew*  
```

## Contributing

1. Fork the repository.
2. Create a feature branch:
   ```bash
git checkout -b feature/my-feature
   ```
3. Commit your changes:
   ```bash
git commit -m "Add my feature"
   ```
4. Push to your branch and open a Pull Request.

Please follow code style conventions and include unit tests for new features.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

