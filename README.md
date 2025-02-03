# Recipe API

You can directly boot this spring boot application. Main starter is RecipeAPIApplication.java. This API application
assumes that there are only two users: userA and userB and their passwords passA and passB respectively. Initial User
data is loaded within booting phase. You can check it from LoadDatabase.java

H2 was chosen for fast development. There are USER, RECIPE and INGREDIENTS tables. In RECIPE table, there is USER_ID
column to keep relation with USER table. Column has foreign key index. This relation is provided by JPA OneToMany
relation in User.java entity.

Another relation is between RECIPE and INGREDIENTS tables. Ingredients list in Recipe entity is persisted with JPA
@ElementCollection annotation in Recipe.java entity.

Spring-security was chosen to secure API requests. Swagger v2 was chosen API documentation and tests. JUnit tests are
written for automation. ModelMapper was chosen to map DTO objects to DB entities and vice versa.

There is also a Dockerfile to run the application in a container in the project folder.

## JUnit Tests

There are two test classes: ValidationTests.java and HappyPathTests.java. GivenWhenThen Style Test scenarios are coded
in those classes. Please check GivenWhenThen Style Test section below. Scenarios 1-6 are in ValidationTests and 7-11 are
in HappyPathTests. You can directly run both java classes as JUnit test starter. Spring MockMvcs in those classes boots
the application and runs test scenarios.

## Swagger API Documentation

After spring boot application started, API documentation and example test values can be accessible from the link below.
You can run manual tests and checks if you want.

You should provide authentication info for userA or userB by clicking lock icon before sending requests. Otherwise, you
will get 401 Unauthorized response.

```
http://localhost:8080/swagger-ui/index.html#
```

## H2 Console

After spring boot application started, H2 console can be accessible and in-memory data can be observed from the link
below:

console user: sa

console user password: password

```
http://localhost:8080/h2-console/login.jsp
```

## GivenWhenThen Style Tests

Feature: Create/update recipe field validations

	Scenario 1: User tries to create recipe without instruction
		Given I have a recipe for 4 persons
			And it is not vegetarian
			And ingredients with chicken, salt, pepper
			And I do not know the instruction yet
		When I try to save this recipe before I get the instruction
		Then I should not be able to save this recipe
			And I should receive "instruction field cannot be empty." message

	Scenario 2: User tries to create recipe without ingredients
		Given I have a recipe for 4 persons
			And it is not vegetarian
			And instruction is test instruction
			And I do not know the ingredients yet
		When I try to save this recipe before I get the ingredients
		Then I should not be able to save this recipe
			And I should receive "ingredients list cannot be empty." message

	Scenario 3: User tries to create recipe without servings amount
		Given I have a recipe that it is not vegetarian
			And ingredients with chicken, salt, pepper
			And instruction is Fry chicken with salt and pepper
			And I do not know amount of people that the recipe is suitable for
		When I try to save this recipe without servings amount
		Then I should not be able to save this recipe
			And I should receive "servings field cannot be null." message

	Scenario 4: User tries to create recipe for 0 people (servings=0)
		Given I have a recipe that it is not vegetarian
			And ingredients with chicken, salt, pepper
			And instruction is Fry chicken with salt and pepper
			And I do not know amount of people so I set the servings amount as 0
		When I try to save this recipe without servings amount
		Then I should not be able to save this recipe
			And I should receive "servings field should be 1 at least" message

	Scenario 5: User tries to create recipe without vegetarian info
		Given I have a recipe for 4 persons
			And ingredients with chicken, salt, pepper
			And instruction is Fry chicken with salt and pepper
			And I do not know whether chicken is vegetarian or not
		When I try to save this recipe without vegetarian info
		Then I should not be able to save this recipe
			And I should receive "vegetarian field cannot be null." message

	Scenario 6: User gives wrong username or password
		Given I have a recipe
			And I do not know the password, so I leave it empty
		When I try to save this recipe
		Then I should not be able to save this recipe
			And I should receive 401 Unauthorized status response

	Scenario 7: User creates a recipe
		Given I have a recipe
		When I try to save this recipe
		Then I should be able to save this recipe
			And I should receive 200 OK response
			And a generated ID of this recipe

	Scenario 8: User creates and updates a recipe
		Given I have a recipe saved before
		When I change the instruction as Bake broccoli with salt and pepper
			And I change the ingredients as broccoli salt and pepper
			And I change the vegetarian info as true
			And I change the servings number as 5
		Then I should not be able to update this recipe
			And I should receive 200 OK response
			And I should see the changed fields

	Scenario 9: User creates and deletes a recipe
		Given I have a recipe saved before
		When I try to delete this recipe
		Then I should receive 200 OK response
			And I should not find that recipe with its Id again

	Scenario 10: User tries to update another user's recipe
		Given someone else has a recipe saved before
		When I try to update that recipe
		Then I should not be able to update this recipe
			And I should receive 403 Forbidden status

	Scenario 11: User lists his recipes
		Given I have two recipes before
			And I know IDs of them
			And there are other recipes from other users with different IDs
		When I list/get all of the recipes of me
		Then I should only get my recipes with correct IDs
