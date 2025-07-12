# Users Service - JWT Authentication

This service provides user registration, authentication, and JWT token generation for the Fintrack microservices architecture.

## Features

- User registration with email and username validation
- User login with JWT token generation
- JWT token validation for protected endpoints
- Password encryption using BCrypt
- CORS configuration for frontend integration

## API Endpoints

### Public Endpoints (No Authentication Required)

#### Register User
```
POST /api/users/register
Content-Type: application/json

{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
}
```

Response:
```json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "email": "john@example.com"
}
```

#### Login User
```
POST /api/users/login
Content-Type: application/json

{
    "usernameOrEmail": "john_doe",
    "password": "password123"
}
```

Response:
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "email": "john@example.com"
}
```

### Protected Endpoints (Authentication Required)

#### Get Current User
```
GET /api/users/me
Authorization: Bearer <jwt_token>
```

Response:
```json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "email": "john@example.com"
}
```

## JWT Token Usage

### For Frontend Applications

1. After successful login, store the JWT token securely (e.g., in localStorage or httpOnly cookies)
2. Include the token in the Authorization header for all subsequent requests:
   ```
   Authorization: Bearer <jwt_token>
   ```

### For Other Microservices

Other microservices can validate JWT tokens using the `JwtUtils` class:

```java
@Autowired
private JwtUtils jwtUtils;

public void validateToken(String token) {
    if (jwtUtils.validateToken(token)) {
        String username = jwtUtils.extractUsername(token);
        String userId = jwtUtils.extractUserId(token);
        // Process the request
    } else {
        // Token is invalid
    }
}
```

## Configuration

### JWT Properties

Add these properties to your `application.properties`:

```properties
# JWT Configuration
jwt.secret=your-super-secret-jwt-key-that-should-be-at-least-256-bits-long-for-security
jwt.expiration=86400000
```

- `jwt.secret`: Secret key for signing JWT tokens (should be at least 256 bits)
- `jwt.expiration`: Token expiration time in milliseconds (default: 24 hours)

### Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fintrack
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Security Features

- **Password Encryption**: All passwords are encrypted using BCrypt
- **JWT Token Validation**: Tokens are validated on every request
- **Stateless Authentication**: No server-side session storage
- **CORS Support**: Configured for frontend integration
- **Input Validation**: All inputs are validated using Bean Validation

## Error Handling

The service returns appropriate HTTP status codes:

- `200 OK`: Successful operation
- `400 Bad Request`: Invalid input data
- `401 Unauthorized`: Invalid or missing JWT token
- `403 Forbidden`: Insufficient permissions
- `409 Conflict`: Username or email already exists
- `500 Internal Server Error`: Server error

## Running the Service

1. Ensure PostgreSQL is running
2. Start the service: `./mvnw spring-boot:run`
3. The service will be available at `http://localhost:8080`

## Testing

You can test the endpoints using curl:

```bash
# Register a user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"password123"}'

# Get current user (use token from login response)
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your_jwt_token>"
``` 