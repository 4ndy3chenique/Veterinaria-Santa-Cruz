/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function abrirModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = 'flex';
    }
}

function cerrarModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = 'none';
    }
}

// Confirmar antes de eliminar un cliente
function confirmarEliminacion(id) {
    if (confirm("¿Estás seguro de que deseas eliminar este cliente? Esta acción no se puede deshacer.")) {
        window.location.href = "ClienteRServlet?accion=eliminar&id=" + id;
    }
    return false;
}

// Validar campos del formulario de cliente
function validarFormularioCliente(form) {
    const dni = form.querySelector('input[name="dni"]').value;
    const telefono = form.querySelector('input[name="telefono"]').value;

    if (!/^\d{8}$/.test(dni)) {
        alert('El DNI debe tener exactamente 8 dígitos.');
        return false;
    }

    if (!/^9\d{8}$/.test(telefono)) {
        alert('El teléfono debe tener 9 dígitos y comenzar con 9.');
        return false;
    }

    return true;
}

document.addEventListener("DOMContentLoaded", () => {
    const modalEditar = document.getElementById("modalVerCliente");
    if (modalEditar && modalEditar.classList.contains("mostrar")) {
        modalEditar.style.display = "flex";
    }

    // Mostrar alerta y cerrar modal al guardar edición
    const formEditar = document.querySelector('#modalVerCliente form[action="ClienteRServlet"]');
    if (formEditar) {
        formEditar.addEventListener("submit", function () {
            alert("Cambios guardados correctamente.");
        });
    }

    const formAgregar = document.querySelector('#modalAgregarCliente form[action="ClienteRServlet"]');
    if (formAgregar) {
        formAgregar.addEventListener("submit", function () {
            alert("Cliente registrado correctamente.");
        });
    }

    // Alternar modo noche (si no se maneja por otro JS)
    const modoBtn = document.getElementById("modoNocheBtn");
    if (modoBtn) {
        modoBtn.addEventListener("click", () => {
            document.body.classList.toggle("dark-mode");
        });
    }
});