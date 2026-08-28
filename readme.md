# Bridged

Bridged is a personal software engineering project for a real-time customer support chat platform that connects customers with suitable support agents based on factors such as language, skill, availability, and workload.

I am building the project to deepen my experience with modern Java backend development, real-time communication, asynchronous processing, relational database design, and system design.

The project is being developed incrementally, starting with the core chat domain and REST APIs before introducing real-time messaging, agent assignment, and deployment infrastructure.

## Current Status

Bridged is currently under active development.

The backend currently includes:

- Spring Boot application with PostgreSQL integration
- Database schema managed using Flyway migrations
- JPA entities and repositories for the core chat domain
- Seeded MVP users, customers, agents, skills, and reference data
- Chat session creation
- Chat triage flow
- REST endpoints for the initial chat lifecycle
- Validation and exception handling

Authentication, real-time messaging, agent assignment, frontend integration, and cloud deployment are planned for later stages.

## Intended Chat Flow

A customer conversation will move through the following lifecycle:

TRIAGE → WAITING → ASSIGNED → ACTIVE → CLOSED

### 1. Triage

A customer starts a chat and provides information such as the issue they need help with and their preferred language.

### 2. Waiting

Once triage is complete, the conversation enters the waiting pool until an appropriate support agent can be selected.

### 3. Assignment

The assignment system will evaluate eligible agents using factors including:

- Availability
- Required skills
- Language proficiency
- Current workload
- Agent capacity
- Fairness across eligible agents
- Chat priority

The goal is to avoid simply routing every conversation to the most highly skilled agent while still ensuring customers are matched appropriately.

### 4. Active Chat

Once assigned, the customer and agent will communicate in real time using WebSocket connections.

Chat messages will be persisted in PostgreSQL so that the database remains the source of truth for conversation history.

### 5. Closed

The chat session is closed once the customer interaction has been completed.

## Architecture

Bridged is intentionally being developed as a modular monolithic application rather than as a collection of microservices.

The planned high-level architecture is:

Customer / Agent
|
v
React Frontend
|
| REST + WebSocket
v
Spring Boot Backend
|
+---- Chat & Triage
|
+---- Assignment
|
+---- Real-time Messaging
|
+---- Queue Processing
|
v
PostgreSQL

The application begins as a single Spring Boot deployment while keeping responsibilities separated within the codebase. This keeps the project manageable while still allowing individual components to evolve later.

## Technology Stack

### Backend

- Java 21
- Spring Boot
- Spring Web / REST
- Spring Data JPA
- Hibernate
- Maven
- Flyway

### Database

- PostgreSQL

### Planned

- Spring WebSocket
- React
- In-memory queue for MVP assignment processing
- AWS SQS as a later asynchronous queue implementation
- AWS EC2 deployment

## Database Design

The database models customers, agents, their capabilities, and customer support conversations.

Core entities include:

- User
- Customer
- Agent
- Skill
- Language
- Proficiency
- AgentSkill
- AgentLanguage
- ChatSession
- ChatMessage

Reference data is used for concepts such as:

- User roles
- User status
- Agent availability
- Chat status
- Chat priority
- Languages
- Skills
- Proficiency levels

Database schema changes are managed through versioned Flyway migrations, while JPA/Hibernate is used to map the relational model to Java entities.

## Agent Assignment

Agent routing is one of the main system-design components of Bridged.

The assignment mechanism is intended to consider both suitability and fairness rather than selecting an agent based only on skill proficiency.

For example, an agent may be eligible based on language and skill requirements, but the final selection should also consider their existing workload and capacity.

The detailed assignment algorithm will be implemented in a later development phase.

## Queue Design

For the MVP, pending chats will initially be handled without introducing external queue infrastructure.

A later version is planned to use AWS SQS to manage pending conversation work items.

The queue will contain conversations waiting for assignment rather than individual chat messages. PostgreSQL will remain the source of truth for chat sessions and message history.

## Real-Time Messaging

WebSocket support will be introduced once the basic chat lifecycle and assignment flow are established.

REST APIs will handle operations such as creating and retrieving chat sessions, while WebSocket connections will handle the real-time exchange of messages between customers and agents.

Messages received through WebSocket will still be persisted to PostgreSQL.

## Project Structure

```text
bridged/
├── backend/
│   ├── src/main/java/com/anita/bridged/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   └── service/
│   │
│   └── src/main/resources/
│       ├── db/migration/
│       └── application.properties
│
└── frontend/                # Planned