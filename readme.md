# Laughing Enigma

> This project is made to demonstrate a working distributed application with microservice architecture

## Introduction
This is an Event Booking application with distributed environment. 
A logged in user can register and pay to book a seat for any event. In case payment is interrupted user can complete the payment from home page. On either success or failure of booking the event, user will receive an email with event details. 

## Tech Stack
| No | Layer | Technology | Provider |
| -------- | -------- | -------- | -------- |
| 1  | Frontend  | Angular  | -  |
| 2  | API Gateway  | Spring Cloud API Gateway  | -  |
| 3  | Saga Command & Response flow  | RabbitMQ  | [Cloud AMQP](https://www.cloudamqp.com/)  |
| 4  | Event management  | Kafka  | [Aiven](https://aiven.io/)  |
| 5  | RDBMS Database  | PostgreSQL  | [Aiven](https://aiven.io/)  |
| 6  | DB Migration  | Flyway  | -  |
| 7  | Distributed Ratelimiting  | Valkey  | [Aiven](https://aiven.io/)  |
| 8  | API Testing  | Postman  | -  |
| 9  | Local Deployment  | Docker Compose  | -  |
| 10  | Payment Gateway  | Razorpay  | [Razorpay](https://razorpay.com/)  |
| 11 | Local Webhook Testing  | Zrok2  | [Zrok](https://zrok.io/)  |
| 12 | Local Mail Inbox  | Mailpit  | [Mailpit](https://mailpit.axllent.org/)  |

## Application Components
| Service/App | Business Capability | Technologies | Location |
|-------------|---------------------|------------|----------|
| visual-enigma | Frontend that connects to API Gateway | Angular | ./frontend/visual-enigma |
| api-gateway | API Gateway that forwards all authenticated requests to corresponding services | Spring Boot + Redis | ./gateway/api-gateway |
| security-service | Take care of Login, Register users | Spring Boot + PostgreSQL | ./services/security-service |
| saga-orchestrator | Orchestrates Saga flows and provides Resuming the Saga which is Stuck | Spring Boot + PostgreSQL + RabbitMQ + Kafka | ./services/saga-orchestrator |
| customer-service | Customer Profile details, Customer Validation & Customer Bookings - all based on username | Spring Boot + PostgreSQL + RabbitMQ | ./services/customer-service |
| event-service | Seat Reservation, Seat UnReserve, Event Details | Spring Boot + PostgreSQL + RabbitMQ + MongoDB | ./services/event-service |
|  payment-service | Payment Order creation and Payment Verification | Spring Boot + PostgreSQL + RabbitMQ + Razorpay | ./services/payment-service |
| notification-service | Sends email on payment success or failure to the customer's email id | Spring Boot + Kafka | ./services/notification-service |
| aggregator-service | Gathers all bookings of logged in customer and their payment details and presents to UI | Spring Boot | ./services/aggregator-service |
| incentive-service | [INCOMPLETE] Provides Coupons, Referrals and other Incentives while booking events | NodeJs + ExpressJs | ./services/incentive-service |

## To Run Locally :-
----------------------
1. Make sure to add .env files in all places where ever we have TEMPLATE.env and matching its contents.
    - These .env files will have credentials for PostgreSQL, RabbitMQ, Kafka, MongoDB which are needed to make these services to run.
    - We are using Flyway whenever we used PosgreSQL so it should take care of setting up DB Schema when connected using the scripts but you still need to fill in some data to make it work.
        - user record in security-service for login.
        - customer record matching above user for customer-service to validate.
        - event records for event-service to serve UI after login.
        - event-details in mongodb for event-service (this is not covered by flyway for now so need some dummy data to work).
2. You also need some proxy tunneling like Zrok tool to receive webhook calls from razorpay. This is not done with docker so we need to install in the system we are trying to run this application stack.
3. Now, make sure Docker Desktop is running in the system. Then, go inside ./infrastructure folder and run command:
```docker compose up --build -d```
. Further all commands are present in ./docs folder in Docker-Compose-notes.txt file.
4. Once, all containers are up, you can visit http://localhost:4200/login which will open Angular App. This angular app has hot reloading kept intact even though it is deployed here via container so you can update code and see changes reflected on browser.
5. To Complete payment you need to setup webhook configuration on Razorpay account with exposed endpoint. 
    - Zrok endpoint need to be mapped to this http://localhost:8080/api/v1/webhooks/razorpay and that Zrok's endpoint need to be configured inside Razorpay webhook.
    - In the Razorpay we need to configure webhook for 3 events:
        1. payment.failed
        2. payment.captured
        3. order.paid 

## Sagas / Workflow
### Registration Flow

Logged in user or customer can explore events and click on interested event's Register button. This will trigger Registration Flow and prompts user to complete payment to complete registration. Incase this payment is interrupted, user can complete payment from the Home page under payment pending tab.