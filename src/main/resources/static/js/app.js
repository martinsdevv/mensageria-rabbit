const API = {
    recipients: '/api/recipients',
    messages: '/api/messages',
    jobs: '/api/jobs',
    send: (messageId) => `/api/send/${messageId}`,
};

// --- Tabs ---
let jobsPollTimer = null;

document.querySelectorAll('.tab').forEach((tab) => {
    tab.addEventListener('click', () => {
        const target = tab.dataset.tab;

        document.querySelectorAll('.tab').forEach((t) => {
            t.classList.toggle('tab--active', t === tab);
            t.setAttribute('aria-selected', t === tab);
        });

        document.querySelectorAll('.panel').forEach((panel) => {
            const isActive = panel.id === `panel-${target}`;
            panel.classList.toggle('panel--active', isActive);
            panel.hidden = !isActive;
        });

        if (target === 'jobs') {
            loadJobs();
            startJobsPolling();
        } else {
            stopJobsPolling();
        }
    });
});

function startJobsPolling() {
    stopJobsPolling();
    jobsPollTimer = setInterval(async () => {
        try {
            await loadJobs();
        } catch {
            /* silencioso durante polling */
        }
    }, 3000);
}

function stopJobsPolling() {
    if (jobsPollTimer) {
        clearInterval(jobsPollTimer);
        jobsPollTimer = null;
    }
}

// --- Toast ---
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast toast--${type}`;
    toast.hidden = false;

    clearTimeout(showToast._timer);
    showToast._timer = setTimeout(() => {
        toast.hidden = true;
    }, 3500);
}

// --- API helpers ---
async function api(url, options = {}) {
    const response = await fetch(url, {
        headers: { 'Content-Type': 'application/json' },
        ...options,
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.message || `Erro ${response.status}`);
    }

    if (response.status === 204) return null;
    return response.json();
}

function formatDate(iso) {
    return new Date(iso).toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });
}

// --- Recipients ---
async function loadRecipients() {
    const recipients = await api(API.recipients);
    const tbody = document.getElementById('recipients-table');
    document.getElementById('recipients-count').textContent = recipients.length;

    if (recipients.length === 0) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="4">Nenhum destinatário cadastrado.</td></tr>';
        return;
    }

    tbody.innerHTML = recipients.map((r) => `
        <tr>
            <td>${escapeHtml(r.email)}</td>
            <td>${escapeHtml(r.name || '—')}</td>
            <td>${formatDate(r.createdAt)}</td>
            <td>
                <button class="btn btn--danger" data-delete-recipient="${r.id}">Remover</button>
            </td>
        </tr>
    `).join('');
}

document.getElementById('form-recipient').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;

    try {
        await api(API.recipients, {
            method: 'POST',
            body: JSON.stringify({
                email: form.email.value,
                name: form.name.value || null,
            }),
        });
        form.reset();
        showToast('Destinatário cadastrado com sucesso!');
        await loadRecipients();
    } catch (err) {
        showToast(err.message, 'error');
    }
});

document.getElementById('recipients-table').addEventListener('click', async (e) => {
    const id = e.target.dataset.deleteRecipient;
    if (!id) return;

    if (!confirm('Remover este destinatário?')) return;

    try {
        await api(`${API.recipients}/${id}`, { method: 'DELETE' });
        showToast('Destinatário removido.');
        await loadRecipients();
    } catch (err) {
        showToast(err.message, 'error');
    }
});

// --- Messages ---
async function loadMessages() {
    const messages = await api(API.messages);
    const container = document.getElementById('messages-list');
    document.getElementById('messages-count').textContent = messages.length;

    if (messages.length === 0) {
        container.innerHTML = '<p class="empty-state">Nenhuma mensagem criada.</p>';
        return;
    }

    container.innerHTML = messages.map((m) => `
        <article class="message-item">
            <div class="message-item__subject">${escapeHtml(m.subject)}</div>
            <div class="message-item__body">${escapeHtml(m.body)}</div>
            <div class="message-item__meta">
                <span>ID ${m.id} · ${formatDate(m.createdAt)}</span>
                <div class="message-item__actions">
                    <button class="btn btn--send" data-send-message="${m.id}">Enviar para todos</button>
                </div>
            </div>
        </article>
    `).join('');
}

document.getElementById('messages-list').addEventListener('click', async (e) => {
    const messageId = e.target.dataset.sendMessage;
    if (!messageId) return;

    if (!confirm('Solicitar envio desta mensagem para todos os destinatarios?')) return;

    try {
        const job = await api(API.send(messageId), { method: 'POST' });
        showToast(`Envio solicitado! Job #${job.id} na fila (status: ${job.status})`);
        await loadJobs();
    } catch (err) {
        showToast(err.message, 'error');
    }
});

document.getElementById('form-message').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;

    try {
        await api(API.messages, {
            method: 'POST',
            body: JSON.stringify({
                subject: form.subject.value,
                body: form.body.value,
            }),
        });
        form.reset();
        showToast('Mensagem criada com sucesso!');
        await loadMessages();
    } catch (err) {
        showToast(err.message, 'error');
    }
});

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// --- Jobs ---
async function loadJobs() {
    const jobs = await api(API.jobs);
    const container = document.getElementById('jobs-list');
    document.getElementById('jobs-count').textContent = jobs.length;

    if (jobs.length === 0) {
        container.innerHTML = '<p class="empty-state">Nenhum envio solicitado ainda.</p>';
        return;
    }

    container.innerHTML = jobs.map((j) => `
        <article class="job-item">
            <div class="job-item__info">
                <h3>Job #${j.id} — ${escapeHtml(j.messageSubject)}</h3>
                <p>Mensagem ID ${j.messageId} · ${formatDate(j.createdAt)} · ${j.totalSent} enviados · ${j.totalFailed} falhas</p>
            </div>
            <span class="status-badge status-badge--${j.status}">${j.status}</span>
        </article>
    `).join('');
}

document.getElementById('btn-refresh-jobs').addEventListener('click', async () => {
    try {
        await loadJobs();
        showToast('Lista de envios atualizada.');
    } catch (err) {
        showToast(err.message, 'error');
    }
});

// --- Init ---
async function init() {
    try {
        await Promise.all([loadRecipients(), loadMessages(), loadJobs()]);
    } catch (err) {
        showToast('Não foi possível conectar à API. A aplicação está rodando?', 'error');
        document.querySelector('.header__status').innerHTML =
            '<span class="status-dot" style="background:var(--danger)"></span> API offline';
    }
}

init();
