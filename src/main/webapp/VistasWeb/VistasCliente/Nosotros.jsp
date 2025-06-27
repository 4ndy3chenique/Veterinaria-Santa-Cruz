<%-- 
    Document   : Nosotros
    Created on : 24 may 2025, 22:48:31
    Author     : PROPIETARIO
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nosotros</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nosotros.css">
    <style>
        /* Estilos para sidebar perfil y overlay */
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
        #sidebarOverlay.active {
            display: block;
        }
        .center-links a.active-link {
            border-bottom: 2px solid #000;
            font-weight: 600;
        }
    </style>
</head>
<body>

<!-- Barra de Navegación -->
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

<!-- Contenido de la página Nosotros -->
<div class="cuadro-nosotros grande">
    <div class="descripcion">
        <h2>Sobre Nosotros</h2>
        <p>
            En Veterinaria Santa Cruz, nos dedicamos con pasión y compromiso al cuidado integral de tus mascotas.
            Con más de 7 años de experiencia, somos un equipo de profesionales que combina el amor por los animales 
            con la más alta calidad en servicios veterinarios.
            <br><br>
            Ofrecemos atención médica preventiva, cirugías, vacunas, urgencias, peluquería canina y asesoramiento 
            personalizado para garantizar la salud y el bienestar de cada paciente. Creemos que cada mascota es 
            parte de la familia, y por eso, nuestro trato es cercano, ético y con calidez humana.
            <br><br>
            Nos enorgullece ser una clínica de confianza para la comunidad, donde tu mascota será atendida como se merece: 
            con respeto, cariño y profesionalismo.
        </p>
    </div>
    <div class="imagen">
        <img src="${pageContext.request.contextPath}/Recursos/ImgDoctor.png" alt="Imagen representativa">
    </div>
</div>

<div class="cuadro-nosotros">
    <div class="descripcion">
        <h2>¿Qué realizamos?</h2>
        <p>
            En Veterinaria Santa Cruz ofrecemos un enfoque integral para el cuidado de tus mascotas. 
            Nuestros servicios incluyen vacunación completa (incluyendo esquemas personalizados para cachorros y adultos), 
            consultas médicas especializadas, desparasitación inteligente con seguimiento, cirugías de alta complejidad 
            (ortopedia, laparoscopía y procedimientos oftalmológicos), hospitalización con monitoreo 24/7, análisis 
            clínicos con tecnología de última generación, planes nutricionales personalizados, y rehabilitación física 
            con hidroterapia. Contamos con un equipo multidisciplinario liderado por médicos veterinarios certificados 
            en diversas especialidades, asegurando diagnósticos precisos y tratamientos efectivos. Además, nuestro 
            servicio de urgencias está disponible las 24 horas con atención inmediata para casos críticos.
        </p>
    </div>
    <div class="imagen">
        <img src="${pageContext.request.contextPath}/Recursos/ImgPeluqueria 1.svg" alt="Servicios veterinarios">
    </div>
</div>

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

<script>
    // Abrir sidebar perfil
    document.getElementById('verPerfilBtn').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.add('active');
        document.getElementById('sidebarOverlay').classList.add('active');
    });

    // Cerrar sidebar perfil al hacer click en overlay
    document.getElementById('sidebarOverlay').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.remove('active');
        this.classList.remove('active');
    });

    // Menú hamburguesa
    document.getElementById('hamburger').addEventListener('click', function() {
        this.classList.toggle('active');
        document.getElementById('nav-links').classList.toggle('active');
    });

    // Subrayado en barra de navegación para la página activa
    window.addEventListener('DOMContentLoaded', () => {
        const navLinks = document.querySelectorAll('.center-links a');
        navLinks.forEach(link => {
            if (link.getAttribute('href') === window.location.pathname + window.location.search) {
                link.classList.add('active-link');
            }
        });
        document.getElementById('link-nosotros').classList.add('active-link');
    });
</script>

</body>
</html>

