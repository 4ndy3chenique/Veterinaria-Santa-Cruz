<%-- 
    Document   : IndexCliente
    Created on : 4 may 2025, 2:38:45
    Author     : andy9
--%>

<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Veterinaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/indexcliente.css" />
    <style>
        .sidebar-perfil {
            display: none;
            position: fixed;
            top: 0; right: 0;
            width: 280px;
            height: 100%;
            background-color: #fff;
            box-shadow: -2px 0 5px rgba(0,0,0,0.3);
            z-index: 2000;
            padding-top: 50px;
            overflow-y: auto;
        }
        .sidebar-perfil.active {
            display: block;
        }
        .sidebar-perfil h2 {
            margin: 0 0 20px 20px;
            font-weight: 600;
            font-size: 22px;
        }
        .sidebar-perfil a {
            display: block;
            padding: 15px 25px;
            color: #333;
            text-decoration: none;
            border-bottom: 1px solid #eee;
            font-size: 16px;
            transition: background-color 0.2s;
        }
        .sidebar-perfil a:hover {
            background-color: #f0f0f0;
        }
        #sidebarOverlay {
            display: none;
            position: fixed;
            top: 0; left: 0;
            width: 100vw; height: 100vh;
            background-color: rgba(0,0,0,0.4);
            z-index: 1500;
        }
    </style>
</head>
<body>

<nav class="navbar">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo" />
        </a>
    </div>

    <div class="hamburger" id="hamburger" aria-label="Menú" aria-expanded="false">
        <span></span><span></span><span></span>
    </div>

    <div class="nav-links" id="nav-links">
        <div class="center-links">
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp" id="link-productos">Productos</a>
                         <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp"  id="link-contacto">Contacto</a>

        </div>
        <div class="buttons">
            <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
        </div>
    </div>
</nav>

<!-- Sidebar perfil -->
<div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
    <h2 id="perfilTitle">Mi Perfil</h2>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecompras.jsp">Historial de compras/servicios</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecitas.jsp">Citas agendadas</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
</div>

<!-- Overlay para cerrar sidebar -->
<div id="sidebarOverlay"></div>

<section class="main-section">
    <div class="main-content">
        <div class="text-container">
            <h1>Veterinaria Santa Cruz en Lima</h1>
            <p class="subtext">Cuidamos a tu mascota con amor, experiencia y compromiso.</p>
        </div>
        <div class="image-container">
            <img src="${pageContext.request.contextPath}/Recursos/ImgDoctor.png" alt="Veterinaria Santa Cruz" />
        </div>
    </div>
</section>

<section class="bloque-principal">
    <div class="div-texto">
        <div class="div-texto-centro">
            <div class="div-titulo1">
                <h2>Priorizando a tu compañero de vida</h2>
            </div>
            <div class="div-contenido1">
                <p>
                    En <span>Santa Cruz</span>, nuestra principal misión es asegurar que cada mascota que atendemos disfrute de una vida feliz y saludable. 
                    Nos dedicamos a ofrecer el más alto nivel de atención veterinaria, con profesionalismo y un profundo compromiso de cariño.<br /><br />
                    Nuestro equipo de veterinarios y personal especializado trabaja incansablemente para fomentar el cuidado preventivo, brindar tratamientos integrales y acompañar a tu querida mascota en cada etapa de su vida.
                </p>
            </div>
        </div>
    </div>
    <div class="div-imagen1">
        <img src="${pageContext.request.contextPath}/Recursos/Gato.svg" alt="Gato acostado" class="imagen" />
    </div>
</section>

<section class="bloque-servicios">
    <div class="div-contenido">
        <div class="titulo-servicios">
            <h2>Nuestros Servicios</h2>
        </div>
        <div class="subtitulo-servicios">
            <p>
                En Veterinaria Santa Cruz estamos plenamente capacitados para diagnosticar y tratar todas las enfermedades que pueda presentar tu mascota. 
                Contamos con tecnología de diagnóstico de última generación y un equipo profesional en constante formación, actualizándonos en nuevas técnicas y métodos para brindarte una atención de calidad en medicina veterinaria para animales de compañía.
            </p>
        </div>
    </div>
    <div class="div-slider">
        <button class="flecha izquierda">
            <img src="${pageContext.request.contextPath}/Recursos/FIzquierda.svg" alt="Flecha Izquierda" />
        </button>
        <div class="slider-contenedor">
            <!-- Tarjetas de servicios aquí -->
            ...
        </div>
        <button class="flecha derecha">
            <img src="${pageContext.request.contextPath}/Recursos/FDerecha.svg" alt="Flecha Derecha" />
        </button>
    </div>
</section>

<section class="bloque-tienda">
    <div class="div-contenido-tienda">
        <h2>Tienda Santa Cruz</h2>
    </div>
    <div class="div-slider-tienda">
        <div class="slider-tienda-contenedor">
            <!-- Tarjetas de tienda aquí -->
            ...
        </div>
    </div>
</section>

<script>
    // Abrir sidebar perfil
    document.getElementById('verPerfilBtn').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.add('active');
        document.getElementById('sidebarOverlay').style.display = 'block';
    });

    // Cerrar sidebar perfil al hacer click en overlay
    document.getElementById('sidebarOverlay').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.remove('active');
        this.style.display = 'none';
    });

    // Menú hamburguesa
    document.getElementById('hamburger').addEventListener('click', function() {
        this.classList.toggle('active');
        document.getElementById('nav-links').classList.toggle('active');
    });

    // Slider flechas
    const slider = document.querySelector('.slider-contenedor');
    const flechaIzq = document.querySelector('.flecha.izquierda');
    const flechaDer = document.querySelector('.flecha.derecha');
    if(flechaIzq && flechaDer && slider){
        flechaIzq.addEventListener('click', () => {
            slider.scrollBy({ left: -300, behavior: 'smooth' });
        });
        flechaDer.addEventListener('click', () => {
            slider.scrollBy({ left: 300, behavior: 'smooth' });
        });
    }

    // Marcar enlace activo en la barra de navegación
    window.addEventListener('DOMContentLoaded', () => {
        const path = window.location.pathname;
        const navLinks = document.querySelectorAll('.center-links a');

        navLinks.forEach(link => {
            if (link.getAttribute('href') === path) {
                link.classList.add('active-link');
            } else {
                link.classList.remove('active-link');
            }
        });
    });
</script>

</body>
</html>
