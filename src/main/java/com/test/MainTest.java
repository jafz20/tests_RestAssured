package com.test;


import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Test
public class MainTest {

    RequestSpecification requestSpecification;
    Response response;
    ValidatableResponse validatableResponse;
    // Part 1
    @Test
    public void testGetBooks() {
        Response response = given()
                .auth().basic("user", "password")
                .contentType("application/json")
                .when()
                .get("http://localhost:8082/books")
                .then()
                .statusCode(200) // Validate that the status code is 200
                .extract().response();

        // Print the response body for debugging
        System.out.println("Response Body: " + response.prettyPrint());

        // Validate the number of books returned
        response.then().body("$", hasSize(greaterThanOrEqualTo(1))); // At least one book

        // Validate the IDs of the books returned
        response.then().body("id", containsInAnyOrder(1, 2, 3)); // Adjusted to match actual IDs

        // Validate the names of the books
        response.then().body("name", hasItems(
                "A Guide to the Bodhisattva Way of Life",
                "The Life-Changing Magic of Tidying Up",
                "Refactoring: Improving the Design of Existing Code"
        ));

        // Validate the authors of the books
        response.then().body("author", containsInAnyOrder(
                "Santideva",
                "Marie Kondo",
                "Martin Fowler"

        ));

        // Validate the prices of the books
        response.then().body("price", containsInAnyOrder(15.41F, 9.69F, 47.99F)); // Adjusted for additional entries

        // Optionally, validate the first book's details
        response.then().body("[0].name", equalTo("A Guide to the Bodhisattva Way of Life"))
                .body("[0].author", equalTo("Santideva"))
                .body("[0].price", equalTo(15.41F)); // Use 15.41F for Float
    }

    // Part 2
  /* @Test
    public void testCreateBook() {
        String requestBody = "{\n" +
                "    \"name\": \"A to the Bodhisattva Way of Life\",\n" +
                "    \"author\": \"Santideva\",\n" +
                "    \"price\": 15.41\n" +
                "}";

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:8082/books")
                .then()
                .statusCode(201)
                .extract().response();

        // Validate the response body
        response.then().body("name", equalTo("A to the Bodhisattva Way of Life"))
                .body("author", equalTo("Santideva"))
                .body("price", equalTo(15.41f));
    }*/
    
    // Part 3
    @Test
    public void testGetBookById() {
        int bookId = 3;

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .when()
                .get("http://localhost:8082/books/" + bookId)
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Response Body: " + response.prettyPrint());

        // Validate the book details
        response.then().body("id", equalTo(bookId))
                .body("name", equalTo("Refactoring: Improving the Design of Existing Code"))
                .body("author", equalTo("Martin Fowler"))
                .body("price", equalTo(20.00f));
    }

    // Part 4
/*@Test
public void testUpdateBook() {
    int bookId = 4; // ID of the book to update

    // Updated request body
    String updatedRequestBody = "{\n" +
            "    \"id\": 4,\n" +  // Ensure this ID matches the book being updated
            "    \"name\": \"Java How To Program\",\n" +
            "    \"author\": \"Santideva\",\n" +
            "    \"price\": 20.00\n" +  // New price for the book
            "}";

    // Send PUT request to update the book
    Response response = given()
            .auth().basic("admin", "password") // Use appropriate authentication
            .contentType("application/json")
            .body(updatedRequestBody)
            .when()
            .put("http://localhost:8082/books/" + bookId) // Update endpoint
            .then()
            .statusCode(200) // Expecting 200 OK
            .extract().response();

    // Validate the updated book details
    response.then().body("price", equalTo(20.00f)); // Validate the updated price
    response.then().body("name", equalTo("Java How To Program")); // Validate the updated name
    response.then().body("author", equalTo("Santideva")); // Validate the updated author
}*/
    // Part 5
   /*@Test
    public void testDeleteBook() {
        int bookId = 21;

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .when()
                .delete("http://localhost:8082/books/" + bookId)
                .then()
                .statusCode(200)
                .extract().response();

        // Optionally, check if the book is deleted by trying to get the same book
        given().auth().basic("admin", "password")
                .when().get("http://localhost:8082/books/" + bookId)
                .then().statusCode(404); // Expecting 404 Not Found
    }*/
    @Test
    public void testUpdateBookWithInvalidId() {
        int invalidBookId = 999; // Assuming this ID does not exist

        String updatedRequestBody = "{\n" +
                "    \"id\": 999,\n" +  // Invalid ID
                "    \"name\": \"Non-Existent Book\",\n" +
                "    \"author\": \"Unknown\",\n" +
                "    \"price\": 10.00\n" +
                "}";

        given().auth().basic("admin", "password")
                .contentType("application/json")
                .body(updatedRequestBody)
                .when()
                .put("http://localhost:8082/books/" + invalidBookId)
                .then()
                .statusCode(500);
    }
    @Test
    public void testUpdateBookWithMissingFields() {
        int bookId = 3; // Valid ID

        String updatedRequestBody = "{\n" +
                "    \"id\": 3,\n" +  // Valid ID
                "    \"author\": \"Martin Fowler\",\n" +  // Missing 'name' and 'price'
                "}";

        given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .body(updatedRequestBody)
                .when()
                .put("http://localhost:8082/books/" + bookId)
                .then()
                .statusCode(400); // Expecting 400 Bad Request
    }
    @Test
    public void testUpdateBookWithInvalidDataTypes() {
        int bookId = 2; // Valid ID

        String updatedRequestBody = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"The Life-Changing Magic of Tidying Up\",\n" +
                "    \"author\": \"Marie Kondo\",\n" +
                "    \"price\": \"twenty\"\n" +  // Invalid data type for price
                "}";

        given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .body(updatedRequestBody)
                .when()
                .put("http://localhost:8082/books/" + bookId)
                .then()
                .statusCode(400); // Expecting 400 Bad Request
    }
    @Test
    public void testUpdateBookUnauthorized() {
        int bookId = 2; // Valid ID

        String updatedRequestBody = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"The Life-Changing Magic of Tidying Up\",\n" +
                "    \"author\": \"Marie Kondo\",\n" +
                "    \"price\": 9.69\n" +
                "}";

        given()
                .contentType("application/json")
                .body(updatedRequestBody)
                .when()
                .put("http://localhost:8082/books/" + bookId)
                .then()
                .statusCode(401); // Expecting 401 Unauthorized
    }
    @Test
    public void testUpdateBookWithEmptyBody() {
        int bookId = 1; // Valid ID

          given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .body("") // Empty body
                .when()
                .put("http://localhost:8082/books/" + bookId)
                .then()
                .statusCode(400); // Expecting 400 Bad Request

    }
    @Test
    public void testGetBookBywrongId() {
        int bookId = 999;

        Response response = given()
                .auth().basic("admin", "password")
                .contentType("application/json")
                .when()
                .get("http://localhost:8082/books/" + bookId)
                .then()
                .statusCode(404)
                .extract().response();

        System.out.println("Response Body: " + response.prettyPrint());


    }
    @Test
    public void testUpdateBookWithValidId() {
        int validBookId = 3; // Assuming this ID exists

        String updatedRequestBody = "{\n" +
                "    \"id\": 3,\n" +  // Valid ID
                "    \"name\": \"Refactoring: Improving the Design of Existing Code\",\n" +
                "    \"author\": \"Martin Fowler\",\n" +
                "    \"price\": 20.00,\n" +  // New price for the book
                "    \"genre\": \"IT\",\n" + // New field for genre
                "    \"updatedDate\": \"2025-03-12T\" " + // Assuming the API returns an updated date
                "}";

        // Send PUT request to update the book
        Response response = given()
                .auth().basic("admin", "password") // Use appropriate authentication
                .contentType("application/json")
                .body(updatedRequestBody)
                .when()
                .put("http://localhost:8082/books/" + validBookId)
                .then()
                .statusCode(200) // Expecting 200 OK
                .extract().response();
        System.out.println("Response Body: " + response.prettyPrint());
        // Validate the updated book details
        response.then().body("id", equalTo(validBookId)); // Validate the updated ID
        response.then().body("name", equalTo("Refactoring: Improving the Design of Existing Code")); // Validate the updated name
        response.then().body("author", equalTo("Martin Fowler")); // Validate the updated author
        response.then().body("price", equalTo(20.00f)); // Validate the updated price
        response.then().body("genre", equalTo(null)); // Validate the updated genre
        response.then().body("updatedDate", equalTo(null)); // Validate the updated date
    }
}
