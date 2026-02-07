
# 🚀 Project Sentinel: AI Marketing Simulator

Project Sentinel is a full-stack AI-powered marketing strategy simulation platform. It uses real-time Monte Carlo simulations to predict campaign performance (CTR) based on Audience Sentiment (scraped/analyzed) and Creative Scoring (visual analysis).

## 🛠️ Tech Stack
- **Backend:** Java 17 (Spring Boot 3.2.0), Spring AI (Groq Integration).
- **Frontend:** React + Vite + TailwindCSS.
- **AI/LLM:** Groq API (Llama3-8b-8192).
- **Visuals:** OpenSource ColorThief (Java port).
- **Simulation:** Monte Carlo Method (10,000 trials).

## 📦 Prerequisites

Your friend needs these installed:
1.  **Java JDK 17+** (Ensure `JAVA_HOME` is set).
2.  **Node.js 18+** & **npm**.
3.  **PowerShell** (Standard on Windows).

## 🔑 Environment Variables

The project requires an `.env` file in the `backend/` directory for API keys.
Create a file named `.env` inside `backend/` with the following content:

```env
# Groq API Key (Sign up at console.groq.com)
GROQ_API_KEY=your_groq_key_here

# SerpApi Key (Optional, for Google Search)
SERPAPI_API_KEY=your_serpapi_key_here

# OpenAI Key (If you switch providers)
OPENAI_API_KEY=sk-...
```

## 🚀 One-Click Setup (Windows)

We have provided a unified PowerShell script to install dependencies and run both servers.

1.  Open PowerShell as Administrator (recommended) or ensure script execution is enabled (`Set-ExecutionPolicy RemoteSigned`).
2.  Run the setup script from the root directory:

```powershell
./setup_and_run.ps1
```

Or manually:

### Backend
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
# Server runs at http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Dashboard runs at http://localhost:5173
```

## 🎮 How to Use
1.  **Dashboard:** Open `http://localhost:5173`.
2.  **Hashtag Analysis:** Enter a hashtag (e.g., `#summer`) to fetch live sentiment.
3.  **Creative Analysis:** Paste an image URL to get a Visual Score.
4.  **Simulation:** The Monte Carlo engine will run automatically.
5.  **AI Mentor:** Look for the "AI Strategy Mentor" card for tactical advice.

## 🤝 Contribution
If you encounter `Environment Error`, ensure your `.env` file is correctly placed in `backend/` and contains valid API keys.
