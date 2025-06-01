# DoJo Movie

## 1. Overview
DoJo Movie is one of the leading providers of high-quality films globally. To expand its customer base, DoJo Movie is developing this mobile application for the Android operating system, allowing users to download it and increase the company's visibility.

This application is developed as a project for the "Mobile Community Solution" course.

## 2. Features
The DoJo Movie application offers several features including:

*   **User Authentication:**
    *   Login with phone number and password.
    *   User registration with phone number, password, and confirmation.
    *   OTP (One-Time Password) verification sent via SMS.
*   **Movie Browsing & Purchase:**
    *   View a list of available films. Film data is fetched from a JSON API and stored locally.
    *   View detailed information for each film, including cover image, title, and price.
    *   Purchase films with an option to select quantity and see a dynamically updated total price.
*   **User Account & History:**
    *   View transaction history, which includes details of films bought, quantity, and price.
    *   User profile section displaying the logged-in user's phone number.
    *   Logout functionality with a confirmation prompt.
*   **Location Services:**
    *   Display the DoJo Movie store location on Google Maps with a designated marker.

## 3. Technologies Used
*   **Language:** Kotlin
*   **Platform:** Android
*   **UI:**
    *   Material Design 3
    *   RecyclerView (for displaying lists of films and transaction history)
    *   Standard Android UI components: ImageView, EditText, Button, TextView
    *   Toast notifications (for user feedback)
*   **Networking:**
    *   Volley (for fetching film data from the JSON API)
*   **Database:**
    *   SQLite (for local storage of user information, film details, and transaction records)
*   **Mapping:**
    *   Google Play Services for Android Maps SDK
*   **Image Loading:**
    *   Glide

## 4. Application Structure (Key Pages)
The application consists of the following main pages:
*   **Login Page:** For existing user authentication.
*   **Register Page:** For new user registration.
*   **OTP Page:** For phone number verification via OTP.
*   **Home Page:** The main landing page after login, containing:
    *   Home Section (Store location, film listings)
    *   History Section (Link to transaction history)
    *   Profile Section (User phone number, logout)
*   **Detail Film Page:** Shows detailed information about a selected film and allows users to purchase it.
*   **History Page:** Displays a list of past transactions for the logged-in user.

## 5. Database Schema
The application uses an SQLite database with three main tables:
*   **`users`**: Stores user credentials (ID, phone number, password).
*   **`films`**: Stores film details (ID, title, image URL, price).
*   **`transactions`**: Stores user purchase history (ID, user ID, film ID, quantity).

## 6. API Endpoint
Initial film data is fetched from the following JSON API:
`https://api.npoint.io/66cce8acb8f366d2a508`

## 7. Setup Instructions
To set up and run this project locally:
1.  Clone the repository: `git clone <repository-url>`
2.  Open the project in Android Studio.
3.  **Important:** Ensure you have a valid Google Maps API key. You will need to add it to the `app/src/main/AndroidManifest.xml` file, typically within a `<meta-data>` tag like this:
    ```xml
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="YOUR_GOOGLE_MAPS_API_KEY_HERE" />
    ```
    (The project currently has a placeholder key which might need to be replaced).
4.  Build and run the application on an Android emulator (API level 24 or higher) or a physical Android device.
