# Bridged

Bridged is a full-stack customer-support chat platform that routes customers
to suitable support agents based on language, skill, availability, capacity
and workload.

The project demonstrates backend development with Spring Boot, transactional
agent assignment, real-time WebSocket messaging, relational database design
and deployment on AWS.

## Current Status

Bridged has a working end-to-end MVP deployed on AWS.

The current application supports:

- A React interface for customers and support agents
- Temporary role-based demo sessions
- Customer chat creation
- Separate language and topic triage
- Priority-based assignment deadlines
- Automatic agent assignment
- Agent matching by skill, language, availability and capacity
- Workload and proficiency-based agent ranking
- Real-time customer-agent messaging using STOMP over WebSocket
- PostgreSQL-persisted message history
- Agent acceptance of assigned chats
- Chat closure and capacity release
- Flyway-managed database migrations and seed data
- Deployment using AWS EC2, PostgreSQL RDS, Nginx and systemd

## Chat Lifecycle

A conversation moves through the following states:

```text
TRIAGE → WAITING → ASSIGNED → ACTIVE → CLOSED
```

### 1. Triage

A customer starts a chat and selects:

- Their preferred language
- The topic they need help with

Language and topic are saved separately so that each selection is persisted
as soon as it is submitted.

The initial prompts, customer selections and bot responses are also stored as
individual chat messages.

### 2. Waiting

After both triage selections have been completed, the chat enters the
PostgreSQL-backed waiting pool.

The selected topic determines the chat priority and assignment deadline.

### 3. Assignment

A scheduled backend process checks the waiting pool every two seconds and
attempts to assign one chat.

Waiting chats are ordered by:

1. Earliest assignment deadline
2. Earliest triage completion time
3. Chat ID as a deterministic tie-breaker

An agent is eligible when the agent:

- Is available
- Has remaining chat capacity
- Supports the required language
- Has the required skill

Eligible agents are ranked by:

1. Lowest workload percentage
2. Highest skill proficiency
3. Highest language proficiency
4. Agent ID as a deterministic tie-breaker

PostgreSQL transactions, pessimistic locking and `SKIP LOCKED` protect the
assignment process from concurrent workers assigning the same chat or agent
capacity.

### 4. Active Chat

An assigned agent accepts the chat, moving it from `ASSIGNED` to `ACTIVE`.

The customer and agent exchange messages using STOMP over WebSocket. Messages
are persisted in PostgreSQL before being delivered to subscribers, keeping the
database as the source of truth for conversation history.

The REST API can retrieve recent messages when a client initially connects or
needs to recover conversation history.

### 5. Closed

The assigned agent can close an active chat.

Closing the chat:

- Changes its status to `CLOSED`
- Records the closure time
- Reduces the agent's open-chat count
- Releases capacity for another assignment

## Assignment Deadlines

Each priority has a target assignment time:

| Priority | Assignment target |
|---|---:|
| CRITICAL | 5 minutes |
| HIGH | 10 minutes |
| NORMAL | 20 minutes |
| LOW | 30 minutes |

The assignment deadline is calculated when triage is completed. This allows
the queue to prioritize chats using actual waiting deadlines rather than a
fixed priority cycle.

## Engineering Challenges and Decisions

### End-to-End Development

Bridged was developed from initial requirements and database modelling through
backend and frontend implementation to cloud deployment. Several technologies,
including Spring Boot, WebSockets and AWS deployment, were new to me.

A major learning experience was understanding how the REST API, real-time
messaging, PostgreSQL database, React state and cloud infrastructure interact
as one system.

### Workload-Aware Agent Assignment

The initial agent model did not track open-chat workload. During design
discussions, it became clear that ranking agents only by proficiency could
cause the most skilled agents to receive most of the work.

The agent model was updated with `open_chat_count` and `max_open_chats`.
Assignment now filters agents by language, skill, availability and capacity,
then ranks eligible agents by workload before proficiency. This provides
fairer workload distribution while still considering agent capability.

The queue design also evolved from a fixed weighted-priority cycle to
deadline-based routing. Each priority receives an assignment target, allowing
the system to process chats according to their actual assignment deadlines.

### Concurrency and Consistency

Concurrent scheduler executions could otherwise assign the same chat more
than once or exceed an agent's capacity. The assignment process therefore
uses database transactions, pessimistic row locking and PostgreSQL
`SKIP LOCKED`.

Chat assignment and agent workload updates occur within the same transaction.

### Local and Cloud Environment Differences

The local and deployed databases maintain independent data and identity
sequences, so user, agent and chat IDs are not guaranteed to match between
environments.

A timezone difference was also discovered between the local machine and EC2.
Because `LocalDateTime` does not contain an offset, newly deployed messages
could be sorted incorrectly. The MVP aligns the JVM timezone with Singapore,
while a future version should store timestamps using `Instant` or
`OffsetDateTime`.

## Architecture

Bridged is implemented as a modular monolith.

```text
Customer / Agent
        |
        v
React + Vite frontend
        |
        | REST and STOMP over WebSocket
        v
Spring Boot backend
        |
        +-- Chat and triage
        +-- Automatic assignment
        +-- Real-time messaging
        +-- Chat lifecycle management
        |
        v
PostgreSQL
```

The application remains a single backend deployment while keeping controllers,
services, repositories and domain entities separated by responsibility.

## Technology Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring WebSocket
- STOMP
- Maven
- Flyway

### Frontend

- React
- Vite
- JavaScript
- CSS
- STOMP.js

### Database

- PostgreSQL
- PostgreSQL row locking and `SKIP LOCKED`

### Deployment

- AWS EC2
- PostgreSQL on AWS RDS
- Nginx
- systemd

## Deployment Approach

The MVP is currently deployed manually on AWS:

- The React production build is served by Nginx on an EC2 instance.
- Nginx proxies REST requests under `/api` and WebSocket connections under
  `/ws` to Spring Boot.
- The Spring Boot JAR runs as a `systemd` service and restarts automatically.
- PostgreSQL runs on a private AWS RDS instance.
- Database credentials and runtime configuration are stored on EC2 and are
  not committed to the repository.
- Flyway applies database migrations when the backend starts.

The repository currently describes the deployed architecture but does not yet
include deployment scripts, Nginx and systemd templates, or
infrastructure-as-code definitions. Reproducing the environment therefore
requires manual configuration.

## Main API Capabilities

The REST API supports:

- Creating a customer chat
- Selecting a triage language
- Selecting a triage topic
- Listing waiting chats
- Retrieving chat details
- Retrieving an agent's open chats
- Accepting an assigned chat
- Closing an active chat
- Retrieving recent messages
- Retrieving language and skill reference data
- Creating temporary demo sessions

WebSocket messaging is used for real-time customer-agent communication.

## Database Design

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

Reference data is used for:

- User roles
- User statuses
- Agent availability
- Chat statuses
- Chat priorities
- Languages
- Skills
- Proficiency levels
- Message sender types

Database changes and seeded MVP data are managed through versioned Flyway
migrations.

## Project Structure

```text
bridged/
├── backend/
│   ├── src/main/java/com/anita/bridged/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── scheduler/
│   │   └── service/
│   └── src/main/resources/
│       ├── db/migration/
│       └── application.properties
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── services/
│   │   └── utils/
│   └── package.json
└── docs/
```

## Running Locally

### Requirements

- Java 21
- Maven, or the included Maven wrapper
- Node.js and npm
- PostgreSQL

### Backend

Configure the required PostgreSQL connection values for your environment.

Then run:

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

### Frontend

In a separate terminal, run:

```bash
cd frontend
npm install
npm run dev
```

The frontend normally runs on:

```text
http://localhost:5173
```

During local development, Vite proxies `/api` and `/ws` requests to the
Spring Boot backend on port `8080`.

## Building and Testing

Run the backend tests and build:

```bash
cd backend
./mvnw clean package
```

Run frontend linting and create a production build:

```bash
cd frontend
npm run lint
npm run build
```

The frontend production files are written to:

```text
frontend/dist/
```

## Current MVP Limitations

- Demo sessions are not secure authentication.
- Users can currently provide a seeded user ID to enter the demo.
- Active customer chats are not restored after a browser refresh.
- The customer interface does not yet prevent multiple simultaneous chats.
- Assignment updates still rely partly on lifecycle polling.
- The current time model uses `LocalDateTime` and depends on consistent server
  timezone configuration.
- Automated test coverage is currently limited.
- Scheduling is enabled during Spring context tests.
- The AWS environment and application deployment are currently configured
  manually rather than reproduced from version-controlled infrastructure code.
- The deployment currently uses HTTP rather than HTTPS.
- The application is a portfolio MVP and is not production-safe for real
  banking or customer data.

## Post-MVP Roadmap

- Add authentication and role-based authorization
- Restore active chats after browser refresh
- Prevent duplicate active customer chats
- Add assignment, capacity, lifecycle and concurrency tests
- Disable the assignment scheduler under the test profile
- Add an index for `assignment_due_at`
- Verify database query plans and assignment performance
- Replace `LocalDateTime` with `Instant` or `OffsetDateTime`
- Deliver assignment updates through WebSocket
- Add an operational queue and agent-workload dashboard
- Add HTTPS and a stable domain
- Add redacted Nginx and systemd configuration templates
- Add repeatable frontend and backend deployment scripts
- Add an environment-variable template containing placeholders
- Define AWS infrastructure using Terraform, CloudFormation or AWS CDK
- Document backup, rollback and disaster-recovery procedures
- Add monitoring, metrics and production security controls
- Add SLA aging and escalation rules
- Evaluate an external queue such as AWS SQS if future scale requires it
- Update the assignment design documentation to match deadline-based routing

## Project Purpose

Bridged is a personal portfolio project built to develop practical experience
with:

- Transactional backend design
- Concurrent resource assignment
- Real-time browser communication
- PostgreSQL data modelling
- Full-stack application development
- AWS deployment
- Incremental system design and technical trade-offs
