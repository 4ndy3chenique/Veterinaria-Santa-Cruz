package Modelo;

public class Recepcionista {
    private int idRecepcionista;
    private String nombre;
    private String apellido;
    private String numero;
    private String dni;
    private String correo;
    private String contrasena; // Es importante no exponer esto directamente
    public String getIdRecepcionista;
    

    public Recepcionista() {
    }

    public Recepcionista(int idRecepcionista, String nombre, String apellido, String numero, String dni, String correo, String contrasena) {
        this.idRecepcionista = idRecepcionista;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.dni = dni;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public Recepcionista(String nombre, String apellido, String numero, String dni, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.dni = dni;
        this.correo = correo;
    }

    public int getIdRecepcionista() {
        return idRecepcionista;
    }

    public void setIdRecepcionista(int idRecepcionista) {
        this.idRecepcionista = idRecepcionista;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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