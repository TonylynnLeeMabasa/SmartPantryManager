# Smart Pantry Manager

Smart Pantry Manager is a Java-based Android application developed to help users manage pantry ingredients and discover recipes based strictly on the ingredients they have available.

The application allows users to add, view, edit and delete pantry items, manage quantities and units, track expiry dates, and receive alerts for expiring or low-stock items. Users can also browse recipes and receive recipe suggestions based on the ingredients currently stored in their pantry.

## Features

- Add pantry ingredients
- Edit pantry ingredients
- Delete pantry ingredients
- Store ingredient quantity and unit
- Store expiry dates
- Store low-stock thresholds
- Store pantry locations
- Search pantry items
- Filter pantry items by category and location
- Expiring-soon pantry alerts
- Low-stock pantry alerts
- Persistent alert settings
- Browse a collection of 20 recipes
- View complete recipe details
- Strict ingredient matching
- Check whether the pantry contains enough ingredients for a recipe
- Add missing recipe ingredients to the shopping list
- Shopping list management
- Mark shopping items as purchased
- Add purchased shopping items to the pantry
- Firebase Realtime Database persistence
- Role-free single-user mobile application design with simple navigation

## Technologies Used

- Java
- Android Studio
- XML layouts
- Android SDK
- Firebase Realtime Database
- Firebase Authentication/Google Services integration
- RecyclerView
- Custom RecyclerView Adapters
- Android Activities and Intents
- SharedPreferences

## Database

The application uses **Firebase Realtime Database** for persistent data storage.

Firebase was selected because it provides a cloud-hosted NoSQL database that can store and retrieve application data in real time. It also allows pantry, recipe and shopping-list data to remain available when the application is closed and reopened.

The main Firebase data areas used by the application include:

- `pantry_items`
- `recipes`
- `shopping_items`

Application preferences such as alert settings are stored locally using Android SharedPreferences.

## Recipe Matching

Smart Pantry Manager uses strict recipe matching.

A recipe is shown as makeable only when:

1. Every required ingredient is available in the pantry.
2. The pantry quantity is equal to or greater than the required recipe quantity.
3. Ingredient names are compared using normalized matching to handle simple differences such as singular and plural forms.

For example, an ingredient such as `tomato` can match `tomatoes`.

Recipes that cannot be made are kept separate from recipes that the user can make, and the application identifies missing ingredients.

## Main Screens

The application contains the following main screens:

- Pantry
- Add Pantry Item
- Edit Pantry Item
- Suggested Recipes
- Recipe Details
- Shopping List
- Profile & Settings

## Project Structure

The project follows a standard Android Studio structure.

Important components include:

- Activities for application screens and navigation
- XML layout files for the user interface
- Java model classes for application data
- RecyclerView adapters for displaying lists
- Firebase Database references for persistent data
- SharedPreferences for application settings
- Recipe matching logic for determining available recipes
- Recipe seeding logic for loading the initial recipe collection

## Setup and Installation

### Requirements

To run the project, install:

- Android Studio
- Android SDK
- Java Development Kit (JDK)
- An Android emulator or Android device

### Steps

1. Clone or download the repository from GitHub.
2. Open the project in Android Studio.
3. Allow Android Studio to sync the Gradle files.
4. Make sure the `google-services.json` Firebase configuration file is placed inside the `app` directory.
5. Connect an Android device or start an Android emulator.
6. Build the project.
7. Run the application from Android Studio.

## Firebase Configuration

The application is connected to a Firebase Realtime Database project.

When setting up the project in a different Firebase environment, create a Firebase project, register the Android application using the correct package name, enable Realtime Database, and place the generated `google-services.json` file inside the `app` directory.

## GitHub Repository

This repository contains the Android Studio source code for the Smart Pantry Manager application, including the Java source files, XML layouts, Firebase integration and project configuration.

## Testing

The application was tested during development to verify:

- Pantry item creation
- Pantry item editing
- Pantry item deletion
- Quantity and unit storage
- Search functionality
- Category and location filtering
- Expiry alerts
- Low-stock alerts
- Settings persistence
- Recipe browsing
- Strict recipe matching
- Shopping list functionality
- Purchased item synchronisation with the pantry
- Firebase data persistence after closing and reopening the application

## Author

Smart Pantry Manager  
Mobile App Development 700  
Java Android Application