import axios from 'axios';
import { AuthResponse } from '../types';

const API_URL = 'https://community-help-d87d.onrender.com/api';

export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await axios.post(`${API_URL}/auth/login`, { email, password });
    return response.data;
  },

  async register(name: string, email: string, password: string): Promise<AuthResponse> {
    const response = await axios.post(`${API_URL}/auth/register`, { name, email, password });
    return response.data;
  },

  async loginWithGoogle(token: string): Promise<AuthResponse> {
    const response = await axios.post(`${API_URL}/auth/google`, { token });
    return response.data;
  },

  setToken(token: string) {
    localStorage.setItem('token', token);
  },

  getToken(): string | null {
    return localStorage.getItem('token');
  },

  logout() {
    localStorage.removeItem('token');
  }
}; 