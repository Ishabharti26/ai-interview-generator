/* ─────────────────────────────────────────────────────────────────────────────
   Recooty — AI Interview Question Generator
   Production Client Logic
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

    if (!response.ok) {
      const err = await response.json().catch(() => ({}));
      throw new Error(err.error || `Server error ${response.status}`);
    }

    currentResponse = await response.json();
    renderResults(currentResponse);

  } catch (err) {
    showError(err.message || 'Something went wrong. Please try again.');
  } finally {
    setLoadingState(false);
  }
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

// ── Render Results (Matching Recooty Reference Layout) ────────────────────────
function renderResults(data) {
  // Reset global toggle
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
    if (!res.ok) return;
    const topics = await res.json();

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
  } catch (e) {
    console.warn('Failed to load topic library', e);
  }
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
    if (!res.ok) throw new Error('Topic not found');
    const questions = await res.json();

    currentTopicCount.textContent = `(${questions.length} Questions)`;
    topicQuestionsList.innerHTML = '';

    questions.forEach((q, qIdx) => {
      const item = buildQuestionItem(q, `topic-${qIdx}`, qIdx + 1);
      topicQuestionsList.appendChild(item);
    });

  } catch (err) {
    currentTopicCount.textContent = '(Error loading questions)';
  }
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
