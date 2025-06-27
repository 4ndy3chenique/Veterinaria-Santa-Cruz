package Modelo;

public class Administrador {
    private int idAdmin;
    private String nombre;
    private String apellido;
    private String numero;
    private String correo;
    private String contrasena;

    // Constructor vacío
    public Administrador() {
    }

    // Constructor completo
    public Administrador(int idAdmin, String nombre, String apellido, String numero, String correo, String contrasena) {
        this.idAdmin = idAdmin;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
