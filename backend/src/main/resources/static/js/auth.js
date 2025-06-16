document.addEventListener('DOMContentLoaded', function() {
    // Elementos del formulario de login
    const loginForm = document.getElementById('loginForm');
    const loginBtn = loginForm.querySelector('button[type="submit"]');
    
    // Elementos del formulario de registro
    const registerForm = document.getElementById('registerForm');
    const registerBtn = registerForm.querySelector('button[type="submit"]');
    const regPasswordInput = document.getElementById('regPassword');
    const regConfirmPasswordInput = document.getElementById('regConfirmPassword');

    // Validación de confirmación de contraseña
    regConfirmPasswordInput.addEventListener('input', function() {
        if (regPasswordInput.value !== regConfirmPasswordInput.value) {
            regConfirmPasswordInput.setCustomValidity("Las contraseñas no coinciden");
            regConfirmPasswordInput.classList.add('is-invalid');
        } else {
            regConfirmPasswordInput.setCustomValidity("");
            regConfirmPasswordInput.classList.remove('is-invalid');
        }
    });

    // Manejo del formulario de login
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        const errorElement = document.getElementById('loginError');
        errorElement.classList.add('d-none');

        const spinner = loginBtn.querySelector('.spinner-border');
        const btnText = loginBtn.querySelector('.btn-text');
        spinner.classList.remove('d-none');
        btnText.classList.add('d-none');
        loginBtn.disabled = true;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value
                })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || data.error || 'Error en el login');
            }

            console.log('Respuesta del login:', data);
            window.location.href = data.redirectUrl || '/home';

        } catch (error) {
            errorElement.textContent = error.message;
            errorElement.classList.remove('d-none');
        } finally {
            spinner.classList.add('d-none');
            btnText.classList.remove('d-none');
            loginBtn.disabled = false;
        }
    });

    // Manejo del formulario de registro
    registerForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        const messageElement = document.getElementById('registerMessage');
        messageElement.classList.add('d-none');

        // Mostrar spinner y deshabilitar botón
        const spinner = registerBtn.querySelector('.spinner-border');
        const btnText = registerBtn.querySelector('.btn-text');
        spinner.classList.remove('d-none');
        btnText.classList.add('d-none');
        registerBtn.disabled = true;

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: document.getElementById('regName').value,
                    email: document.getElementById('regEmail').value,
                    phoneNumber: document.getElementById('regPhone').value || null,
                    password: document.getElementById('regPassword').value
                })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Error en el registro');
            }

            // Registro exitoso
            messageElement.textContent = '¡Registro exitoso! Redirigiendo...';
            messageElement.classList.remove('alert-danger');
            messageElement.classList.add('alert-success');
            messageElement.classList.remove('d-none');

            // Auto-login después del registro
            setTimeout(() => {
                document.getElementById('email').value = document.getElementById('regEmail').value;
                document.getElementById('password').value = document.getElementById('regPassword').value;
                document.getElementById('login-tab').click();
                loginForm.dispatchEvent(new Event('submit'));
            }, 1500);

        } catch (error) {
            console.error("Error en el registro:", error);
            messageElement.textContent = error.message || 'Error durante el registro';
            messageElement.classList.remove('alert-success');
            messageElement.classList.add('alert-danger');
            messageElement.classList.remove('d-none');
        } finally {
            spinner.classList.add('d-none');
            btnText.classList.remove('d-none');
            registerBtn.disabled = false;
        }
    });
});