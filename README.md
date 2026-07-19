# Spring IObox Starter

## Описание на русском

*spring-iobox-starter* - стартер для Spring Boot, реализующий типовые задачи включения паттернов Inbox и Outbox в приложениях с микросервисной архитектурой.
Стартер позволяет при простом использовании аннотаций @OutboxEntity и @InboxEntity сократить код и скрыть логику.

Пример настройки outbox:
```yaml
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

Флаги:
- enabled: включает/выключает использование стартера
- max-retries: максимальное кол-во попыток при отправке outbox-сообщений. Если это число превышено, сообщение удаляется из базы данных
- retry-delay: время (в МС), спустя которое производится новая попытка отправить outbox-сообщение
- deduplication-enabled: включает/выключает механизм дедупликации в брокере
- retention-days: кол-во дней, как долго могут храниться сообщения с published=true (обработанные)
- scheduler.enabled: доступен ли шедулер из стартера
- scheduler.interval: время для шедулера
- scheduler.cleanup-cron: время, в которое производится чистка базы данных (в примере - 3 часа ночи по местному времени). Аналог cron job
- context-manager.enabled: доступен ли менеджер контекста Outbox (используется для работы с сохранением сообщений и дальнейшей их отправки в шедулер)
- context-manager.default-topic: название топика для отправки сообщений по умолчанию (Kafka).

### Как добавить стартер в проект

В pom.xml:
```xml
<dependency>
    <groupId>org.jedi_bachelor.ioboxstarter</groupId>
	<artifactId>spring-iobox-starter</artifactId>
	<version>1.0.1-alpha</version>
</dependency>
```

В build.gradle:
```kotlin
implementation 'org.jedi_bachelor:spring-iobox-starter:1.0.1-alpha'
```

### Первичная настройка и использование (Outbox)

После добавления стартера в проект, надо добавить аннотацию @EnableOutboxing над главным классом проекта:
```java
@SpringBootApplication
@EnableOutboxing
public class IoboxTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(IoboxTestApplication.class, args);
	}
}
```

Теперь надо добавить новый класс DTO с аннотацией @OutboxEntity:
```java
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;
import lombok.Data;

@Data
@OutboxEntity(topic = "user-created")
public class UserCreatedEvent {
    private Long userId;
    private String email;
    private String name;
}
```

В этом примере используется Apache Kafka. После сохранения объекта UserCreatedEvent в OutboxContentManager шедулер подхватит сообщение и отправит его в топик user-created.
```java
public UserCreatedEvent createUser() {
    UserCreatedEvent event = new UserCreatedEvent();
    event.setUserId(1L);
    event.setEmail("example@company.ru");
    event.setName("example");

    outboxContextManager.save(event);

    return event;
}
```

Как мы можем видеть, сообщение лежит в топике.
Объект OutboxContentManager создаётся с помощью OutboxAutoConfiguration, и потому вам не нужно это делать вручную.

## Description on English

*spring-iobox-starter* is a starter for Spring Boot that realize routine tasks of Outbox and Inbox patterns in microservice architecture' applications.
Starter allow decrease writing code and hide a logic with simple annotations @OutboxEntity and @InboxEntity.

Outbox settings' example:
```yaml
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
- enabled: on/off a starter in Outbox tasks
- max-retries: max amount retries to send outbox message. If current amount exceed this value, message deleted from DB
- retry-delay: the time (in MS) after which a new attempt is made to send an outbox message
- deduplication-enabled: enables/disables the deduplication mechanism in the broker
- retention-days: number of days, how long messages with published=true (processed) can be stored
- scheduler.enabled: is the shader available from the starter
- scheduler.interval: interval time for scheduler
- scheduler.cleanup-cron: the time at which the database is cleaned (in the example, 3 a.m. local time). The equivalent of a cron job
- context-manager.enabled: is the Outbox context manager available (used to work with saving messages and then sending them to the shader)
- context-manager.default-topic: the name of the topic for sending messages by default (Kafka).

### How add starter to project

If u use Maven, u need add this dependency to pom.xml:
```xml
<dependency>
    <groupId>org.jedi_bachelor.ioboxstarter</groupId>
	<artifactId>spring-iobox-starter</artifactId>
	<version>1.0.1-alpha</version>
</dependency>
```

If project build on Gradle, write this fragment to build.gradle:
```kotlin
implementation 'org.jedi_bachelor:spring-iobox-starter:1.0.1-alpha'
```

### Initial setup and using (Outbox)

After adding starter to project, need to add annotation @EnableOutboxing in project' main class:
```java
@SpringBootApplication
@EnableOutboxing
public class IoboxTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(IoboxTestApplication.class, args);
	}
}
```

Then u need add new DTO class with @OutboxEntity annotation:
```java
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;
import lombok.Data;

@Data
@OutboxEntity(topic = "user-created")
public class UserCreatedEvent {
    private Long userId;
    private String email;
    private String name;
}
```

In this case used Apache Kafka to example. After saving UserCreatedEvent object in OutboxContentManager scheduler will receive this message and send it to user-created topic.
```java
public UserCreatedEvent createUser() {
    UserCreatedEvent event = new UserCreatedEvent();
    event.setUserId(1L);
    event.setEmail("example@company.ru");
    event.setName("example");

    outboxContextManager.save(event);

    return event;
}
```

As we can see, after processing in topic stored a message.
Object of OutboxContentManager created via OutboxAutoConfiguration, and therefore why u don't have to doing it manually.