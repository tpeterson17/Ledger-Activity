# Ledger Activity

## Instructor Notes
The point of this lesson is to expose students to a RESTful CRUD API in Spring Boot for the first time. So far, they have built APIs without a data access layer. They have also built projects with Spring Data JPA and Spring Data JDBC. However, this is where we bring these concepts together by building out a RESTful CRUD API called Ledger API.

This activity is intended to be primarily an instructor-do. However, delegating certain aspects of the activity to the class as well as asking questions while you're coding are encouraged. This is because the students are familiar with everything we're doing in this project aside from combining REST and CRUD.

Spring Data JPA is used for this activity. Once the lesson is over, feel free to encourage students to code out this activity on their own using Spring Data JDBC instead so that they can gain exposure in both.

Since we have not covered TDD, service layers, and validation yet in the course, some students who are familiar with these topics may wonder why we aren't incorporating them. For the sake of time, consider testing, service layers, and validation as out of scope. These topics may occur later in the course. Either way, feel free to send out the following links if the students are curious. They can look into these on their own time.
* [JSR303 Validation Guide](https://www.javadevjournal.com/spring-mvc/spring-bean-validation/)
* [Why Service Layers?](https://blog1.westagilelabs.com/why-to-use-service-layer-in-spring-mvc-5f4fc52643c0)
* [MockMVC Guide](https://spring.io/guides/gs/testing-web/)
* [Mockito Guide](https://www.baeldung.com/mockito-annotations)

The solution for this activity can be found here: [Ledger Activity Solution](./Solution/Ledger-API)


## Introduction and Level Set

Before going right into the project, lead a discussion with the students regarding what they've seen so far with Spring Boot, REST, JPA, JDBC, and exception handling. Let's refresh their memory of these topics along with getting them curious about what's ahead. Consider the following questions as jumping off points to get the discussion going:
1. What does CRUD mean?
2. What is Spring Data JPA?
3. What is Spring Data JDBC?
4. What are some differences and similarities between Spring Data JPA and JDBC?
5. What is exception handling?
6. What exceptions have we seen so far? Which of those have we handled?
7. What is REST?
8. What is a REST controller?
9. What is RESTControllerAdvice?
10. Which HTTP verbs have we used? What about HTTP status codes?

### Purpose

The purpose of this activity is to tie REST and CRUD together by building out a RESTful CRUD API called Ledger API.

### Learning Objectives

1. Understand how CRUD correlates with POST, GET, PUT, and DELETE.
2. Be able to make calls to the DAOs via a REST controller.
3. Be able to write a Spring Boot project that utilizes REST, JPA, MySQL, and exception handling.

### Project Overview

* Spring Data JPA will be used with a MySQL database. The database schema will be created automatically by JPA if it does not exist, so there's no need to create it manually. The data model for this database can be found in Step 2 below. Lastly, there will be a single JPA interface serving as the DAO for the Transaction table. The interface contains just one custom method which can be seen in Step 3 below.
* The Java model is comprised of just one class called Transaction. This model includes JPA annotations for object relational mapping.
* The REST Controller will have basic CRUD endpoints along with a getSumOfAllTransactions() endpoint as specified in Step 4. RestControllerAdvice will be used as well for exception handling as the students have seen in the past.

### Assumptions

The students have knowledge and ability in the following prerequisites for this activity:
1. Programming basics and Java fundamentals
2. OOP basics
3. Data structures
4. Collections
5. Exception handling
6. Spring Boot RESTful APIs (which includes Spring MVC)
7. MySQL
8. Spring Data JPA


## Step 1: start.spring.io

Direct students to [spring initializr](https://start.spring.io) and send out the following outline for how the form should be filled out:

1. Project: Maven Project
2. Language: Java
3. Spring Boot: Choose the pre-selected version.
4. Group: com.twou (or another variation of com.company of your choosing)
5. Artifact: Ledger-API
6. Name: Ledger-API
7. Description: This is a RESTful Spring Boot API responsible for performing CRUD operations on a ledger database.
8. Package name: com.twou.Ledger-API (or another variation of com.company.Ledger-API of your choosing)
9. Packaging: Jar
10. Java: 8
11. Dependencies: Take Spring Web, MySQL Driver, and Spring Data JPA.

Make sure everyone is taking the correct dependencies before proceeding.

Have the class generate the project and open it in their IDEs.

Before we go any further, we cannot run the project until the application.properties is set. The class has used all of the properties in the project before, so there's no need to spend a lot of time here unless there are questions or if there are students who cannot run their applications.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ledger?useSSL=false&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=rootroot
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

```

Verify that everyone can run their application and that JPA is able to create a blank Ledger schema in each student's MySQL instance before moving onto Step 2.

## Step 2: Transaction Model

Since we'll be writing the Java model based off of the data model for the database, send the following data model out to the students. This will provide them context while we're coding out this class. Remind them that JPA will handle the conversion between camel case and snake case. Lastly, point out that we're only using one entity (table) which is named Transaction.

| Column           | Type       | Nullable | Extra          |
| ---------------- | ---------- | -------- | -------------- |
| id               | Long       | no       | auto_increment |
| recipient        | String     | no       |                |
| sender           | String     | no       |                |
| softDelete       | Boolean    | no       |                |
| transactionValue | BigDecimal | no       |                |

Point out the usages of Long and BigDecimal. The students have seen these data types before, but it would be good to reiterate why we use them: 

* In the case of Long, we are using this as an alternative to Integer so that it's less likely for us to run out of primary keys. It would be highly unlikely for this to occur in the classroom, but it's an important possibility to keep in mind in the wild. Some databases have to store incredible amounts of records. In which case, we need to consider future proofing our data models if this is a possibility.
* In the case of BigDecimal, we are using this because it's the mostly commonly used Java data type for financials. It is very precise (unlike double and float). It also packs features for rounding, truncation, arithmetic, and more. 

Start by generating the class com.twou.LedgerAPI.model.Transaction.

Once the class is generated, take the students' input on how they would like to develop the class based off of the data model. They should all be comfortable with declaring instance variables, using basic JPA annotations, and generating typical POJO methods such as equals, hashcode, and toString by now. Guide them towards the following intermediate solution for this model. (This is the final solution aside from the soft delete which we will get to in a moment.)

Not all setters and getters are generated for this class. Students may point this out. Only the necessary setters and getters are included for this specific activity. They can always be added later when they're needed. Some developers will add these methods before they're needed and some won't. Point out that it is a matter of preference and that there are pros and cons of both approaches.


```java
package com.twou.LedgerAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction implements Serializable {

    @Column(nullable = false, updatable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private Boolean softDelete = Boolean.FALSE;

    @Column(nullable = false)
    private BigDecimal transactionValue;

    public Long getId() {
        return id;
    }

    public BigDecimal getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(BigDecimal transactionValue) {
        this.transactionValue = transactionValue;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return getId().equals(transaction.getId()) && getSender().equals(transaction.getSender())
                && getRecipient().equals(transaction.getRecipient())
                && getTransactionValue().equals(transaction.getTransactionValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSender(), getRecipient(), getTransactionValue());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", transactionValue=" + transactionValue +
                '}';
    }
}

```

Now that the Transaction class fulfills the data model, we'll have to address the implications of the softDelete field. For this activity, we need to make sure that the Ledger API does not delete any records. Instead, it should be setting the soft_delete column to true when an API call is made to delete a record. Describe this requirement to the students before moving forward with coding so that they have context. 

This is another great opportunity for student input and discussion. Consider asking the following questions to aid in the discussion:

1. What some reasons why we would incorporate soft deletion in an application?
2. What are some pros and cons of incorporating soft deletion?
3. How can we bypass JPA's default deletion behavior?

Be sure to carefully address any questions and concerns since this is one of the more difficult aspects of the Transaction class for beginners. It would also be a good idea to pulse check here to make sure everyone is comfortable enough with these concepts.

Guide students towards the following solution which is a class level annotation on the Transaction class:

```java
@SQLDelete(sql = "UPDATE transaction SET soft_delete = true WHERE id = ?")

```

Your IDE will likely prompt for an import. Be sure the following import is the one that you're taking:

```java
import org.hibernate.annotations.SQLDelete;

```

The final requirement for the Transaction class is for us to make sure only the records that have soft_delete set to false are visible to the caller. Explain this requirement to the class as well. This one is a little more straightforward conceptually and is also easier for beginners to implement. Guide the students towards the following solution which is another class level annotation on the Transaction class:

```java
@Where(clause = "soft_delete = false")

```

Your IDE will likely prompt for an import. Be sure the following import is the one that you're taking:

```java
import org.hibernate.annotations.Where;

```

Now that this model is finished, the following is the final version which is identical to the provided solution:

```java
package com.twou.LedgerAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLDelete(sql = "UPDATE transaction SET soft_delete = true WHERE id = ?")
@Where(clause = "soft_delete = false")
public class Transaction implements Serializable {

    @Column(nullable = false, updatable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private Boolean softDelete = Boolean.FALSE;

    @Column(nullable = false)
    private BigDecimal transactionValue;

    public Long getId() {
        return id;
    }

    public BigDecimal getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(BigDecimal transactionValue) {
        this.transactionValue = transactionValue;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return getId().equals(transaction.getId()) && getSender().equals(transaction.getSender())
                && getRecipient().equals(transaction.getRecipient())
                && getTransactionValue().equals(transaction.getTransactionValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSender(), getRecipient(), getTransactionValue());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", transactionValue=" + transactionValue +
                '}';
    }
}

```

## Step 3: Transaction JPA Repository

Let's focus on the boilerplate aspects of the TransactionRepository first. Have the class generate the interface com.twou.LedgerAPI.repository.TransactionRepository. Next, ask the students what else we need to do to the interface in order for it to serve as the DAO interface for the Transaction model. (They have seen this boilerplate a lot, so they should be quick to the answer. If not, take a moment to review the purpose of @Repository and JpaRepository.)

Guide the class to the following intermediate solution for this interface. (This is the final solution minus the one custom method for this project.):

```java
package com.twou.LedgerAPI.repository;

import com.twou.LedgerAPI.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}

```

Verify that the class has the above boilerplate for their interface and address any questions or concerns before moving onto the custom method.

The final portion to code in this interface is to create a custom method that returns the sum of all transactions. Inform the class of this requirement. Since this custom method specifically is going to require a native SQL query, ask a student to write this query in SQL. In doing so, guide the class to the following solution to this requirement:

```java
    @Query(value = "SELECT SUM(transaction_value) FROM transaction WHERE soft_delete = false", nativeQuery = true)
    public BigDecimal getSumOfAllTransactions();

```

Your IDE will likely prompt for an import. Be sure the following import is the one that you're taking:

```java
import org.springframework.data.jpa.repository.Query;

```

Now that this DAO interface is finished, the following is the final version which is identical to the provided solution:

```java
package com.twou.LedgerAPI.repository;

import com.twou.LedgerAPI.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT SUM(transaction_value) FROM transaction WHERE soft_delete = false", nativeQuery = true)
    public BigDecimal getSumOfAllTransactions();
}

```

Now would be an excellent time to do a major pulse check. Make sure everyone's application can run. Also, we'll need to make sure that each student's MySQL instance contains the Ledger schema with the Transaction table now that everyone should have run at least once.

Side note: If there are any students who are have a discrepancy between their MySQL data model and their Java data model. First, make sure that their Java is congruent with the solution code. Then, have them drop the Ledger schema and re-run the Spring Boot application.

If everyone is on the same page and there are no lingering questions or concerns, this would also be a great time for a major break. We just did a whole lot of JPA!

## Step 4: Transaction Controller

This step (as well as Step 5) is going to be where we connect the dots between REST and CRUD for the students. Be sure to take your time when coding out these controller methods. Also, keep in mind that we'll be saving all of the exception handling for Step 5, so the solution for this step overall is an intermediate solution.

Before we get started with coding out this controller, send out the following specification for the API:

| Method Name                | Verb   | URI               | Request Body | Response Body       | Response Status |
| -------------------------- | ------ | ----------------- | ------------ | ------------------- | --------------- |
| getTransactionById         | GET    | /transaction/{id} | none         | Transaction         | 200 OK          |
| addTransaction             | POST   | /transaction      | Transaction  | Transaction         | 201 CREATED     |
| updateTransactionValueById | PUT    | /transaction/{id} | Transaction  | none                | 204 NO CONTENT  |
| deleteTransactionById      | DELETE | /transaction/{id} | none         | none                | 204 NO CONTENT  |
| getAllTransactions         | GET    | /transaction      | none         | List\<Transaction\> | 200 OK          |
| getSumOfAllTransactions    | GET    | /transaction/sum  | none         | BigDecimal          | 200 OK          |

Go over this API spec with the class and carefully field any questions about it before proceeding. It's important that everyone understands what the API is supposed to do before we start coding. It would also be a good idea to mention that we will handle exception handling later on in the lesson. For now, we will be focusing on the "happy path."

Next, have the students generate the class com.twou.LedgerAPI.controller.TransactionController. Be sure to add the @RestController annotation before we get started with any of the endpoints. At this point, the class should look like the following:

```java
package com.twou.LedgerAPI.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

}

```

### POST Transaction

It's worth mentioning that since the Hibernate sequence is at play, we should not be inserting any records manually into the database. Therefore, it's best that we get started with the POST endpoint first. This will allow the students to get some data into the Transaction table right away.

Code this endpoint out in front of the students and have them follow along. There may be questions about dependency injection and the save JPA method. Spend extra time to make sure the class is comfortable with our usage of the transactionRepository so far in the controller. They have already seen dependency injection and the save method before in other contexts, but they're common sticking points for beginners.

The controller should now look like the following:

```java
package com.twou.LedgerAPI.controller;

import com.twou.LedgerAPI.model.Transaction;
import com.twou.LedgerAPI.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {
    
    @Autowired
    TransactionRepository transactionRepository;
    
    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}

```

Now we're at an exciting point in the lesson where we get to test the full system out for the first time! 
1. Have the class run the application and then open up Insomnia. 
2. Create a new folder named Ledger.
3. In the Ledger folder, create a new request for the POST endpoint we just created.
4. When building the request body, be sure to select JSON and include sender, recipient, and transactionValue.

Make sure that everyone is able to not only hit their APIs, but are also able to persist their data as well. Address any questions or issues before moving onto the next endpoint.

### Students Attempt Remaining Endpoints
The boilerplate for the controller is in place and the students have now seen an example of one endpoint utilizing a DAO. This is a great opportunity to let them loose for a little while. Let's see how far they can get coding out the happy paths for the rest of the endpoints. Be extra sensitive to struggling students and reassure them that we will code through the remaining endpoints once the class has had a chance to attempt them.

### Live Code Remaining Endpoints (Happy Path Only)
Keep the following points in mind as you code the rest of the controller:

1. Optionals are a common sticking point for beginners. Remind the students that JPA's findById method returns an Optional which is why we have to handle them. Optional in Java is a data structure that encapsulates another type that may or may not be null. JPA uses Optional because it has no way of knowing whether or not it will get back a null Transaction record from the database.
2. Since we'll be coding our exception handling later, the logic for each endpoint is happy path only. For now, any erroneous flow will result in a 500 which is totally fine for now.
3. The method chaining and argument passing in updateTransactionValueById in the intermediate controller solution might be confusing at first to students. Especially since they may have not used Optional a whole lot yet. Consider the following alternative logic to make the code more beginner readable:
```java
    @PutMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateTransactionValueById(@PathVariable Long id, @RequestBody Transaction transaction) {
        
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        
        if (optionalTransaction.isPresent()) {
            Transaction foundTransaction = optionalTransaction.get();
            BigDecimal newTransactionValue = transaction.getTransactionValue();
            foundTransaction.setTransactionValue(newTransactionValue);
            transactionRepository.save(foundTransaction);
        }
    }

```

The following is the intermediate (happy path) solution for the whole controller:

```java
package com.twou.LedgerAPI.controller;

import com.twou.LedgerAPI.model.Transaction;
import com.twou.LedgerAPI.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Transaction getTransactionById(@PathVariable Long id) {
        return transactionRepository.findById(id).get();
    }

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @PutMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateTransactionValueById(@PathVariable Long id, @RequestBody Transaction transaction) {
        Optional<Transaction> foundTransaction = transactionRepository.findById(id);

        if (foundTransaction.isPresent()) {
            foundTransaction.get().setTransactionValue(transaction.getTransactionValue());
            transactionRepository.save(foundTransaction.get());
        }
    }

    @DeleteMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTransactionById(@PathVariable Long id) {
        transactionRepository.deleteById(id);
    }

    @GetMapping("/transaction")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/transaction/sum")
    @ResponseStatus(value = HttpStatus.OK)
    public BigDecimal getSumOfAllTransactions() {
        return transactionRepository.getSumOfAllTransactions();
    }
}

```

The last thing to do before moving onto step 5 is to make sure that everyone is able to hit each endpoint via Insomnia. The students at this point have access to the spec, the Controller solution, and bring with them the familiarity of creating requests in Insomnia. Address any questions, concerns, or issues that anyone may have.

Now is a great time for another major break! We just connected the dots between our RESTController and our DAO for the first time, so it's important to step away.

## Step 5: Exception Handling

Before we get started with the exception handling, ask the class how we went about this with our controllers in the past. Guide a brief review discussion about the role of the ControllerExceptionHandler. It's an example of AOP (Aspect Oriented Programming) which means that we're writing one class (in this case) to handle an entire "aspect" of the application. Also, point out that we practiced creating custom exceptions as well as a CustomErrorResponse leveraged by the ControllerExceptionHandler.

Once the class' memory of controller exception handling is refreshed, have them copy over the necessary boilerplate. We'll be incorporating the following that we've already written in previous sessions. Feel free to send these out in case some students do not have copies readily accessible:

### NotFoundException

This is to be placed in com.twou.LedgerAPI.exceptions:

```java
package com.twou.LedgerAPI.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }
}

```

### CustomErrorResponse

This is to be placed in com.twou.LedgerAPI.model:

```java
package com.twou.LedgerAPI.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CustomErrorResponse {

    String errorMsg;
    int status;
    String errorCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime timestamp;

    public CustomErrorResponse() {
    }

    public CustomErrorResponse(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

```

### ControllerExceptionHandler

This is to be placed in com.twou.LedgerAPI.controller

``` java
package com.twou.LedgerAPI.controller;

import com.twou.LedgerAPI.exceptions.NotFoundException;
import com.twou.LedgerAPI.model.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CustomErrorResponse> notFoundException(NotFoundException e) {
        CustomErrorResponse error = new CustomErrorResponse(HttpStatus.NOT_FOUND.toString(), e.getMessage());
        error.setStatus((HttpStatus.NOT_FOUND.value()));
        error.setTimestamp(LocalDateTime.now());
        ResponseEntity<CustomErrorResponse> responseEntity = new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        return responseEntity;
    }
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<CustomErrorResponse> outOfRangeException(IllegalArgumentException e) {
        CustomErrorResponse error = new CustomErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString(), e.getMessage());
        error.setStatus((HttpStatus.UNPROCESSABLE_ENTITY.value()));
        error.setTimestamp(LocalDateTime.now());
        ResponseEntity<CustomErrorResponse> responseEntity = new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
        return responseEntity;
    }

}

```

Before we get into writing the "sad path" logic, start a discussion about controller exception handling. Consider the following questions as a jumping off point:

1. Which endpoints might (need to) throw exceptions and why? 
2. When would we want to return a status of 422? 
3. When would we want to return a status of 404? 
4. What information should we be returning to the caller? Is there any information that we would not want to send?
5. Why is it important that we return clear and concise error messages?

Considering that there's not a whole lot in common logically between the endpoints when it comes to exception handling, it would be best to code out the rest of the logic in front of the class-- explaining the logic as you go. They will have chances in the future to write this kind of logic themselves, so this will serve as an initial exposure combining all these different concepts together. Be sensitive to any questions that may arise as you're going through this. The following is the final solution for the TransactionController. This is congruent with the provided solution project:

```java
package com.twou.LedgerAPI.controller;

import com.twou.LedgerAPI.exceptions.NotFoundException;
import com.twou.LedgerAPI.model.Transaction;
import com.twou.LedgerAPI.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Transaction getTransactionById(@PathVariable Long id) {
        Optional<Transaction> foundTransaction = transactionRepository.findById(id);

        if (foundTransaction.isPresent()) {
            return foundTransaction.get();
        }
        else {
            throw new NotFoundException("Transaction not found");
        }
    }

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @PutMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateTransactionValueById(@PathVariable Long id, @RequestBody Transaction transaction) {
        if (transaction.getId() != id) {
            throw new IllegalArgumentException("The ID in the URL must match the ID in the request body.");
        }

        Optional<Transaction> foundTransaction = transactionRepository.findById(id);

        if (foundTransaction.isPresent()) {
            foundTransaction.get().setTransactionValue(transaction.getTransactionValue());
            transactionRepository.save(foundTransaction.get());
        }
        else {
            throw new NotFoundException("Transaction not found");
        }
    }

    @DeleteMapping("/transaction/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTransactionById(@PathVariable Long id) {
        try {
            transactionRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Transaction not found");
        }
    }

    @GetMapping("/transaction")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Transaction> getAllTransactions() {
        List<Transaction> foundTransactions = transactionRepository.findAll();

        if (foundTransactions.isEmpty()) {
            throw new NotFoundException("No transactions found");
        }
        else {
            return transactionRepository.findAll();
        }
    }

    @GetMapping("/transaction/sum")
    @ResponseStatus(value = HttpStatus.OK)
    public BigDecimal getSumOfAllTransactions() {
        BigDecimal sum = transactionRepository.getSumOfAllTransactions();

        if (sum == null) {
            return BigDecimal.ZERO;
        }
        else {
            return sum;
        }
    }
}

```

## Recap and Questions
