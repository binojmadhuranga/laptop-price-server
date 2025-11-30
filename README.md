# Laptop Price Server

A lightweight Spring Boot REST API that forwards laptop hardware details to a downstream Python/Flask machine learning service in order to predict the market price of a device. Requests are accepted at `/api/predict` and proxied to the Flask model hosted on port `5001`.

## Project Overview

- **Stack**: Spring Boot 4, Java 17, Lombok, RestTemplate
- **Responsibility**: Accept laptop specs (RAM, weight, screen options, CPU/GPU identifiers, etc.) and return the price prediction produced by the external ML model.
- **Architecture**: Controller-only service that delegates to a Flask API via HTTP POST.

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Running Flask service with the `/predict` endpoint on `http://localhost:5001` (or update the URL in `PredictionController`).

### Install & Run

```powershell
cd E:\AI_ML\Laptop_Price\laptop-price-server
./mvnw.cmd spring-boot:run
```

The API will start on `http://localhost:8080` by default (configurable in `src/main/resources/application.properties`).

### API Usage

**Endpoint**: `POST /api/predict`

**Request body** (JSON):

```json
{
  "ram": 16,
  "weight": 1.5,
  "touchscreen": 1,
  "ips": 1,
  "company": "Dell",
  "typename": "Ultrabook",
  "opsys": "Windows",
  "cpu": "Intel i7",
  "gpu": "NVIDIA"
}
```

**Response**:

```json
{
  "success": true,
  "prediction": 1234.56,
  "error": null
}
```

In failure scenarios (e.g., Flask service unavailable), the response is still HTTP 200 but contains `success: false` and an `error` message.

## Tests

Unit tests live under `src/test/java`. To execute them:

```powershell
cd E:\AI_ML\Laptop_Price\laptop-price-server
./mvnw.cmd test
```

Coverage includes controller unit tests (mocked RestTemplate interactions) and the default Spring context load test.

## Configuration

Update `src/main/resources/application.properties` for:

- `server.port` – the Spring Boot server port (default 8080)
- `spring.jackson.serialization.indent-output` – pretty print JSON
- `spring.mvc.async.request-timeout` – adjust async timeout

To re-point the downstream service, change `FLASK_URL` in `PredictionController` or convert it to a configuration property.

## Next Steps

- Externalize `FLASK_URL` into configuration profiles / environment variables
- Add validation annotations to `PredictionRequestDto`
- Create integration tests that spin up a mock HTTP server for the Flask endpoint

