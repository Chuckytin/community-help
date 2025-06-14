import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Grid, 
  Paper, 
  Typography, 
  Button,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Box
} from '@mui/material';
import Map from '../components/Map';
import CustomGrid from '../components/CustomGrid';
import { Request } from '../types';
import axios from 'axios';

const Home: React.FC = () => {
  const [requests, setRequests] = useState<Request[]>([]);
  const [selectedRequest, setSelectedRequest] = useState<Request | null>(null);
  const [userLocation, setUserLocation] = useState<[number, number]>([0, 0]);

  useEffect(() => {
    // Obtener ubicación del usuario
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setUserLocation([latitude, longitude]);
        fetchNearbyRequests(latitude, longitude);
      },
      (error) => {
        console.error('Error al obtener la ubicación:', error);
      }
    );
  }, []);

  const fetchNearbyRequests = async (latitude: number, longitude: number) => {
    try {
      const response = await axios.get(
        `https://community-help-d87d.onrender.com/api/requests/nearby?latitude=${latitude}&longitude=${longitude}&radius=5000`
      );
      setRequests(response.data);
    } catch (error) {
      console.error('Error al obtener solicitudes cercanas:', error);
    }
  };

  const handleCreateRequest = () => {
    // Implementar navegación a la página de creación de solicitud
    console.log('Crear nueva solicitud');
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Grid container spacing={3}>
          <CustomGrid item xs={12}>
            <Paper sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6" component="h1">
                Solicitudes Cercanas
              </Typography>
              <Button variant="contained" color="primary" onClick={handleCreateRequest}>
                Nueva Solicitud
              </Button>
            </Paper>
          </CustomGrid>
          <CustomGrid item xs={12} md={8}>
            <Paper sx={{ p: 2, height: '600px' }}>
              <Map
                requests={requests}
                center={userLocation}
                onMarkerClick={setSelectedRequest}
              />
            </Paper>
          </CustomGrid>
          <CustomGrid item xs={12} md={4}>
            <Paper sx={{ p: 2, height: '600px', overflow: 'auto' }}>
              <Typography variant="h6" gutterBottom>
                Lista de Solicitudes
              </Typography>
              <List>
                {requests.map((request) => (
                  <ListItem key={request.id} disablePadding>
                    <ListItemButton
                      selected={selectedRequest?.id === request.id}
                      onClick={() => setSelectedRequest(request)}
                    >
                      <ListItemText
                        primary={request.title}
                        secondary={
                          <>
                            <Typography component="span" variant="body2" color="text.primary">
                              {request.status}
                            </Typography>
                            {` — ${request.description.substring(0, 50)}...`}
                          </>
                        }
                      />
                    </ListItemButton>
                  </ListItem>
                ))}
              </List>
            </Paper>
          </CustomGrid>
        </Grid>
      </Container>
    </Box>
  );
};

export default Home; 