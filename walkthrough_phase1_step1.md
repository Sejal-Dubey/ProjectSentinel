# Phase 1, Step 1: Project Initialization Walkthrough

## 1. Architecture Overview
We have set up a monorepo-style structure:
- **/backend**: Java Spring Boot application (The brain).
- **/frontend**: React + Vite application (The face).

## 2. Backend (Spring Boot)
We initialized a Spring Boot 3.2.2 app with Java 21.

### Key Components:
- **`SentinelApplication.java`**: The entry point. It bootstraps the Spring context.
- **`InstagramServiceClient.java`**: Our first service.
    - *Concept*: A **Service** contains business logic. Here, it acts as the bridge to the outside world (Instagram).
    - *Code Highlight*:
      ```java
      @Service
      public class InstagramServiceClient {
          public List<Map<String, Object>> getRecentPosts(String accountId) {
              // Logic to get posts
          }
      }
      ```
    - *Why*: We isolate API calls here so the rest of the app doesn't care *how* we get data, just that we *get* it.

## 3. Frontend (React + Vite + Tailwind)
We created a modern React app.

### Key Concepts:
- **Vite**: A build tool that is much faster than the old Webpack. It serves your code instantly.
- **Tailwind CSS**: A utility-first CSS framework. Instead of writing separate `.css` files, we use classes like `flex`, `p-4`, `text-red-500` directly in HTML.
    - *Setup*: Configured in `tailwind.config.js` and injected via `index.css`.

## 4. Verification
- **Backend Setup**: `pom.xml` contains all dependencies (Web, Data JPA, Postgres, Lombok, LangChain4j).
- **Frontend Setup**: `npm install` (in progress) sets up React and Tailwind.

## Next Steps
We will connect the database and define the data shape (Entities).
