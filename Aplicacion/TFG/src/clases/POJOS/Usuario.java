package clases.POJOS;

public class Usuario {

    /* 
    *  Atributos del objeto usuario
    */

    private int id;
    private String nombre;
    private String contrasenia;
    private String correo;
    private int telefono;
    private String rol;


    /* 
    *  Constructor vacio
    */
    public Usuario() {
    
    }

    /* 
    *  Constructor con el id
    */

    public Usuario(int id, String nombre, String contrasenia, String correo, int telefono, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
    }

    /* 
    *  Constructor sin el id
    */

    public Usuario(String nombre, String contrasenia, String correo, int telefono, String rol) {
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
    }


    /*
    *  Getters y Setters
    */
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}