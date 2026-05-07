# Parkulator

Parkulator is a mobile application that helps users find available parking spaces in Rijeka.
The user enters a desired location, after which the application automatically searches for nearby parking lots. These parking options are then displayed to the user.
Each parking lot is ranked based on multiple criteria to help the user choose the best option. The ranking is based on the distance from the entered location, the parking price and the number of free spaces at the time of the search.

## Functionality

- entering a destination location
- finding nearby parking lots
- ranking parking options
- displaying parking details (price, distance, availability)
- showing parking locations on a map
- navigation to selected parking
- saving parking lots to favorites
- viewing search and selected parking history


## Architecture

- Frontend: React Native (Expo)
- Backend: Spring Boot REST API
- Database: PostgreSQL

The frontend communicates with the backend via HTTP requests.


## Installation

Before running the project, make sure you have installed:

  - Java JDK 17
  - Android Studio
  - PostgreSQL
  - Git
  - Node.js


## Running the Project

  ### Backend (Spring Boot)
  
  Navigate to the backend directory and run:
  ```bash
  cd parkulator-backend
  ./mvnw spring-boot:run
  ```
  
  ### Frontend (React Native - Expo)
  
  Navigate to the frontend directory and run:
  ```bash
  cd parkulator-frontend
  npx expo start
  ```

  This will open the Expo Developer Tools.
  
  To run the app:
  
  - Press  ```a```  to open on an Android emulator (must be running in Android Studio)
  - Or scan the QR code using Expo Go on a physical device


## Usage guide

  1. Launch the application on your device
  2. Enter a destination location
  3. The app will display nearby parking options ranked by distance, price and availability
  4. Select a parking lot to view detailed information
  5. Start navigation to your chosen parking location
