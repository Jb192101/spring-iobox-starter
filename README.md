# Spring IObox Starter

Current version: 1.1.1. Kotlin update

Next version: 1.1.2. Metrics

## Contents of instruction

1. Short description / Annotations
2. Common properties for Outbox/Inbox
3. DLQ (Dead Letters Queue)
4. Maintaned message queues
5. Maintaned DBs
6. Endpoints
7. Current version limitations

## 1. Short description / Annotations

*spring-iobox-starter* it's special starter with objective reach maximal automatization of Outbox/Inbox patterns in Java projects.

The Transactional Outbox and Inbox patterns are architectural solutions used in microservices to guarantee reliable message delivery and exactly-once processing without relying on distributed transactions (2PC).

### Transactional Outbox

It solves the problem of atomically updating a database and publishing an event to a message broker. Instead of sending the message directly to Kafka/RabbitMQ, the service saves the event into an "Outbox" table within the same local database transaction as the business entity change. A separate background process (relay/scheduler) then reads the Outbox and publishes the messages to the broker.

Atomicity: Because the database write and the Outbox record are committed in a single atomic transaction, either both succeed or both fail. This prevents the "dual-write" problem, where a database commit succeeds but a broker publish fails (or vice versa), leading to data inconsistency or lost events.

### Inbox
It solves the problem of idempotent message consumption (exactly-once processing). When a consumer receives a message from the broker, it doesn't process it immediately. Instead, it saves the message ID into an "Inbox" table within the same atomic database transaction used to execute the business logic.

Atomicity: If the same message is delivered again (e.g., due to network retries or broker at-least-once delivery semantics), the consumer will try to insert the duplicate message ID. The database's unique constraint will fail the transaction atomically. This prevents the business logic from being executed twice (deduplication) and ensures that the consumption and processing are atomic.

The starter allows you to shorten the code and hide the logic with simple use of the @OutboxEntity and @InboxEntity annotations.

Annotations:
* @EnableOutboxing - mark that all configurations in application.yaml to Outbox must be applied. it is attached to the main class of the project with annotation @SpringBootApplication or configuration-class.
* @EnableInboxing - mark that all configurations in application.yaml to Inbox must be applied. it is attached to the main class of the project with annotation @SpringBootApplication or configuration-class.
* @OutboxEntity(queueName = "queue-name") - annotation that mark class of event, that will be sended to queue with "queue-name".
* @InboxListener(queueName = "queue-name", groupId = "group-id") - 

All features of *spring-iobox-starter* has been checked on distributed environment from three pods of test project with this starter in dependencies. To see changes with CI see 7. Current version limitations.

Maven:
```xml
<dependency>
    <groupId>org.jedi_bachelor.ioboxstarter</groupId>
	<artifactId>spring-iobox-starter</artifactId>
	<version>1.1.1</version>
</dependency>
```

Gradle:
```kotlin
implementation 'org.jedi_bachelor:spring-iobox-starter:1.1.1'
```

## 2. Common properties for Outbox/Inbox

Example of Outbox configuration in application.yaml:
```yaml
iobox:
    outbox:
        enabled: true
        max-retries: 5
        deduplication-enabled: true
        retention-days: 7
        scheduler:
            enabled: true
            interval: 5000
            cleanup-cron: "0 0 3 * * *"
        context-manager:
            enabled: true
            default-topic: "default"
```
Flags:
* enabled - enables/disables the use of the Outbox
* max-retries - the maximum number of attempts when sending outbox messages. If this number is exceeded, the message is deleted from the database
* retry-delay - time (in MS) after which a new attempt is made to send an outbox message
* deduplication-enabled - enables/disables the deduplication mechanism in the broker
* retention-days - number of days, how long messages with published=true (processed)
* scheduler.enabled - whether the shader is available from
the starter
* scheduler.interval: time for the shader
* scheduler.cleanup-cron - time at which the database is cleaned (in the example, 3 a.m. local time). An analog of cron job
* context-manager.enabled - enable the Outbox context manager or not (used for saving messages and then sending them to the shader)
* context-manager.default-topic - name of the topic for sending messages by default (Kafka)

Principle of working:

1. The developer calls the save() method of the OutboxContextManager (ideally inside an active transaction). The event is serialized and saved to the Outbox table.
2. If scheduler.enabled is true, the OutboxScheduler periodically queries the database for unpublished messages.
3. The scheduler passes each message to the OutboxService, which publishes it to the message broker (Kafka, RabbitMQ, etc.) using the BrokerContext.
4. If the publication is successful, the message is marked as published=true in the database. If it fails, the retryCount is incremented.
5. If retryCount reaches max-retries, the message is moved to the DLQ (if enabled) and deleted from the Outbox table.

Example of Inbox configuration in application.yaml:
```yaml
iobox:
  inbox:
    enabled: true
    max-retries: 5
    deduplication-enabled: true
    retention-days: 7
    scheduler:
      enabled: true
      interval: 5000
      cleanup-cron: "0 0 3 * * *"
```

Flags:
* enabled - enables/disables the use of the Inbox
* max-retries - the maximum number of attempts when processing incoming messages. If this number is exceeded, the message is deleted from the database (or sent to DLQ)
* deduplication-enabled - enables/disables the check for duplicate messages by messageId before saving to the Inbox table
* retention-days - number of days to keep successfully processed messages (published=true) before cleanup
* scheduler.enabled - whether the Inbox processing scheduler is available from the starter
* scheduler.interval - time (in MS) for the scheduler to query the database for unprocessed messages
* scheduler.cleanup-cron - cron expression for cleaning up old processed messages from the database

Principle of working:
1. When a message arrives from the broker, the InboxProcessor intercepts it (using the listener registered via the broker strategy).
2. If deduplication-enabled is true, it checks if a message with this messageId already exists in the Inbox table. If yes, the message is skipped.
3. The raw message (with metadata like topic, messageId, and payload) is saved to the Inbox table with processed=false.
4. If scheduler.enabled is true, the InboxScheduler periodically fetches unprocessed messages from the database.
5. For each message, the scheduler finds the appropriate handler method (marked with @InboxListener) via the InboxListenerRegistry and invokes it.
6. If the handler executes successfully, the message is marked as processed=true. If an exception is thrown, the retryCount is incremented.
7. If retryCount reaches max-retries, the message is sent to the DLQ (if enabled) and deleted from the Inbox table.

## 3. DLQ (Dead Letters Queue)

If in your project invalid messages could't be deleted, but saved to special table, *spring-iobox-starter* provide configuring of Dead Letters Queue. For this, you must add in application.yaml section iobox.dlq. Below you can see example of DLQ settings.

```yaml
iobox:
  dlq:
    enabled: true
    dlqName: "dead-letters"
    scheduler:
      enabled: true
      interval: 5000
```

Flags:
- enabled - enable DLQ in project or not (default - false)
- dlqName - name of DLQ (default - "dead-letters")
- scheduler.enabled - enable DLQ scheduler or not (default - false); if iobox.dlq.enabled set to true, it's don't mean iobox.dlq.scheduler.enabled setted to true, it's different settings.
- scheduler.interval - interval for scheduler to save DL from message queue to table

Principle of working:
1. If InboxScheduler mark message as died, this send to DLQ;
2. If DlqScheduler is enabled, it take all DL from queue and save them to table dlq_messages (name of table can't be changed).
3. In each moment of time you can see records in dlq_messages if send request to special endpoint (see 6. Endpoints).

## 4. Maintaned message queues
//TODO

## 5. Maintaned DBs
//TODO

## 6. Endpoints

In project defined three special endpoints to watch all current records in Inbox, Outbox and DL tables. Below presented this endpoints.

1. GET /inbox
2. GET /outbox
3. GET /dead-letters

Important: this endpoints by default don't secured and available to everybody who know your API. If you don't want the endpoints to "stick out", protect them with Spring Security / Spring Cloud Gateway.

## 7. Current version limitations

In version 1.1.1. there are same problems, that can influence in designing of Java/Kotlin project.

* In current version *spring-iobox-starter* blocks all others DB autoconfigurations, if they don't represented in iobox.storage. As in iobox.storage can be defined only one DB type, it can be problem for all other autoconfigurations. For example, if you use for main storage PostgreSQL, but in iobox.storage writed `mongo`, JPA autoconfiguration will be blocked. In future versions it will be fixed, but in current realisation it not.
* Working with CassandraDB in the current version is taking longer than it could have been. The estimated time difference with a simpler implementation is small, but requests may take longer.
* Now in *spring-iobox-starter* don't added Spring Boot Actuator.
* All tests on distributed system from 3 pods in Kubernetes have been done in local. In future in CI will added TestContainers to visibility of starter working.
