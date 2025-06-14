import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/auth.service';

declare global {
  interface Window {
    google: {
      accounts: {
        id: {
          initialize: (config: any) => void;
          renderButton: (element: HTMLElement, config: any) => void;
        };
      };
    };
  }
}

export const useGoogleAuth = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Cargar el script de Google
    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  const initializeGoogleAuth = () => {
    if (window.google) {
      window.google.accounts.id.initialize({
        client_id: process.env.REACT_APP_GOOGLE_CLIENT_ID,
        callback: handleGoogleResponse,
      });

      window.google.accounts.id.renderButton(
        document.getElementById('googleButton')!,
        { theme: 'outline', size: 'large', width: '100%' }
      );
    }
  };

  const handleGoogleResponse = async (response: any) => {
    try {
      const result = await authService.loginWithGoogle(response.credential);
      authService.setToken(result.token);
      navigate('/home');
    } catch (error) {
      console.error('Error en la autenticación con Google:', error);
    }
  };

  return { initializeGoogleAuth };
}; 