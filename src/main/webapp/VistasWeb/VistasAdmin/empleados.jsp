<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Gestión de Empleados</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { padding-top: 20px; }
        .container { max-width: 1200px; }
        h1, h2 { margin-bottom: 20px; }
        table { margin-bottom: 30px; }
        .alert { margin-top: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Gestión de Empleados</h1>

        <%-- Mostrar mensajes del Servlet --%>
        <c:if test="${not empty requestScope.mensaje}">
            <div class="alert alert-${requestScope.tipoMensaje || 'info'}" role="alert">
                ${requestScope.mensaje}
            </div>
        </c:if>

        <hr>

        <h2>Veterinarios</h2>
        <button type="button" class="btn btn-primary mb-3" data-toggle="modal" data-target="#addVeterinarioModal">Agregar Nuevo Veterinario</button>

        <table class="table table-bordered table-striped">
            <thead class="thead-dark">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Teléfono</th>
                    <th>DNI</th>
                    <th>Especialidad</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty requestScope.listaVeterinarios}">
                        <c:forEach var="vet" items="${requestScope.listaVeterinarios}">
                            <tr>
                                <td>${vet.idVeterinario}</td>
                                <td>${vet.nombre}</td>
                                <td>${vet.apellido}</td>
                                <td>${vet.numero}</td>
                                <td>${vet.dni}</td>
                                <td>${vet.especialidad}</td>
                                <td>
                                    <button type="button" class="btn btn-warning btn-sm"
                                            onclick="editVeterinario(${vet.idVeterinario}, '${vet.nombre}', '${vet.apellido}', '${vet.numero}', '${vet.dni}', '${vet.especialidad}')">
                                        Editar
                                    </button>
                                    <a href="EmpleadoServlet?accion=eliminarveterinario&id=${vet.idVeterinario}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('¿Está seguro de que desea eliminar a este veterinario?');">
                                        Eliminar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7">No hay veterinarios registrados.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>

        <hr>

        <h2>Recepcionistas</h2>
        <button type="button" class="btn btn-primary mb-3" data-toggle="modal" data-target="#addRecepcionistaModal">Agregar Nuevo Recepcionista</button>

        <table class="table table-bordered table-striped">
            <thead class="thead-dark">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Teléfono</th>
                    <th>DNI</th>
                    <th>Correo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty requestScope.listaRecepcionistas}">
                        <c:forEach var="rec" items="${requestScope.listaRecepcionistas}">
                            <tr>
                                <td>${rec.idRecepcionista}</td>
                                <td>${rec.nombre}</td>
                                <td>${rec.apellido}</td>
                                <td>${rec.numero}</td>
                                <td>${rec.dni}</td>
                                <td>${rec.correo}</td>
                                <td>
                                    <button type="button" class="btn btn-warning btn-sm"
                                            onclick="editRecepcionista(${rec.idRecepcionista}, '${rec.nombre}', '${rec.apellido}', '${rec.numero}', '${rec.dni}', '${rec.correo}')">
                                        Editar
                                    </button>
                                    <a href="EmpleadoServlet?accion=eliminarrecepcionista&id=${rec.idRecepcionista}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('¿Está seguro de que desea eliminar a este recepcionista?');">
                                        Eliminar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7">No hay recepcionistas registrados.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>

        <%-- Modales para agregar y editar --%>

        <div class="modal fade" id="addVeterinarioModal" tabindex="-1" role="dialog" aria-labelledby="addVeterinarioModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="EmpleadoServlet" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title" id="addVeterinarioModalLabel">Agregar Veterinario</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="guardarVeterinario">
                            <div class="form-group">
                                <label for="vet_add_nombre">Nombre:</label>
                                <input type="text" class="form-control" id="vet_add_nombre" name="nombre" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_add_apellido">Apellido:</label>
                                <input type="text" class="form-control" id="vet_add_apellido" name="apellido" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_add_numero">Teléfono:</label>
                                <input type="text" class="form-control" id="vet_add_numero" name="numero" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_add_dni">DNI:</label>
                                <input type="text" class="form-control" id="vet_add_dni" name="dni" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_add_especialidad">Especialidad:</label>
                                <input type="text" class="form-control" id="vet_add_especialidad" name="especialidad" required>
                            </div>
                            <%-- NOTA: No hay campos de correo ni contraseña para Veterinarios --%>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cerrar</button>
                            <button type="submit" class="btn btn-primary">Guardar Veterinario</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="modal fade" id="editVeterinarioModal" tabindex="-1" role="dialog" aria-labelledby="editVeterinarioModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="EmpleadoServlet" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editVeterinarioModalLabel">Editar Veterinario</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="guardarVeterinario">
                            <input type="hidden" name="idVeterinario" id="edit_idVeterinario">
                            <div class="form-group">
                                <label for="vet_edit_nombre">Nombre:</label>
                                <input type="text" class="form-control" id="vet_edit_nombre" name="nombre" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_edit_apellido">Apellido:</label>
                                <input type="text" class="form-control" id="vet_edit_apellido" name="apellido" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_edit_numero">Teléfono:</label>
                                <input type="text" class="form-control" id="vet_edit_numero" name="numero" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_edit_dni">DNI:</label>
                                <input type="text" class="form-control" id="vet_edit_dni" name="dni" required>
                            </div>
                            <div class="form-group">
                                <label for="vet_edit_especialidad">Especialidad:</label>
                                <input type="text" class="form-control" id="vet_edit_especialidad" name="especialidad" required>
                            </div>
                            <%-- NOTA: No hay campos de correo ni contraseña para Veterinarios --%>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cerrar</button>
                            <button type="submit" class="btn btn-primary">Actualizar Veterinario</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="modal fade" id="addRecepcionistaModal" tabindex="-1" role="dialog" aria-labelledby="addRecepcionistaModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="EmpleadoServlet" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title" id="addRecepcionistaModalLabel">Agregar Recepcionista</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="guardarRecepcionista">
                            <div class="form-group">
                                <label for="R_Nombre">Nombre:</label>
                                <input type="text" class="form-control" id="R_Nombre" name="R_Nombre" required>
                            </div>
                            <div class="form-group">
                                <label for="R_Apellido">Apellido:</label>
                                <input type="text" class="form-control" id="R_Apellido" name="R_Apellido" required>
                            </div>
                            <div class="form-group">
                                <label for="R_Numero">Teléfono:</label>
                                <input type="text" class="form-control" id="R_Numero" name="R_Numero" required>
                            </div>
                            <div class="form-group">
                                <label for="R_Dni">DNI:</label>
                                <input type="text" class="form-control" id="R_Dni" name="R_Dni" required>
                            </div>
                            <div class="form-group">
                                <label for="R_Correo">Correo:</label>
                                <input type="email" class="form-control" id="R_Correo" name="R_Correo" required>
                            </div>
                            <div class="form-group">
                                <label for="R_Contrasena">Contraseña:</label>
                                <input type="password" class="form-control" id="R_Contrasena" name="R_Contrasena" required>
                                <small class="form-text text-muted">La contraseña se encriptará (idealmente).</small>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cerrar</button>
                            <button type="submit" class="btn btn-primary">Guardar Recepcionista</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="modal fade" id="editRecepcionistaModal" tabindex="-1" role="dialog" aria-labelledby="editRecepcionistaModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="EmpleadoServlet" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editRecepcionistaModalLabel">Editar Recepcionista</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="guardarRecepcionista">
                            <input type="hidden" name="idRecepcionista" id="edit_idRecepcionista">
                            <div class="form-group">
                                <label for="edit_R_Nombre">Nombre:</label>
                                <input type="text" class="form-control" id="edit_R_Nombre" name="R_Nombre" required>
                            </div>
                            <div class="form-group">
                                <label for="edit_R_Apellido">Apellido:</label>
                                <input type="text" class="form-control" id="edit_R_Apellido" name="R_Apellido" required>
                            </div>
                            <div class="form-group">
                                <label for="edit_R_Numero">Teléfono:</label>
                                <input type="text" class="form-control" id="edit_R_Numero" name="R_Numero" required>
                            </div>
                            <div class="form-group">
                                <label for="edit_R_Dni">DNI:</label>
                                <input type="text" class="form-control" id="edit_R_Dni" name="R_Dni" required>
                            </div>
                            <div class="form-group">
                                <label for="edit_R_Correo">Correo:</label>
                                <input type="email" class="form-control" id="edit_R_Correo" name="R_Correo" required>
                            </div>
                            <div class="form-group">
                                <label for="edit_R_Contrasena">Nueva Contraseña (opcional):</label>
                                <input type="password" class="form-control" id="edit_R_Contrasena" name="R_Contrasena">
                                <small class="form-text text-muted">Dejar en blanco para no cambiar la contraseña.</small>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cerrar</button>
                            <button type="submit" class="btn btn-primary">Actualizar Recepcionista</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

    <script>
        // Función para poblar el modal de edición de Veterinario
        function editVeterinario(id, nombre, apellido, numero, dni, especialidad) {
            $('#edit_idVeterinario').val(id);
            $('#vet_edit_nombre').val(nombre);
            $('#vet_edit_apellido').val(apellido);
            $('#vet_edit_numero').val(numero);
            $('#vet_edit_dni').val(dni);
            $('#vet_edit_especialidad').val(especialidad);
            $('#editVeterinarioModal').modal('show');
        }

        // Función para poblar el modal de edición de Recepcionista
        function editRecepcionista(id, nombre, apellido, numero, dni, correo) {
            $('#edit_idRecepcionista').val(id);
            $('#edit_R_Nombre').val(nombre);
            $('#edit_R_Apellido').val(apellido);
            $('#edit_R_Numero').val(numero);
            $('#edit_R_Dni').val(dni);
            $('#edit_R_Correo').val(correo);
            $('#edit_R_Contrasena').val(''); // Asegurar que el campo de contraseña esté vacío para que el usuario la introduzca si quiere cambiarla
            $('#editRecepcionistaModal').modal('show');
        }
    </script>
</body>
</html>