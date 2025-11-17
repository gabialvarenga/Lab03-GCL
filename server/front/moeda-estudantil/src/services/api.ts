import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Função para decodificar JWT e verificar tempo de expiração
const isTokenExpiringSoon = (token: string): boolean => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expirationTime = payload.exp * 1000; // Converter para ms
    const currentTime = Date.now();
    const timeUntilExpiration = expirationTime - currentTime;
    
    // Se faltam menos de 5 minutos (300000 ms) para expirar
    return timeUntilExpiration < 300000;
  } catch {
    return false;
  }
};

// Função para renovar o token
const refreshToken = async (): Promise<string | null> => {
  try {
    const currentToken = localStorage.getItem('token');
    if (!currentToken) {
      console.log('Nenhum token para renovar');
      return null;
    }

    const response = await axios.post(
      `${API_BASE_URL}/auth/refresh`,
      {},
      {
        headers: {
          Authorization: `Bearer ${currentToken}`,
        },
      }
    );

    const newToken = response.data.token;
    localStorage.setItem('token', newToken);
    console.log('✓ Token renovado automaticamente');
    return newToken;
  } catch (error) {
    console.error('Erro ao renovar token:', error);
    // Limpa o token inválido
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
    return null;
  }
};

// Add token to requests if available
api.interceptors.request.use(
  async (config) => {
    // Não adiciona token para endpoints públicos (login e refresh)
    const isAuthEndpoint = config.url?.includes('/auth/login') || 
                          config.url?.includes('/auth/refresh') ||
                          config.url?.includes('/students') && config.method === 'post' ||
                          config.url?.includes('/companies') && config.method === 'post' ||
                          config.url?.includes('/institutions');
    
    if (isAuthEndpoint) {
      return config;
    }

    const token = localStorage.getItem('token');
    if (token) {
      // Verifica se o token está próximo de expirar
      if (isTokenExpiringSoon(token)) {
        console.log('⚠ Token expirando em breve, renovando...');
        const newToken = await refreshToken();
        if (newToken) {
          config.headers.Authorization = `Bearer ${newToken}`;
        } else {
          config.headers.Authorization = `Bearer ${token}`;
        }
      } else {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Unauthorized - clear token and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userId');
      localStorage.removeItem('userEmail');
      globalThis.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
