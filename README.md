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

## Ranking

The application uses this scoring system for ranking:

- distance (40%)
- price (30%)
- availability (30%)

## Architecture

- Frontend: React Native (Expo)
- Backend: Spring Boot REST API
- Database: PostgreSQL

The frontend communicates with the backend via HTTP requests.

## Security and Authentication

- JWT authentication
- passwords are hashed
- token validity: 1 hour
