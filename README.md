# Dojo-Movie

<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="Logo" width="120" />
  <p align="center">
    <strong>A modern Android application for cinema ticket booking.</strong>
    <br />
    Built with Kotlin for the Mobile Community Solution course.
  </p>
  
  <p align="center">
    <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg" alt="Platform" />
    <img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language" />
    <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License" />
  </p>
</div>

---

## 📖 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Previews](#previews)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Project Structure](#project-structure)

## 🌟 Overview
**Dojo-Movie** is a comprehensive movie ticketing platform designed to provide a seamless user experience for browsing current cinema listings and securing tickets. The app combines robust authentication, local data persistence, and interactive location services.

## ✨ Features
- 🔐 **Secure Authentication**: Phone-based registration and login with simulated OTP verification.
- 🎬 **Dynamic Movie Catalog**: Real-time fetching of movie data from a remote JSON API.
- 📍 **Store Locator**: Integrated Google Maps SDK to find the nearest Dojo-Movie branch.
- 🛒 **Intuitive Booking**: Streamlined transaction flow with automated price calculations.
- 📜 **Purchase History**: Local tracking of all movie tickets purchased by the user.
- 💾 **Offline Support**: SQLite integration for caching movie lists and user data.

## 📱 Previews

| Login | Register | OTP | Home |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/login.png" width="180" /> | <img src="screenshots/register.png" width="180" /> | <img src="screenshots/otp.png" width="180" /> | <img src="screenshots/home.png" width="180" /> |

| Detail | History | Profile |
| :---: | :---: | :---: |
| <img src="screenshots/detail.png" width="180" /> | <img src="screenshots/history.png" width="180" /> | <img src="screenshots/profile.png" width="180" /> |

## 🛠️ Tech Stack
- **Core**: Kotlin, Android SDK
- **Networking**: Volley (HTTP requests)
- **Image Loading**: Glide
- **Database**: SQLite (Local persistence)
- **Services**: Google Play Services (Maps & Location)
- **UI Components**: Material Design 3

## 🚀 Installation
1. **Clone the repository**
   ```bash
   git clone https://github.com/ghtmarco/dojo-movie.git
   ```
2. **Open in Android Studio**
   Wait for Gradle to sync dependencies.
3. **Configure API Keys**
   Add your Google Maps API Key to `local.properties`:
   ```properties
   MAPS_API_KEY=YOUR_API_KEY_HERE
   ```
4. **Build & Run**
   Run the project on an Emulator or physical device (Min SDK 24).

## 📂 Project Structure
```text
app/src/main/java/com/example/dojomovie/
├── activities/       # UI Pages (Login, Register, Home, etc.)
├── models/           # Data classes
├── adapters/         # RecyclerView adapters
└── utils/            # Database and network helpers
```

---

**Developed for the Mobile Community Solution Course**
Copyright (c) 2026 [Dojo Movie Team](https://github.com/ghtmarco)
Licensed under the [MIT License](LICENSE).
