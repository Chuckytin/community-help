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
            attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
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

        // Obtener el ID del usuario actual desde Thymeleaf
        const currentUserId = /*[[${user?.id}]]*/ null;

        // Añadir marcadores de solicitudes
        requests.forEach(request => {
            if (request.latitude && request.longitude) {
                // Determinar si la solicitud fue creada por el usuario actual
                const isCreator = currentUserId && request.creator_id === currentUserId;

                // Configurar el contenido del popup según si es el creador
                const popupContent = isCreator
                    ? `
                        <b>${request.title || 'Sin título'}</b><br>
                        <div class="mt-2">
                            <a href="/request/${request.request_id}" class="btn btn-sm btn-primary">Ver detalles</a>
                        </div>
                      `
                    : `
                        <b>${request.title || 'Sin título'}</b><br>
                        <small>Categoría: ${request.category || 'Sin categoría'}</small><br>
                        <span>Distancia: ${request.distance ? (request.distance / 1000).toFixed(2) + ' km' : 'N/A'}</span><br>
                        <span>Tiempo de viaje: ${request.travelDuration ? (request.travelDuration / 60).toFixed(1) + ' minutos' : 'N/A'}</span>
                        <div class="mt-2">
                            <a href="/request/${request.request_id}" class="btn btn-sm btn-primary">Ver detalles</a>
                        </div>
                      `;

                const marker = L.marker([request.latitude, request.longitude], {
                    icon: L.divIcon({
                        className: 'request-marker',
                        html: '<i class="fas fa-hands-helping" style="color: red; font-size: 20px;"></i>',
                        iconSize: [20, 20]
                    })
                }).bindPopup(popupContent);
                marker.addTo(map);
                requestMarkers.push(marker);
            }
        });

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