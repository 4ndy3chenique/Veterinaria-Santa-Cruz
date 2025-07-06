package Modelo;

import java.math.BigDecimal;

public class CarritoItem {

    private int idCarrito;
    private String nombreProducto;
    private BigDecimal precio;
    private int cantidad;
    private BigDecimal subtotal;

    public CarritoItem() {
    }

    public CarritoItem(int idCarrito, String nombreProducto, BigDecimal precio, int cantidad, BigDecimal subtotal) {
        this.idCarrito = idCarrito;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(int idCarrito) {
        this.idCarrito = idCarrito;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "CarritoItem{" +
                "idCarrito=" + idCarrito +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", subtotal=" + subtotal +
                '}';
    }
}
