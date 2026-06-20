# Spring IObox Starter

*spring-iobox-starter* - стартер для Spring Boot, реализующий типовые задачи включения паттернов Inbox и Outbox в приложениях с микросервисной архитектурой.
Стартер позволяет при простом использовании аннотаций @Outbox и @Inbox сократить код и скрыть логику.

Пример настройки outbox:
```yaml
outbox:
  enabled: true
  max-retries: 5
  retry-delay: 5000
  deduplication-enabled: true
  retention-days: 7
  scheduler:
    interval: 5000ms
    cleanup-cron: "0 0 3 * * *"
```

Флаги:
- enabled: включает/выключает использование библиотеки
- max-retries: максимальное кол-во попыток при отправке outbox-сообщений. Если это число превышено, сообщение удаляется из базы данных
- retry-delay: время (в МС), спустя которое производится новая попытка отправить outbox-сообщение
- deduplication-enabled: включает/выключает механизм дедупликации в брокере
- retention-days: кол-во дней, как долго могут храниться сообщения с published=true (обработанные)
- scheduler.interval: время для шедулера
- scheduler.cleanup-cron: время, в которое производится чистка базы данных (в примере - 3 часа ночи по местному времени)

## Как добавить стартер в проект

В pom.xml:
```xml
<dependepcy>
    <groupId>org.jedi_bachelor</groupId>
	<artifactId>spring-iobox-starter</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

В build.gradle:
```gradle
implementation 'org.jedi_bachelor:spring-iobox-starter:0.0.1-SNAPSHOT'
```
