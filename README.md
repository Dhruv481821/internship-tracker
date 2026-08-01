# Internship Application Tracker

A full-stack web app to track internship/job applications — company, role, 
status, deadlines, and notes — instead of a scattered notes file. Built as 
a personal tool while applying to Software Developer internships.

## Features
- Add, update, and delete applications
- Track status through a pipeline: Applied → OA/Test → Interview → Offer / Rejected / Ghosted
- Live stats: total applications, interview rate, offer rate
- Automatic highlighting for applications with an overdue follow-up date

## Tech stack
- **Frontend:** HTML, CSS, vanilla JavaScript (fetch API)
- **Backend:** Java (built-in `com.sun.net.httpserver`, no framework) — a hand-rolled REST API
- **Database:** MySQL (JDBC via MySQL Connector/J)

## Architecture
Browser (HTML/CSS/JS) → REST API (Java) → MySQL

## Setup

### 1. Database
Run this in MySQL (Workbench or CLI):
\`\`\`sql
CREATE DATABASE IF NOT EXISTS internship_tracker;
USE internship_tracker;

CREATE TABLE applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL,
    status ENUM('Applied', 'OA/Test', 'Interview', 'Offer', 'Rejected', 'Ghosted') DEFAULT 'Applied',
    applied_date DATE NOT NULL,
    follow_up_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
\`\`\`

### 2. JDBC driver
Download [MySQL Connector/J 9.7.0](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.7.0/mysql-connector-j-9.7.0.jar) 
and place it in a `lib/` folder in the project root.

### 3. Set your database password as an environment variable
\`\`\`powershell
$env:DB_PASSWORD = "your_mysql_password"
\`\`\`

### 4. Compile and run the backend
\`\`\`powershell
javac -cp "lib\mysql-connector-j-9.7.0.jar" -d out src\Application.java src\Database.java src\Json.java src\Server.java
java -cp "out;lib\mysql-connector-j-9.7.0.jar" Server
\`\`\`

### 5. Open the frontend
Open `public/index.html` in a browser (or use VS Code's Live Server extension).

## Author
Dhruv Sharma — [GitHub](https://github.com/Dhruv481821) · [LinkedIn](https://www.linkedin.com/in/dhurv-sharma-sd/)