# Sprout

## Local development

Run infrastructure with Docker Compose:

```bash
docker compose up -d
```

This starts only:

- PostgreSQL on `127.0.0.1:15432`
- Redis on `127.0.0.1:16379`
- Kafka on `127.0.0.1:19092`

Run the Spring Boot application from IntelliJ or Maven on the host machine. The default values in `application.properties` are already aligned to those ports.

Kafka producer/consumer wiring is disabled by default for local runs. Enable it only when needed:

```bash
APP_KAFKA_ENABLED=true
```

If you want to run the application in Docker as well, use the optional profile:

```bash
docker compose --profile full-stack up -d app
```
