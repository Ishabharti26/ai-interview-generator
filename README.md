# 🤖 TalentIQ — AI Interview Question Generator

A modern, production-grade AI interview preparation platform that generates structured, role-specific technical and HR interview rounds with comprehensive model answers.

---

## 🛠️ Tech Stack

- **Java 21 & Spring Boot 3**
- **Google Gemini AI**
- **HTML5, CSS3 & JavaScript**
- **Maven & Docker**

---

## ✨ Features

- **5 Structured Interview Rounds**: Generates authentic rounds in chronological order:
  1. *Opening Questions* (HR & Background)
  2. *Technical Core* (Java & OOP Concepts)
  3. *Database & Architecture* (DBMS & SQL)
  4. *Problem Solving* (Data Structures & Algorithms)
  5. *Project Discussion* (Frameworks & Hands-on Projects)
- **Comprehensive Model Answers**: Each question comes with an expandable model answer for quick self-review.
- **Master Revision Library**: 200+ curated technical questions across Java, OOP, SQL, Spring Boot, DSA, and System Design.
- **Zero-Downtime Reliability**: Built-in curated question bank ensures instant questions even when external API quotas are reached.
- **One-Click Export**: Easily copy formatted questions and answers to clipboard for mock interview sessions.

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+ (optional, or use the pre-built JAR)

### 1. Clone the Repository
```bash
git clone https://github.com/Ishabharti26/ai-interview-generator.git
cd ai-interview-generator
```

### 2. Configure API Key (Optional)
Set your Gemini API key in `src/main/resources/application.properties` or via environment variable:
```bash
export GEMINI_API_KEY="your-gemini-api-key"
```
*(If no API key is provided, the platform automatically serves from the built-in Master Question Bank.)*

### 3. Run the Application

**Option 1: Run with Maven**
```bash
mvn spring-boot:run
```

**Option 2: Run the Standalone JAR**
```bash
java -jar target/ai-interview-generator-1.0.0.jar
```

**Option 3: Run with Docker**
```bash
docker build -t ai-interview-generator .
docker run -p 8080:8080 ai-interview-generator
```

Once running, open your browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 📡 REST API

### Generate Interview Questions
- **Endpoint**: `POST /api/v1/questions/generate`
- **Request Body**:
```json
{
  "jobTitle": "Java Backend Developer",
  "level": "Junior",
  "type": "Technical",
  "language": "English"
}
```

### Get Revision Topics
- **Endpoint**: `GET /api/v1/questions/bank/topics`

### Get Topic Questions
- **Endpoint**: `GET /api/v1/questions/bank/topic/{topicName}`

---

## 👩‍💻 Author

Created by **[Isha Bharti](https://github.com/Ishabharti26)**
