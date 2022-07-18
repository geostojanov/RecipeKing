# RecipeKing
          
Features Implemented:
1. Login / Signup
2. Browsing recipies by category
3. Favorites
4. User Creation of Recipes
5. Popular Recipe articles and videos
6. Commenting and Rating on Recipes
7. Sharing Recipes

SQLite and SharedPreferences:
* SharedPreferences was used for remembering the login info for a user. When a user logs in, he/she stays logged in, even if the app is closed and then started again. SharedPreferences was used because it is a convenient way to store small set of data like login info.
* SQLite was used for storing the user’s  ‘MyRecipes’ recipes locally, so that even when no network connection is available, the user will get back the data from a local database. SQLite was used in this case, because SQLite is more convenient for storing bigger set of data.

Web Services:
* Web services was used to pull recipes from an online API database. 

Content Sharing:
* In the RecipeDetailsFragment there is a share icon which enables the user to share the recipe via all corresponding outlets of messaging, Ex: Email, Messages, Hangouts, etc...

User Stories Implemented:
1. As a user, I would like to browse popular recipes by categories.
2. As a user, I would like to be able to add certain recipes I like to a favorite list, so I can refer to them later.
3. As a user, I would like to be able to add my own recipe, so all my recipes I have on paper would be easily accessible.
4. As a user, I would like to be able to rate recipes.
5. As a user, I would like to be able to comment on recipes.
6. As a user, I would like to be able to have access to helpful guide videos for recipes.
7. As a user, I would like to be able to create an account so I can keep track of my favorite recipes.
8. As a user, I would like to be able to share recipes I like over social media. 
10. As a user, I would like to be able to have access to popular cooking articles.
