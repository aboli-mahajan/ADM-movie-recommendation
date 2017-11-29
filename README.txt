SUMMARY
************************************************************************************************************************
There are numerous movies available to entertain you. But deciding which movie to watch can be a tough choice, especially 
after a long day. So, in order to avoid the problem of deciding which one to watch, we tend to simply repeat our favorite
ones. This problem is common to all of us. To make our lives a little easier, we have come up with a Movie Recommender!! 
Would it not be great to have a system that will tell you what movies you could watch based on your preferences? Not only
this, it will recommend you the movies based on the types of movies you have watched previously as well! We have used movies
previously rated by hundreds of users as a basis for our recommendation.


************************************************************************************************************************
CHANGES
************************************************************************************************************************

version 0.4
Release Candidate
- Modified the simple recommendation done using rating and movie release year.
- Added a feature to enable users to rate new movies and update existing ratings.
- Reduced code repetition by storing Map-Reduce output in a new collection.
- Used MongoDB aggregation pipeline to group movies into a unique list.
- Added input validations.
- Performed error handling.
- Minor bug fixes.


version 0.3
Beta Release
- Devised a Map-Reduce code to find average ratings of movies belonging to a set of input genres.
- Evaluated a user's commonly reviewed movie genres. Used the most frequent genres as an input to the Map-Reduce function to find matching movies.
- Added the option to run the recommendation system in a loop.
- Further added a utility package for common code.
- Limited the results to the top 20 records based on highest average rating in each case.
- To do: error handling


version 0.2
Alpha Release
- Completed a first pass implementation of the primary use case.
- Implemented recommendation functions based on the rating and the release year of the movies through user input.
- Modularized the code that handles database connectivity and read-write calls into a different class. Business logic is included in RecommendMovies.java. The main class i.e "MoviesMain.java" is responsible for calling the functions of different classes.


version 0.1
Proof of Architecture


************************************************************************************************************************
SETUP
************************************************************************************************************************

1. Unzip the folder.

2. Navigate to the project folder called 'team3-project'.

3. If gradle exists, then run the command 'gradle run' in the command prompt.

4. Otherwise run 'gradlew.bat' for Windows or './gradlew' for Linux systems and then run 'gradle run' command.


************************************************************************************************************************
OTHER NOTES
************************************************************************************************************************

1. The database has a slight modification. We have added columns for username and email address and fixed columns that had incomplete values.

2. You may try one of the email addresses - "Cody.Almeda@gmail.com", "Edwin.Frankel@gmail.com", "Crissy.Siemens@gmail.com" and "Kay.Kriner@gmail.com" for testing menu case 4.

R&D aspects
- Developed a genre similarity and rating distance based algorithm for recommendation.
- Wrote a test code to create a matrix of all movies and the genres they belong to.
- Deferred using this matrix until future releases for finding similarities between the movies viewed by a user and the ones from the database.

