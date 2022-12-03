## Sprint 5
- [x] Add profiles functionality
- [x] MainActivity/Other classes?: Ask for permissions before appropriate activity (storage & camera in AddToWardrobe, location in GenerateOutfit... etc)
- [x] ConfirmToWardrobe: Finish implementing Color API -- add color field to Clothes item in DB
- [x] **Jason** | Integrate Settings with Firebase
- [x] **Sarsen** | ViewWardrobe: Add filtering by type
- [x] **Michael** | ConfirmToWardrobe: Add waterproof setting and make it look better
- [x] **Michael** | ViewWardrobe: Edit button
- [ ] **Abdel** | GenerateOutfit: Start generating simple outfits on button click
- [ ] **Abdel** | GenerateOutfit: Add save button which saves current outfit to database
- [ ] ViewSavedOutfits: Pull saved outfits from DB and display
- [ ] **Sarsen** | User friendly improvements (QA)

## Sprint 4
- [x] Automate RadioGroups and RadioButtons for clothing qualities in ConfirmToWardrobe
- [x] Implement background removal API
- [x] Implement color analysis API (Works, doesn't add color data to DB entries yet)
- [x] Upload images to Firebase Storage manager
- [x] Successfully add items to database
- [x] Successfully read items from database
- [x] Develop scrolling layout XML template for ViewWardrobe (RecyclerView)
- [x] Display items from database in ViewWardrobe activity

## Sprint 3
- [x] Establish authentication and connection to Firebase
- [x] Add camera functionality to take and store multiple pictures
- [x] Work on "ConfirmToWardrobe" activity - add clothing options and submit feature
- [x] Request location permissions from user
- [x] Display location data in GenerateOutfit activity
- [x] Expand process for color-matching algorithm
- [x] Add layering system into Clothes class

## Sprint 2

- [x] Create data structure for clothing
- [x] Add functionality to add clothes
- [x] Display clothing members (attributes)
- [x] Refine main menu XML
- [x] Add additional functionality to main menu
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
