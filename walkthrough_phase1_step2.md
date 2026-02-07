# Phase 1, Step 2: Database & Entity Configuration Walkthrough

## 1. The Data Model (`InstagramPost`)
We defined the blueprint for how we store Instagram posts in our PostgreSQL database.

### Key Fields:
- **`id`**: The unique ID from Instagram.
- **`caption`**: The text content of the post.
- **`sentimentScore`**: A `Double` ranging from `-1.0` (Negative) to `1.0` (Positive).
- **`creativeScore`**: A `Double` ranging from `0.0` (Bad) to `1.0` (Perfect) to rate the visual quality.

### Why JPA?
We use **JPA (Java Persistence API)** annotations:
- `@Entity`: Tells Spring "This class represents a database table".
- `@Id`: Primary key.
- `@Column`: Maps variable names to specific database columns (e.g., `creativeScore` -> `creative_score`).

## 2. The Repository (`InstagramPostRepository`)
We created an interface extending `JpaRepository`.
- **Magic**: We don't write SQL. Spring generates queries like `SELECT * FROM instagram_posts` automatically.
- **Custom Methods**:
    - `findTop10ByOrderByLikesCountDesc()`: Fetches the viral posts.

## 3. Infrastructure (`docker-compose.yml`)
We added a Docker configuration to run PostgreSQL.
- **Service**: `postgres:16`
- **Port**: `5432`
- **Database**: `sentinel_db`

## Verification
- Run `docker-compose up -d` to start the database.
- The Spring Boot app will automatically create the tables on startup (`ddl-auto=update`).
