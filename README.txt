SUMMARY
************************************************************************************************************************
There are numerous movies available to entertain you. But deciding which movie to watch can be a tough choice, especially 
after a long day. So, in order to avoid the problem of deciding which one to watch, we tend to simply repeat our favorite
ones. This problem is common to all of us. To make our lives a little easier, we have come up with a Movie Recommender!! 
Would it not be great to have a system that will tell you what movies you could watch based on your preferences? Not only
this, it will recommend you the movies of your favorite stars too! You may wonder what movies your friends are watching,
and whether they are more interesting than the ones you are watching. No worries! Our system will link to your Facebook
account and notify you of the movies that your friends have liked! Hurray!!


************************************************************************************************************************
CHANGES
************************************************************************************************************************
version 0.2
- alpha release
- complete a first pass implementation of the primary use case 
- implementation of recommendation functions based on rating and release year through user input
- modularized the code that handles database calls and read write to database in a different class. Business logic is included in RecommendMovies.java. The main class i.e "MoviesMain.java" is responsible for calling the function of different classes


version 0.1
- proof of architecture


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
