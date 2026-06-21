# MangoApps PhoneBook

A production-quality Android phonebook application built with Kotlin, Jetpack Compose, and Clean Architecture.

---

## Features

### Contacts
- **Local Contacts** — Reads device contacts with circular photo/initials avatars
- **Remote Contacts** — Infinite-scroll paginated list from REST API (dummyjson.com)
- **Offline Cache** — Remote contacts cached in Room DB; available without network
- **Offline Fallback** — "Connection lost" error screen with retry when no cached data
- **Pull-to-Refresh** — Swipe down to reload on both local and remote tabs
- **Contact Actions** — Tap any contact to open a Call / SMS action dialog

### Call Logs
- **Three tabs** — Incoming, Outgoing, Missed with live badge counts
- **Call details** — Caller name/number, duration, smart timestamp (Today/Yesterday/date)
- **Tap to dial** — Opens native phone dialer with number pre-filled

### SMS Inbox
- **Message preview** — Sender + 2-line body preview with timestamp
- **Detail dialog** — Full message body, sender, formatted timestamp
- **Real-time updates** — ContentObserver on `content://sms` and `content://mms-sms` detects new/deleted messages automatically without manual refresh

### App-wide
- **Navigation Drawer** — Contacts / Call Logs / SMS with last-screen persistence across app restarts
- **Collapsing Toolbar** — MediumTopAppBar with `exitUntilCollapsedScrollBehavior`
- **Shimmer Loading** — Animated skeleton placeholders while data loads
- **Error + Empty States** — Dedicated UI for every failure and empty scenario
- **Animated Transitions** — Fade + slide between navigation destinations
- **Dark Mode** — Dynamic color (Android 12+), manual Material 3 palette on older devices
- **Runtime Permissions** — Per-screen permission requests with rationale dialogs and Settings deep-link; one denied permission does not break other modules

---

## Architecture
**Package structure** — feature-based inside a single Gradle module:
```
com.mangoapps.phonebook
├── core/
│   ├── db/           AppDatabase (Room)
│   ├── datastore/    AppPreferences (last screen)
│   ├── navigation/   NavHost + Screen sealed class
│   ├── network/      NetworkConnectivityChecker
│   └── ui/           Shared composables + Material 3 theme
├── di/               Hilt modules
└── feature/
    ├── contacts/     Local + Remote tabs, Paging, Room cache
    ├── calllogs/     Incoming / Outgoing / Missed tabs
    └── sms/          Inbox, ContentObserver, detail dialog
```

---

## Tech Stack

| Library | Purpose | Version |
|---|---|---|
| Kotlin | Language | 1.9.22 |
| Jetpack Compose | Declarative UI | BOM 2024.02.00 |
| Material 3 | Design system | via BOM |
| Hilt | Dependency injection | 2.50 |
| Retrofit + OkHttp | REST client | 2.9.0 / 4.12.0 |
| Paging 3 | Remote contact infinite scroll | 3.2.1 |
| Room | Local DB — remote contact cache | 2.6.1 |
| DataStore | Last-visited screen preference | 1.0.0 |
| Coil | Image loading | 2.5.0 |
| Navigation Compose | Screen navigation | 2.7.6 |
| Accompanist | Permissions + SwipeRefresh | 0.32.0 |
| Coroutines + Flow | Async / reactive streams | 1.7.3 |
| MockK | Unit test mocking | 1.13.8 |
| Turbine | Flow / StateFlow testing | 1.0.0 |
| Compose UI Test | Instrumented UI tests | via BOM |

---

## Requirements

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** (compile target) / JDK 24 (APK build)
- **Min SDK** 21 (Android 5.0)
- **Target SDK** 34 (Android 14)
- Physical device or emulator with API 21+

---

## Setup

```bash
# Clone
git clone <repo-url>
cd MangoAppsPhoneBooks

# Open in Android Studio and sync Gradle

# Debug APK
./gradlew assembleDebug

# Release APK (signed)
./gradlew assembleRelease

# Unit tests
./gradlew test

# Instrumented UI tests (requires connected device/emulator)
./gradlew connectedDebugAndroidTest
```

---

## Tests

### Unit Tests — `app/src/test/`

| Test | Coverage |
|---|---|
| `ContactsViewModelTest` | Loading → Success flow, error handling, refresh re-fetches |

### UI Tests (Instrumented) — `app/src/androidTest/`

| Test | Coverage |
|---|---|
| `EmptyStateTest` | Contacts / Call Logs / SMS empty state titles and subtitles |
| `ErrorStateTest` | Error message display, retry button visibility, retry callback |
| `LoadingStateTest` | Circular progress indicator renders |
| `SmsItemTest` | Sender name, body preview, click callback, phone number sender |
| `SmsDetailDialogTest` | Sender title, full body, Message/Time labels, Close button and dismiss |
| `CallLogItemTest` | Caller name, duration format, "Not connected" label, SIM label, click |

**Total: 3 unit tests + 26 UI tests — all passing**

---

## Permissions

| Permission | When requested | Purpose |
|---|---|---|
| `READ_CONTACTS` | On Contacts screen open | Display local device contacts |
| `READ_CALL_LOG` | On Call Logs screen open | Display call history |
| `CALL_PHONE` | On call action tap | Initiate phone call |
| `READ_SMS` | On SMS screen open | Display inbox messages |
| `SEND_SMS` | On SMS action tap | Open SMS composer |
| `INTERNET` | Always | Fetch remote contacts from API |
| `ACCESS_NETWORK_STATE` | Always | Offline connectivity detection |

Permissions are requested lazily — only when the relevant screen is first opened. Permanently denied state directs user to app Settings. Denying one permission does not affect other modules.

---

## API

Remote contacts are fetched from [DummyJSON](https://dummyjson.com/users) — a free mock REST API.

```
GET https://dummyjson.com/users?limit=20&skip=0
GET https://dummyjson.com/users?limit=20&skip=20
```

Page size: 30. Each page is cached to Room on successful fetch. On network failure, Room cache is served as fallback.

---

## Known Behavior

**SMS deletion via Google Messages bin** — When a message is moved to Google Messages' "Recently Deleted" bin, it is NOT immediately removed from the Android system SMS ContentProvider. Our app reads from the system provider, so the message remains visible until the bin is permanently emptied. This is expected behavior for any third-party SMS reader — Google Messages' bin state is stored in Google's private database and is not accessible via public Android APIs.
