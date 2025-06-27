<%-- 
    Document   : GestionCitasUsuario
    Created on : 27 jun 2025, 10:41:00
    Author     : PROPIETARIO
--%>

<%-- /VistasWeb/VistasRecep/GestionCitasUsuario.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- 
    ********************************************************************************
    *** ¡¡CONFIGURACIÓN DE JSTL CRÍTICA PARA TOMCAT 10 (JAKARTA EE)!! ***
    
    Como tu servidor es Tomcat 10, DEBES usar las URIs de JSTL para Jakarta EE 9+.
    Estas URIs son las correctas y son necesarias para que las etiquetas JSTL
    (como <c:forEach>, <c:if>, <fmt:formatDate>) sean reconocidas y procesadas.
--%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%--
    Si, por alguna razón, las líneas de arriba NO funcionaran con Tomcat 10
    (lo cual sería muy inusual si tienes los JARs correctos), entonces podrías
    intentar con las URIs antiguas de Java EE 8, pero con Tomcat 10 no deberían ser necesarias:
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
--%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Gestión de Citas - Recepción</title>
    
    <%-- Enlace a Bootstrap CSS (versión 5.3.3) para un diseño moderno y responsive --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" 
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    
    <style>
        /* Estilos CSS personalizados para mejorar la apariencia */
        .container {
            margin-top: 20px;
            padding-bottom: 50px; /* Asegura espacio al final de la página */
        }
        .badge {
            font-size: 0.85em; /* Tamaño de fuente para los badges de estado */
            padding: 0.4em 0.6em; /* Padding para los badges */
            min-width: 80px; /* Ancho mínimo para que todos los badges se vean uniformes */
            display: inline-block; /* Para que min-width y text-align funcionen */
            text-align: center;
        }
    </style>
</head>
<body>

    <div class="container">
        <h1 class="my-4 text-center">Historial Completo de Citas</h1>
        <p class="lead text-muted text-center">Aquí se muestran todas las citas registradas en el sistema.</p>
        <hr>

        <%-- Sección para mostrar mensajes de éxito o error enviados desde el Servlet --%>
        <%-- Usamos 'requestScope.' para acceder explícitamente a los atributos del objeto request --%>
        <c:if test="${not empty requestScope.mensajeExito}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${requestScope.mensajeExito}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty requestScope.mensajeError}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${requestScope.mensajeError}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <%-- Verificar si la lista de citas está vacía. Si es así, mostrar un mensaje. --%>
        <c:if test="${empty requestScope.listaDeCitas}">
            <div class="alert alert-info text-center" role="alert">
                No hay citas registradas por el momento. ¡Anima a tus clientes a reservar una!
            </div>
        </c:if>

        <%-- Solo mostramos la tabla si hay citas en la lista (la lista no está vacía) --%>
        <c:if test="${not empty requestScope.listaDeCitas}">
            <div class="table-responsive"> <%-- Envuelve la tabla en un div responsivo para pantallas pequeñas --%>
                <table class="table table-striped table-hover table-bordered align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th scope="col">ID Cita</th>
                            <th scope="col">Cliente</th>
                            <th scope="col">Fecha</th>
                            <th scope="col">Hora</th>
                            <th scope="col">Veterinario Asignado</th>
                            <th scope="col">Estado</th>
                            <%-- Esta columna puede ser habilitada para botones de acción --%>
                            <%-- <th scope="col">Acciones</th> --%>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- Usamos c:forEach para iterar sobre cada objeto "cita" en "listaDeCitas" --%>
                        <c:forEach var="cita" items="${requestScope.listaDeCitas}">
                            <tr>
                                <%-- Accede a las propiedades de cada objeto "cita" usando EL (Expression Language) --%>
                                <td>${cita.idCita}</td>
                                <td>${cita.nombreCliente}</td>
                                <td>
                                    <%-- Usa fmt:formatDate para formatear el objeto java.sql.Date a una cadena legible --%>
                                    <fmt:formatDate value="${cita.fecha}" pattern="dd/MM/yyyy" />
                                </td>
                                <td>
                                    <%-- Usa fmt:formatDate para formatear el objeto java.sql.Time a una cadena legible (formato 24h) --%>
                                    <fmt:formatDate value="${cita.hora}" pattern="HH:mm" />
                                </td>
                                <td>${cita.veterinario}</td>
                                <td>
                                    <%-- Usa c:choose y c:when para aplicar clases CSS de Bootstrap (badges) según el valor del estado --%>
                                    <c:choose>
                                        <c:when test="${cita.estado eq 'Confirmada'}">
                                            <span class="badge bg-success">${cita.estado}</span>
                                        </c:when>
                                        <c:when test="${cita.estado eq 'Pendiente'}">
                                            <span class="badge bg-warning text-dark">${cita.estado}</span>
                                        </c:when>
                                        <c:when test="${cita.estado eq 'Cancelada'}">
                                            <span class="badge bg-danger">${cita.estado}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${cita.estado}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <%-- Ejemplo de celda con botones de acción (descomentar y habilitar Servlets si se usan) --%>
                                <%-- 
                                <td> 
                                    <a href="EditarCitaServlet?id=${cita.idCita}" class="btn btn-sm btn-primary me-1">Editar</a> 
                                    <a href="EliminarCitaServlet?id=${cita.idCita}" class="btn btn-sm btn-danger" 
                                       onclick="return confirm('¿Estás seguro de que quieres eliminar esta cita?');">Eliminar</a>
                                </td> 
                                --%>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>

    <%-- Bootstrap JS (Bundle con Popper) - Necesario para funcionalidades interactivas como las alertas dismissibles --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" 
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>