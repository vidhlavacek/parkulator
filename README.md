# Parkulator

Parkulator je mobilna aplikacija koja pomaže korisnicima pronaći parkirna mjesta u Rijeci.
Korisnik unosi željenu lokaciju, nakon čega aplikacija automatski pretražuje parkirališta u okolici te lokacije koja se zatim prikazuju korisniku. Svako parkiralište se rangira prema više kriterija, kako bi korisnik što lakše odabrao najbolju opciju. Rangiranje se temelji na udaljenosti od unesene lokacije, cijeni parkiranja te dostupnosti slobodnih mjesta u trenutku pretrage.

## Funkcionalnost

- unos odredišne lokacije
- pronalaženje parkirališta u okolici lokacije
- rangiranje parkirališta
- prikaz detalja parkirališta (cijena, udaljenost, dostupnost)
- prikaz parkirališta na karti
- omogućena navigacija do parkirališta
- spremanje parkirališta u favorite
- prikaz povijesti

## Rangiranje

Aplikacija za rangiranje koristi bodovanje:
- udaljenost (40%)
- cijena (30%)
- dostupnost (30%)

## Arhitektura

- Frontend: React Native (Expo)
- Backend: Spring Boot REST API
- Baza: PostgreSQL

Frontend komunicira s backendom putem HTTP zahtjeva.

## Sigurnost i autentikacija

- JWT autentikacija
- lozinke se hashiraju
- token vrijedi 1 sat
