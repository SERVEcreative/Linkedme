# Configure IntelliJ for Java 21 - Step by Step

## ✅ Confirmed: Java 21 is Installed

Your system has Java 21, which is perfect! Now let's configure IntelliJ to use it.

## 🔧 Step-by-Step Configuration

### Step 1: Set Project SDK to Java 21

1. **Open Project Structure:**
   - Press `Ctrl+Alt+Shift+S`
   - Or: **File → Project Structure**

2. **Set Project SDK:**
   - Go to **"Project Settings"** → **"Project"** (left sidebar)
   - Look at **"Project SDK"** dropdown
   - Click the dropdown
   - If **Java 21** is listed → Select it
   - If **Java 21 is NOT listed** → Click **"Add SDK"** → **"JDK"**
     - Navigate to: `C:\Program Files\Java\jdk-21` (or wherever Java 21 is)
     - Or: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot`
     - Click **OK**

3. **Set Language Level:**
   - **"Project language level"** should auto-set to **21**
   - If not, select **21** from dropdown

4. **Click OK**

### Step 2: Set Module SDKs to Java 21

1. Still in **Project Structure** (`Ctrl+Alt+Shift+S`)
2. Go to **"Modules"** (left sidebar)
3. For **EACH module** (api-gateway, user-service, etc.):
   - Select the module
   - Look at **"Language level"** dropdown
   - Set it to **21**
   - Go to **"Dependencies"** tab
   - Check **"Module SDK"** - should be **Java 21**
4. Click **OK**

### Step 3: Configure Maven to Use Java 21

1. **File → Settings** (`Ctrl+Alt+S`)
2. **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. **JRE**: 
   - Select **"Use Project JDK"** (this uses Java 21)
   - OR select **Java 21** directly from dropdown
4. Click **OK**

### Step 4: Reload Maven Project

1. Right-click on root **`pom.xml`** (in project tree)
2. **Maven → Reload Project**
3. Wait for dependencies to download (check bottom right progress)

### Step 5: Invalidate Caches

1. **File → Invalidate Caches...**
2. Check **ALL boxes:**
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **"Invalidate and Restart"**
4. Wait for IntelliJ to restart
5. Wait for re-indexing (check bottom right)

### Step 6: Clean and Rebuild

1. **Build → Clean Project**
2. Wait for clean to complete
3. **Build → Rebuild Project** (`Ctrl+Shift+F9`)
4. Wait for rebuild (may take a few minutes)

## ✅ Verification Steps

### Check 1: Verify Project SDK

1. **File → Project Structure** (`Ctrl+Alt+Shift+S`)
2. **Project → Project SDK**: Should show **Java 21**
3. **Project → Project language level**: Should show **21**

### Check 2: Verify Maven Compilation

Open IntelliJ terminal (Alt+F12) and run:

```bash
mvn -version
```

**Expected output:**
```
Apache Maven 3.x.x
Maven home: ...
Java version: 21.0.6, vendor: ...
Java home: C:\Program Files\Java\jdk-21...
```

### Check 3: Try Compiling

```bash
mvn clean compile
```

**Should see:**
```
[INFO] BUILD SUCCESS
```

If you see errors, share them and I'll help fix.

### Check 4: Look for Run Button

1. Open: `api-gateway/src/main/java/com/linkedme/gateway/ApiGatewayApplication.java`
2. Look at **line 11** (the `main` method)
3. You should see a **green ▶️ play icon** in the left gutter
4. If you see it → Click it → **"Run 'ApiGatewayApplication.main()'"**

## 🎯 Quick Test

After all steps, try this:

1. **Open:** `ApiGatewayApplication.java`
2. **Right-click** on the file in project tree
3. **Run 'ApiGatewayApplication.main()'**

Or use keyboard shortcut:
- **Shift+F10** (Run)
- **Shift+F9** (Debug)

## 📋 Checklist

- [ ] Project SDK set to Java 21
- [ ] Project language level set to 21
- [ ] All modules set to Java 21
- [ ] Maven runner uses Java 21
- [ ] Maven project reloaded
- [ ] Caches invalidated
- [ ] Project rebuilt
- [ ] `mvn -version` shows Java 21
- [ ] Run button appears

## 🚨 If Still Having Issues

### Option 1: Find Java 21 Installation Path

If IntelliJ can't find Java 21:

1. **Check where Java 21 is installed:**
   ```bash
   where java
   ```

2. **Common locations:**
   - `C:\Program Files\Java\jdk-21`
   - `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot`
   - `C:\Program Files (x86)\Java\jdk-21`

3. **In IntelliJ:**
   - **Add SDK → JDK**
   - Navigate to the folder above
   - Select it

### Option 2: Use Command Line (Bypass IntelliJ)

If IntelliJ still has issues, run from terminal:

```bash
# Build project
mvn clean install -DskipTests

# Run API Gateway
cd api-gateway
mvn spring-boot:run
```

This will work even if IntelliJ has issues.

## 💡 What We've Done

1. ✅ Updated `pom.xml` to use Java 21
2. ✅ Configured IntelliJ to use Java 21
3. ✅ Set all modules to Java 21
4. ✅ Cleaned caches and rebuilt

## 🎉 Expected Result

After these steps:
- ✅ No more `ExceptionInInitializerError`
- ✅ No more `TypeTag UNKNOWN` error
- ✅ Green run button appears
- ✅ Project compiles successfully
- ✅ You can run the application

Try these steps and let me know:
1. Do you see the run button now?
2. Can you compile successfully?
3. Any remaining errors?
