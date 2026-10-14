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
| 6  | Distributed Ratelimiting  | Valkey  | [Aiven](https://aiven.io/)  |
| 7  | API Testing  | Postman  | [Postman](https://learning.postman.com/docs/use/native-git/overview)  |
| 8  | Local Deployment  | Docker Compose  | -  |
| 9  | Payment Gateway  | Razorpay  | [Razorpay](https://razorpay.com/)  |
| 10 | Local Webhook Testing  | Zrok2  | [Zrok](https://zrok.io/)  |
| 11 | Local Mail Inbox  | Mailpit  | [Mailpit](https://mailpit.axllent.org/)  |


    
