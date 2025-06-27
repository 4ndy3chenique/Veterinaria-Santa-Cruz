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

document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("modalVerProveedor");
    if (modal && modal.classList.contains("mostrar")) {
        modal.style.display = "flex";
    }

    // Cerrar modal al enviar formulario de proveedor
    const formGuardar = document.querySelector('#modalVerProveedor form[action="ProveedorServlet"]');
    if (formGuardar) {
        formGuardar.addEventListener("submit", function () {
            alert("Cambios guardados correctamente.");
            cerrarModal("modalVerProveedor");
        });
    }

    const formAgregar = document.querySelector('#modalAgregarProveedor form[action="ProveedorServlet"]');
    if (formAgregar) {
        formAgregar.addEventListener("submit", function () {
            alert("Proveedor registrado correctamente.");
            cerrarModal("modalAgregarProveedor");
        });
    }
});

// Confirmación al cambiar estado del proveedor
function confirmarCambioEstado(estadoActual) {
    if (parseInt(estadoActual) === 1) {
        return confirm("¿Está seguro de que desea marcar este proveedor como INACTIVO?");
    } else {
        return confirm("¿Desea reactivar este proveedor?");
    }
}
