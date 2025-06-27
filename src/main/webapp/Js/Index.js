
let rolSeleccionado = '';

// -------------------- MODALES --------------------

document.querySelector('.btn.login').addEventListener('click', () => {
    document.getElementById('modalSeleccionInicial').style.display = 'flex';
});

function mostrarModalEquipo() {
    document.getElementById('modalSeleccionInicial').style.display = 'none';
    document.getElementById('modalEquipo').style.display = 'flex';
}

function abrirLogin(rol) {
    rolSeleccionado = rol || '';
    document.getElementById('modalSeleccionInicial').style.display = 'none';
    document.getElementById('modalEquipo').style.display = 'none';

    const modalLogin = document.getElementById('modalLogin');
    const titulo = document.getElementById('tituloLogin');
    const inputRol = document.getElementById('inputRol');
    const opcionesRegistro = document.getElementById('opcionesRegistro');

    inputRol.value = rol;
    titulo.textContent = rol ? `Iniciar Sesión (${rol})` : 'Iniciar Sesión';
    opcionesRegistro.style.display = rol === 'Cliente' ? 'block' : 'none';

    modalLogin.style.display = 'flex';
}

function abrirRegistro() {
    document.getElementById('modalLogin').style.display = 'none';
    document.getElementById('modalRegistro').style.display = 'flex';
}

function abrirLoginDesdeRegistro() {
    document.getElementById('modalRegistro').style.display = 'none';
    document.getElementById('modalSeleccionInicial').style.display = 'flex';
}

function abrirRegistroDesdeLogin() {
    document.getElementById('modalLogin').style.display = 'none';
    document.getElementById('modalRegistro').style.display = 'flex';
}

function cerrarModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.style.display = 'none';

    if (id === 'modalRegistro') {
        document.querySelectorAll('#modalRegistro .campo-error').forEach(input => {
            input.classList.remove('campo-error');
        });

        const mensajes = document.querySelectorAll('#modalRegistro .alert, #modalRegistro div[style*="color: red"]');
        mensajes.forEach(msg => msg.remove());

        const url = new URL(window.location);
        url.searchParams.delete('error');
        url.searchParams.delete('exito');
        history.replaceState(null, '', url);
    }

    if (id === 'modalLogin') {
        const mensajeError = document.getElementById('mensajeErrorLogin');
        if (mensajeError) mensajeError.style.display = 'none';

        const url = new URL(window.location);
        url.searchParams.delete('errorLogin');
        history.replaceState(null, '', url);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const errorLogin = urlParams.get('errorLogin');
    const errorRegistro = urlParams.get('error');
    const exitoRegistro = urlParams.get('exito');

    if (errorLogin === '1') {
        abrirLogin('');
        const mensajeError = document.getElementById('mensajeErrorLogin');
        if (mensajeError) mensajeError.style.display = 'block';
    }

    if (errorRegistro || exitoRegistro) {
        document.getElementById('modalRegistro').style.display = 'flex';
    }


});

// -------------------- NAVBAR RESPONSIVE --------------------
const hamburger = document.getElementById('hamburger');
const navLinks = document.getElementById('nav-links');
const overlay = document.getElementById('overlay');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('open');
    hamburger.classList.toggle('open');
    overlay.classList.toggle('active');
    hamburger.setAttribute('aria-expanded', hamburger.classList.contains('open'));
});

overlay.addEventListener('click', () => {
    navLinks.classList.remove('open');
    hamburger.classList.remove('open');
    overlay.classList.remove('active');
    hamburger.setAttribute('aria-expanded', 'false');
});

// -------------------- SLIDER SERVICIOS --------------------
const sliderContenedor = document.querySelector('.slider-contenedor');
const izquierdaBtn = document.querySelector('.flecha.izquierda');
const derechaBtn = document.querySelector('.flecha.derecha');

const tarjetaAncho = 331 + 20;
const tarjetasVisibles = 3;
const totalTarjetas = document.querySelectorAll('.tarjeta').length;
const maxScroll = (totalTarjetas - tarjetasVisibles) * tarjetaAncho;
let scrollActual = 0;

derechaBtn.addEventListener('click', () => {
    if (scrollActual < maxScroll) {
        scrollActual += tarjetaAncho * tarjetasVisibles;
        if (scrollActual > maxScroll) scrollActual = maxScroll;
        sliderContenedor.style.transform = `translateX(-${scrollActual}px)`;
    }
});

izquierdaBtn.addEventListener('click', () => {
    if (scrollActual > 0) {
        scrollActual -= tarjetaAncho * tarjetasVisibles;
        if (scrollActual < 0) scrollActual = 0;
        sliderContenedor.style.transform = `translateX(-${scrollActual}px)`;
    }
});

// -------------------- SLIDER TIENDA AUTOMÁTICO --------------------
const sliderTienda = document.querySelector('.slider-tienda-contenedor');
const tarjetasOriginales = document.querySelectorAll('.tarjeta-tienda');

// Clonar tarjetas para efecto de bucle infinito
tarjetasOriginales.forEach(tarjeta => {
    const clon = tarjeta.cloneNode(true);
    sliderTienda.appendChild(clon);
});

let scrollSpeed = 0.5;
let intervalo;

function startSlider() {
    intervalo = setInterval(() => {
        sliderTienda.scrollLeft += scrollSpeed;

        // Reinicia el scroll cuando llega al final (por estar duplicadas)
        if (sliderTienda.scrollLeft >= sliderTienda.scrollWidth / 2) {
            sliderTienda.scrollLeft = 0;
        }
    }, 15);
}

function stopSlider() {
    clearInterval(intervalo);
}

sliderTienda.addEventListener('mouseenter', stopSlider);
sliderTienda.addEventListener('mouseleave', startSlider);

startSlider();


    
    