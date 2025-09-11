Let's Play API
==============

1\. Project Overview
--------------------

This project is a complete RESTful CRUD (Create, Read, Update, Delete) API built with **Spring Boot** and **MongoDB**. It provides functionalities for managing users and products, secured with a robust, token-based authentication and authorization system using JWTs stored in HttpOnly cookies.

The API adheres to modern best practices, including role-based access control (User vs. Admin), global exception handling, input validation, and secure password management. It also includes bonus features like rate limiting to protect against abuse.

### Key Features

*   **User Management:** Full CRUD operations for users.
    
*   **Product Management:** Full CRUD operations for products, with ownership checks.
    
*   **Authentication:** JWT-based login/logout system using secure, HttpOnly cookies.
    
*   **Authorization:** Role-based access control (USER, ADMIN) and ownership-based permissions.
    
*   **Security:** Password hashing (BCrypt), input validation, DTOs to prevent data leakage, and rate limiting.
    
*   **Error Handling:** A global exception handler that returns appropriate HTTP status codes and prevents 5XX errors.
    

2\. Project Structure
---------------------

The project follows a standard layered architecture to ensure a clean separation of concerns.

```
src
└── main
    └── java
        └── com
            └── gritlab
                └── lets_play
                    ├── LetsPlayApplication.java    // Main application entry point
                    ├── config/                     // Security, beans, and other configurations
                    ├── controller/                 // API endpoints (REST controllers)
                    ├── dto/                        // Data Transfer Objects for API requests/responses
                    ├── exception/                  // Custom exception classes
                    ├── model/                      // MongoDB document models and enums
                    ├── repository/                 // Spring Data MongoDB repositories
                    └── service/                    // Business logic and service layer
```

3\. Prerequisites
-----------------

Before you begin, ensure you have the following software installed on your system:

*   **Java (JDK):** Version 21 or higher.
    
*   **Apache Maven:** Version 3.8.x or higher.
    
*   **MongoDB:** MongoDB Community Edition, running on the default port (27017).
    
*   **An API Client:** [Postman](https://www.postman.com/) or a similar tool for testing the API endpoints.
    

4\. How to Run the Project
--------------------------

Follow these steps to get the application up and running.

### Step 1: Clone the Repository

Clone this project to your local machine using your preferred method.

### Step 2: Configure the Application

Navigate to src/main/resources/ and open the application.properties file. Ensure the following properties are set correctly for your environment:

```bash
# MongoDB Connection
# (Update this if your database has a different name or requires credentials)
spring.data.mongodb.uri=mongodb://localhost:27017/lets-play-db

# JWT Secret Key
# IMPORTANT: Replace this with your own strong, randomly generated Base64 key.
application.security.jwt.secret-key=bXlWZXJ5U2VjdXJlS2V5Rm9ySldUU2lnbmluZzEyMzQ1Njc4OThhYmNkZWZnaGlqa2xtbm9wcXJz
```

### Step 3: Build the Project with Maven

Open a terminal or command prompt in the root directory of the project and run the following Maven command. This will compile the code, run all tests, and package the application into a .jar file.

```bash
mvn clean install   
```

### Step 4: Run the Application

Once the build is successful, you can run the application in one of two ways:

**A) From the Command Line:**Navigate to the target directory and run the generated .jar file.

```bash  
java -jar target/lets-play-0.0.1-SNAPSHOT.jar
```

**B) From Your IDE (e.g., IntelliJ IDEA):**Simply find the LetsPlayApplication.java file and run its main() method.

### Step 5: Confirmation

The application is running successfully when you see the following lines in your console:

```bash   
...  Tomcat started on port(s): 8080 (http) with context path ''  ...
Started LetsPlayApplication in X.XXX seconds  ...
✅ Dummy data inserted!   
```

The API is now running and ready to accept requests at http://localhost:8080.

You can now use Postman or another API client to interact with the endpoints.
