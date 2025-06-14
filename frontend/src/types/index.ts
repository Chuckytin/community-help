export interface User {
  id: number;
  email: string;
  name: string;
  location: {
    latitude: number;
    longitude: number;
  };
}

export interface Request {
  id: number;
  title: string;
  description: string;
  location: {
    latitude: number;
    longitude: number;
  };
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
  createdAt: string;
  userId: number;
}

export interface AuthResponse {
  token: string;
  user: User;
} 