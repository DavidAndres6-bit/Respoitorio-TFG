package clases.POJOS;

 public class Cliente {

    /*
    *   Atributos de la clase
    */

    private int id;
    private String dni;
    private String nombre;

    /*
    *   Constructor
    */

    public Cliente(String dni, String nombre) {
        this.dni = dni;
        this.nombre = nombre;
    }

    /*
    *   Constructor vacio
    */

    public Cliente(){
        
    }

    /*
    *   Getters y Setters
    */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }   
}