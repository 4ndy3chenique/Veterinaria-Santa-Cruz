<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="Modelo.UsuarioCliente" %>
<%
    // Obtener el objeto usuario de la sesión
    UsuarioCliente usuarioObj = (UsuarioCliente) session.getAttribute("usuario");
    String usuario = usuarioObj != null ? usuarioObj.getNombre() : ""; // Cambia getNombre() si tu getter es otro
    String mensaje = request.getParameter("mensaje");
    LocalDate fechaActual = LocalDate.now();
    LocalTime horaActual = LocalTime.now();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Registrar Cita | Veterinaria Santa Cruz</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
    <style>
        body { font-family: 'Poppins', Arial, sans-serif; background: #f8f8f8; margin: 0; }
        .navbar {
            display: flex; align-items: center; justify-content: space-between;
            background: #3aafa9; padding: 0 30px; height: 70px;
        }
        .logo-container { display: flex; align-items: center; }
        .logo { height: 56px; }
        .nav-links { display: flex; align-items: center; }
        .center-links a {
            color: #fff; text-decoration: none; margin: 0 16px; font-weight: 500; font-size: 1.1rem;
            transition: color 0.2s;
        }
        .center-links a:hover, .center-links a.active-link { color: #17252a; border-bottom: 2px solid #fff; }
        .buttons .btn { background: #def2f1; color: #3aafa9; padding: 8px 18px; border-radius: 20px; text-decoration: none; font-weight: 600; margin-left: 10px; }
        .sidebar-perfil {
            display: none; position: fixed; top: 0; right: 0; width: 280px; height: 100%;
            background: #fff; box-shadow: -2px 0 5px rgba(0,0,0,0.3); z-index: 2000; padding-top: 50px; overflow-y: auto;
        }
        .sidebar-perfil.active { display: block; }
        .sidebar-perfil h2 { margin: 0 0 20px 20px; font-weight: 600; font-size: 22px; }
        .sidebar-perfil a { display: block; padding: 15px 25px; color: #333; text-decoration: none; border-bottom: 1px solid #eee; font-size: 16px; transition: background 0.2s; }
        .sidebar-perfil a:hover { background: #f0f0f0; }
        #sidebarOverlay { display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.4); z-index: 1500; }
        #sidebarOverlay.active { display: block; }
        .registro-cita {
            max-width: 600px; margin: 40px auto 60px auto; padding: 20px;
            border: 1px solid #ccc; border-radius: 8px; background: #fff;
        }
        .registro-cita h3 { text-align: center; margin-bottom: 20px; font-weight: 600; font-size: 1.8rem; color: #3aafa9; }
        .registro-cita label { display: block; margin: 12px 0 6px; font-weight: 500; }
        .registro-cita input, .registro-cita select {
            width: 100%; padding: 10px; border: 1px solid #999; border-radius: 4px; font-size: 1rem; box-sizing: border-box;
        }
        .registro-cita .button-group { display: flex; gap: 15px; margin-top: 20px; }
        .registro-cita button {
            flex: 1; padding: 12px; font-size: 1.2rem; font-weight: 600; cursor: pointer; border-radius: 5px; border: none; transition: background 0.3s;
        }
        .registro-cita button[type="submit"] { background: #3aafa9; color: white; }
        .registro-cita button[type="submit"]:hover { background: #2f8f8a; }
        .registro-cita button[type="button"] { background: #f44336; color: white; }
        .registro-cita button[type="button"]:hover { background: #d32f2f; }
        .alert-success, .alert-error {
            max-width: 600px; margin: 20px auto; padding: 15px; border-radius: 5px; font-size: 1rem; text-align: center;
        }
        .alert-success { background: #d4edda; color: #155724; }
        .alert-error { background: #f8d7da; color: #721c24; }
        .error-message { color: #dc3545; font-size: 0.9rem; margin-top: 5px; display: none; }
    </style>
</head>
<body>
<!-- Barra de Navegación -->
<nav class="navbar">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo Veterinaria Santa Cruz" class="logo" />
        </a>
    </div>
    <div class="nav-links">
        <div class="center-links">
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp" id="link-productos">Productos</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto">Contacto</a>
        </div>
        <div class="buttons">
            <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
        </div>
    </div>
</nav>

<!-- Mensajes de éxito o error -->
<% if ("registrado".equals(mensaje)) { %>
    <div class="alert-success">¡Cita registrada correctamente!</div>
<% } else if ("error".equals(mensaje)) { %>
    <div class="alert-error">Error al registrar la cita. Por favor, inténtalo de nuevo.</div>
<% } %>

<!-- Formulario para registrar cita -->
<section class="registro-cita">
    <h3>Registrar Nueva Cita</h3>
    <form id="formCita" action="${pageContext.request.contextPath}/UsuarioCitasServlet" method="post" onsubmit="return validarFechaHora()">
        <label for="nombreCliente">Cliente:</label>
        <input type="text" id="nombreCliente" name="nombreCliente" value="<%= usuario %>" required readonly />

        <label for="fecha">Fecha:</label>
        <input type="date" id="fecha" name="fecha" min="<%= fechaActual %>" required />
        <div id="errorFecha" class="error-message">No puedes seleccionar una fecha pasada.</div>

        <label for="hora">Hora:</label>
        <input type="time" id="hora" name="hora" required />
        <div id="errorHora" class="error-message">No puedes seleccionar una hora pasada para hoy.</div>

        <label for="veterinario">Veterinario:</label>
        <select id="veterinario" name="veterinario" required>
            <option value="Dra. Laura Gómez ">Dra. Laura Gómez </option>
            <option value="Dr. Miguel Santos ">Dr. Miguel Santos  </option>
            <option value="Dra. Carla Ramírez ">Dra. Carla Ramírez  </option>
            <option value="Dr. Lucas Robinson ">Dr. Lucas Robinson  </option>
        </select>

        <label for="estado">Motivo:</label>
        <select id="estado" name="estado" required>
            <option value="Medicina Interna">Medicina Interna</option>
            <option value="Cirugía">Cirugía</option>
            <option value="Dermatología">Dermatología</option>
            <option value="Desparasitación">Desparasitación</option>
            <option value="Chequeo General">Chequeo General</option>
        </select>
        <div class="button-group">
            <button type="submit">Registrar Cita</button>
            <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp'">Salir</button>
        </div>
    </form>
</section>

<!-- Sidebar perfil -->
<div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
    <h2 id="perfilTitle">Mi Perfil</h2>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecompras.jsp">Historial de compras/servicios</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecitas.jsp">Citas agendadas</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
</div>
<div id="sidebarOverlay"></div>

<script>
    // Sidebar perfil
    document.getElementById('verPerfilBtn').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.add('active');
        document.getElementById('sidebarOverlay').classList.add('active');
    });
    document.getElementById('sidebarOverlay').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.remove('active');
        this.classList.remove('active');
    });

    // Validación de fecha y hora
    function validarFechaHora() {
        const fechaInput = document.getElementById('fecha');
        const horaInput = document.getElementById('hora');
        const errorFecha = document.getElementById('errorFecha');
        const errorHora = document.getElementById('errorHora');
        const hoy = new Date();
        const fechaSeleccionada = new Date(fechaInput.value);
        errorFecha.style.display = 'none';
        errorHora.style.display = 'none';
        if (fechaSeleccionada < new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate())) {
            errorFecha.style.display = 'block';
            fechaInput.focus();
            return false;
        }
        if (fechaSeleccionada.getDate() === hoy.getDate() && fechaSeleccionada.getMonth() === hoy.getMonth() && fechaSeleccionada.getFullYear() === hoy.getFullYear()) {
            const horaSeleccionada = horaInput.value.split(':');
            const horaActual = hoy.getHours();
            const minutoActual = hoy.getMinutes();
            if (parseInt(horaSeleccionada[0]) < horaActual || (parseInt(horaSeleccionada[0]) === horaActual && parseInt(horaSeleccionada[1]) < minutoActual)) {
                errorHora.style.display = 'block';
                horaInput.focus();
                return false;
            }
        }
        return true;
    }
</script>
</body>
</html>