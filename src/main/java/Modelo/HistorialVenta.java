package Modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class HistorialVenta {

    private int idVenta;
    private int idUsuario;
    private int idProducto;
    private int cantidad;
    private BigDecimal total;
    private String estado;
    private String comprobante; // opcional, si almacenas ruta de archivo
    private Timestamp fechaVenta;
    private String tipoVenta;   // "Recepcionista" o "ClienteWeb"

    private String nombreUsuario;
    private String nombreProducto;

    // Constructor vacío
    public HistorialVenta() {}

    // Constructor completo
    public HistorialVenta(int idVenta, int idUsuario, int idProducto, int cantidad, BigDecimal total, String estado, String comprobante, Timestamp fechaVenta, String tipoVenta, String nombreUsuario, String nombreProducto) {
        this.idVenta = idVenta;
        this.idUsuario = idUsuario;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.total = total;
        this.estado = estado;
        this.comprobante = comprobante;
        this.fechaVenta = fechaVenta;
        this.tipoVenta = tipoVenta;
        this.nombreUsuario = nombreUsuario;
        this.nombreProducto = nombreProducto;
    }

    // Getters y Setters
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public Timestamp getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Timestamp fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
