// Variables globales
let map = null;
let userMarkers = [];
let requestMarkers = [];
let mapInitialized = false;

// Función para mostrar alerta en el mapa
function showMapAlert(message, type = 'warning') {
    const alert = document.getElementById('mapAlert');
    alert.textContent = message;
    alert.className = `alert alert-${type} alert-map`;
    alert.style.display = 'block';

    setTimeout(() => {
        alert.style.display = 'none';
    }, 5000);
}

// Función para limpiar el mapa existente
function clearMap() {
    if (map) {
        map.off();
        map.remove();
        map = null;
    }
    userMarkers.forEach(marker => {
        if (marker && map) map.removeLayer(marker);
    });
    requestMarkers.forEach(marker => {
        if (marker && map) map.removeLayer(marker);
    });
    userMarkers = [];
    requestMarkers = [];
    mapInitialized = false;
}

// Función para inicializar el mapa
function initMap(latitude, longitude, requests = []) {
    // Limpiar mapa existente
    clearMap();

    // Crear nuevo mapa
    map = L.map('map', {
        preferCanvas: true // Mejor rendimiento para muchos marcadores
    }).setView([latitude, longitude], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Marcador del usuario
    const userMarker = L.marker([latitude, longitude], {
        icon: L.divIcon({
            className: 'user-marker',
            html: '<i class="fas fa-user" style="color: blue; font-size: 24px;"></i>',
            iconSize: [24, 24]
        })
    }).bindPopup(`<b>Tu ubicación</b><br>${latitude.toFixed(4)}, ${longitude.toFixed(4)}`);
    userMarker.addTo(map);
    userMarkers.push(userMarker);

    // Añadir marcadores de solicitudes
    requests.forEach(request => {
        if (request.latitude && request.longitude) {
            const marker = L.marker([request.latitude, request.longitude], {
                icon: L.divIcon({
                    className: 'request-marker',
                    html: '<i class="fas fa-hands-helping" style="color: red; font-size: 20px;"></i>',
                    iconSize: [20, 20]
                })
            }).bindPopup(`
                <b>${request.title || 'Sin título'}</b><br>
                <small>Categoría: ${request.category || 'Sin categoría'}</small><br>
                <span>Distancia: ${request.distance ? (request.distance / 1000).toFixed(2) + ' km' : 'N/A'}</span>
                <div class="mt-2">
                    <a href="/request/${request.id}" class="btn btn-sm btn-primary">Ver detalles</a>
                </div>
            `);
            marker.addTo(map);
            requestMarkers.push(marker);
        }
    });

    // Ajustar el zoom para mostrar todos los marcadores
    if (requests.length > 0) {
        const allMarkers = [...userMarkers, ...requestMarkers];
        const group = new L.featureGroup(allMarkers);
        map.fitBounds(group.getBounds().pad(0.2));
    }

    mapInitialized = true;
}

// Función para buscar solicitudes por ubicación
async function searchByLocation(city, postalCode) {
    const searchBtn = document.querySelector('#searchForm button[type="submit"]');
    const originalText = searchBtn.innerHTML;
    searchBtn.disabled = true;
    searchBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Buscando...';

    try {
        const response = await fetch('/api/requests/search-by-location', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            },
            body: JSON.stringify({
                city: city,
                postalCode: postalCode || null
            })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Error en la búsqueda');
        }

        const requests = await response.json();

        if (requests.length > 0) {
            // Usar las coordenadas de la primera solicitud como centro
            initMap(requests[0].latitude, requests[0].longitude, requests);
            showMapAlert(`Encontradas ${requests.length} solicitudes cercanas`, 'success');
        } else {
            // Si no hay resultados, obtener coordenadas de la ciudad
            const coords = await fetchCoordinates(city, postalCode);
            initMap(coords[0], coords[1], []);
            showMapAlert('No se encontraron solicitudes cercanas en esta ubicación', 'info');
        }
    } catch (error) {
        console.error("Error en la búsqueda:", error);
        showMapAlert("Error al buscar solicitudes: " + error.message, 'danger');
    } finally {
        searchBtn.disabled = false;
        searchBtn.innerHTML = originalText;
    }
}

// Función auxiliar para obtener coordenadas
async function fetchCoordinates(city, postalCode) {
    const response = await fetch('/api/requests/search-by-location', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify({
            city: city,
            postalCode: postalCode || null,
            coordinatesOnly: true
        })
    });
    return await response.json();
}

// Función para obtener la ubicación actual
function getCurrentLocation() {
    if (navigator.geolocation) {
        const locationBtn = document.getElementById('currentLocationBtn');
        const originalText = locationBtn.innerHTML;
        locationBtn.disabled = true;
        locationBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

        navigator.geolocation.getCurrentPosition(
            position => {
                initMap(position.coords.latitude, position.coords.longitude);
                loadNearbyRequests(position.coords.latitude, position.coords.longitude);
                locationBtn.disabled = false;
                locationBtn.innerHTML = originalText;
            },
            error => {
                showMapAlert('No se pudo obtener tu ubicación: ' + error.message, 'danger');
                locationBtn.disabled = false;
                locationBtn.innerHTML = originalText;
            },
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
    } else {
        showMapAlert('Tu navegador no soporta geolocalización', 'danger');
    }
}

// Función para cargar solicitudes cercanas
async function loadNearbyRequests(lat, lng) {
    try {
        const response = await fetch(`/api/requests/nearby?latitude=${lat}&longitude=${lng}&radius=10000`);
        if (!response.ok) {
            throw new Error('Error al obtener solicitudes cercanas');
        }
        const requests = await response.json();
        initMap(lat, lng, requests);
        showMapAlert(`Encontradas ${requests.length} solicitudes cercanas`, 'success');
    } catch (error) {
        console.error("Error cargando solicitudes cercanas:", error);
        showMapAlert("Error al cargar solicitudes cercanas: " + error.message, 'danger');
    }
}

// Event listeners
document.getElementById('searchForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const city = document.getElementById('city').value.trim();
    const postalCode = document.getElementById('postalCode').value.trim();

    if (!city) {
        showMapAlert('Por favor ingresa al menos una ciudad', 'warning');
        return;
    }

    await searchByLocation(city, postalCode);
});

document.getElementById('currentLocationBtn').addEventListener('click', function() {
    getCurrentLocation();
});

// Inicialización cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    // Datos del usuario
    const user = {
        id: /*[[${user?.id}]]*/ null,
        name: /*[[${user?.name}]]*/ 'Usuario',
        email: /*[[${user?.email}]]*/ 'email@ejemplo.com',
        phoneNumber: /*[[${user?.phoneNumber}]]*/ null,
        latitude: /*[[${user?.latitude}]]*/ null,
        longitude: /*[[${user?.longitude}]]*/ null,
        picture: /*[[${authAttributes?.picture}]]*/ '/images/default-user.png'
    };

    if (user.latitude && user.longitude) {
        initMap(user.latitude, user.longitude, /*[[${requests}]]*/ []);
    } else {
        console.log("Ubicación del usuario no disponible para mostrar el mapa");
        showMapAlert('Ingresa una ubicación para ver solicitudes cercanas', 'info');
    }
});