
![Event Storming](event-storming.png)

# Architecture Overview

## Purpose

This project is a study-oriented implementation of a communication case management system, built to demonstrate practical application of:

- Domain-Driven Design
- Clean Architecture
- Modular monolith structure
- Ports and adapters
- Use case oriented application design

The goal is not only to build a working system, but to show a clear architectural approach suitable for discussion during technical interviews.

---

## 1. Business problem

A small service center already has an external order system, but communication with customers is often handled informally by phone.

Typical problems:

- customer does not answer the phone
- repeated calls are made without shared visibility
- communication history is fragmented
- operators and managers cannot easily see what happened before
- follow-up communication is hard to track

The system solves this by introducing a dedicated communication workflow around a **communication case**.

---

## 2. Core domain concept

The central concept of the domain is `CommunicationCase`.

A communication case represents one communication process with one customer for one reason, usually related to one external order.

Examples:

- customer did not answer the call and needs a follow-up message
- repair is complete and customer must be notified
- additional information must be clarified
- a technician needs to confirm contact before arrival

This makes `CommunicationCase` the main business object and the central aggregate root.

---

## 3. Aggregate root

### `CommunicationCase`

`CommunicationCase` is the aggregate root of the communication-case module.

It owns and protects the consistency of:

- customer snapshot
- external order reference
- contact reason
- case status
- call attempts
- messages

### Why it is the aggregate root

The communication workflow is organized around the case:

- a case is opened
- call attempts are registered within the case
- outgoing messages are prepared and sent within the case
- incoming messages are received within the case
- the case status changes according to the communication process
- the case can be closed

This makes `CommunicationCase` the natural boundary for invariants and lifecycle management.

---

## 4. Main domain objects

### `CommunicationCase`
Central aggregate root representing a communication process.

### `CustomerSnapshot`
A lightweight customer snapshot used inside the case.
It is intentionally not a full CRM customer model.

Typical data:
- full name
- phone number

### `ExternalOrderReference`
A lightweight reference to an order stored in an external system.

Typical data:
- source system
- external order id
- order summary

### `CallAttempt`
A value-like entity inside the aggregate that stores:
- who attempted the call
- when
- result

### `Message`
Represents inbound or outbound communication.
Stores:
- direction
- channel
- text
- delivery status for outbound messages
- creation time

---

## 5. Status model

The communication case uses a small status model for MVP:

- `OPEN`
- `WAITING_FOR_CUSTOMER`
- `CUSTOMER_REPLIED`
- `FOLLOW_UP_REQUIRED`
- `CLOSED`

### Meaning

- `OPEN`  
  The case exists and is active, but no customer response is currently awaited.

- `WAITING_FOR_CUSTOMER`  
  An outgoing message was sent and the system is waiting for a reply.

- `CUSTOMER_REPLIED`  
  The customer has already replied.

- `FOLLOW_UP_REQUIRED`  
  Another action is needed: additional message, repeat call, clarification, etc.

- `CLOSED`  
  The communication flow is complete.

---

## 6. Architectural style

The system is implemented as a **modular monolith**.

### Why modular monolith

For this project, a modular monolith is a better choice than microservices because:

- the domain is still evolving
- the team size is small
- the MVP scope is limited
- deployment should remain simple
- architectural clarity is more important than distribution

### Design goals

- clear module boundaries
- explicit application use cases
- infrastructure hidden behind ports
- domain logic independent of persistence and external APIs

---

## 7. Layering

The communication-case module is divided into four conceptual layers.

### Domain

Contains core business model and rules.

Typical contents:
- aggregate root
- domain objects
- enums
- repository interface for aggregate persistence

The domain layer should not depend on:
- controllers
- SQL
- HTTP clients
- provider APIs

### Application

Contains use cases and application-facing contracts.

Typical contents:
- command use cases
- query use cases
- command/query DTOs
- ports for external systems

Application coordinates business scenarios but does not implement technical integration details.

### Infrastructure

Contains technical implementations.

Typical contents:
- JDBC repositories
- HTTP messaging adapter
- fake provider
- stub adapter
- configuration classes
- persistence mappings

Infrastructure depends on application and domain, not the other way around.

### Web

Contains REST controllers and web request/response models.

Typical contents:
- business REST API
- validation annotations
- OpenAPI annotations
- exception-to-HTTP mapping

---

## 8. Use case driven design

The application layer is organized around explicit use cases rather than generic services.

Examples:

- `OpenCommunicationCaseUseCase`
- `RegisterCallAttemptUseCase`
- `SendOutgoingMessageUseCase`
- `ReceiveIncomingMessageUseCase`
- `CloseCommunicationCaseUseCase`
- `SearchCommunicationCasesUseCase`
- `GetCommunicationCaseByIdUseCase`

### Why this matters

This makes business scenarios visible in code.
Instead of a large generic service class, the application layer exposes clear, intention-revealing operations.

---

## 9. Persistence design

The project supports two runtime persistence modes:

- `in-memory`
- `jdbc`

### In-memory profile

Used for quick local development and simple testing without a database.

### JDBC profile

Uses PostgreSQL and Liquibase.

The aggregate is persisted across:

- `communication_case`
- `call_attempt`
- `message`

### Rehydration

The domain model supports restoration from persistence, so aggregate instances can be reconstructed from database state instead of being treated as plain CRUD records.

This is important because the domain model is behavioral, not anemic.

---

## 10. Read side vs aggregate side

The system distinguishes between:

### Aggregate repository
Used for command-side work with the aggregate root.

Example:
- load `CommunicationCase`
- change state
- save aggregate

### Query repository
Used for read-side scenarios.

Example:
- search communication cases
- list communication cases

This avoids loading full aggregates just to render lightweight list screens or search results.

---

## 11. Messaging integration

The project uses a ports/adapters approach for outgoing messaging.

### Application port

`OutgoingMessageSender`

This is the contract required by the application layer.
The use case knows only that a message must be sent; it does not know how.

### Adapters

#### Stub adapter
A simple in-process adapter used for local runs and predictable tests.

#### HTTP adapter
A real adapter using `RestClient` to call an external messaging API.

#### Fake provider
A simulated external provider implemented inside the project for demonstration purposes.

This allows the repository to show a realistic integration path without depending on a real third-party provider.

---

## 12. Integration flow

### Outgoing flow

1. Controller triggers `SendOutgoingMessageUseCase`
2. Aggregate prepares outbound message
3. Message is marked as requested
4. Aggregate state is persisted
5. `OutgoingMessageSender` is called
6. Adapter sends the request
7. Result is mapped back to the domain flow
8. Message becomes `SENT` or `FAILED`
9. Aggregate is saved again

### Why two saves

The use case persists the intermediate `REQUESTED` state before making the external call.

This is not a full distributed consistency solution, but it is a reasonable MVP compromise and makes the state transition more explicit.

---

## 13. Fake provider and reply simulation

The fake provider simulates an external messaging system.

Implemented capabilities:

- accept outgoing message requests
- reject messages under a controlled condition
- simulate reply callback

This makes it possible to demonstrate:

- outbound HTTP integration
- provider response handling
- inbound communication flow
- status transition to `CUSTOMER_REPLIED`

without any real provider account.

---

## 14. Profiles

The system combines persistence and messaging profiles.

### Persistence
- `in-memory`
- `jdbc`

### Messaging
- `stub-messaging`
- `http-messaging`

### Fake provider
- `fake-provider`

### Example combinations

#### Local lightweight mode
`in-memory,stub-messaging`

#### Local database mode
`jdbc,stub-messaging`

#### Full demo mode
`jdbc,http-messaging,fake-provider`

This separation is intentional: persistence strategy and messaging strategy are independent concerns.

---

## 15. Why this project demonstrates DDD

This repository is useful for interview discussion because it shows:

- a meaningful business problem
- a clearly chosen aggregate root
- a domain model with behavior, not just data
- explicit use cases
- separation of command and query concerns
- technical infrastructure hidden behind ports
- integration adapters that do not leak into the domain

This is not just a CRUD application with controllers and services.
It is a project intentionally shaped around domain boundaries and architectural decisions.

---

## 16. Known simplifications

The current MVP intentionally simplifies some areas:

- no security yet
- no UI yet
- no outbox pattern
- no retries for provider failures
- no advanced webhook authentication
- no provider-neutral correlation strategy for inbound replies
- no full omnichannel support

These are consciously postponed in order to keep the project focused on core architecture and domain design.

---

## 17. Possible next steps

Potential future improvements:

- minimal browser UI
- security and roles
- stronger inbound message correlation
- retry strategy for failed provider calls
- outbox/event-based delivery
- richer query side
- automated tests for integration scenarios
- documentation of event storming and domain discovery

---

## 18. Suggested repository documentation structure

A practical documentation structure for this repository:

```text
README.md
docs/
  ARCHITECTURE.md
  event-storming.png