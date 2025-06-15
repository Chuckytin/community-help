document.addEventListener('DOMContentLoaded', function() {
    // Configuración del mapa
    const latitude = parseFloat(document.getElementById('latitude').value);
    const longitude = parseFloat(document.getElementById('longitude').value);

    if (!isNaN(latitude) && !isNaN(longitude)) {
        const map = L.map('requestMap').setView([latitude, longitude], 15);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        const marker = L.marker([latitude, longitude]).addTo(map);
        marker.bindPopup("<b>Ubicación de la solicitud</b>").openPopup();

        // Añadir círculo de radio aproximado
        L.circle([latitude, longitude], {
            color: '#28a745',
            fillColor: '#28a745',
            fillOpacity: 0.2,
            radius: 500 // 500 metros
        }).addTo(map);
    } else {
        document.getElementById('requestMap').innerHTML =
            '<div class="alert alert-warning m-3">Ubicación no disponible</div>';
    }
});