# Users Service

This service handles user authentication and management for the Fintrack application.

## Features

- User registration
- User login with JWT token generation
- Get current user information (requires authentication)

## API Endpoints

### Register User
```
POST /api/users/register
Content-Type: application/json

{
  "username": "john_doe",
  "firstname": "John",
  "lastname": "Doe", 
  "email": "john@example.com",
  "password": "password123"
}
```

### Login User
```
POST /api/users/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "username": "john_doe",
  "email": "john@example.com"
}
```

### Get Current User (Me)
```
GET /api/users/me
Authorization: Bearer <jwt_token>
```

Response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "username": "john_doe",
  "email": "john@example.com"
}
```

## Authentication

The `/me` endpoint requires a valid JWT token in the Authorization header. The token is obtained from the login endpoint.

### How to use the /me endpoint:

1. First, register a user using the `/register` endpoint
2. Login using the `/login` endpoint to get a JWT token
3. Use the token in the Authorization header to access the `/me` endpoint

Example using curl:
```bash
# Register a user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","firstname":"Test","lastname":"User","email":"test@example.com","password":"password123"}'

# Login to get token
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Use token to get current user info
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <token_from_login_response>"
```

## Security

- Passwords are hashed using BCrypt
- JWT tokens are used for stateless authentication
- All endpoints except `/register` and `/login` require authentication
- JWT tokens expire after 24 hours

## Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default.

## Testing

Run the tests with:
```bash
./mvnw test
``` 