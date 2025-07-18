# Wallet Service

## Overview
The Wallet Service is a Spring Boot 3 microservice that manages user wallet balances and initiates transactions by sending messages to an Apache Kafka topic (`transaction-topic`). It uses MySQL as the database for persistent storage and is optimized for high throughput, making it suitable for scalable, distributed systems. This project is designed to demonstrate proficiency in microservices architecture, Kafka integration, and Spring Boot with JPA, commonly evaluated in technical interviews.

## Features
- **REST API**: Exposes a POST endpoint (`/wallet/transaction`) to initiate transactions.
- **Kafka Producer**: Publishes transaction requests to the `transaction-topic` for asynchronous processing.
- **MySQL Integration**: Stores and updates wallet balances using Spring Data JPA.
- **High Throughput**: Configured with Kafka producer settings (`linger.ms`, `batch.size`, `compression.type`) for performance optimization.
- **Error Handling**: Basic validation for transaction initiation (e.g., sufficient balance can be added).

## Technologies Used
- **Java**: 17
- **Spring Boot**: 3.2.5
- **Apache Kafka**: Producer for asynchronous messaging
- **MySQL**: Database for wallet data
- **Spring Data JPA**: For database operations
- **Lombok**: To reduce boilerplate code
- **Maven**: Dependency management and build tool

## Prerequisites
- **JDK 17**: Ensure Java 17 is installed.
- **Maven**: For building and running the project.
- **MySQL**: Running on `localhost:3306` (or configure as needed).
- **Apache Kafka**: Running on `localhost:9092` with Zookeeper.
- **IDE**: IntelliJ IDEA, Eclipse, or similar (optional for development).

## Setup Instructions

### 1. Set Up MySQL
1. Install and start MySQL server.
2. Create a database named `wallet_db`:
   ```sql
   CREATE DATABASE wallet_db;
   ```
3. Ensure MySQL credentials in `application.yml` match your setup (default: `root`/`root`).

### 2. Set Up Apache Kafka
1. Download Apache Kafka from [Apache Kafka Downloads](https://kafka.apache.org/downloads).
2. Start Zookeeper:
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```
3. Start Kafka server:
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```
4. Create the `transaction-topic`:
   ```bash
   bin/kafka-topics.sh --create --topic transaction-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
   ```

### 3. Clone and Build the Project
1. Clone the repository (or use the project directory):
   ```bash
   git clone <repository-url>
   cd wallet-service
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

### 4. Configure the Application
- The `application.yml` file is pre-configured for MySQL (`localhost:3306/wallet_db`) and Kafka (`localhost:9092`).
- Update credentials or connection details if necessary:
  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/wallet_db?createDatabaseIfNotExist=true
      username: root
      password: root
    kafka:
      bootstrap-servers: localhost:9092
  ```

### 5. Run the Application
- Start the Spring Boot application:
  ```bash
  mvn spring-boot:run
  ```
- The service will run on `http://localhost:8081`.

## Usage
### Initiate a Transaction
- Use the following cURL command to initiate a transaction:
  ```bash
  curl -X POST http://localhost:8081/wallet/transaction \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123","amount":100.0}'
  ```
- **Response**: `"Transaction initiated for user: user123"`
- **Behavior**:
  - Deducts the specified `amount` from the wallet balance in `wallet_db`.
  - Sends a `TransactionRequest` message to the `transaction-topic` in Kafka.

### Verify Database
- Check the wallet balance in MySQL:
  ```sql
  SELECT * FROM wallet WHERE userId = 'user123';
  ```

### Verify Kafka Messages
- Use a Kafka console consumer to view messages:
  ```bash
  bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic transaction-topic --from-beginning
  ```
- Expected output: `{"userId":"user123","amount":100.0}`

## Project Structure
```
wallet-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/wallet/
│   │   │   ├── config/KafkaProducerConfig.java   # Kafka producer configuration
│   │   │   ├── controller/WalletController.java  # REST API endpoint
│   │   │   ├── dto/TransactionRequest.java       # DTO for Kafka messages
│   │   │   ├── model/Wallet.java                 # JPA entity for wallet
│   │   │   ├── repository/WalletRepository.java  # JPA repository
│   │   │   ├── service/WalletService.java        # Business logic
│   │   ├── resources/
│   │       ├── application.yml                   # Configuration file
├── pom.xml                                       # Maven dependencies
└── README.md                                     # Project documentation
```

## Performance Optimizations
- **Kafka**:
  - `linger.ms=10`: Batches messages for reduced network overhead.
  - `batch.size=16384`: Larger batch sizes for higher throughput.
  - `compression.type=gzip`: Compresses messages to optimize network usage.
  - Topic configured with 3 partitions for parallel processing.
- **Database**:
  - HikariCP for efficient connection pooling.
  - Add an index on `userId` for faster queries:
    ```sql
    CREATE INDEX idx_user_id ON wallet (userId);
    ```

## Extending the Project
To enhance the Wallet Service for interview scenarios:
- **Add Validation**: Check for sufficient balance in `WalletService`:
  ```java
  if (wallet.getBalance() < request.getAmount()) {
      throw new RuntimeException("Insufficient balance");
  }
  ```
- **Transactional Safety**: Use `@Transactional` in `WalletService` for database consistency:
  ```java
  import org.springframework.transaction.annotation.Transactional;
  @Transactional
  public String initiateTransaction(TransactionRequest request) { ... }
  ```
- **Error Handling**: Add global exception handling with `@ControllerAdvice`.
- **Monitoring**: Integrate Actuator for health checks (`/actuator/health`).

## Troubleshooting
- **Kafka Producer Error**: Ensure `value-serializer` is `org.springframework.kafka.support.serializer.JsonSerializer` in `application.yml` and `KafkaProducerConfig.java`.
- **Kafka Connection Issues**: Verify Kafka server is running and `bootstrap-servers` is correct.
- **MySQL Connection Issues**: Confirm database credentials and that `wallet_db` exists.
- **Enable Debug Logging**:
  ```yaml
  logging:
    level:
      org.apache.kafka: DEBUG
      org.springframework.kafka: DEBUG
  ```

## License
This project is for educational and demonstration purposes, licensed under the MIT License.
