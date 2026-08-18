# Messenger KMP Client Knowledge Base

This repository contains the client app for the messenger project. The server-side code lives in a separate folder and is not covered here.

## What This Client Is

- Kotlin Multiplatform app targeting Android and Desktop JVM
- UI is built with Compose Multiplatform
- Shared business logic lives in `shared/src/commonMain`
- Platform-specific setup lives in `shared/src/androidMain` and `shared/src/jvmMain`

## Main Building Blocks

- `App.kt` decides whether the app starts on splash, login, or the authenticated chat flow
- `MainNavigation.kt` defines the main routes and bottom navigation
- `local/` stores session data and app state in Room and DataStore
- `network/` contains Ktor APIs and the websocket client
- `repository/` combines network, database, and session logic
- `viewModel/` holds screen state and user-facing actions

## Runtime Flow

1. `SessionStorage` checks whether a user id is already stored.
2. `AppStartupViewModel` turns that into `Loading`, `Unauthenticated`, or `Authenticated`.
3. `SessionScopeManager` recreates the Koin session scope so auth-scoped dependencies are reset cleanly.
4. After login, tokens are saved, and the message socket is connected.
5. After logout, the socket is disconnected, local data is cleared, and the session storage is wiped.

## Data And Sync Model

- Room is used for cached users, chats, and messages
- Paging is used for message history
- Pending messages are written locally first, then updated with the server response
- Incoming websocket events update the local database so the UI stays live

## Network Contract Summary

- Base API URL is built from `AppConfig.API_URL`
- Websocket host comes from `AppConfig.WS_HOST`
- Android and Desktop both use the same host value: `netalab.ru`
- Auth endpoints live under `/api/v1/auth`
- User endpoints live under `/api/v1/user`
- Message endpoints live under `/api/v1/message`
- Websocket events include `new_message`, `delete_message`, and `edit_message`

## Important Client Rules

- Authenticated network calls use bearer tokens from `SessionStorage`
- The auth client is separate from the main API client
- The main API client refreshes tokens automatically when possible
- The websocket connection should be considered session-scoped
- If the backend contract changes, update DTOs and repository logic together

## Good First Places To Read

- `shared/src/commonMain/kotlin/git/alektro3000/messenger/App.kt`
- `shared/src/commonMain/kotlin/git/alektro3000/messenger/MainNavigation.kt`
- `shared/src/commonMain/kotlin/git/alektro3000/messenger/repository/MessageRepository.kt`
- `shared/src/commonMain/kotlin/git/alektro3000/messenger/network/`
- `shared/src/commonMain/kotlin/git/alektro3000/messenger/local/`

