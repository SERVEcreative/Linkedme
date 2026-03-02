#!/bin/bash

# Script to initialize Git repository and push to GitHub

echo "🚀 LinkedMe Platform - Git Initialization"
echo "=========================================="
echo ""

# Check if git is initialized
if [ -d ".git" ]; then
    echo "⚠️  Git repository already initialized!"
    read -p "Do you want to continue? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Initialize git if not already done
if [ ! -d ".git" ]; then
    echo "📦 Initializing Git repository..."
    git init
    echo "✅ Git repository initialized"
fi

# Add all files
echo "📝 Adding files to Git..."
git add .

# Create initial commit
echo "💾 Creating initial commit..."
git commit -m "Initial commit: LinkedMe platform project structure

- Complete microservices architecture
- Docker Compose setup
- API Gateway configuration
- User Service with Spring Security
- Documentation and setup guides"

# Ask for GitHub repository URL
echo ""
echo "📤 Ready to push to GitHub!"
echo ""
read -p "Enter your GitHub repository URL (or press Enter to skip): " GITHUB_URL

if [ -z "$GITHUB_URL" ]; then
    echo "⏭️  Skipping remote setup. You can add it later with:"
    echo "   git remote add origin <your-github-url>"
    echo "   git push -u origin main"
else
    # Check if remote already exists
    if git remote | grep -q "origin"; then
        echo "⚠️  Remote 'origin' already exists!"
        read -p "Do you want to update it? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            git remote set-url origin "$GITHUB_URL"
        fi
    else
        git remote add origin "$GITHUB_URL"
    fi
    
    # Set main branch
    git branch -M main
    
    # Push to GitHub
    echo "🚀 Pushing to GitHub..."
    git push -u origin main
    
    if [ $? -eq 0 ]; then
        echo "✅ Successfully pushed to GitHub!"
    else
        echo "❌ Failed to push to GitHub. Please check your credentials and try again."
    fi
fi

echo ""
echo "✨ Git initialization complete!"
echo ""
echo "Next steps:"
echo "1. Review the code structure"
echo "2. Follow SETUP.md to start the services"
echo "3. Start implementing features"
echo ""
echo "Useful commands:"
echo "  git status              - Check repository status"
echo "  git log                 - View commit history"
echo "  git branch -a           - List all branches"
echo "  git checkout -b develop - Create development branch"
