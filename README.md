# AI Context Assistant — IntelliJ Plugin

An IntelliJ IDEA plugin that explains selected code using [Claude AI](https://www.anthropic.com/claude), automatically enriching every request with IDE context (file name, language, project name) so answers are grounded in your actual codebase — not just the snippet in isolation.

Built as a technical task for the JetBrains AI Assistant internship application.

---

## Motivation

Most AI coding tools treat each prompt as stateless. The IDE already knows what language you are in, what file you are editing, and what your project is called — this plugin wires that context into every query automatically, demonstrating the core idea behind the JD's focus on *"enhancing third-party coding agents with IDE-specific context and capabilities."*

---

## Features

- **Right-click → Ask AI about This** on any selection, or press `Ctrl+Alt+A`
- Context-aware prompts: file name, language extension, and project name are included automatically
- Responses appear in a persistent **AI Assistant** tool window (right panel)
- API key stored securely via IntelliJ's settings persistence (`PersistentStateComponent`)
- Non-blocking: API calls run on a pooled thread; the EDT is never blocked

---

## Architecture

```
actions/
  AskAIAction.kt          — editor action; collects selection + IDE context, dispatches API call
api/
  ClaudeClient.kt         — lightweight HTTP client (java.net.http); calls Anthropic Messages API
settings/
  PluginSettings.kt       — PersistentStateComponent; stores API key across IDE restarts
  PluginSettingsConfigurable.kt — Settings UI under Tools → AI Context Assistant
toolwindow/
  AIToolWindowFactory.kt  — registers the persistent right-panel tool window
  AIToolWindowPanel.kt    — JPanel with a scrollable text area for AI responses
```

No third-party dependencies beyond the IntelliJ Platform SDK. JSON is handled with a targeted regex on the stable Anthropic response shape rather than pulling in a JSON library.

---

## Setup

### Prerequisites
- IntelliJ IDEA 2024.1+ (Community or Ultimate)
- JDK 17+
- A [Claude API key](https://console.anthropic.com)

### Run in development
```bash
./gradlew runIde
```

### Build distributable plugin
```bash
./gradlew buildPlugin
# Output: build/distributions/intellij-ai-context-assistant-1.0.0.zip
```

### Install
1. Build the plugin (above) or download from releases
2. In IntelliJ: **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select the `.zip` file and restart

### Configure API key
**Settings → Tools → AI Context Assistant** → paste your Claude API key.

---

## Usage

1. Open any file in the editor
2. Select a block of code
3. Right-click → **Ask AI about This** (or `Ctrl+Alt+A`)
4. The **AI Assistant** panel opens on the right with Claude's explanation

---

## Design decisions

| Decision | Rationale |
|---|---|
| `java.net.http.HttpClient` over OkHttp | Avoids an extra dependency; available in JDK 11+ which IntelliJ already requires |
| `executeOnPooledThread` + `invokeLater` | Keeps the EDT free during network I/O; standard IntelliJ pattern |
| `PersistentStateComponent` for API key | IDE-native persistence; no manual file I/O or external credential stores |
| Regex JSON extraction | The Anthropic Messages API response shape is stable and documented; a full JSON parser would be disproportionate for extracting a single field |
| `ActionUpdateThread.BGT` | Moves the `update()` check off the EDT as required by modern IntelliJ Platform guidelines |

---

## Author

**Musa Berkay Kocabasoglu** — [github.com/musaberkay](https://github.com/musaberkay) — [LinkedIn](https://linkedin.com/in/musa-berkay-kocabasoglu)
