// 登录态辅助工具：统一管理 token 的存取与失效判断
const auth = {
    // 获取 token
    getToken() {
        return localStorage.getItem(CONFIG.TOKEN_KEY);
    },

    // 保存 token，并记录过期时间
    setToken(token) {
        localStorage.setItem(CONFIG.TOKEN_KEY, token);
        localStorage.setItem('ai_tutor_auth_expire', String(Date.now() + CONFIG.TOKEN_EXPIRE));
    },

    // 保存用户摘要（昵称/角色/email/userId），供导航栏显示
    setUser(user) {
        localStorage.setItem('ai_tutor_user', JSON.stringify(user || {}));
    },
    getUser() {
        try {
            return JSON.parse(localStorage.getItem('ai_tutor_user') || '{}');
        } catch (e) {
            return {};
        }
    },
    hasRole(role) {
        return this.getUser().role === role;
    },

    // 是否已登录
    isLoggedIn() {
        const token = this.getToken();
        if (!token) return false;
        const expire = localStorage.getItem('ai_tutor_auth_expire');
        if (expire && Number(expire) < Date.now()) {
            this.logout();
            return false;
        }
        return true;
    },

    // 退出登录
    logout() {
        localStorage.removeItem(CONFIG.TOKEN_KEY);
        localStorage.removeItem('ai_tutor_auth_expire');
        localStorage.removeItem('ai_tutor_user');
    },

    // 未登录则跳转登录页
    requireLogin(redirectUrl = '/login.html') {
        if (!this.isLoggedIn()) {
            window.location.href = redirectUrl;
        }
    }
};