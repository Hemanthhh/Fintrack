# Transactions Service

This service handles financial transactions with simple JWT token extraction.

## JWT Token Extraction Implementation

The transactions service extracts user ID directly from JWT tokens without any authentication validation. All validation logic remains in the users service.

### Components

1. **Direct JWT Extraction** (`controller/TransactionController.java`)
   - Extracts user ID directly from JWT token in Authorization header
   - Uses private methods `getCurrentUserId()` and `extractUsernameFromToken()`
   - No Spring Security or authentication filters

### How It Works

1. **Request Flow:**
   ```
   Client Request (with JWT token) 
   → Controller (extracts user ID from JWT token)
   → Service Layer (uses extracted user ID)
   ```

2. **User ID Extraction:**
   - The `getCurrentUserId()` method extracts the user ID from the JWT token
   - Assumes the username in the JWT is the user ID (UUID string)
   - No token validation - only extraction

### Usage

All transaction endpoints require a JWT token in the Authorization header:

```bash
Authorization: Bearer <jwt_token>
```

### Configuration

The JWT secret is configured in `application.properties`:

```properties
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

### Dependencies

- JWT (jjwt library) - for token parsing only
- Spring Boot Starter Web
- Spring Boot Starter Data JPA 