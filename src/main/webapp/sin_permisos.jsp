<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Acceso Denegado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .access-denied {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 15px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="access-denied text-center">
            <h1 class="text-danger mb-4">🚫 Acceso Denegado</h1>
            <p class="lead">No cuentas con los permisos necesarios para acceder a esta sección.</p>
            
            <%
                HttpSession sesion = request.getSession(false);
                String volverUrl = "index.jsp";
                String btnTexto = "Volver al inicio";
                
                if (sesion != null) {
                    if (sesion.getAttribute("username") != null) {
                        volverUrl = "VistasWeb/VistasAdmin/AdminDash.jsp";
                        btnTexto = "Volver al Panel de Administración";
                    } 
                    else if (sesion.getAttribute("usuario") != null) {
                        volverUrl = "VistasWeb/VistasCliente/indexCliente.jsp";
                        btnTexto = "Volver a Mi Perfil";
                    }
                    else if (sesion.getAttribute("recepcionista") != null) {
                        volverUrl = "VistasWeb/VistasRecep/RecepDash.jsp";
                        btnTexto = "Volver al Panel de Recepción";
                    }
                }
            %>
            
            <div class="mt-4">
                <a href="<%= volverUrl %>" class="btn btn-primary btn-lg">
                    <%= btnTexto %>
                </a>
            </div>
        </div>
    </div>
</body>
</html>