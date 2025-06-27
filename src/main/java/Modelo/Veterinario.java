package Modelo;

public class Veterinario {
    private int idVeterinario;
    private String nombre; // Propiedad 'nombre'
    private String apellido;
    private String numero;
    private String dni;
    private String especialidad;
    // Agrega aquí otras propiedades si las tienes (ej. contraseña, etc.)

    // Constructor vacío (es una buena práctica para JavaBeans)
    public Veterinario() {
    }

    // Constructor con todos los campos (opcional)
    public Veterinario(int idVeterinario, String nombre, String apellido, String numero, String dni, String especialidad) {
        this.idVeterinario = idVeterinario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.dni = dni;
        this.especialidad = especialidad;
    }

    // Getters y Setters para todas las propiedades

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    // *** ESTE ES EL MÉTODO CRÍTICO QUE DEBE EXISTIR ***
    public String getNombre() {
        return nombre;
    }

    // *** Y ESTE TAMBIÉN ***
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    // Otros getters/setters si tienes más campos...
}