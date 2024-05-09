<!-- TOC -->
* [Application Setup and Execution Guide](#application-setup-and-execution-guide)
  * [Prerequisites](#prerequisites)
  * [Setup](#setup)
  * [Execution](#execution)
* [Setting configuration for CPF validation](#setting-configuration-for-cpf-validation)
* [RulingController API Documentation](#rulingcontroller-api-documentation)
  * [Base URL](#base-url)
  * [1. Create Ruling](#1-create-ruling)
  * [2. Create Vote](#2-create-vote)
  * [3. List Rulings](#3-list-rulings)
  * [4. Get Ruling Result](#4-get-ruling-result)
  * [5. Open Ruling](#5-open-ruling)
  * [6. Close Ruling](#6-close-ruling)
* [Design choices and Technologies Employed](#design-choices-and-technologies-employed)
  * [Design](#design)
  * [Domain-Driven Design (DDD)](#domain-driven-design-ddd)
  * [Hexagonal Architecture](#hexagonal-architecture)
  * [Technologies](#technologies)
  * [Application Services](#application-services)
  * [Records](#records)
* [Troubleshooting](#troubleshooting)
<!-- TOC -->

# Application Setup and Execution Guide
This guide provides instructions on how to set up and run the application.

## Prerequisites
Before you begin, ensure you have met the following requirements:
* You have installed Java 21.
* You have installed Docker 25.0.3.
* This application was developed using the IntelliJ IDEA 2024.1 IDE.

## Setup
Clone the application repository to your local machine using the following command in your terminal:

```bash
git clone <repository_url>
```
Replace `<repository_url>` with the URL of your Git repository.

Change your current directory to the project's root directory with:

```bash
cd <project_directory>
```
Replace `<project_directory>` with the name of the directory where you cloned the repository.

Build the project using Gradle with the following command:
```bash
./gradlew build
```
This command compiles the Java code and packages the application into a JAR file.

## Execution
You can build the Docker image for the application using the docker build command. Here's the command:

```bash
docker build -t dbserver-challenge-app:latest .
```

You can use Docker Compose to run the application. This requires a docker-compose.yml file in your project directory. Here's the command:
```bash
docker-compose up
```
This command starts the application along with any services defined in `docker-compose.yml` file.

# Setting configuration for CPF validation

The application uses an external service to validate CPFs. The URL of the service is defined in the environment variable. You can change the URL by setting the `-DCPF_VALIDATOR_URL` variable in the `compose.yaml` docker file. The file will be some like that:

```yaml
services:
    app:
        environment:
          - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/testdb
          - SPRING_DATASOURCE_USERNAME=testuser
          - SPRING_DATASOURCE_PASSWORD=testpassword
          - CPF_VALIDATOR_URL=http://{base_url}
```

With you prefer to run the application without Docker, you can set the environment variable in the command line. Here's the command:

```bash
./gradlew build -DCPF_VALIDATOR_URL=https://user-info.herokuapp.com/users/
```


>[!WARNING]
If you change the name of docker image in the Dockerfile, you need to update the image name in the docker-compose.yml file as well.

# RulingController API Documentation

This section provides a guide on how to interact with the Ruling API. The API is versioned and currently, version 1 is available. The version is specified in the header of the HTTP request with the key X-API-Version.

## Base URL

The base URL for the API is `{base_url}/api/ruling`. Please replace `{base_url}` with the actual base URL of the API. Probably it will be `http://localhost:8080`.

## 1. Create Ruling

- **Endpoint**: `/api/ruling`
- **HTTP Method**: `POST`
- **Headers**: Content-Type: application/json, X-API-Version: 1
- **Response**: UUID of the created ruling.
- **Request Body**:
    - `title` (required): The title of the ruling.
    - `description` (required): The description of the ruling.
    - `end_date` (required): The end date of the ruling in the format `yyyy-MM-dd`

```curl 
curl -X POST '{base_url}/api/ruling' \
-H 'Content-Type: application/json' \
-d '{
    "title": "Ruling Title",
    "description": "Ruling Description",
    "end_date": "2022-12-31"
}'
```

## 2. Create Vote

- **Endpoint**: `/api/ruling/vote`
- **HTTP Method**: `POST`
- **Headers**: Content-Type: application/json, X-API-Version: 1
- **Response**: UUID of the created vote.
- **Request Body**:
    - `ruling_id` (required): The UUID of the ruling to vote on.
    - `cpf` (required): The CPF of the voter.
    - `vote_in_favor` (required): The vote of the voter. `true` for in favor and `false` for against.

```curl
curl -X POST '{base_url}/api/ruling/vote' \
-H 'Content-Type: application/json' \
-d '{
    "ruling_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "cpf": "123.456.789-00",
    "vote_in_favor": true
}'
```

## 3. List Rulings

- **Endpoint**: `/api/ruling`
- **HTTP Method**: `GET`
- **Headers**: Content-Type: application/json, X-API-Version: 1
- **Query Parameters**: `uuid` (optional) - The UUID of the ruling to retrieve, `status` - The status of the rulings to retrieve.
- **Response**: List of rulings.

```curl
curl -X GET '{base_url}/api/ruling?status=OPEN'
```

## 4. Get Ruling Result

- **Endpoint**: `/api/ruling/{uuid}/result`
- **HTTP Method**: `GET`
- **Headers**: Content-Type: application/json, X-API-Version: 1
- **Path Variable**: `uuid` - The UUID of the ruling to retrieve the result for.
- **Response**: Result of the ruling.

```curl
curl -X GET '{base_url}/api/ruling/3fa85f64-5717-4562-b3fc-2c963f66afa6/result'
```

## 5. Open Ruling

- **Endpoint**: `/api/ruling/{uuid}/open`
- **HTTP Method**: `GET`
- **Headers**: Content-Type: application/json, X-API-Version: 1
- **Path Variable**: `uuid` - The UUID of the ruling to open.

```curl
curl -X GET '{base_url}/api/ruling/3fa85f64-5717-4562-b3fc-2c963f66afa6/open'
```

## 6. Close Ruling

- **Endpoint**: `/api/ruling/{uuid}/close`
- **HTTP Method**: `GET`
- **Headers**: Content-Type: application/json, X-API-Version: 1
- **Path Variable**: `uuid` - The UUID of the ruling to close.

```curl
curl -X GET '{base_url}/api/ruling/3fa85f64-5717-4562-b3fc-2c963f66afa6/close'
```

For more information on the API, please refer to the Swagger documentation `{base_url}/api/swagger-ui.html`.

# Design choices and Technologies Employed

## Design
The application is designed using Domain-Driven Design (DDD) and Hexagonal Architecture.

## Domain-Driven Design (DDD)
DDD is an approach to software development that centers the development on programming a domain model that has a rich understanding of the processes and rules of a domain. This approach is typically used for complex systems where the domain model and the business processes need to be in sync.  In this application, DDD is applied by having a clear separation of the domain layer (`br.challenge.domain.adapters`) from the infrastructure layer (`br.challenge.infrastracture`). The domain layer contains the business logic and the business rules, while the infrastructure layer contains the technical concerns (like database access).

## Hexagonal Architecture
Hexagonal Architecture is an architectural style that moves a developer's focus from conceptual layers to a distinction between the software's inside and outside parts. The main idea is to allow an application to equally be driven by users, programs, automated test or batch scripts, and to be developed and tested in isolation from its eventual run-time devices and databases.

In this application, Hexagonal Architecture is applied by having clear separations between the application, domain, and infrastructure layers. The application layer is where the use cases are implemented. The domain layer contains the business logic and the business rules. The infrastructure layer contains the technical concerns (like database access).

## Technologies
The application uses the following technologies:

* Java: The main programming language used in the application.
* Postgres: An open-source relational database management system emphasizing extensibility and SQL compliance.
* Spring Boot: An open-source Java-based framework used to create stand-alone, production-grade Spring-based Applications. It is used to simplify the bootstrapping and development of a new Spring application.
* Liquibase: An open-source database-independent library for tracking, managing, and applying database schema changes.
* Gradle: A build automation tool focused on flexibility and performance.

## Application Services
The application main service, `RulingService`, which are responsible for handling ruling and votes respectively. This service use repositories to interact with the database and perform operations like ruling and vote.  The `RulingService` has methods for creating a vote (`VoteOnRuling`) and for computing the ruling (`tallyVoteForRuling`).

## Records
The application uses records (`CreateRuling`, `ResultRuling`, `VoteOnRuling`) to represent a group of related data items. The state description, which is in the body of the class, is represented by the compact canonical constructor parameters.

# Troubleshooting

Firewall Settings: Ensure that your firewall is not blocking the connection to port 1521.

Check Firewall Settings: Check the firewall settings on both the Windows machine and the Linux server. You might need to configure the firewalls to allow ICMP packets.
