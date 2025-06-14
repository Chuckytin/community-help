# Community Help Frontend

Este es el frontend de la aplicación Community Help, desarrollado con React, TypeScript y Material-UI.

## Requisitos Previos

- Node.js (versión 14 o superior)
- npm (incluido con Node.js)

## Instalación

1. Clona el repositorio
2. Navega al directorio del proyecto:
   ```bash
   cd frontend
   ```
3. Instala las dependencias:
   ```bash
   npm install
   ```

## Configuración

1. Crea un archivo `.env` en el directorio raíz del proyecto con las siguientes variables:
   ```
   REACT_APP_API_URL=https://community-help-d87d.onrender.com/api
   REACT_APP_GOOGLE_CLIENT_ID=your_google_client_id
   ```

## Desarrollo

Para iniciar el servidor de desarrollo:

```bash
npm start
```

La aplicación estará disponible en `http://localhost:3000`.

## Construcción

Para construir la aplicación para producción:

```bash
npm run build
```

Los archivos de construcción se generarán en el directorio `build`.

## Características

- Autenticación de usuarios (email/password y Google)
- Visualización de solicitudes en un mapa interactivo
- Lista de solicitudes cercanas
- Creación de nuevas solicitudes
- Interfaz responsiva y moderna

## Tecnologías Utilizadas

- React
- TypeScript
- Material-UI
- Leaflet (para mapas)
- React Router
- Axios
