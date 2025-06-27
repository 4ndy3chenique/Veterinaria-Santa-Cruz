document.addEventListener("DOMContentLoaded", () => {
    const modoNocheBtn = document.getElementById('modoNocheBtn');
    if (!modoNocheBtn) return;

    const logoAdmin = document.getElementById('logoAdmin');

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

        const contextPath = window.location.pathname.split('/')[1]; // obtiene "/nombreContexto"
        const basePath = '/' + contextPath + '/Recursos/';

        actualizarImagen(logoAdmin,
            basePath + 'Logo.png',
            basePath + 'Logo-oscuro.png',
            activado
        );
    }

    const modoGuardado = localStorage.getItem('modo-noche');
    const activado = modoGuardado === 'activado';
    aplicarModo(activado);

    modoNocheBtn.addEventListener('click', () => {
        const nuevoEstado = !document.body.classList.contains('modo-noche');
        aplicarModo(nuevoEstado);
    });

    // Animación del texto del header
    const headerText = document.querySelector(".image-text");
    if (headerText) {
        headerText.classList.add("animar-header");
    }
});
