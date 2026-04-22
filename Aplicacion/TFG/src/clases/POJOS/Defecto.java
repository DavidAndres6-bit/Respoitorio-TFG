package clases.POJOS;



public class Defecto {

    /*
    *   Atributos de la clase
    */

    private String unidad;
    private String descripcion;
    private String calificacion;

    /*
     * Constructor
    */

    public Defecto(String unidad, String descripcion, String calificacion) {
        this.unidad = unidad;
        this.descripcion = descripcion;
        this.calificacion = calificacion;
    }

    /*
     * Getters y Setters
    */
   
    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }
}