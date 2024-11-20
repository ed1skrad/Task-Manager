# Task Manager
Это приложение, написанное с использованием следующих технологий:

- **JAVA 17**
- **SPRING WEB**
- **SPRING DATA JPA**
- **SPRING BOOT**
- **POSTGRESQL**
- **DOCKER**
- **SPRING SECURITY**
- **JWT**
- **FLYWAY**
- **SONAR CLOUD/QUBE**

## Описание

Базовая реализация Task Manager, позволяет управлять задачами. Также реализована аутентификация/авторизация при помощи JWT токенов.

## Качество кода

В процессе разработки использовался SonarCloud для проверки качества написания кода.

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=ed1skrad_Task-Manager)](https://sonarcloud.io/summary/new_code?id=ed1skrad_Task-Manager)

Также тестами покрывалась бизнес-логика, процент покрытия определялся анализом SonarQube:

![sonarqube](https://github.com/ed1skrad/Task-Manager/blob/main/docs/img.png)

## Запуск

Для запуска приложения как в локальной, так и в дев среде используйте команду:
`docker-compose up`

## Документация

После запуска программы в контейнере перейдите по адресу `http://localhost:8080/swagger-ui/index.html`

