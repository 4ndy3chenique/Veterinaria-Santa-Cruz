<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Contacto | Veterinaria Santa Cruz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/contacto.css" />
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
    <!-- Si usas FontAwesome para iconos sociales, incluye aquí -->
    <!-- <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" /> -->
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
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto" class="active-link">Contacto</a>
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
<div id="sidebarOverlay"></div>

<!-- Contenido de contacto solo texto -->
<main class="contacto-texto">
    <h1>Contacto</h1>
    <p>
        ¿Tienes alguna pregunta, sugerencia o necesitas información sobre nuestros servicios?<br>
        Puedes comunicarte con nosotros a través de los siguientes medios:
    </p>
    <div class="contacto-datos">
        <p><strong>Dirección:</strong> Av. Siempre Viva 742, Lima, Perú</p>
        <p><strong>Teléfono:</strong> +51 987 654 321</p>
        <p><strong>Email:</strong> contacto@veterinariasantacruz.com</p>
        <p><strong>Horario de atención:</strong> Lunes a Viernes, 9:00 - 18:00 hrs</p>
    </div>
    <div class="contacto-extra">
        <p>
            También puedes visitarnos en nuestra clínica o escribirnos por correo electrónico.<br>
            ¡Estaremos encantados de ayudarte y cuidar de tu mascota!
        </p>
    </div>
</main>

<!-- Footer mejorado -->
<footer>
    <div class="footer-container">
        <div class="footer-section">
            <h4>Contacto</h4>
            <p>Av. Siempre Viva 742, Lima, Perú</p>
            <p>Teléfono: +51 987 654 321</p>
            <p>Email: contacto@veterinariasantacruz.com</p>
        </div>
        <div class="footer-section">
            <h4>Síguenos</h4>
            <div class="redes-sociales">
                <span>Facebook</span>
                <span>Instagram</span>
                <span>Twitter</span>
            </div>
        </div>
    </div>
    <div class="footer-bottom">
        <p>© 2025 Veterinaria Santa Cruz - Todos los derechos reservados</p>
    </div>
</footer>


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
