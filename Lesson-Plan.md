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
* The REST Controller will have basic CRUD endpoints along with a getSumOfAllTransactions() endpoint as specified in Step 5. RestControllerAdvice will be used as well for exception handling as the students have seen in the past.

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
| softDelete       | Boolean    | no      |                |
| transactionValue | BigDecimal | no       |                |

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

## Step 3: Transaction JPA Repository


## Step 4: Exception Handling


## Step 5: Transaction Controller


## Recap and Questions
