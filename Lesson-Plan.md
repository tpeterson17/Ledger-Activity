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

Before going right into the project, lead a discussion with the students regarding what they've seen so far with Spring Boot, REST, JPA, JDBC, and exception handling. Consider the following questions to get the discussion going:
1. What is Spring Data JPA?
2. What is Spring Data JDBC?
3. What are some differences and similarities between Spring Data JPA and JDBC?
4. 

### Purpose

The purpose of this activity is to tie REST and CRUD together by building out a RESTful CRUD API called Ledger API.

### Learning Objectives

1. Understand how CRUD correlates with POST, GET, PUT, and DELETE.
2. Be able to make calls to the DAOs via a REST controller.
3. Be able to write a Spring Boot project that utilizes REST, JPA, MySQL, and exception handling.

### Project Overview

* Spring Data JPA will be used with a MySQL database. The database schema will be created automatically by JPA if it does not exist, so there's no need to create it manually. The data dictionary for this database can be found in Step 2 below.
* The Java Model
* Data Access Layer
* REST Controller

### Assumptions



## Step 1: start.spring.io


## Step 2: Transaction Model


## Step 3: Transaction JPA Repository


## Step 4: Exception Handling


## Step 5: Transaction Controller


## Recap and Questions
