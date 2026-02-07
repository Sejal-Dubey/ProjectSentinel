# Running Project Sentinel

## Prerequisites
- **Node.js**: Installed.
- **Java**: Installed.
- **Maven**: **REQUIRED** for Backend (but missing).

## Quick Start (Demo Mode)
Since Maven is not available to build the backend, I have enabled **Demo Mode** in the frontend. It will simulate the math locally if it cannot connect to the server.

### 1. Run Frontend
1. Open terminal in `frontend` directory.
2. Run: 
   ```bash
   npm run dev
   ```
3. Open the link (usually http://localhost:5173).

## How to Enable Full Backend
To run the actual Spring Boot backend (with H2 Database), you must install Maven:
1. Download Apache Maven (https://maven.apache.org/download.cgi).
2. Add `bin` folder to your PATH.
3. Run:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
