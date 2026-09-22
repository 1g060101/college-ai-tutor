// 统一顶部导航栏：站名 + 功能菜单 + 用户显示/退出
// 依赖 config.js / auth.js（在 nav.js 之前加载）

const NAV_MENU = [
    { href: 'index.html',    label: '首页',     icon: '🏠' },
    { href: 'qa.html',       label: '答疑',     icon: '💬' },
    { href: 'report.html',   label: '学习报告', icon: '📊' },
    { href: 'avatar.html',   label: '数字人',   icon: '🤖' },
    { href: 'course.html',   label: '课程',     icon: '📚' },
    { href: 'homework.html', label: '作业·错题',icon: '✏️' },
    { href: 'studyplan.html',label: '学习计划', icon: '🗓️' },
    { href: 'classroom.html',label: '课堂',     icon: '🎓' },
    { href: 'paper.html',    label: '备考',     icon: '🎯' },
    { href: 'subject.html',  label: '学科导入', icon: '📥' },
    { href: 'teacher.html',  label: '教师后台', icon: '👩‍🏫', role: 'TEACHER' }
];

const ROLE_LABEL = { STUDENT: '学生', TEACHER: '教师', ADMIN: '管理员' };

// 渲染导航栏，active 为当前页面文件名（如 qa.html）
function renderNav(activePage) {
    const nav = document.querySelector('.navbar');
    if (!nav) return;

    const user = typeof auth !== 'undefined' ? auth.getUser() : {};
    const isTeacher = user.role === 'TEACHER';

    const links = NAV_MENU
        .filter(m => !m.role || (m.role === 'TEACHER' && isTeacher))
        .map(m => `<a href="${m.href}" class="nav-link ${m.href === activePage ? 'active' : ''}">${m.icon} ${m.label}</a>`)
        .join('');

    const nickname = user.nickname || user.email || '用户';
    const roleText = ROLE_LABEL[user.role] || user.role || '';

    nav.innerHTML = `
        <div class="navbar-inner">
            <a class="nav-brand" href="index.html">🎓 AI 数字人学习助手</a>
            <nav class="nav-links">${links}</nav>
            <div class="nav-user">
                <span class="user-chip">
                    <span>👤 ${nickname}</span>
                    ${roleText ? `<span class="badge badge-primary role-badge">${roleText}</span>` : ''}
                </span>
                <button class="btn-logout" id="btn-logout">退出</button>
            </div>
        </div>`;

    document.getElementById('btn-logout').addEventListener('click', () => {
        if (typeof auth !== 'undefined') auth.logout();
        window.location.href = 'login.html';
    });
}

// 全局 toast 提示
function showToast(message, type = 'success', duration = 2600) {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const el = document.createElement('div');
    el.className = `toast toast-${type}`;
    el.textContent = message;
    container.appendChild(el);
    requestAnimationFrame(() => el.classList.add('show'));
    setTimeout(() => {
        el.classList.remove('show');
        setTimeout(() => el.remove(), 300);
    }, duration);
}

// 便捷 DOM 选择器
function $(sel) { return document.querySelector(sel); }
function $all(sel) { return document.querySelectorAll(sel); }

// 格式化难度的颜色
function difficultyBadge(n) {
    const map = { 1: 'badge-success', 2: 'badge-success', 3: 'badge-warning', 4: 'badge-warning', 5: 'badge-danger' };
    return `<span class="badge ${map[n] || 'badge-primary'}">难度 ${n}</span>`;
}

// 转义 HTML
function esc(s) {
    return String(s == null ? '' : s)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

// 日期时间格式化
function fmtTime(s) {
    if (!s) return '';
    return String(s).replace('T', ' ').substring(0, 19);
}

// ===== SSE 流式 POST =====
// 用原生 fetch 透传解析 text/event-stream，body 为 JSON 对象。
// handlers: { onChunk(text), onGuidedCorrected(text), onDone(data), onError(msg) } 全部可选
async function ssePost(url, body, handlers) {
    const token = typeof auth !== 'undefined' ? auth.getToken() : localStorage.getItem(CONFIG.TOKEN_KEY || 'ai_tutor_auth_token');
    const fullUrl = `${CONFIG.API_BASE_URL}${url}`;
    const response = await fetch(fullUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'text/event-stream',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {})
        },
        body: JSON.stringify(body)
    });

    if (response.status === 401) {
        localStorage.removeItem('ai_tutor_auth_token');
        window.location.href = '/index.html';
        throw new Error('未登录');
    }
    if (!response.ok || !response.body) {
        // 前置校验错误时后端返回 JSON
        const text = await response.text();
        let msg = '请求失败';
        try { msg = JSON.parse(text).message || msg; } catch (_) {}
        throw new Error(msg);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let buffer = '';
    let seenDone = false;

    const dispatch = handleEvent(handlers);

    try {
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;
            buffer += decoder.decode(value, { stream: true });
            // 按空行拆分为事件块
            let idx;
            while ((idx = buffer.indexOf('\n\n')) >= 0) {
                const raw = buffer.slice(0, idx);
                buffer = buffer.slice(idx + 2);
                const evt = dispatch(raw);
                if (evt === 'done') seenDone = true;
            }
        }
        // 尾部残余
        if (buffer.trim()) { if (dispatch(buffer) === 'done') seenDone = true; }
    } catch (err) {
        // 服务端调用 emitter.complete() 后立即关闭连接，浏览器可能在最后一个 read 抛网络错误；
        // 若已收到 done 事件，视为正常结束，否则交给上层按错误处理。
        if (!seenDone) throw err;
    }
}

function handleEvent(handlers) {
    let event = 'message';
    const dataLines = [];
    return function dispatch(raw) {
        const lines = raw.split('\n');
        for (const line of lines) {
            if (line.startsWith('event:')) event = line.slice(6).trim();
            else if (line.startsWith('data:')) dataLines.push(line.slice(5).trim());
            else if (line.startsWith(':')) { /* 注释忽略 */ }
        }
        if (!dataLines.length) return;
        const data = dataLines.join('\n');
        dataLines.length = 0;

        if (event === 'chunk' && handlers.onChunk) handlers.onChunk(data);
        else if (event === 'guided_corrected' && handlers.onGuidedCorrected) handlers.onGuidedCorrected(data);
        else if (event === 'done' && handlers.onDone) {
            try { handlers.onDone(JSON.parse(data)); }
            catch (_) { handlers.onDone({}); }
        }
        else if (event === 'error' && handlers.onError) handlers.onError(data);
        else if (handlers.onMessage) handlers.onMessage(event, data);
        const evt = event;
        event = 'message';
        return evt;
    };
}