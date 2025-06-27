package Modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Producto {
    private int idProducto;
    private String nombreProducto;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private String unidadMedida;
    private String imagen; // nombre del archivo o ruta relativa
    private int estado;
    private int idProveedor;
    private Timestamp fechaRegistro;

    // ----- Constructores -----
    public Producto() {}

    public Producto(int idProducto, String nombreProducto, String descripcion, BigDecimal precio, int stock,
                    String unidadMedida, String imagen, int estado, int idProveedor, Timestamp fechaRegistro) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.imagen = imagen;
        this.estado = estado;
        this.idProveedor = idProveedor;
        this.fechaRegistro = fechaRegistro;
    }

    // ----- Getters y Setters -----
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
