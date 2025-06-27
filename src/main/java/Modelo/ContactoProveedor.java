package Modelo;

public class ContactoProveedor {
    private int idContacto;
    private int idProveedor;
    private String nombreContacto;
    private String cargo;
    private String telefono;
    private String correoContacto;

    // Constructor vacío
    public ContactoProveedor() {}

    // Constructor completo
    public ContactoProveedor(int idContacto, int idProveedor, String nombreContacto, String cargo, String telefono, String correoContacto) {
        this.idContacto = idContacto;
        this.idProveedor = idProveedor;
        this.nombreContacto = nombreContacto;
        this.cargo = cargo;
        this.telefono = telefono;
        this.correoContacto = correoContacto;
    }

    // Getters y Setters
    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        this.correoContacto = correoContacto;
    }

    @Override
    public String toString() {
        return "ContactoProveedor{" +
                "idContacto=" + idContacto +
                ", idProveedor=" + idProveedor +
                ", nombreContacto='" + nombreContacto + '\'' +
                ", cargo='" + cargo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correoContacto='" + correoContacto + '\'' +
                '}';
    }
}
