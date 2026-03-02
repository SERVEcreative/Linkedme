# GitHub Repository Setup Guide

## Initializing the Repository

### 1. Create GitHub Repository

1. Go to [GitHub](https://github.com)
2. Click "New repository"
3. Repository name: `linkedme` (or your preferred name)
4. Description: "LinkedMe - Professional Networking Platform built with Spring Boot microservices"
5. Choose Public or Private
6. **DO NOT** initialize with README, .gitignore, or license (we already have these)
7. Click "Create repository"

### 2. Initialize Local Git Repository

```bash
# Navigate to project root
cd linkedme

# Initialize git repository
git init

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit: LinkedMe platform project structure"

# Add remote repository (replace with your GitHub URL)
git remote add origin https://github.com/YOUR_USERNAME/linkedme.git

# Push to GitHub
git branch -M main
git push -u origin main
```

### 3. Create Development Branch

```bash
# Create and switch to development branch
git checkout -b develop

# Push development branch
git push -u origin develop
```

## Repository Structure on GitHub

Your repository should have:

```
linkedme/
├── .gitignore
├── LICENSE
├── README.md
├── HLD.md
├── SETUP.md
├── CONTRIBUTING.md
├── PROJECT_STRUCTURE.md
├── pom.xml
├── api-gateway/
├── user-service/
├── profile-service/
├── post-service/
├── connection-service/
├── feed-service/
├── recommendation-service/
├── job-service/
├── notification-service/
├── search-service/
├── analytics-service/
├── docker/
├── kubernetes/
└── scripts/
```

## GitHub Repository Settings

### 1. Add Topics

Add these topics to your repository:
- `spring-boot`
- `microservices`
- `kafka`
- `kubernetes`
- `docker`
- `java`
- `linkedme`
- `professional-networking`
- `rest-api`
- `jwt-authentication`

### 2. Add Description

```
A scalable professional networking platform built with Spring Boot microservices, 
Kafka Streams for real-time feed generation, and full observability stack (Zipkin, ELK, Prometheus).
```

### 3. Enable GitHub Actions (Optional)

Create `.github/workflows/ci.yml` for CI/CD:

```yaml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean install -DskipTests
    
    - name: Run Tests
      run: mvn test
```

## Badges to Add to README

Add these badges at the top of your README.md:

```markdown
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Kafka](https://img.shields.io/badge/Kafka-Streams-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)
```

## Commit Message Guidelines

Follow conventional commits:

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes
- `refactor:` - Code refactoring
- `test:` - Adding tests
- `chore:` - Build process or auxiliary tool changes

Examples:
```bash
git commit -m "feat: add user authentication with JWT"
git commit -m "fix: resolve connection service database connection issue"
git commit -m "docs: update API documentation"
```

## Branch Strategy

- `main` - Production-ready code
- `develop` - Development branch
- `feature/*` - Feature branches
- `bugfix/*` - Bug fix branches
- `hotfix/*` - Hotfix branches

## Pull Request Template

Create `.github/pull_request_template.md`:

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
```

## Issues Template

Create `.github/ISSUE_TEMPLATE/bug_report.md`:

```markdown
## Bug Description
Clear description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- Java Version:
- Spring Boot Version:
- Service:
- OS:
```

## Next Steps

1. ✅ Initialize repository
2. ✅ Push initial code
3. ✅ Add topics and description
4. ✅ Set up branch protection (optional)
5. ✅ Add CI/CD pipeline (optional)
6. ✅ Start implementing features

## Useful Git Commands

```bash
# Check status
git status

# Add files
git add .
git add <file>

# Commit
git commit -m "message"

# Push
git push origin <branch>

# Create and switch branch
git checkout -b feature/new-feature

# Merge branch
git checkout main
git merge feature/new-feature

# View commit history
git log --oneline

# View differences
git diff
```
