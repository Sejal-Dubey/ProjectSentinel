# Project Sentinel Implementation Plan

## Goal
Build a Java (Spring Boot) backend and React frontend that simulates ad performance based on real Instagram sentiment using the Monte Carlo Method.

## Tech Stack
- **Backend**: Java 21+, Spring Boot 3.x, LangChain4j, PostgreSQL
- **Frontend**: React (Vite), Tailwind CSS
- **Design**: Octalysis Framework (Gamification), Modern Aesthetics

## Phase 1: Ingestion & Foundation
1.  **Project Initialization**: Set up Spring Boot and React projects.
2.  **Instagram Integration**: Implement `InstagramServiceClient` to fetch data (mock or real Graph API).
3.  **Data Cleaning**: Integrate LangChain4j for sentiment analysis and data cleaning.
4.  **Database Setup**: Configure PostgreSQL and JPA entities for storing raw and processed data.

## Phase 2: Agents (MAS)
1.  **Architecture Setup**: Define Agent interfaces.
2.  **Sentiment Analyst**: detailed sentiment auditing.
3.  **Audience Architect**: profiling target audiences.
4.  **Creative Scorer**: scoring ad creatives.

## Phase 3: Math Engine
1.  **Monte Carlo Service**: Implement simulation logic (10,000 trials).
2.  **Prediction Model**: Calculate CTR based on sentiment and creative scores.

## Phase 4: Gamification & UI
1.  **Dashboard**: React-based dashboard with Octalysis design.
2.  **Visualization**: Charts for simulation results.
3.  **Gamification**: Badges, Quests, Budget Tracking.
