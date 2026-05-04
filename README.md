# AI Context Assistant — IntelliJ Plugin

A persistent chat assistant inside IntelliJ IDEA powered by any **OpenAI-compatible model** (Ollama, LM Studio, OpenAI, Groq) or the **Claude API** — no API key required when running a local model.

Built as a technical task for the JetBrains AI Assistant internship application.

---

## Motivation

Most AI coding tools treat each prompt as stateless. The IDE already knows what language you are in, what file you are editing, and what your project is called — this plugin wires that context into every query automatically. It also keeps the full conversation history, so the model can reason about earlier exchanges in the same session.

---

## Features

- **Persistent chat panel** with full conversation history (right panel)
- **Right-click → Ask AI about This** (`Ctrl+Alt+A`) pastes the selection into the chat input with file/language/project context pre-filled
- **OpenAI-compatible endpoint support**: works with Ollama, LM Studio, OpenAI, Groq — no API key needed for local models
- **Claude API support**: switch providers in Settings
- **Per-model override**: pick any model from the chat panel without changing global settings
- **Per-project history**: conversation saved to `.idea/ai-assistant/history.json`, restored on IDE restart
- Non-blocking: API calls run on a pooled thread; the EDT is never blocked

---

## Architecture

```
actions/
  AskAIAction.kt            — editor action; pastes selection into ChatPanel input
chat/
  ChatMessage.kt            — Role (USER/ASSISTANT) + content + timestamp
  ChatSession.kt            — in-memory ordered message list
  ChatSessionManager.kt     — project service; Gson persistence to .idea/ai-assistant/history.json
provider/
  LlmProvider.kt            — interface: send(messages, model) → String
  OpenAiProvider.kt         — OpenAI-compatible HTTP client (Ollama, LM Studio, OpenAI, Groq…)
  ClaudeProvider.kt         — Anthropic Messages API client
settings/
  PluginSettings.kt         — PersistentStateComponent; providerType, baseUrl, defaultModel, apiKey
  PluginSettingsConfigurable.kt — Settings UI under Tools → AI Context Assistant
toolwindow/
  AIToolWindowFactory.kt    — registers the persistent right-panel tool window
  ChatPanel.kt              — 3-zone panel: header (model picker + Clear), message list, input row
  MessageRenderer.kt        — ListCellRenderer with indented assistant replies
util/
  JsonUtil.kt               — escapeJson / unescapeJson string extensions
```

No third-party dependencies beyond the IntelliJ Platform SDK (Gson is bundled with the platform).

---

## Setup

### Prerequisites
- IntelliJ IDEA 2024.1+ (Community or Ultimate)
- JDK 17+
- Optional: a Claude API key **or** a local Ollama/LM Studio installation

### Quick start with Ollama (no API key needed)
1. Install [Ollama](https://ollama.com) and run: `ollama pull llama3.2`
2. Run `./gradlew runIde`
3. In the sandbox IDE: **Settings → Tools → AI Context Assistant**
   - Provider: `OpenAI-compatible endpoint`
   - Base URL: `http://localhost:11434`
   - Default model: `llama3.2`
4. Open any file, select code, press `Ctrl+Alt+A`

### Build distributable plugin
```bash
./gradlew buildPlugin
# Output: build/distributions/intellij-ai-context-assistant-1.0.0.zip
```

### Install
1. Build the plugin (above) or download from releases
2. In IntelliJ: **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select the `.zip` file and restart

---

## Usage

1. Open any file in the editor
2. Select a block of code
3. Right-click → **Ask AI about This** (or `Ctrl+Alt+A`)
4. The **AI Assistant** panel fills the input with your selection — review, edit, and press Enter or click Send
5. The response appears in the chat list; conversation history is preserved across messages

---

## Design decisions

| Decision | Rationale |
|---|---|
| `LlmProvider` interface | Decouples HTTP logic from UI; trivial to add new providers |
| `java.net.http.HttpClient` over OkHttp | Avoids an extra dependency; available in JDK 11+ which IntelliJ already requires |
| Gson (platform-bundled) for history | No extra dependency; stable serialization for a simple flat list |
| `executeOnPooledThread` + `invokeLater` | Keeps the EDT free during network I/O; standard IntelliJ pattern |
| `PersistentStateComponent` for settings | IDE-native persistence; no manual file I/O or external credential stores |
| `internal` on `buildBody`/`parseContent` | Allows unit testing pure JSON logic without the IntelliJ Platform test harness |
| `ActionUpdateThread.BGT` | Moves the `update()` check off the EDT as required by modern IntelliJ Platform guidelines |

---

## Author

**Musa Berkay Kocabasoglu** — [github.com/musaberkay](https://github.com/musaberkay) — [LinkedIn](https://linkedin.com/in/musa-berkay-kocabasoglu)
