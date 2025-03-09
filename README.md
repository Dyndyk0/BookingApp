# Hotel Booking App

This repository contains the code for a Hotel Booking App.

## Functionality Description

The application provides interface for browsing and managing hotel bookings. Key features include:

*   **Bottom Navigation Bar:** Easy navigation between main sections using a bottom navigation bar.
*   **My Bookings:** View and manage user's existing hotel bookings.
*   **Hotel Search:** Find hotels based on city, check-in, and check-out dates.
*   **User Profile:** User authentication (login/registration) and profile management.

### Navigation

The bottom navigation bar provides access to the three main sections:

1.  **My Bookings**
2.  **Hotel Search**
3.  **Profile**

### 1. My Bookings

This section displays all of the user's hotel bookings using a `RecyclerView`. Each booking displays:

*   Hotel Name
*   Check-in Date
*   Check-out Date
*   Room Number
*   City

### 2. Hotel Search

Allows the user to search for hotels based on city, check-in date, and check-out date.

The search results are displayed in a `RecyclerView` showing:

*   Hotel Image
*   Hotel Name
*   Price

Clicking the "Book" button navigates to a screen with detailed hotel information and room selection.

### 3. Profile

This section handles user authentication.

*   **Login/Registration:** If the user is not logged in, a login form is displayed.
*   **Registration:** A registration form allows new users to create an account by providing a username, email, and password.

*   **Profile Information:** After logging in, the user sees their profile information and a "Logout" button.
