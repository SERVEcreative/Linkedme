# Understanding Java Classpath

## What is Classpath?

**Classpath** is a parameter that tells the Java Virtual Machine (JVM) where to find:
- **Classes** (`.class` files)
- **JAR files** (Java Archive - contains compiled classes)
- **Resources** (like `.properties`, `.xml`, `.yml` files)

Think of it as Java's way of saying: *"Here are all the places where you should look for code and resources when running my program."*

## Simple Analogy

Imagine you're looking for a book in a library:
- **Classpath** = The list of all library sections where books might be
- **Classes** = The actual books you're looking for
- **JVM** = The librarian who searches those sections

## How Classpath Works

### 1. **Compile Time Classpath**
When you compile Java code (`javac`), the compiler needs to find:
- Other classes your code uses
- Libraries your code depends on

### 2. **Runtime Classpath**
When you run Java code (`java`), the JVM needs to find:
- Your compiled classes
- All dependencies (JAR files)
- Configuration files

## Classpath in Your LinkedMe Project

### Example from Your Project

When you run `ApiGatewayApplication`, Java needs to find:

```
Classpath includes:
├── Your compiled classes
│   └── com/linkedme/gateway/ApiGatewayApplication.class
│
├── Spring Boot libraries (from Maven)
│   ├── spring-boot-starter-web.jar
│   ├── spring-cloud-starter-gateway.jar
│   └── ... (many more)
│
└── Resources
    └── application.yml
```

## Setting Classpath

### Method 1: Using `-cp` or `-classpath` flag

```bash
java -cp "path/to/classes:path/to/jar1.jar:path/to/jar2.jar" com.linkedme.gateway.ApiGatewayApplication
```

### Method 2: Using `CLASSPATH` environment variable

```bash
# Linux/Mac
export CLASSPATH="path/to/classes:path/to/jar1.jar"

# Windows
set CLASSPATH=path\to\classes;path\to\jar1.jar
```

### Method 3: Using Maven (Your Project)

Maven automatically manages classpath! When you run:
```bash
mvn spring-boot:run
```

Maven:
1. Compiles your code
2. Downloads dependencies
3. Sets up classpath automatically
4. Runs your application

## Classpath in IntelliJ IDEA

### How IntelliJ Sets Classpath

1. **Module Classpath:**
   - IntelliJ reads your `pom.xml`
   - Downloads Maven dependencies
   - Adds them to the module's classpath
   - Shows them under "External Libraries" in Project tree

2. **Run Configuration Classpath:**
   - When you create a run configuration
   - IntelliJ automatically includes:
     - Your compiled classes
     - All Maven dependencies
     - Resources (like `application.yml`)

### Viewing Classpath in IntelliJ

1. **Run → Edit Configurations...**
2. Select your run configuration
3. Look at:
   - **Use classpath of module**: `api-gateway`
   - This tells IntelliJ to use all dependencies from `api-gateway` module

## Common Classpath Issues

### Issue 1: "ClassNotFoundException"

**Problem:**
```
Exception in thread "main" java.lang.ClassNotFoundException: com.linkedme.gateway.ApiGatewayApplication
```

**Cause:** Classpath doesn't include your compiled classes

**Solution:**
- Rebuild project: `Ctrl+F9`
- Check if classes are in `target/classes` folder
- Verify run configuration includes correct module

### Issue 2: "NoClassDefFoundError"

**Problem:**
```
Exception in thread "main" java.lang.NoClassDefFoundError: org/springframework/boot/SpringApplication
```

**Cause:** Missing dependency in classpath

**Solution:**
- Reload Maven project
- Check if dependency is in `pom.xml`
- Verify Maven downloaded the JAR

### Issue 3: Resource Not Found

**Problem:**
```
Could not find application.yml
```

**Cause:** Resources folder not in classpath

**Solution:**
- Verify `src/main/resources` exists
- Check if `application.yml` is in resources folder
- Rebuild project

## Classpath Structure in Spring Boot

### Your Project Structure

```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/              ← Compiled to target/classes
│   │   │   └── com/linkedme/gateway/
│   │   │       └── ApiGatewayApplication.java
│   │   │
│   │   └── resources/          ← Copied to target/classes
│   │       └── application.yml
│   │
│   └── test/
│
└── target/                     ← Build output
    └── classes/
        ├── com/linkedme/gateway/
        │   └── ApiGatewayApplication.class
        └── application.yml
```

### Maven Dependencies (External Libraries)

```
~/.m2/repository/               ← Maven local repository
├── org/springframework/boot/
│   └── spring-boot-starter-web/
│       └── 3.2.0/
│           └── spring-boot-starter-web-3.2.0.jar
└── ... (many more)
```

## How Spring Boot Uses Classpath

### 1. **Finding Application Class**

```java
@SpringBootApplication
public class ApiGatewayApplication {
    // Spring Boot looks for this class in classpath
}
```

### 2. **Loading Configuration Files**

Spring Boot automatically looks for:
- `application.yml` in classpath
- `application.properties` in classpath
- Files in `src/main/resources/` are on classpath

### 3. **Component Scanning**

```java
@SpringBootApplication  // Scans com.linkedme.gateway and sub-packages
public class ApiGatewayApplication {
    // Spring looks for @Component, @Service, @Repository in classpath
}
```

## Practical Examples

### Example 1: Running from Command Line

```bash
# Maven sets classpath automatically
cd api-gateway
mvn spring-boot:run

# Behind the scenes, Maven does:
java -cp "target/classes:target/dependency/*" com.linkedme.gateway.ApiGatewayApplication
```

### Example 2: IntelliJ Run Configuration

When you click "Run" in IntelliJ:

1. IntelliJ compiles your code → `target/classes`
2. IntelliJ reads `pom.xml` → Gets all dependencies
3. IntelliJ sets classpath:
   ```
   target/classes
   + all Maven dependencies
   + resources folder
   ```
4. IntelliJ runs: `java -cp [classpath] com.linkedme.gateway.ApiGatewayApplication`

### Example 3: Loading Resources

```java
// This works because resources/ is on classpath
@Value("${spring.application.name}")
private String appName;

// Or loading a file
InputStream is = getClass().getResourceAsStream("/application.yml");
```

## Classpath vs Module Path (Java 9+)

### Classpath (Traditional)
- Used for JAR files and classes
- Single flat list
- Used in your project

### Module Path (Java 9+)
- Used for Java modules
- More structured
- Not used in your Spring Boot project (yet)

## Best Practices

### ✅ DO:
- Let Maven/IntelliJ manage classpath automatically
- Keep resources in `src/main/resources`
- Use Maven for dependency management
- Rebuild project if classpath issues occur

### ❌ DON'T:
- Manually set CLASSPATH environment variable (unless necessary)
- Manually add JARs to classpath (use Maven instead)
- Mix different ways of setting classpath

## Quick Reference

| Term | Meaning |
|------|---------|
| **Classpath** | List of locations where JVM looks for classes/resources |
| **JAR** | Java Archive - contains compiled classes |
| **Class** | Compiled Java file (`.class`) |
| **Resource** | Non-Java files (`.yml`, `.properties`, etc.) |
| **Maven** | Build tool that manages classpath automatically |
| **Module** | In IntelliJ, a module has its own classpath |

## Summary

**Classpath = Where Java looks for code and resources**

In your LinkedMe project:
- ✅ Maven automatically manages classpath
- ✅ IntelliJ automatically sets classpath when you run
- ✅ Resources in `src/main/resources` are on classpath
- ✅ All Maven dependencies are on classpath

You usually don't need to worry about classpath manually - Maven and IntelliJ handle it for you! 🎯
