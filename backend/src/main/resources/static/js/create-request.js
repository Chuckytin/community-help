document.addEventListener('DOMContentLoaded', function() {
    let map;
    let marker;

    // Inicializar el mapa
    function initMap() {
        map = L.map('map').setView([40.4168, -3.7038], 13); // Centro en Madrid por defecto

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        // Intentar geolocalización
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                position => {
                    const { latitude, longitude } = position.coords;
                    map.setView([latitude, longitude], 15);
                    updateMarker(latitude, longitude);
                },
                error => {
                    console.error("Error al obtener ubicación:", error);
                }
            );
        }

        // Manejar clic en el mapa
        map.on('click', function(e) {
            updateMarker(e.latlng.lat, e.latlng.lng);
        });
    }

    // Actualizar marcador de ubicación
    function updateMarker(lat, lng) {
        if (marker) {
            map.removeLayer(marker);
        }

        marker = L.marker([lat, lng], {
            icon: L.divIcon({
                className: 'location-marker',
                html: '<i class="fas fa-map-marker-alt" style="color: red; font-size: 32px;"></i>',
                iconSize: [32, 32]
            })
        }).addTo(map);

        document.getElementById('latitude').value = lat;
        document.getElementById('longitude').value = lng;
    }

    // Manejar envío del formulario
    document.getElementById('requestForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const form = e.target;
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;

        try {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creando...';

            const formData = {
                title: form.title.value,
                description: form.description.value,
                category: form.category.value,
                deadline: form.deadline.value,
                latitude: form.latitude.value ? parseFloat(form.latitude.value) : null,
                longitude: form.longitude.value ? parseFloat(form.longitude.value) : null
            };

            const response = await fetch('/api/requests', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al crear la solicitud');
            }

            const data = await response.json();

            // Verifica explícitamente el request_id
            if (data && data.request_id) {
                window.location.href = `/request/${data.request_id}`;
            } else {
                throw new Error('No se recibió el ID de la solicitud creada en la respuesta del servidor');
            }

        } catch (error) {
            console.error("Error:", error);
            // Mostrar error en el formulario de manera más elegante
            const errorContainer = document.getElementById('error-container') || document.createElement('div');
            errorContainer.id = 'error-container';
            errorContainer.className = 'alert alert-danger mt-3';
            errorContainer.innerHTML = `
                <i class="fas fa-exclamation-triangle"></i>
                <strong>Error:</strong> ${error.message}
            `;

            if (!document.getElementById('error-container')) {
                form.parentNode.insertBefore(errorContainer, form);
            } else {
                errorContainer.textContent = `Error: ${error.message}`;
            }

        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    });

    initMap();
});