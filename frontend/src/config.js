const CONFIG = {
    API_BASE_URL: window.location.hostname === 'localhost'
        ? 'http://localhost:8080/api/v1'
        : '/api/v1',
    TOKEN_KEY: 'ai_tutor_auth_token',
    TOKEN_EXPIRE: 7 * 24 * 60 * 60 * 1000
};