import React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { Request } from '../types';
import L from 'leaflet';

// Fix para los íconos de Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

interface MapProps {
  requests: Request[];
  center: [number, number];
  zoom?: number;
  onMarkerClick?: (request: Request) => void;
}

const Map: React.FC<MapProps> = ({ 
  requests, 
  center, 
  zoom = 13,
  onMarkerClick 
}) => {
  return (
    <MapContainer
      center={center}
      zoom={zoom}
      style={{ height: '100%', width: '100%' }}
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      {requests.map((request) => (
        <Marker
          key={request.id}
          position={[request.location.latitude, request.location.longitude]}
          eventHandlers={{
            click: () => onMarkerClick?.(request),
          }}
        >
          <Popup>
            <div>
              <h3>{request.title}</h3>
              <p>{request.description}</p>
              <p>Estado: {request.status}</p>
            </div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
};

export default Map; 