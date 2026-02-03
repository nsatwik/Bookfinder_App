# Bookfinder (React + Spring Boot + MySQL)

Bookfinder is a clean, industry-style login experience with a modern library view backed by a Spring Boot API and a local MySQL database. It includes demo users, an admin entity, and seeded books.

## Local Setup

### 1) Create the MySQL database
Use your local MySQL install (no Docker required). Example:

```bash
mysql -u root -p
```

```sql
CREATE DATABASE bookfinder;
```

By default the backend uses:
- DB name: `bookfinder`
- Username: `root`
- Password: `root`
- Port: `3306`

You can override these via environment variables.

### 2) Run the backend
```bash
cd backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`.

### 3) Run the frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

## Environment Variables
Backend (`backend/src/main/resources/application.yml`):
- `DB_URL` (default `jdbc:mysql://localhost:3306/bookfinder`)
- `DB_USERNAME` (default `root`)
- `DB_PASSWORD` (default `root`)
- `FRONTEND_ORIGIN` (default `http://localhost:5173`)

Frontend (`frontend/.env.example`):
- `VITE_API_BASE_URL` (default `http://localhost:8080`)

## Demo Accounts
- Admin: `admin` or `admin@bookfinder.com` / `Admin@123`
- User: `reader` or `reader@bookfinder.com` / `User@123`

## API Endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/forgot`
- `GET /api/books`
- `GET /api/health`

## Next Step (RDS)
When you migrate to AWS RDS, update `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to match your RDS instance.
