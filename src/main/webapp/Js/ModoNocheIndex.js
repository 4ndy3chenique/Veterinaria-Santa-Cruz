/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
    
// -------------------- MODO NOCHE --------------------//
    document.addEventListener("DOMContentLoaded", () => {
        const modoNocheBtn = document.getElementById('modoNocheBtn');
        if (!modoNocheBtn) return; // si no hay botón, no ejecutar

        const logo = document.getElementById('logo');
        const logoAdmin = document.getElementById('logoAdmin');
        const logoSeleccionInicial = document.getElementById('logoSeleccionInicial');
        const logoModalEquipo = document.getElementById('logoModalEquipo');
        const logoRegistro = document.getElementById('logoRegistro');
        const iconoUsuario = document.getElementById('iconoUsuario');
        const imagenesModal = document.querySelectorAll('.modal img');

        function actualizarImagen(elemento, claro, oscuro, activado) {
            if (elemento) {
                const nuevaSrc = activado ? oscuro : claro;
                elemento.setAttribute('src', nuevaSrc);
            }
        }

        function aplicarModo(activado) {
            document.body.classList.toggle('modo-noche', activado);
            modoNocheBtn.textContent = activado ? '☀️' : '🌙';
            localStorage.setItem('modo-noche', activado ? 'activado' : 'desactivado');

            actualizarImagen(logo, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
            actualizarImagen(logoAdmin, '../../Recursos/Logo.png', '../../Recursos/Logo-oscuro.png', activado);
            actualizarImagen(logoSeleccionInicial, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
            actualizarImagen(logoModalEquipo, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
            actualizarImagen(logoRegistro, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
            actualizarImagen(iconoUsuario, 'Recursos/IconUser.svg', 'Recursos/IconUser-dark.svg', activado);

            imagenesModal.forEach(imagen => {
                if (!imagen.src) return;
                if (activado && imagen.src.includes('IconUser.svg')) {
                    imagen.src = imagen.src.replace('IconUser.svg', 'IconUser-dark.svg');
                } else if (!activado && imagen.src.includes('IconUser-dark.svg')) {
                    imagen.src = imagen.src.replace('IconUser-dark.svg', 'IconUser.svg');
                }
            });
        }

        const modoGuardado = localStorage.getItem('modo-noche');
        const activado = modoGuardado === 'activado';
        aplicarModo(activado);

        modoNocheBtn.addEventListener('click', () => {
            const nuevoEstado = !document.body.classList.contains('modo-noche');
            aplicarModo(nuevoEstado);
        });
    });

    window.addEventListener("DOMContentLoaded", () => {
    document.querySelector(".image-text").classList.add("animar-header");
    });