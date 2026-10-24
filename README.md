# 🤖 AI Interview Question Generator & Master Revision Bank

A production-ready full-stack AI interview question generator built with **Java 21**, **Spring Boot 3.3.5**, and **Google Gemini API**, featuring a zero-token **Smart Offline Question Bank** (200+ curated questions & model answers).

---

## 🌟 Key Highlights & Architecture

1. **Dual-Engine Generation**:
   - **Gemini 1.5 Flash AI Engine**: Crafts dynamic role-tailored interview questions and model answers on demand.
   - **Smart Offline Cache / Curated Master Bank**: Over 200 high-yield questions covering HR, Java Core, OOP, Collections, SQL, Spring Boot, REST APIs, DSA, Networks, OS, Cloud, and specific resume projects.
2. **Zero-Token Protection & Seamless Fallback**:
   - If the Gemini API key runs out of free-tier tokens or encounters an error, the system automatically falls back to curated questions matching the job title and role. **The user is never blocked and never faces an error screen.**
   - Users can also explicitly select **"Zero-Token Bank (Instant)"** mode to practice without consuming any API tokens.
3. **Interactive UI (Matching Recooty Reference)**:
   - Expandable model answers per question or globally with "SHOW ALL ANSWERS" switch.
   - One-click copy formatted questions & answers to clipboard.
   - Interactive **Master Revision Checklist** browser to practice by topic.

---

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.3.5, Spring Security, Spring WebFlux (`WebClient`), Jackson, Bean Validation
- **AI Integration**: Google Gemini 1.5 Flash REST API (`generateContent`)
- **Frontend**: Responsive SPA (Semantic HTML5, CSS3 Variables, Vanilla ES6 JavaScript)
- **Caching & Resilience**: Smart in-memory curated bank with keyword-based role routing and auto-fallback

---

## 📂 Project Structure

```
Ai_question/
├── pom.xml
├── target/ai-interview-generator-1.0.0.jar   ← Production-ready standalone JAR
└── src/
    ├── main/
    │   ├── java/com/isha/aiinterview/
    │   │   ├── AiInterviewApplication.java  ← Application entry point
    │   │   ├── cache/
    │   │   │   ├── CachedQuestion.java      ← Cache entity
    │   │   │   ├── CachedCategory.java      ← Grouped questions
    │   │   │   └── QuestionBankService.java ← 200+ master interview questions & answers
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java      ← Spring Security (CORS + API permits)
    │   │   │   └── WebClientConfig.java     ← Reactive WebClient for Gemini API
    │   │   ├── controller/
    │   │   │   ├── HomeController.java      ← Serves SPA at /
    │   │   │   └── InterviewController.java ← REST APIs (/api/v1/questions/*)
    │   │   ├── dto/
    │   │   │   ├── QuestionRequest.java     ← Request with mode selection
    │   │   │   └── QuestionResponse.java    ← Standardized response with source & fallback
    │   │   └── service/
    │   │       └── GeminiService.java       ← Gemini API caller + seamless cache fallback
    │   └── resources/
    │       ├── application.properties       ← Gemini API key & server configuration
    │       └── static/
    │           ├── index.html               ← Full interactive UI
    │           ├── css/style.css            ← Modern responsive styles & badges
    │           └── js/app.js                ← SPA logic, toggles, clipboard & topic browser
    └── test/
        └── java/com/isha/aiinterview/
            └── AiInterviewApplicationTests.java
```

---

## 🚀 How to Run

The application is already packaged and can be run with a single command:

### Option 1: Run the Pre-Built JAR
```bash
java -jar /Users/akashmani/Documents/Programming/Ai_question/target/ai-interview-generator-1.0.0.jar
```

### Option 2: Run via Maven
```bash
cd /Users/akashmani/Documents/Programming/Ai_question
mvn spring-boot:run
```

Once started, open your browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 📡 REST API Reference

### 1. Generate Interview Questions
- **Endpoint**: `POST /api/v1/questions/generate`
- **Request Body**:
```json
{
  "jobTitle": "Java Developer",
  "level": "junior",
  "type": "technical",
  "mode": "auto",
  "language": "English"
}
```
*Modes:*
- `"auto"`: Tries Gemini AI; if quota or network fails, automatically falls back to curated cache.
- `"cache"`: Directly serves verified curated questions (0 AI tokens used).
- `"ai"`: Calls Gemini AI directly.

### 2. Get All Master Checklist Topics
- **Endpoint**: `GET /api/v1/questions/bank/topics`
- **Response**: Array of all 18 topics in the revision bank.

### 3. Get Questions for a Specific Topic
- **Endpoint**: `GET /api/v1/questions/bank/topic/{topicName}`
- **Example**: `GET /api/v1/questions/bank/topic/Spring%20Boot`

### 4. Health Check
- **Endpoint**: `GET /api/v1/questions/health`

---

## 💼 How to Present This on Your Resume & in Interviews

### Resume Bullet Points:
- **AI Interview Question Generator (Java, Spring Boot, Google Gemini API)**
  - Designed and deployed a full-stack AI interview preparation platform using **Java 21**, **Spring Boot 3**, and **Google Gemini 1.5 Flash**.
  - Built resilient fallback architecture with a zero-token **in-memory question cache (200+ questions)** that prevents user disruption during API quota limits.
  - Implemented asynchronous REST communication via Spring WebFlux `WebClient` with JSON schema enforcement and prompt engineering.
  - Developed an intuitive web interface with role-tailored categories, per-question accordion answers, and one-click clipboard export.
