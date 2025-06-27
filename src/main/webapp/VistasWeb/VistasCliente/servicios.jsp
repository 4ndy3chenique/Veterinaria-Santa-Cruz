<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Servicios | Veterinaria Santa Cruz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/servicios.css">
    <style>
        /* Sidebar perfil */
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
            transition: transform 0.3s ease;
            transform: translateX(100%);
        }
        .sidebar-perfil.active {
            display: block;
            transform: translateX(0);
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
        /* Overlay */
        #sidebarOverlay {
            display: none;
            position: fixed;
            top: 0; left: 0;
            width: 100vw; height: 100vh;
            background-color: rgba(0,0,0,0.4);
            z-index: 1500;
            transition: opacity 0.3s ease;
        }
        #sidebarOverlay.active {
            display: block;
            opacity: 1;
        }
        /* Botón flotante Crear cita */
        .flotante-cita {
            position: fixed;
            bottom: 30px;
            right: 30px;
            z-index: 1000;
        }
        .btn-cita-flotante {
            background: linear-gradient(135deg, #2c3342 0%, #5b657a 100%);
            color: white;
            padding: 15px 25px;
            border-radius: 50px;
            font-weight: 600;
            box-shadow: 0 10px 25px rgba(44, 51, 66, 0.3);
            display: flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
            font-size: 1rem;
            transition: all 0.3s;
        }
        .btn-cita-flotante:hover {
            transform: translateY(-3px) scale(1.07);
            box-shadow: 0 15px 30px rgba(44, 51, 66, 0.4);
        }
    </style>
</head>
<body>

<!-- Barra de Navegación -->
<nav class="navbar">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo" />
        </a>
    </div>
    <div class="hamburger" id="hamburger" aria-label="Menú">
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

<!-- Hero Section -->
<section class="hero-servicios">
    <div class="hero-content">
        <h1>Nuestros Servicios</h1>
        <p>Cuidado integral con tecnología de vanguardia</p>
    </div>
</section>

<!-- Servicios Grid -->
<section class="servicios-grid">
    <!-- Peluquería -->
    <div class="servicio-card">
        <div class="servicio-icon">
            <img src="${pageContext.request.contextPath}/Recursos/ImgPeluqueria 1.svg" alt="Peluquería">
        </div>
        <h3>Peluquería Canina</h3>
        <ul>
            <li>Corte de raza específica</li>
            <li>Baño terapéutico</li>
            <li>Limpieza dental</li>
        </ul>
        <a href="#contacto" class="btn-servicio">Más información</a>
    </div>
    <!-- Baños -->
    <div class="servicio-card">
        <div class="servicio-icon">
            <img src="${pageContext.request.contextPath}/Recursos/Higiene.svg" alt="Baños">
        </div>
        <h3>Spa & Bienestar</h3>
        <ul>
            <li>Baño deshedding</li>
            <li>Hidroterapia</li>
            <li>Aromaterapia</li>
        </ul>
        <a href="#contacto" class="btn-servicio">Más información</a>
    </div>
    <!-- Juegos -->
    <div class="servicio-card">
        <div class="servicio-icon">
            <img src="${pageContext.request.contextPath}/Recursos/ImgJuguetes 1.svg" alt="Juegos">
        </div>
        <h3>Área Recreativa</h3>
        <ul>
            <li>Socialización controlada</li>
            <li>Juegos interactivos</li>
            <li>Entrenamiento básico</li>
        </ul>
        <a href="#contacto" class="btn-servicio">Más información</a>
    </div>
    <!-- Revisión Médica -->
    <div class="servicio-card">
        <div class="servicio-icon">
            <img src="${pageContext.request.contextPath}/Recursos/Vacunacion.svg" alt="Médico">
        </div>
        <h3>Chequeo Completo</h3>
        <ul>
            <li>Examen físico general</li>
            <li>Pruebas de laboratorio</li>
            <li>Plan preventivo</li>
        </ul>
        <a href="#contacto" class="btn-servicio">Más información</a>
    </div>
</section>

<!-- Sección CTA (sin botón) -->
<section class="cta-section">
    <div class="cta-content">
        <h2>¿Listo para cuidar de tu mascota?</h2>
        <p>Nuestro equipo está preparado para brindarle la mejor atención</p>
    </div>
</section>

<!-- Footer -->
<footer class="footer-servicios">
    <p>© 2025 Veterinaria Santa Cruz - Todos los derechos reservados</p>
</footer>

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

<!-- Botón flotante que redirige a Citas.jsp -->
<div class="flotante-cita">
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Citas.jsp" class="btn-cita-flotante" title="Crear cita">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
        </svg>
        Crear cita
    </a>
</div>

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
</script>

</body>
</html>
