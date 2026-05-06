<div align="center">
  <img src="screenshots/logo.png" alt="DoJo Movie" width="120" />
  <h1>DoJo Movie</h1>
  <p>Android app for browsing and purchasing cinema tickets.</p>

  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue" />
  <img src="https://img.shields.io/badge/License-MIT-yellow" />
</div>

---

## Screenshots

| Login | Register | OTP |
| :---: | :---: | :---: |
| <img src="screenshots/LoginPage.png" width="200" /> | <img src="screenshots/RegitesPage.png" width="200" /> | <img src="screenshots/OTPPage.png" width="200" /> |

| Home | Detail | History | Profile |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/HomePage.png" width="200" /> | <img src="screenshots/DetailPage.png" width="200" /> | <img src="screenshots/HistoryPage.png" width="200" /> | <img src="screenshots/UserPage.png" width="200" /> |

## Features

- **Phone Auth** — Register and sign in using phone number + OTP verification with a 2-minute expiry window.
- **Movie Catalog** — Pulls live movie listings from a remote JSON API and renders them in a RecyclerView with Glide-loaded posters.
- **Store Locator** — Embedded Google Maps fragment pinpointing the nearest DoJo Movie store location.
- **Purchase Flow** — Select quantity, see real-time total price, and confirm checkout in a single screen.
- **Transaction History** — SQLite-backed purchase records with filter support, persisted locally across sessions.
- **Profile** — Displays the logged-in user's phone number with a sign-out option.

## Tech Stack

| Layer | Library |
|---|---|
| Language | Kotlin |
| Networking | Volley |
| Image loading | Glide 4.16 |
| Maps | Google Maps SDK 18.2 + Location 21.0 |
| UI | Material Design 3, ViewBinding |
| Storage | SQLite (via SQLiteOpenHelper) |
| Min SDK | 24 (Android 7.0) |

## Setup

1. Clone the repo and open it in **Android Studio Hedgehog or later**.
2. Add the following to your `local.properties`:
   ```
   MAPS_API_KEY=your_google_maps_api_key
   BASE_URL=your_api_base_url
   ```
3. Sync Gradle and run on a device or emulator running **API 24+**.

> Targets SDK 35. Make sure your local Android SDK includes platform 35.

## License

[MIT](LICENSE) — 2026 ghtmarco
