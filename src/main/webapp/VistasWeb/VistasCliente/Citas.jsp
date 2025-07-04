<%@ include file="/proteger.jsp" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="Modelo.UsuarioCliente" %>
<%@ page import="java.util.List" %>
<%@ page import="Modelo.Veterinario" %>
<%-- No es estrictamente necesario importar UsuarioCitas aquí a menos que vayas a instanciarlo o usar sus métodos directamente en el JSP --%>
<%-- <%@ page import="Modelo.UsuarioCitas" %> --%>

<%
    // Obtener el objeto usuario de la sesión
    UsuarioCliente usuarioObj = (UsuarioCliente) session.getAttribute("usuario");
    String usuarioNombre = (usuarioObj != null && usuarioObj.getNombre() != null) ? usuarioObj.getNombre() : ""; // Renombrado para claridad
    String mensaje = request.getParameter("mensaje");

    // Fecha y hora actuales para validación en el cliente y valores mínimos
    LocalDate fechaActual = LocalDate.now();
    LocalTime horaActual = LocalTime.now();

    // Obtener la lista de veterinarios del request (pasada por el servlet)
    // Asegúrate del cast correcto si el tipo genérico es importante
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");
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
        #sidebarOverlay { display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.4); z-index: 1500; }
        #sidebarOverlay.active { display: block; }
        .sidebar-perfil h2 { margin: 0 0 20px 20px; font-weight: 600; font-size: 22px; }
        .sidebar-perfil a { display: block; padding: 15px 25px; color: #333; text-decoration: none; border-bottom: 1px solid #eee; font-size: 16px; transition: background 0.2s; }
        .sidebar-perfil a:hover { background: #f0f0f0; }
        .registro-cita {
            max-width: 600px; margin: 40px auto 60px auto; padding: 20px;
            border: 1px solid #ccc; border-radius: 8px; background: #fff;
        }
        .registro-cita h3 { text-align: center; margin-bottom: 20px; font-weight: 600; font-size: 1.8rem; color: #3aafa9; }
        .registro-cita label { display: block; margin: 12px 0 6px; font-weight: 500; }
        .registro-cita input, .registro-cita select, .registro-cita textarea {
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

<%
    if (mensaje != null) {
        if ("registrado".equals(mensaje)) {
%>
            <div class="alert-success">¡Cita registrada correctamente!</div>
<%
        } else if ("error_registro".equals(mensaje)) {
%>
            <div class="alert-error">Error al registrar la cita. Por favor, inténtalo de nuevo.</div>
<%
        } else if ("veterinario_no_encontrado".equals(mensaje)) { // Mensaje corregido
%>
            <div class="alert-error">Error: No se pudo encontrar el veterinario seleccionado. Por favor, intente de nuevo.</div>
<%
        } else if ("formato_invalido".equals(mensaje)) {
%>
            <div class="alert-error">Error: Formato de fecha u hora inválido.</div>
<%
        } else if ("error_formato_numero".equals(mensaje)) {
%>
            <div class="alert-error">Error: Datos numéricos inválidos (ID de veterinario).</div>
<%
        } else if ("error_inesperado".equals(mensaje)) {
%>
            <div class="alert-error">Se produjo un error inesperado. Por favor, intente de nuevo más tarde.</div>
<%
        } else if ("fecha_hora_pasada".equals(mensaje)) {
%>
            <div class="alert-error">No puedes seleccionar una fecha u hora pasada para la cita.</div>
<%
        }
    }
%>

<section class="registro-cita">
    <h3>Registrar Nueva Cita</h3>
    <form id="formCita" action="${pageContext.request.contextPath}/UsuarioCitasServlet" method="post" onsubmit="return validarFechaHora()">
        <input type="hidden" name="accion" value="registrar">
        <%-- El idCliente ya se obtiene de la sesión en el Servlet, este hidden input es redundante para el registro
             pero si lo quieres mantener por si acaso, no causa daño. --%>
        <%-- <input type="hidden" name="idCliente" value="<%= usuarioObj != null ? usuarioObj.getIdUsuario() : "" %>"> --%>

        <label for="nombreCliente">Cliente:</label>
        <%-- Este campo es solo para visualización, ya que el ID se obtiene de la sesión en el Servlet --%>
        <input type="text" id="nombreCliente" name="nombreClienteDisplay" value="<%= usuarioNombre %>" required readonly />
        <small class="form-text text-muted">Este es tu nombre de usuario. Tu ID de cliente se gestiona automáticamente.</small>

        <label for="fecha">Fecha:</label>
        <input type="date" id="fecha" name="fecha" min="<%= fechaActual %>" required />
        <div id="errorFecha" class="error-message">No puedes seleccionar una fecha pasada.</div>

        <label for="hora">Hora:</label>
        <input type="time" id="hora" name="hora" required />
        <div id="errorHora" class="error-message">No puedes seleccionar una hora pasada para hoy.</div>

        <label for="idVeterinario">Veterinario:</label>
        <select name="idVeterinario" id="idVeterinario" required>
            <option value="">-- Seleccione un veterinario --</option>
            <%
                // Eliminar los comentarios de depuración que imprimen en el HTML final
                // if (listaVeterinarios == null) {
                //     out.println("<option value=\"\" disabled>DEBUG JSP: listaVeterinarios es NULL</option>");
                // } else if (listaVeterinarios.isEmpty()) {
                //     out.println("<option value=\"\" disabled>DEBUG JSP: listaVeterinarios está VACÍA</option>");
                // } else {
                //     out.println("<option value=\"\" disabled>DEBUG JSP: " + listaVeterinarios.size() + " veterinarios encontrados</option>");
                // }
            %>
            <%
                if (listaVeterinarios != null && !listaVeterinarios.isEmpty()) {
                    for (Modelo.Veterinario vet : listaVeterinarios) {
            %>
                        <option value="<%= vet.getIdVeterinario()%>">
                            Dr(a). <%= vet.getNombre()%> <%= vet.getApellido()%> - <%= vet.getEspecialidad()%>
                        </option>
            <%
                    }
                } else {
            %>
                    <option value="" disabled>No se han encontrado veterinarios disponibles.</option>
            <%
                }
            %>
        </select>
        <label for="motivo">Motivo de la Cita:</label>
        <textarea id="motivo" name="motivo" rows="3" required></textarea>

        <div class="button-group">
            <button type="submit">Registrar Cita</button>
            <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp'">Salir</button>
        </div>
    </form>
</section>

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

    // Validación de fecha y hora en el lado del cliente
    function validarFechaHora() {
        const fechaInput = document.getElementById('fecha');
        const horaInput = document.getElementById('hora');
        const errorFecha = document.getElementById('errorFecha');
        const errorHora = document.getElementById('errorHora');

        const hoy = new Date();
        // Normalizar la fecha de hoy a YYYY-MM-DD para comparación
        const hoySoloFecha = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());
        const fechaSeleccionada = new Date(fechaInput.value);
        // Normalizar la fecha seleccionada a YYYY-MM-DD para comparación
        const fechaSeleccionadaSoloFecha = new Date(fechaSeleccionada.getFullYear(), fechaSeleccionada.getMonth(), fechaSeleccionada.getDate());

        errorFecha.style.display = 'none';
        errorHora.style.display = 'none';

        // Validar que la fecha no sea pasada
        if (fechaSeleccionadaSoloFecha < hoySoloFecha) {
            errorFecha.style.display = 'block';
            fechaInput.focus();
            return false;
        }

        // Si la fecha seleccionada es hoy, validar la hora
        if (fechaSeleccionadaSoloFecha.getTime() === hoySoloFecha.getTime()) {
            const horaSeleccionadaPartes = horaInput.value.split(':');
            const horaSeleccionadaObj = new Date();
            horaSeleccionadaObj.setHours(parseInt(horaSeleccionadaPartes[0], 10)); // Base 10
            horaSeleccionadaObj.setMinutes(parseInt(horaSeleccionadaPartes[1], 10)); // Base 10
            horaSeleccionadaObj.setSeconds(0);
            horaSeleccionadaObj.setMilliseconds(0);

            const horaActualObj = new Date();
            horaActualObj.setSeconds(0);
            horaActualObj.setMilliseconds(0);

            if (horaSeleccionadaObj < horaActualObj) {
                errorHora.style.display = 'block';
                horaInput.focus();
                return false;
            }
        }
        return true;
    }

    // Establecer la fecha mínima para el input de fecha (hoy) al cargar la página
    document.addEventListener('DOMContentLoaded', function() {
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0'); // Mes + 1 porque es 0-indexado
        const day = String(today.getDate()).padStart(2, '0');
        const minDate = `${year}-${month}-${day}`;
        document.getElementById('fecha').setAttribute('min', minDate);
    });
</script>
</body>
</html>