## Sprint 3
- [ ] Connect backend model with GUI
- [ ] Decide on and implement preliminary database (Firebase, SQLite?)
- [ ] Add wardrobe functionality to display user clothes
- [ ] Add functionality to add Clothes objects to user database
- [ ] Research color API/algorithm to examine user images
- [ ] Add more categorical information to clothes model (hot/cold, formal/casual, waterproof)

## Sprint 2

- [x] Create data structure for clothing
- [x] Add functionality to add clothes
- [x] Display clothing members (attributes)
- [ ] Refine main menu XML
- [ ] Add additional functionality to main menu
- [x] Research our API (find useful endpoints)
- [x] Implement preliminary weather API

## Sprint 1

- [x] Define basic activities (screens) in app
- [x] Create storyboards
- [x] Use storyboards to generate user stories
- [x] Create skeleton layout XML and Java logic file for main menu
- [x] Create skeleton layout XML and Java logic file for “Personal Closet”
- [x] Create basic functionality to go from main menu to personal closet

---

## Backlog

- Create interface to interact with the app (split this into more logs)
  - GUI to go back and forth between activities
  - GUI to add clothing
  - GUI to edit clothing
  - GUI to add outfits
  - GUI to save outfits (waiting for backend + database)
  - GUI to display saved outfits + clothing

- Create backend data structures (split this into more logs)
  - Create Java classes for clothes
  - Add functionality to add clothes in the backend
  - Design hierarchy for classes 
  - Design interface for object interactions with the front-end
  - Implement database storage

- Research database API
  - Decide what database to use
  - Implement database with back-end
  - Need schemas (prob the same as the classes)

- Research our API (Weather and Color)
  - How to interact with the API
  - How to extract useful information from responses
  - How to implement data from API into our app
  - Integrate API into the app

## User stories

- As a user I want to view all of my clothes (individual items).
- As a user I want to filter and sort clothing items by their attributes.
- As a user I want to use my phone camera to add clothes items.
- As a user I want to see outfits that fit the current weather to know what is comfortable to wear
- As a user I want to be able to save and view outfits that the app generated to use them in the future

## Design implementations

#### Back end

Use factory for clothes generation, and maybe decorator for customizing each category of clothing. Use adapter pattern for parsing third party API.
