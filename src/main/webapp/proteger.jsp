<%-- ESTE ARCHIVO NO DEBE TENER DIRECTIVAS @page --%>
<%@page import="jakarta.servlet.http.HttpSession"%>
<%
    // Configuración de seguridad de caché
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Obtener sesión sin crear nueva
    HttpSession sesion = request.getSession(false);
    String contextPath = request.getContextPath();
    String requestURI = request.getRequestURI();

    // Validar intentos de acceso mediante URL directa
    if(requestURI.contains(";") || requestURI.contains("%3B") || requestURI.contains("jsessionid")) {
        response.sendRedirect(contextPath + "/sin_permisos.jsp");
        return;
    }

    // Obtener atributos de sesión según tu LoginServlet
    Object usuario = (sesion != null) ? sesion.getAttribute("usuario") : null;
    Object recepcionista = (sesion != null) ? sesion.getAttribute("recepcionista") : null;
    Object username = (sesion != null) ? sesion.getAttribute("username") : null;

    // Determinar rol del usuario
    String rol = null;
    if (username != null) {
        rol = "admin";
    } else if (recepcionista != null) {
        rol = "recepcionista";
    } else if (usuario != null) {
        rol = "cliente";
    }

    // Identificar área de acceso
    boolean accedeAdmin = requestURI.contains("/VistasAdmin/");
    boolean accedeCliente = requestURI.contains("/VistasCliente/");
    boolean accedeRecep = requestURI.contains("/VistasRecep/");

    // Redirección si no hay sesión activa
    if (sesion == null) {
        response.sendRedirect(contextPath + "/index.jsp");
        return;
    }

    // Validación de permisos por rol
    if (accedeAdmin && !"admin".equals(rol)) {
        response.sendRedirect(contextPath + "/sin_permisos.jsp");
        return;
    }

    if (accedeCliente && !"cliente".equals(rol)) {
        response.sendRedirect(contextPath + "/sin_permisos.jsp");
        return;
    }

    if (accedeRecep && !"recepcionista".equals(rol)) {
        response.sendRedirect(contextPath + "/sin_permisos.jsp");
        return;
    }

    // Redirección si tiene sesión pero accede a ruta incorrecta
    if (rol != null) {
        if ("admin".equals(rol) && (accedeCliente || accedeRecep)) {
            response.sendRedirect(contextPath + "/VistasWeb/VistasAdmin/AdminDash.jsp");
            return;
        }
        
        if ("cliente".equals(rol) && (accedeAdmin || accedeRecep)) {
            response.sendRedirect(contextPath + "/VistasWeb/VistasCliente/indexCliente.jsp");
            return;
        }
        
        if ("recepcionista".equals(rol) && (accedeAdmin || accedeCliente)) {
            response.sendRedirect(contextPath + "/VistasWeb/VistasRecep/RecepDash.jsp");
            return;
        }
    }
%>