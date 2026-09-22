async function request(url, options = {}) {
    const token = localStorage.getItem(CONFIG.TOKEN_KEY);
    const fullUrl = `${CONFIG.API_BASE_URL}${url}`;

    const response = await fetch(fullUrl, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
            ...options.headers
        }
    });

    if (response.status === 401) {
        localStorage.removeItem(CONFIG.TOKEN_KEY);
        window.location.href = '/index.html';
        return;
    }

    const data = await response.json();
    if (!response.ok) {
        throw new Error(data.message || '请求失败');
    }
    return data;
}

const api = {
    get:  (url)           => request(url),
    post: (url, body)     => request(url, { method: 'POST', body: JSON.stringify(body) }),
    put:  (url, body)     => request(url, { method: 'PUT',  body: JSON.stringify(body) }),
    del:  (url)           => request(url, { method: 'DELETE' }),
};