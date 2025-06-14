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
    script.onload = () => {
      console.log('Google Sign-In script loaded successfully');
      initializeGoogleAuth();
    };
    script.onerror = (error) => {
      console.error('Error loading Google Sign-In script:', error);
    };
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  const initializeGoogleAuth = () => {
    if (!window.google) {
      console.error('Google Sign-In not loaded');
      return;
    }

    const clientId = process.env.REACT_APP_GOOGLE_CLIENT_ID;
    if (!clientId) {
      console.error('Google Client ID not configured');
      return;
    }

    try {
      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: handleGoogleResponse,
      });

      const buttonElement = document.getElementById('googleButton');
      if (!buttonElement) {
        console.error('Google button element not found');
        return;
      }

      window.google.accounts.id.renderButton(
        buttonElement,
        { theme: 'outline', size: 'large', width: '100%' }
      );
      console.log('Google Sign-In button rendered successfully');
    } catch (error) {
      console.error('Error initializing Google Sign-In:', error);
    }
  };

  const handleGoogleResponse = async (response: any) => {
    try {
      console.log('Google Sign-In response received');
      const result = await authService.loginWithGoogle(response.credential);
      authService.setToken(result.token);
      navigate('/home');
    } catch (error) {
      console.error('Error en la autenticación con Google:', error);
    }
  };

  return { initializeGoogleAuth };
};