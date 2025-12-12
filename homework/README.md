# Plagiarism Checking System
## 1) Архитектура системы

Система состоит из 3 микросервисов и инфраструктуры:

* **API Gateway** (порт 8080)

    Единая точка входа для клиента. Принимает загрузку файла и метаданные, оркестрирует вызовы других сервисов, возвращает агрегированный ответ. Имеет Swagger UI.


* **File Storage Service** (порт 8081)

    Принимает файл + метаданные, сохраняет метаданные в PostgreSQL, файл — в S3-совместимое хранилище (MinIO). Возвращает submissionId и информацию о загрузке. Также умеет отдавать метаданные/файл по id.


* **File Analysis Service** (порт 8082)

    Принимает submissionId и метаданные (assignmentId, studentIdentifier), получает нужные данные/контент (или работает по заданной логике), формирует отчёт анализа, сохраняет результаты (если нужно) и отдаёт отчёт.

Инфраструктура:

* **PostgreSQL** — единая БД (таблицы storage/analysis по необходимости)

* **MinIO** — S3-compatible object storage для файлов

### Схема взаимодействия

Client → API Gateway → (File Storage) → (File Analysis) → API Gateway → Client

## 2) Пользовательские сценарии и технические сценарии обмена данными
   
### Сценарий A: Загрузка работы и получение анализа (основной поток)

**Цель:** пользователь загружает файл, получает ответ: где сохранено + результаты анализа.

1. **Client → API Gateway**

    POST /api/submissions (multipart/form-data)

    **file:** бинарный файл
    
    **meta:** JSON строкой

```json
{
  "studentName":"Alice",
  "studentIdentifier":"alice01",
  "assignmentId":"HW1"
}
```

2. **API Gateway → File Storage Service**
    POST http://file-storage:8081/internal/submissions (multipart/form-data)

    file: бинарный файл

    meta: application/json (важно)
 
На стороне storage:
* создаётся запись Submission в PostgreSQL
* файл сохраняется в MinIO (bucket + objectKey)
* возвращается JSON с id (submissionId) и метаданными

3. **API Gateway → File Analysis Service**
POST http://file-analysis:8082/internal/analysis (application/json)

```json
{
"submissionId": "....",
"assignmentId": "HW1",
"studentIdentifier": "alice01"
}
```


На стороне analysis:

* выполняется анализ (логика сервиса)

* формируется отчёт

* возвращается JSON-результат анализа

4. **API Gateway → Client**

    Возвращает агрегированный ответ:
```json
{
"storage": { ...ответ file-storage... },
"analysis": { ...ответ file-analysis... }
}
```

### Сценарий B: Получить метаданные работы

1. **Client → API Gateway**
    
    GET /api/submissions/{id}

2. **API Gateway → File Storage Service**

    GET /internal/submissions/{id}

3. API Gateway → Client

    Возвращается JSON с метаданными работы.

### Сценарий C: Получить отчёт анализа по submissionId

1. **Client → API Gateway**

   GET /api/submissions/{id}/analysis

2. **API Gateway → File Analysis Service**

   GET /internal/reports/submissions/{id}

3. **API Gateway → Client**

    Возвращается отчёт анализа.

### Сценарий D: Получить отчёты по заданию (assignment)

1. **Client → API Gateway**

    GET /api/assignments/{assignmentId}/reports

2. **API Gateway → File Analysis Service**

    GET /internal/works/{assignmentId}/reports

3. **API Gateway → Client**

    Возвращается список отчётов.

## 3) Запуск проекта (Docker Compose)
   
### Требования

* Docker + Docker Compose

* Java 17 + Gradle для локального запуска

### Запуск
```
docker compose up --build
```

### Проверка

* Swagger Gateway: http://localhost:8080/swagger

* MinIO Console: http://localhost:9001 (логин/пароль: minioadmin/minioadmin)

## 4) Примечания по конфигурации

* File Storage сохраняет метаданные в PostgreSQL и файлы в MinIO (S3 API).