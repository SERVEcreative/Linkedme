# Start All Services
# This script starts API Gateway, User Service, and Profile Service with H2 database

Write-Host "Starting LinkedIn Microservices..." -ForegroundColor Cyan
Write-Host ""

# Start API Gateway
Write-Host "[1/3] Starting API Gateway (port 8080)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot'; mvn spring-boot:run -pl api-gateway"
Start-Sleep -Seconds 3

# Start User Service
Write-Host "[2/3] Starting User Service (port 8081)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot'; mvn spring-boot:run -pl user-service"
Start-Sleep -Seconds 3

# Start Profile Service
Write-Host "[3/3] Starting Profile Service (port 8082)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot'; mvn spring-boot:run -pl profile-service"

Write-Host ""
Write-Host "All services are starting in separate windows..." -ForegroundColor Green
Write-Host ""
Write-Host "Service URLs:" -ForegroundColor Cyan
Write-Host "  API Gateway:    http://localhost:8080" -ForegroundColor Gray
Write-Host "  User Service:   http://localhost:8081" -ForegroundColor Gray
Write-Host "  Profile Service: http://localhost:8082" -ForegroundColor Gray
Write-Host ""
Write-Host "H2 Consoles:" -ForegroundColor Cyan
Write-Host "  User DB:    http://localhost:8081/h2-console" -ForegroundColor Gray
Write-Host "  Profile DB: http://localhost:8082/h2-console" -ForegroundColor Gray
Write-Host ""
Write-Host "Wait 30-60 seconds for all services to start, then test with:" -ForegroundColor Yellow
Write-Host "  curl -X POST http://localhost:8080/api/users/register -H 'Content-Type: application/json' -d '{\"email\":\"test@example.com\",\"password\":\"password123\",\"firstName\":\"Test\",\"lastName\":\"User\"}'" -ForegroundColor Gray
