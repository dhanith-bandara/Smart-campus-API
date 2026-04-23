# Smart Campus REST API

A RESTful API for managing campus rooms and IoT sensors, built with JAX-RS (Jersey) and an embedded Grizzly server.

## Features
- **Discovery**: Root endpoint with HATEOAS links.
- **Rooms**: Manage campus rooms.
- **Sensors**: Manage IoT sensors linked to rooms.
- **Readings**: Track sensor data over time.
- **HATEOAS**: Navigational links in API responses.
- **Custom Exceptions**: Specialized error handling with appropriate HTTP status codes (422, 409, 503).
- **Logging**: Request and response logging via Container Filters.

## Requirements
- Java 11 or higher
- Maven 3.6+

## How to Run

### 1. Compile the Project
Open a terminal in the project root and run:
```bash
mvn clean compile
```

### 2. Run the Server
Run the following command:
```bash
mvn exec:java -Dexec.mainClass="com.smartcampus.Main"
```
The server will start at `http://localhost:8080/api/v1/`.

## API Endpoints

- **Discovery**: `GET /api/v1/`
- **Rooms**:
  - `GET /api/v1/rooms`: List all rooms.
  - `POST /api/v1/rooms`: Create a room.
  - `GET /api/v1/rooms/{id}`: Get room details.
  - `DELETE /api/v1/rooms/{id}`: Delete a room.
- **Sensors**:
  - `GET /api/v1/sensors`: List all sensors.
  - `POST /api/v1/sensors`: Create a sensor.
  - `GET /api/v1/sensors/{id}`: Get sensor details.
- **Readings**:
  - `GET /api/v1/sensors/{id}/readings`: Get readings for a sensor.
  - `POST /api/v1/sensors/{id}/readings`: Add a new reading.

## Testing with cURL
Example to get all rooms:
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### Discovery
```bash
curl -X GET http://localhost:8080/api/v1/
```

### Rooms
**Create a New Room**:
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
     -H "Content-Type: application/json" \
     -d '{"id": "R3", "name": "Conference Room A", "capacity": 50}'
```

**Get a Specific Room**:
```bash
curl -X GET http://localhost:8080/api/v1/rooms/R1
```

### Sensors
**Create a New Sensor**:
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
     -H "Content-Type: application/json" \
     -d '{"id": "S3", "type": "Humidity", "status": "Active", "currentValue": 45.0, "roomId": "R1"}'
```

### Sensor Readings
**Add a New Reading**:
```bash
curl -X POST http://localhost:8080/api/v1/sensors/S1/readings \
     -H "Content-Type: application/json" \
     -d '{"value": 24.5}'
```

**Get All Readings for a Sensor**:
```bash
curl -X GET http://localhost:8080/api/v1/sensors/S1/readings
```

### Error Validation Examples
**Delete Room with Sensors (Returns 409 Conflict)**:
```bash
curl -I -X DELETE http://localhost:8080/api/v1/rooms/R1
```

**Link to Non-existent Room (Returns 422 Unprocessable Entity)**:
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
     -H "Content-Type: application/json" \
     -d '{"id": "S4", "type": "Temp", "status": "Active", "currentValue": 20.0, "roomId": "INVALID"}'
```

## Coursework Report Answers

### 1. Architectural Design
The Smart Campus system follows a **2-tier Client-Server architecture**.
- **Backend (Server)**: Built using **Java JAX-RS (Jersey)** and hosted on an embedded **Grizzly HTTP Server**. It manages the state of campus resources (Rooms, Sensors) and provides a unified interface for data access.
- **Frontend (Client)**: Any HTTP-capable client (like `curl`, Postman, or a web browser) can interact with the API endpoints to manage campus data.

### 2. RESTful Implementation
The API adheres to REST principles by:
- **Resource Identification**: Every entity (Room, Sensor, Reading) has a unique URI.
- **Uniform Interface**: Uses standard HTTP verbs:
    - `GET`: Retrieve resource data.
    - `POST`: Create new resources or readings.
    - `DELETE`: Remove resources (with validation).
- **Representation**: Data is exchanged using **JSON** (MIME type `application/json`).
- **Statelessness**: The server does not store client session state; each request is self-contained.

### 3. HATEOAS (Hypermedia as the Engine of Application State)
HATEOAS is implemented to achieve **Level 3 of the Richardson Maturity Model**.
- **Implementation**: Each response includes a `_links` array containing navigational hypermedia.
- **Benefits**: It improves **discoverability**, allowing clients to navigate the API without hardcoding URIs. It also **decouples** the client from the server's URI structure, as the client follows links provided by the server.

### 4. Advanced Error Handling
The system uses specialized **ExceptionMappers** to return semantic HTTP status codes:
- **422 Unprocessable Entity**: Used when a resource link fails (e.g., adding a sensor to a non-existent room).
- **409 Conflict**: Used to maintain data integrity (e.g., preventing the deletion of a room that still contains sensors).
- **503 Service Unavailable**: Used when a sensor's status is set to 'Maintenance', signaling that data is temporarily unavailable.

### 5. Activity Logging
A server-side **LoggingFilter** is implemented using JAX-RS `ContainerRequestFilter` and `ContainerResponseFilter`. This provides real-time monitoring of all incoming requests and outgoing responses, facilitating debugging and audit trails.
