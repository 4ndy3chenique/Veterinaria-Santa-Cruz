package Modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Proveedor {
    private int idProveedor;
    private String razonSocial;
    private String ruc;
    private String direccion;
    private String correo;
    private int estado;
    private LocalDateTime fechaRegistro;

    private List<ContactoProveedor> contactos;
    private List<Producto> productos; // NUEVO

    public Proveedor() {
        this.contactos = new ArrayList<>();
        this.productos = new ArrayList<>();
    }

    public Proveedor(int idProveedor, String razonSocial, String ruc, String direccion, String correo, int estado, LocalDateTime fechaRegistro) {
        this.idProveedor = idProveedor;
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.direccion = direccion;
        this.correo = correo;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.contactos = new ArrayList<>();
        this.productos = new ArrayList<>();
    }

    // Getters y setters...

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<ContactoProveedor> getContactos() {
        return contactos;
    }

    public void setContactos(List<ContactoProveedor> contactos) {
        this.contactos = contactos;
    }

    public void agregarContacto(ContactoProveedor contacto) {
        if (this.contactos == null) {
            this.contactos = new ArrayList<>();
        }
        this.contactos.add(contacto);
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public void agregarProducto(Producto producto) {
        if (this.productos == null) {
            this.productos = new ArrayList<>();
        }
        this.productos.add(producto);
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "idProveedor=" + idProveedor +
                ", razonSocial='" + razonSocial + '\'' +
                ", ruc='" + ruc + '\'' +
                ", direccion='" + direccion + '\'' +
                ", correo='" + correo + '\'' +
                ", estado=" + estado +
                ", fechaRegistro=" + fechaRegistro +
                ", contactos=" + contactos +
                ", productos=" + productos +
                '}';
    }
}
