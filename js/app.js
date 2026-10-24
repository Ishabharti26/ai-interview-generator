/* ─────────────────────────────────────────────────────────────────────────────
   TalentIQ — AI Interview Question Generator
   Production Client Logic with Universal Cloud Fallback (Vercel / Render / Local)
   ───────────────────────────────────────────────────────────────────────────── */

const API_BASE = '/api/v1/questions';

// ── DOM Elements ──────────────────────────────────────────────────────────────
const form             = document.getElementById('questionForm');
const generateBtn      = document.getElementById('generateBtn');
const loadingState     = document.getElementById('loadingState');
const errorState       = document.getElementById('errorState');
const errorMessage     = document.getElementById('errorMessage');
const resultsSection   = document.getElementById('resultsSection');
const categoriesCont   = document.getElementById('categoriesContainer');
const showAllToggle    = document.getElementById('showAllAnswers');
const copyBtn          = document.getElementById('copyBtn');

// Topic Library DOM Elements
const topicPillsCont   = document.getElementById('topicPills');
const topicViewer      = document.getElementById('topicViewer');
const currentTopicTitle= document.getElementById('currentTopicTitle');
const currentTopicCount= document.getElementById('currentTopicCount');
const topicQuestionsList= document.getElementById('topicQuestionsList');
const toggleTopicAns   = document.getElementById('toggleTopicAnswers');

// ── State ─────────────────────────────────────────────────────────────────────
let currentResponse = null;

// ── Client-Side Master Bank (For Static Cloud Hosting like Vercel) ────────────
const CLIENT_TOPIC_BANK = {
  "Introduction & HR": [
    { number: 1, question: "Tell me about yourself.", answer: "I'm a Computer Science graduate specializing in Java and Spring Boot backend engineering. I have built full-stack applications including a Student Placement Tracker and an AI Interview Platform. I focus on writing clean, scalable code and solving real-world architecture problems." },
    { number: 2, question: "Walk me through your resume and key technical projects.", answer: "My resume highlights hands-on experience with Java 21, Spring Boot REST APIs, MySQL, and cloud tools. My primary project is an enterprise-grade Student Placement Tracker with full CRUD operations and secure JWT authentication." },
    { number: 3, question: "Why did you choose Java as your primary programming language?", answer: "Java's platform independence, rich ecosystem, type safety, and strong object-oriented principles make it the standard for high-performance enterprise systems." },
    { number: 4, question: "What are your greatest strengths and weaknesses?", answer: "My strength is breaking down complex problems methodically and debugging systematically. My weakness is occasionally spending too much time optimizing early; I now balance perfectionism with rapid iteration." },
    { number: 5, question: "Where do you see yourself in 3 years?", answer: "I see myself taking ownership of core backend modules, mastering distributed microservices, and contributing to high-throughput system design." }
  ],
  "Core Java & OOP": [
    { number: 1, question: "Explain the four pillars of Object-Oriented Programming with real examples.", answer: "Encapsulation: hiding internal state (private variables with getters/setters). Inheritance: code reuse via superclasses. Polymorphism: method overloading and overriding. Abstraction: exposing essential interfaces while hiding implementation complexity." },
    { number: 2, question: "What is the difference between == and .equals() in Java?", answer: "== compares memory references for objects. .equals() compares the logical content of objects. Two different String objects with the same content return false for == but true for .equals()." },
    { number: 3, question: "Why are Strings immutable in Java?", answer: "Immutability allows string caching in the String Pool (saving heap memory), guarantees thread safety without synchronization, and ensures security when used as HashMap keys or network connections." },
    { number: 4, question: "What is the difference between an Abstract Class and an Interface?", answer: "An abstract class can maintain state (instance fields), constructors, and partial implementations (single inheritance). An interface is a pure contract allowing multiple inheritance (Java 8+ allows default/static methods)." },
    { number: 5, question: "Explain Exception Handling in Java (checked vs unchecked exceptions).", answer: "Checked exceptions (e.g. IOException, SQLException) are verified at compile-time and must be caught or declared. Unchecked exceptions (subclasses of RuntimeException like NullPointerException) occur at runtime." }
  ],
  "DBMS & SQL Fundamentals": [
    { number: 1, question: "Explain ACID properties with a banking transaction example.", answer: "Atomicity: debit and credit both succeed or both fail. Consistency: total money remains valid according to business rules. Isolation: concurrent transactions do not read partial states. Durability: committed updates persist even after power failure." },
    { number: 2, question: "What is the difference between Primary Key, Foreign Key, and Unique Key?", answer: "Primary Key uniquely identifies a row (not null, unique). Foreign Key references a primary key in another table enforcing referential integrity. Unique Key ensures distinct values but permits one NULL entry." },
    { number: 3, question: "What is Normalization? Explain 1NF, 2NF, and 3NF.", answer: "1NF requires atomic values and no repeating groups. 2NF removes partial functional dependencies on composite keys. 3NF removes transitive dependencies (non-key attributes depending on other non-key attributes)." },
    { number: 4, question: "Write a SQL query to find the second-highest salary in an employee table.", answer: "SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) FROM employees); Or using pagination: SELECT DISTINCT salary FROM employees ORDER BY salary DESC LIMIT 1 OFFSET 1;" },
    { number: 5, question: "What is the difference between DELETE, TRUNCATE, and DROP?", answer: "DELETE removes rows matching a condition and can be rolled back. TRUNCATE removes all rows quickly by deallocating pages (DDL, no rollback in most DBs). DROP deletes both table structure and data entirely." }
  ],
  "Data Structures & Problem Solving": [
    { number: 1, question: "What is the difference between an Array and a Linked List in memory?", answer: "Arrays allocate contiguous memory blocks allowing O(1) random access by index. Linked Lists use heap-allocated nodes with pointers, offering O(1) insertion/deletion at known references but O(n) access." },
    { number: 2, question: "How do you find the second largest element in an array in O(n) time?", answer: "Maintain two variables (max and secondMax) initialized to minimum value. In a single pass: if current > max, update secondMax = max, max = current; else if current > secondMax and current != max, update secondMax = current." },
    { number: 3, question: "How do you check whether a string is a palindrome using two pointers?", answer: "Place left pointer at 0 and right pointer at length - 1. While left < right, compare characters at left and right. If any mismatch, return false; otherwise increment left and decrement right." },
    { number: 4, question: "How does a HashMap work internally, and how does it handle hash collisions?", answer: "HashMap uses an array of buckets. The key's hashCode determines bucket index. Collisions are resolved using separate chaining with linked lists, which convert to balanced Red-Black trees when bucket size exceeds 8 in Java 8+." },
    { number: 5, question: "Explain Kadane's algorithm for finding the Maximum Subarray Sum.", answer: "Kadane's algorithm tracks maxEndingHere and maxSoFar in a single pass O(n). At each index, maxEndingHere = Math.max(num, maxEndingHere + num), and maxSoFar = Math.max(maxSoFar, maxEndingHere)." }
  ],
  "Project & Backend Architecture": [
    { number: 1, question: "Explain the architecture of your primary backend project.", answer: "Three-tier architecture: Controller layer exposes REST API endpoints with request validation; Service layer implements business rules and orchestration; Repository layer leverages Spring Data JPA and Hibernate for MySQL persistence." },
    { number: 2, question: "Why did you choose Spring Boot? What are @RestController, @Service, and @Repository?", answer: "Spring Boot eliminates XML configuration through auto-configuration and embedded Tomcat. @RestController serializes responses to JSON. @Service holds domain logic. @Repository provides exception translation for database errors." },
    { number: 3, question: "Explain HTTP methods: GET, POST, PUT, PATCH, and DELETE.", answer: "GET retrieves resources (idempotent, safe). POST creates new entities (non-idempotent). PUT replaces entire resource (idempotent). PATCH applies partial updates. DELETE removes resources (idempotent)." },
    { number: 4, question: "How do you validate incoming REST API requests?", answer: "Annotate DTO fields with Jakarta validation annotations (@NotBlank, @Size, @Email, @Min). In controller methods, add @Valid. Handle MethodArgumentNotValidException globally via @RestControllerAdvice." },
    { number: 5, question: "How would you prevent duplicate applications or entries in a database?", answer: "Apply a composite UNIQUE constraint at database level (e.g., student_id + company_id). In the service layer, check existence before inserting and catch DataIntegrityViolationException." }
  ]
};

// ── Initialize ────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  loadMasterTopics();
});

// ── Master Answer Toggle ──────────────────────────────────────────────────────
showAllToggle.addEventListener('change', () => {
  const show = showAllToggle.checked;
  categoriesCont.querySelectorAll('.answer-box').forEach(box => {
    box.classList.toggle('visible', show);
  });
  categoriesCont.querySelectorAll('.question-info-btn').forEach(btn => {
    btn.classList.toggle('open', show);
  });
  categoriesCont.querySelectorAll('.cat-toggle-input').forEach(t => {
    t.checked = show;
  });
});

// Topic viewer global answer toggle
toggleTopicAns.addEventListener('change', () => {
  const show = toggleTopicAns.checked;
  topicQuestionsList.querySelectorAll('.answer-box').forEach(b => b.classList.toggle('visible', show));
  topicQuestionsList.querySelectorAll('.question-info-btn').forEach(btn => btn.classList.toggle('open', show));
});

// ── Form Submit ───────────────────────────────────────────────────────────────
form.addEventListener('submit', async (e) => {
  e.preventDefault();
  if (!validateForm()) return;
  await generate();
});

function validateForm() {
  const jobTitle = document.getElementById('jobTitle').value.trim();
  const errEl    = document.getElementById('jobTitleError');
  const input    = document.getElementById('jobTitle');

  if (!jobTitle) {
    errEl.textContent = 'Please enter a job title.';
    input.classList.add('error');
    input.focus();
    return false;
  }
  errEl.textContent = '';
  input.classList.remove('error');
  return true;
}

document.getElementById('jobTitle').addEventListener('input', () => {
  document.getElementById('jobTitleError').textContent = '';
  document.getElementById('jobTitle').classList.remove('error');
});

// ── Generate ──────────────────────────────────────────────────────────────────
async function generate() {
  const payload = {
    jobTitle: document.getElementById('jobTitle').value.trim(),
    level:    document.getElementById('level').value,
    type:     document.getElementById('type').value,
    language: document.getElementById('language').value,
  };

  setLoadingState(true);
  hideError();
  hideResults();

  try {
    const response = await fetch(`${API_BASE}/generate`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(payload),
    });

    const contentType = response.headers.get('content-type') || '';
    if (!response.ok || !contentType.includes('application/json')) {
      // If deployed on a static cloud provider (e.g. Vercel) where Java backend isn't running,
      // seamlessly serve the verified master bank!
      return serveClientGenerated(payload);
    }

    currentResponse = await response.json();
    renderResults(currentResponse);

  } catch (err) {
    // Graceful offline / static cloud fallback
    serveClientGenerated(payload);
  } finally {
    setLoadingState(false);
  }
}

function serveClientGenerated(payload) {
  const categories = Object.keys(CLIENT_TOPIC_BANK).map(catName => ({
    categoryName: catName,
    questions: CLIENT_TOPIC_BANK[catName]
  }));

  currentResponse = {
    jobTitle: payload.jobTitle,
    level: payload.level,
    type: payload.type,
    language: payload.language,
    categories: categories
  };

  renderResults(currentResponse);
}

function regenerate() {
  if (!validateForm()) return;
  generate();
}

function retryGenerate() {
  hideError();
  generate();
}

// ── UI States ─────────────────────────────────────────────────────────────────
function setLoadingState(loading) {
  generateBtn.disabled = loading;
  if (loading) {
    generateBtn.innerHTML = '<span class="btn-icon">⚡</span><span>Generating...</span>';
  } else {
    generateBtn.innerHTML = '<span class="btn-icon">⚡</span><span>Generate</span>';
  }
  loadingState.classList.toggle('hidden', !loading);
  if (loading) {
    document.getElementById('loadingState').scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
}

function showError(msg) {
  errorMessage.textContent = msg;
  errorState.classList.remove('hidden');
}
function hideError() {
  errorState.classList.add('hidden');
}

function hideResults() {
  resultsSection.classList.add('hidden');
  categoriesCont.innerHTML = '';
}

// ── Render Results ────────────────────────────────────────────────────────────
function renderResults(data) {
  showAllToggle.checked = false;
  categoriesCont.innerHTML = '';

  const cleanCategories = [
    'Opening Questions',
    'Technical Questions',
    'Database & Architecture Questions',
    'Problem Solving & DSA Questions',
    'Project Discussion Questions'
  ];

  data.categories.forEach((cat, catIdx) => {
    const block = document.createElement('div');
    block.className = 'category-block';

    const catToggleId = `catToggle-${catIdx}`;
    const displayName = cleanCategories[catIdx] || cleanCategoryName(cat.categoryName);

    block.innerHTML = `
      <div class="category-header">
        <h3 class="category-name">${escHtml(displayName)}</h3>
        <label class="toggle-label" for="${catToggleId}">
          <span class="toggle-text">SHOW ANSWERS</span>
          <div class="toggle-switch">
            <input type="checkbox" id="${catToggleId}" class="cat-toggle-input" data-cat="${catIdx}" />
            <span class="toggle-slider"></span>
          </div>
        </label>
      </div>
      <div class="question-list" id="qList-${catIdx}"></div>
    `;

    categoriesCont.appendChild(block);

    const qList = block.querySelector(`#qList-${catIdx}`);
    if (cat.questions) {
      cat.questions.forEach((qa, qIdx) => {
        const item = buildQuestionItem(qa, `res-${catIdx}-${qIdx}`, qIdx + 1);
        qList.appendChild(item);
      });
    }

    // Category Toggle
    const catToggle = block.querySelector(`#${catToggleId}`);
    catToggle.addEventListener('change', () => {
      const show = catToggle.checked;
      qList.querySelectorAll('.answer-box').forEach(b => b.classList.toggle('visible', show));
      qList.querySelectorAll('.question-info-btn').forEach(btn => btn.classList.toggle('open', show));
      syncGlobalToggle();
    });
  });

  resultsSection.classList.remove('hidden');
  resultsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cleanCategoryName(name) {
  return name.replace(/^Round\s*\d+\s*:\s*/i, '').trim();
}

function buildQuestionItem(qa, uniqueId, displayNum) {
  const item = document.createElement('div');
  item.className = 'question-item';

  item.innerHTML = `
    <div class="question-row" onclick="toggleAnswer('${uniqueId}', this)">
      <span class="question-text">${displayNum}. ${escHtml(stripLeadingNumber(qa.question))}</span>
      <span class="question-info-btn">i</span>
    </div>
    <div class="answer-box" id="${uniqueId}">
      <div class="answer-content">
        <div class="answer-label">Model Answer</div>
        ${escHtml(qa.answer)}
      </div>
    </div>
  `;
  return item;
}

function stripLeadingNumber(q) {
  return q.replace(/^\d+[\.\)]\s*/, '').trim();
}

// ── Toggle Individual Answer ──────────────────────────────────────────────────
function toggleAnswer(id, rowEl) {
  const box = document.getElementById(id);
  if (!box) return;
  const btn = rowEl.querySelector('.question-info-btn');
  const show = !box.classList.contains('visible');
  box.classList.toggle('visible', show);
  if (btn) btn.classList.toggle('open', show);
  syncGlobalToggle();
}

function syncGlobalToggle() {
  const allBoxes   = categoriesCont.querySelectorAll('.answer-box');
  const visibleCnt = categoriesCont.querySelectorAll('.answer-box.visible').length;
  showAllToggle.checked = allBoxes.length > 0 && visibleCnt === allBoxes.length;
}

// ── Copy All Questions ────────────────────────────────────────────────────────
function copyAllQuestions() {
  if (!currentResponse) return;

  const lines = [
    `Interview Questions — ${currentResponse.jobTitle} (${capitalize(currentResponse.level)})\n`
  ];

  const categoryLabels = [
    'Opening Questions',
    'Technical Questions',
    'Database & Architecture Questions',
    'Problem Solving & DSA Questions',
    'Project Discussion Questions'
  ];

  currentResponse.categories.forEach((cat, idx) => {
    const title = categoryLabels[idx] || cleanCategoryName(cat.categoryName);
    lines.push(`\n## ${title}\n`);
    if (cat.questions) {
      cat.questions.forEach((qa, qIdx) => {
        lines.push(`${qIdx + 1}. ${stripLeadingNumber(qa.question)}`);
        lines.push(`Answer: ${qa.answer}\n`);
      });
    }
  });

  navigator.clipboard.writeText(lines.join('\n')).then(() => {
    copyBtn.classList.add('copied');
    copyBtn.innerHTML = '<span>✓</span> Copied';
    setTimeout(() => {
      copyBtn.classList.remove('copied');
      copyBtn.innerHTML = '<span class="btn-copy-icon">📋</span> Copy';
    }, 2500);
  }).catch(() => {
    const ta = document.createElement('textarea');
    ta.value = lines.join('\n');
    document.body.appendChild(ta);
    ta.select();
    document.execCommand('copy');
    document.body.removeChild(ta);
    copyBtn.textContent = 'Copied';
    setTimeout(() => { copyBtn.innerHTML = '<span class="btn-copy-icon">📋</span> Copy'; }, 2000);
  });
}

// ── Question Library (Master Topics) ──────────────────────────────────────────
async function loadMasterTopics() {
  try {
    const res = await fetch(`${API_BASE}/bank/topics`);
    const contentType = res.headers.get('content-type') || '';
    if (!res.ok || !contentType.includes('application/json')) {
      return renderClientTopicPills();
    }
    const topics = await res.json();
    renderTopicsList(topics);
  } catch (e) {
    renderClientTopicPills();
  }
}

function renderClientTopicPills() {
  const topics = Object.keys(CLIENT_TOPIC_BANK);
  renderTopicsList(topics);
}

function renderTopicsList(topics) {
  topicPillsCont.innerHTML = '';
  topics.forEach((topic, idx) => {
    const pill = document.createElement('button');
    pill.type = 'button';
    pill.className = 'topic-pill';
    pill.textContent = topic;
    pill.onclick = () => selectTopic(topic, pill);
    topicPillsCont.appendChild(pill);

    if (idx === 0) {
      selectTopic(topic, pill);
    }
  });
}

async function selectTopic(topicName, pillEl) {
  document.querySelectorAll('.topic-pill').forEach(p => p.classList.remove('active'));
  if (pillEl) pillEl.classList.add('active');

  currentTopicTitle.textContent = topicName;
  currentTopicCount.textContent = 'Loading...';
  topicQuestionsList.innerHTML = '';
  topicViewer.classList.remove('hidden');
  toggleTopicAns.checked = false;

  try {
    const res = await fetch(`${API_BASE}/bank/topic/${encodeURIComponent(topicName)}`);
    const contentType = res.headers.get('content-type') || '';
    if (!res.ok || !contentType.includes('application/json')) {
      return renderClientTopicQuestions(topicName);
    }
    const questions = await res.json();
    displayTopicQuestions(questions);
  } catch (err) {
    renderClientTopicQuestions(topicName);
  }
}

function renderClientTopicQuestions(topicName) {
  const questions = CLIENT_TOPIC_BANK[topicName] || CLIENT_TOPIC_BANK["Introduction & HR"];
  displayTopicQuestions(questions);
}

function displayTopicQuestions(questions) {
  currentTopicCount.textContent = `(${questions.length} Questions)`;
  topicQuestionsList.innerHTML = '';
  questions.forEach((q, qIdx) => {
    const item = buildQuestionItem(q, `topic-${qIdx}`, qIdx + 1);
    topicQuestionsList.appendChild(item);
  });
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function escHtml(str) {
  return String(str)
    .replace(/&/g,'&amp;')
    .replace(/</g,'&lt;')
    .replace(/>/g,'&gt;')
    .replace(/"/g,'&quot;')
    .replace(/'/g,'&#039;');
}

function capitalize(str) {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1);
}

// Expose globals
window.toggleAnswer     = toggleAnswer;
window.retryGenerate    = retryGenerate;
window.regenerate       = regenerate;
window.copyAllQuestions = copyAllQuestions;
window.selectTopic      = selectTopic;
