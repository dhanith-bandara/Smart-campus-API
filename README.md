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

Part 1: Service Architecture and Installation.

Question 1: 
In your report, explain the default lifecycle of a JAX-RS Resource class. Is a
new instance instantiated for every incoming request, or does the runtime treat it as a
singleton? Elaborate on how this architectural decision impacts the way you manage and
synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

The default lifecycle of a resource class in JAX-RS is per-request. This implies that the runtime environment will spawn a new resource class on each incoming HTTP request and destroy it upon sending a response. 
-Effect on State Management: The instances are transient, and you cannot hold application state (such as HashMaps or lists) as instance variables of the resource class. Rather, you have to store data externally, e.g. in a Singleton Repository or in a database, to be globally available. It is essential to use thread-safe collections (e.g., to use ConcurrentHashMap) since numerous request threads can access the shared repository at the same time.


Question 2:
Why is the provision of ”Hypermedia” (links and navigation within responses)
considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach
benefit client developers compared to static documentation? 

HATEOAS (Hypermedia as the Engine of Application State) enables a client to interoperate with the API entirely based on the links offered dynamically in responses, instead of using hardcoded URLs or hardcopy documentation.
-Value to Developers: It renders the API to be self-discoverable. Should there be a shift in the URI structure, there will be no breaking of links by the client. It separates the client and the internal URI scheme of the server, minimizing maintenance costs and letting the server provide advice on which state transitions are valid.


Part 2: Room Management

Question 1:
 When returning a list of rooms, what are the implications of returning only
IDs versus returning the full room objects? Consider network bandwidth and client side
processing.

Response with only IDs: Saves network bandwidth and makes the first response quicker. Nevertheless, the client will need to issue follow-up "N+1" requests to retrieve the information about each room, which further raises latency and server load.
Sending complete objects: Wastes more bandwidth per request but enables the client to present information in real-time and not require further network requests. It is most usually chosen when small collections are involved, or when chattiness must be kept down.


Questions 2: 
Is the DELETE operation idempotent in your implementation? Provide a detailed
justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

Yes, DELETE is idempotent here.
Justification An operation is idempotent when repeated requests to the server state produce an identical effect on the state. When a client deletes a room, it is removed. Receiving the same DELETE request the server verifies the repository, realizes that it is not there and responds with a 404 (or 204 depending on the implementation). In both instances, once the initial deletion is successful the room is lost. The end state of the server is the same no matter the number of calls to the DELETE.


Part 3: Sensor Operations & Linking.

Question 1: 
We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on
the POST method. Explain the technical consequences if a client attempts to send data in
a different format, such as text/plain or application/xml. How does JAX-RS handle this
mismatch?
When a client transmits information that does not have a content-type of application/json (application/text, i.e. text/plain), the JAX-RS runtime will reject the request with an error of HTTP 415 Unsupported Media Type. The runtime uses the annotation of an input media type through the Consumes annotation to find the right provider (such as Jackson) to deserialize an incoming media type. In case no match is found, then it rejects the body.



Question 2: 
You implemented this filtering using @QueryParam. Contrast this with an alterna-
tive design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why
is the query parameter approach generally considered superior for filtering and searching
collections?
QueryParam (/sensors?type=CO2): The type is an optional modifier to the underlying collection. It is better since it can be used with flexible combinations of filters (e.g., `?type=CO2&status=ACTIVE`) without generating a combinatoric explosion of fixed URL paths.
PathParam (/sensors/type/CO2)**: It presupposes that CO2 is a special resource or a definite sub-collection. This is not as flexible and cannot be easily scaled when there are several filter criteria required.



Part 4: Intensive Nesting with Sub-Resources

Discuss the architectural benefits of the Sub-Resource Locator pattern. How
does delegating logic to separate classes help manage complexity in large APIs compared
to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive con-
troller class?
Advantages: It encourages separation of concerns and does not permit a single resource class (such as one of the above SensorResource) to turn into a God Object with dozens of methods to do all the possible nested operations. It renders the codebase more modular, with less testing, and readability.



Part 5: Advanced Handling of errors and logging.

Question 1:
Why is HTTP 422 often considered more semantically accurate than a standard
404 when the issue is a missing reference inside a valid JSON payload?

A 404 is an error code that typically indicates that the URL is not found. An HTTP 422 (Unprocessable Entity) is used when the server knows the content type and the syntax of the request is correct, but it could not interpret the instructions contained. The JSON is valid in this instance, whereas the reference to a room with the ID of roomId is semantically invalid since it refers to a non-existing entity. This difference assists developers to debug their URLs are incorrect, or their data payload is incorrect.



Question 2:
 From a cybersecurity standpoint, explain the risks associated with exposing
internal Java stack traces to external API consumers. What specific information could an
attacker gather from such a trace?

Publication of stack traces puts an attacker in a position of having a roadmap of the internal architecture of the server. It reveals:
1. The specific library and framework versions (and, possibly, targeted CVE exploits).
2. File paths and package names.
3. Data structure or internal logic processes.
Such knowledge is a major step in reducing the obstacle that an attacker may face in identifying the vulnerability or developing a sophisticated exploit.



Question 3: 
Why is it advantageous to use JAX-RS filters for cross-cutting concerns like
logging, rather than manually inserting Logger.info() statements inside every single re-
source method?

Filtering gives preserves a consistent and centralized logging throughout the API.
Pros: It complies with the DRY (Don't Repeat Yourself) principle. Should you require altering the logging format or adding additional metadata (such as request IDs), you simply need to modify it in a single location. Manual logging is not only prone to errors, but also pollutes the business logic with boilerplate code and can result in forgetting to log new endpoints.
