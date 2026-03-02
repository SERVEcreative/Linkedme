# Quick Start Guide - Running LinkedMe

## Prerequisites Check

Before running, ensure you have:
- ✅ Java 17 or higher installed
- ✅ Maven 3.8+ installed
- ✅ Docker Desktop installed and running
- ✅ IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Step 1: Start Infrastructure Services

First, start all the required infrastructure (PostgreSQL, Redis, Kafka, etc.):

```bash
# Navigate to project root
cd C:\Users\RAHKUMAR\Desktop\Project_Learning\Self_Help

# Start Docker services
docker-compose -f docker/docker-compose.yml up -d

# Verify services are running
docker ps
```

You should see containers for:
- linkedme-postgres
- linkedme-redis
- linkedme-kafka
- linkedme-elasticsearch
- etc.

## Step 2: Build the Project

### Option A: Using Maven Command Line

```bash
# From project root
mvn clean install -DskipTests
```

### Option B: Using IDE

1. **IntelliJ IDEA:**
   - Right-click on `pom.xml` (root)
   - Select "Add as Maven Project"
   - Wait for dependencies to download
   - Right-click on project → Maven → Reload Project

2. **Eclipse:**
   - Right-click on project → Import → Maven → Existing Maven Projects
   - Select the root directory

3. **VS Code:**
   - Install "Extension Pack for Java"
   - Open the project folder
   - It will auto-detect Maven project

## Step 3: Run Services

### Option A: Using IDE (Recommended)

**IntelliJ IDEA:**
1. Open the project in IntelliJ
2. Wait for Maven to sync (bottom right corner)
3. Navigate to: `api-gateway/src/main/java/com/linkedme/gateway/ApiGatewayApplication.java`
4. You should see a green ▶️ play button next to `main` method
5. Click it and select "Run ApiGatewayApplication"
6. Repeat for `UserServiceApplication`

**Eclipse:**
1. Right-click on `ApiGatewayApplication.java`
2. Run As → Java Application

**VS Code:**
1. Open the Java file
2. Click "Run" above the main method
3. Or use F5 to debug

### Option B: Using Maven Command Line

```bash
# Terminal 1 - API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 2 - User Service
cd user-service
mvn spring-boot:run
```

## Step 4: Verify Services Are Running

Check if services started successfully:

- **API Gateway**: http://localhost:8080/actuator/health
- **User Service**: http://localhost:8081/actuator/health

You should see:
```json
{"status":"UP"}
```

## Troubleshooting

### Issue 1: No Run Button / Can't Find Main Class

**Solution:**
1. Make sure Maven has synced dependencies
2. In IntelliJ: File → Invalidate Caches → Invalidate and Restart
3. Rebuild project: Build → Rebuild Project
4. Check if package structure matches: `com.linkedme.gateway` should be in `src/main/java/com/linkedme/gateway/`

### Issue 2: Port Already in Use

**Solution:**
```bash
# Windows - Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

Or change the port in `application.yml`:
```yaml
server:
  port: 8081  # Change to different port
```

### Issue 3: Database Connection Error

**Solution:**
1. Make sure Docker containers are running: `docker ps`
2. Check PostgreSQL is up: `docker logs linkedme-postgres`
3. Verify connection string in `application.yml`

### Issue 4: Maven Dependencies Not Downloading

**Solution:**
1. Check internet connection
2. Try: `mvn clean install -U` (force update)
3. Check Maven settings: `~/.m2/settings.xml`

### Issue 5: Java Version Mismatch

**Solution:**
```bash
# Check Java version
java -version

# Should show Java 17 or higher
# If not, install Java 17 and set JAVA_HOME
```

## Step 5: Test the Application

Once services are running, test with:

```bash
# Test User Service health
curl http://localhost:8081/actuator/health

# Test API Gateway
curl http://localhost:8080/actuator/health
```

## Next Steps

1. ✅ Services are running
2. Start implementing features (see IMPLEMENTATION_GUIDE.md)
3. Add more services (Profile, Post, etc.)
4. Test APIs using Postman or Swagger UI

## IDE-Specific Instructions

### IntelliJ IDEA

1. **Import Project:**
   - File → Open → Select project root folder
   - Select "Import project from external model" → Maven
   - Click Next → Finish

2. **Enable Auto-Import:**
   - When Maven popup appears, click "Enable Auto-Import"

3. **Run Configuration:**
   - Right-click on main class → Run
   - Or use Run → Edit Configurations to create custom run configs

### Eclipse

1. **Import Project:**
   - File → Import → Maven → Existing Maven Projects
   - Browse to project root
   - Select all pom.xml files
   - Click Finish

2. **Run:**
   - Right-click on main class → Run As → Java Application

### VS Code

1. **Install Extensions:**
   - Extension Pack for Java
   - Spring Boot Extension Pack

2. **Open Project:**
   - File → Open Folder → Select project root
   - Wait for Java projects to load

3. **Run:**
   - Click Run button above main method
   - Or use Debug panel (F5)

## Common Commands

```bash
# Build project
mvn clean install

# Run specific service
mvn spring-boot:run -pl api-gateway

# Check Maven version
mvn -version

# Check Java version
java -version

# View Docker containers
docker ps

# View Docker logs
docker logs linkedme-postgres
```

If you still can't see the run button, let me know which IDE you're using and I'll provide specific steps!
