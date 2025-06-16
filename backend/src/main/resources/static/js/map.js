// Variables globales
let map = null;
let userMarkers = [];
let requestMarkers = [];

/**
 * Muestra una alerta en el mapa
 * @param {string} message - Mensaje a mostrar
 * @param {string} type - Tipo de alerta (warning, success, danger, info)
 */
function showMapAlert(message, type = 'warning') {
    const alert = document.getElementById('mapAlert');
    if (!alert) return;

    alert.textContent = message;
    alert.className = `alert alert-${type} alert-map`;
    alert.style.display = 'block';

    setTimeout(() => {
        alert.style.display = 'none';
    }, 5000);
}

/**
 * Limpia el mapa existente y todos los marcadores
 */
function clearMap() {
    if (map) {
        map.remove();
        map = null;
    }

    // Limpiar marcadores
    userMarkers.forEach(marker => marker.remove());
    requestMarkers.forEach(marker => marker.remove());
    userMarkers = [];
    requestMarkers = [];
}

/**
 * Inicializa el mapa con una ubicación y solicitudes
 * @param {number} latitude - Latitud del centro del mapa
 * @param {number} longitude - Longitud del centro del mapa
 * @param {Array} requests - Lista de solicitudes para mostrar como marcadores
 */
function initMap(latitude, longitude, requests = []) {
    // Validar coordenadas
    if (!latitude || !longitude) {
        showMapAlert('Coordenadas inválidas para inicializar el mapa', 'danger');
        return;
    }

    // Limpiar mapa existente
    clearMap();

    try {
        // Crear nuevo mapa
        map = L.map('map').setView([latitude, longitude], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            maxZoom: 18
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
                      <div class="mt-2">
                          <a href="/request/${request.request_id}" class="btn btn-sm btn-primary">Ver detalles</a>
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
    } catch (error) {
        console.error("Error al inicializar el mapa:", error);
        showMapAlert("Error al cargar el mapa", 'danger');
    }
}

/**
 * Busca solicitudes por ciudad
 * @param {string} city - Ciudad para buscar
 */
async function searchByCity(city) {
    const searchBtn = document.querySelector('#searchForm button[type="submit"]');
    if (!searchBtn) {
        console.error("Botón de búsqueda no encontrado");
        return;
    }

    const originalText = searchBtn.innerHTML;
    searchBtn.disabled = true;
    searchBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Buscando...';

    try {
        // 1. Obtener coordenadas de la ciudad
        const coordsResponse = await fetch(`/api/requests/city-coordinates?city=${encodeURIComponent(city)}`);
        if (!coordsResponse.ok) throw new Error('Ciudad no encontrada');
        const coords = await coordsResponse.json();

        // 2. Buscar solicitudes cercanas
        const requestsResponse = await fetch(`/api/requests/nearby?latitude=${coords.latitude}&longitude=${coords.longitude}&radius=10000`);
        if (!requestsResponse.ok) throw new Error('Error al buscar solicitudes');
        const requests = await requestsResponse.json();

        // 3. Mostrar resultados
        initMap(coords.latitude, coords.longitude, requests);
        showMapAlert(
            requests.length > 0
                ? `Encontradas ${requests.length} solicitudes en ${city}`
                : `No se encontraron solicitudes en ${city}`,
            requests.length > 0 ? 'success' : 'info'
        );
    } catch (error) {
        console.error("Error en la búsqueda:", error);
        showMapAlert("Error al buscar: " + error.message, 'danger');
    } finally {
        searchBtn.disabled = false;
        searchBtn.innerHTML = originalText;
    }
}

/**
 * Obtiene la ubicación actual del usuario
 */
async function getCurrentLocation() {
    const locationBtn = document.getElementById('currentLocationBtn');
    if (!locationBtn) return;

    const originalText = locationBtn.innerHTML;
    locationBtn.disabled = true;
    locationBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

    try {
        if (!navigator.geolocation) {
            throw new Error('Tu navegador no soporta geolocalización');
        }

        const position = await new Promise((resolve, reject) => {
            navigator.geolocation.getCurrentPosition(resolve, reject, {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 0
            });
        });

        await loadNearbyRequests(position.coords.latitude, position.coords.longitude);
    } catch (error) {
        console.error("Error al obtener ubicación:", error);
        showMapAlert('No se pudo obtener tu ubicación: ' + error.message, 'danger');
    } finally {
        locationBtn.disabled = false;
        locationBtn.innerHTML = originalText;
    }
}

/**
 * Carga solicitudes cercanas a una ubicación
 * @param {number} lat - Latitud
 * @param {number} lng - Longitud
 */
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

/**
 * Inicializa los event listeners
 */
function initEventListeners() {
    // Formulario de búsqueda
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const cityInput = document.getElementById('city');
            const city = cityInput?.value.trim();

            if (!city) {
                showMapAlert('Por favor ingresa una ciudad', 'warning');
                return;
            }

            await searchByCity(city);
        });
    }

    // Botón de ubicación actual
    const currentLocationBtn = document.getElementById('currentLocationBtn');
    if (currentLocationBtn) {
        currentLocationBtn.addEventListener('click', function(e) {
            e.preventDefault();
            getCurrentLocation();
        });
    }
}

/**
 * Inicialización cuando el DOM esté listo
 */
document.addEventListener('DOMContentLoaded', function() {
    // Datos del usuario desde Thymeleaf
    const user = {
        latitude: /*[[${user?.latitude}]]*/ null,
        longitude: /*[[${user?.longitude}]]*/ null
    };

    // Inicializar listeners
    initEventListeners();

    // Mostrar mapa inicial si tenemos ubicación del usuario
    if (user.latitude && user.longitude) {
        initMap(user.latitude, user.longitude, /*[[${requests}]]*/ []);
    } else {
        showMapAlert('Ingresa una ubicación o ciudad para ver solicitudes cercanas', 'info');

        if (navigator.geolocation) {
            getCurrentLocation();
        }
    }
});