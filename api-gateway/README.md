# API Gateway

Single entry point for all client requests. Routes and auth live here.

## Structure

```
com.linkedme.gateway
├── ApiGatewayApplication.java
├── config/                     # Gateway & infrastructure
│   ├── GatewayConfig          # Redis template, properties
│   ├── GatewayProperties      # Public paths, user-service URI
│   └── SecurityConfig         # Spring Security (permit all; filters do auth)
└── auth/                       # Authentication (JWT + filters)
    ├── JwtProperties          # jwt.secret, jwt.expiration
    ├── JwtUtil                 # Generate/validate token, extract claims
    ├── JwtConfig               # JwtUtil bean
    ├── AuthPayload             # User-service login/register response shape
    ├── AuthResponse            # Client response (token + user info)
    └── filter/
        ├── LoginRegisterFilter # POST /api/users/login|register → user-service → issue JWT (order -200)
        ├── LogoutFilter        # POST /api/users/logout → blacklist token (order -150)
        └── JwtAuthenticationFilter # Validate JWT, add X-User-* headers, forward (order -100)
```

## Request flow

1. **Public** (no token): `/api/users/login`, `/api/users/register`, `/actuator`, `/health`  
   → `LoginRegisterFilter` handles login/register; rest pass through.

2. **Login/Register**: POST to above paths  
   → Gateway calls user-service → gateway issues JWT → returns `AuthResponse` to client.

3. **Logout**: POST `/api/users/logout` with Bearer token  
   → `LogoutFilter` blacklists token in Redis.

4. **Protected** (token required): all other `/api/*`  
   → `JwtAuthenticationFilter` validates token, checks blacklist, strips `Authorization`, adds `X-User-Id`, `X-User-Email`, `X-User-Username`, `X-User-Role` → forwards to downstream service.

5. **Routes** (from `application.properties`): `/api/users/**` → user-service, `/api/profiles/**` → profile-service, etc. Prefix `/api` is stripped when forwarding.

## Where to look

| Need to…              | Look at |
|-----------------------|--------|
| Add/change routes     | `application.properties` (spring.cloud.gateway.routes) |
| Change public paths   | `config/GatewayProperties` |
| Change JWT behaviour  | `auth/JwtUtil`, `auth/JwtProperties` |
| Change login/register | `auth/filter/LoginRegisterFilter` |
| Change logout        | `auth/filter/LogoutFilter` |
| Change validation/headers | `auth/filter/JwtAuthenticationFilter` |
