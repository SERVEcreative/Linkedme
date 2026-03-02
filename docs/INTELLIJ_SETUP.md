# IntelliJ IDEA Setup Guide for LinkedMe

## Step-by-Step IntelliJ Setup

### Step 1: Open Project in IntelliJ

1. **Open IntelliJ IDEA**
2. **File → Open** (or `Ctrl+O`)
3. Navigate to: `C:\Users\RAHKUMAR\Desktop\Project_Learning\Self_Help`
4. Select the folder and click **OK**
5. IntelliJ will ask: **"Trust Project?"** → Click **Trust Project**

### Step 2: Import as Maven Project

1. IntelliJ should automatically detect it's a Maven project
2. Look for a popup at the bottom right: **"Maven projects need to be imported"**
3. Click **"Import Maven Projects"** or **"Enable Auto-Import"**
4. Wait for Maven to download dependencies (check bottom right progress bar)

**If you don't see the popup:**
- Right-click on `pom.xml` (root level)
- Select **"Add as Maven Project"**

### Step 3: Wait for Indexing

1. IntelliJ will start indexing the project
2. Check bottom right corner - you'll see "Indexing..." 
3. Wait until it completes (this may take a few minutes)
4. You'll see "Indexing completed" when done

### Step 4: Verify Project Structure

1. **File → Project Structure** (or `Ctrl+Alt+Shift+S`)
2. Check:
   - **Project SDK**: Should be Java 17 or higher
   - **Project language level**: 17 or higher
3. Go to **Modules** tab
4. You should see:
   - `linkedme` (parent)
   - `api-gateway`
   - `user-service`
   - etc.
5. Click **OK**

### Step 5: Find the Run Button

**Method 1: Using the Main Class**

1. Navigate to: `api-gateway/src/main/java/com/linkedme/gateway/ApiGatewayApplication.java`
2. Open the file
3. Look at **line 11** where `main` method is
4. You should see a **green ▶️ play icon** in the left gutter (next to line numbers)
5. Click it → Select **"Run 'ApiGatewayApplication.main()'"**

**Method 2: Right-Click Menu**

1. Right-click on `ApiGatewayApplication.java` in the Project tree
2. Select **"Run 'ApiGatewayApplication.main()'"**

**Method 3: Using Run Configuration**

1. **Run → Edit Configurations...**
2. Click **"+"** (plus icon) → **"Application"**
3. Fill in:
   - **Name**: `API Gateway`
   - **Main class**: `com.linkedme.gateway.ApiGatewayApplication`
   - **Module**: `api-gateway`
   - **Use classpath of module**: `api-gateway`
4. Click **OK**
5. Now you can run from the dropdown at the top toolbar

### Step 6: Build Project First (If Run Button Still Missing)

1. **Build → Build Project** (or `Ctrl+F9`)
2. Wait for build to complete
3. Check for any errors in the **Build** tool window
4. If there are errors, fix them first

### Step 7: Invalidate Caches (If Still Not Working)

1. **File → Invalidate Caches...**
2. Check all boxes:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **"Invalidate and Restart"**
4. Wait for IntelliJ to restart
5. Wait for re-indexing to complete

## Troubleshooting IntelliJ Issues

### Issue 1: "Cannot resolve symbol" errors

**Solution:**
1. **File → Invalidate Caches → Invalidate and Restart**
2. After restart: **File → Reload Project from Disk**
3. **Maven → Reload Project**

### Issue 2: Maven dependencies not downloading

**Solution:**
1. **File → Settings** (or `Ctrl+Alt+S`)
2. **Build, Execution, Deployment → Build Tools → Maven**
3. Check:
   - **Maven home path**: Should point to your Maven installation
   - **User settings file**: Should have correct path
4. Click **OK**
5. **Maven → Reload Project**

### Issue 3: Java version not recognized

**Solution:**
1. **File → Project Structure** (`Ctrl+Alt+Shift+S`)
2. **Project Settings → Project**
3. **Project SDK**: Click dropdown → **Add SDK → JDK**
4. Navigate to your Java 17 installation
5. Click **OK**

### Issue 4: No green run button appears

**Solution:**
1. Make sure the file is actually a Java file (not a text file)
2. Check if there's a `main` method
3. Try: **Run → Run 'ApiGatewayApplication'** (even if button not visible)
4. Or use keyboard shortcut: `Shift+F10` (Run) or `Shift+F9` (Debug)

### Issue 5: "Module not found" error

**Solution:**
1. **File → Project Structure** (`Ctrl+Alt+Shift+S`)
2. **Modules**
3. Select each module (api-gateway, user-service, etc.)
4. **Dependencies** tab
5. Make sure all dependencies are listed
6. If missing, click **"+"** → **"Library"** → Add required JARs

## Quick Test After Setup

1. **Start Docker services first:**
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

2. **Run API Gateway:**
   - Click green ▶️ button next to `main` method
   - Or press `Shift+F10`

3. **Check console output:**
   - Should see: "Started ApiGatewayApplication"
   - Should see: "Tomcat started on port(s): 8080"

4. **Test in browser:**
   - Open: http://localhost:8080/actuator/health
   - Should see: `{"status":"UP"}`

## IntelliJ Keyboard Shortcuts

- **Run**: `Shift+F10`
- **Debug**: `Shift+F9`
- **Stop**: `Ctrl+F2`
- **Build Project**: `Ctrl+F9`
- **Reload Maven**: `Ctrl+Shift+O` (or right-click pom.xml → Maven → Reload)

## Verify Everything is Set Up

✅ Project opens without errors
✅ Maven dependencies downloaded (check External Libraries in Project tree)
✅ No red underlines in Java files
✅ Green run button appears next to `main` method
✅ Can see "Spring Boot" icon in run configuration dropdown

## Still Having Issues?

If you still can't see the run button:

1. **Take a screenshot** of:
   - The Java file you're trying to run
   - The Project Structure window
   - Any error messages

2. **Check these:**
   - Is Java 17 installed? (`java -version` in terminal)
   - Is Maven installed? (`mvn -version` in terminal)
   - Are there any red error markers in the file?

3. **Try this alternative:**
   - Open terminal in IntelliJ (Alt+F12)
   - Navigate to api-gateway: `cd api-gateway`
   - Run: `mvn spring-boot:run`

Let me know what you see and I'll help you fix it!
