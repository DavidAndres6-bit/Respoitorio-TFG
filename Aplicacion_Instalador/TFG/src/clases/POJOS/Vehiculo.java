package clases.POJOS;

import java.time.LocalDate;

public class Vehiculo {

    /*
    *  Atributos de la clase
    */

    private String matricula;
    private String marca;
    private String modelo;
    private LocalDate fechaMatriculacion; 
    private String tipoDgt;

    /* 
    *  Constructor vacio
    */
    public Vehiculo() {
    }

    /*  
    *  Constructor con atributos que necesitamos para mostrar en el panel del tecnico
    */

    public Vehiculo(String matricula, String marca, String modelo, String combustible, LocalDate fechaMatriculacion, String tipoDgt) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.fechaMatriculacion = fechaMatriculacion;
        this.tipoDgt = tipoDgt;
    }

    /*
    *  Getters y Setters
    */

    public String getMatricula() {
        return matricula; 
    }
    
    public void setMatricula(String matricula) { 
        this.matricula = matricula; 
    }

    public String getMarca() { 
        // Controlamos posibles valores incompletos de la DGT
        if (this.marca == null || this.marca.equals("0")) {
            return "Desconocido";
        }
        return this.marca;

    }
    
    public void setMarca(String marca) { 
        this.marca = marca; 
    }

    public String getModelo() {
        if (this.modelo == null || this.modelo.equals("0")) {
            return "Desconocido";
        }
        return this.modelo;
    }
    
    public void setModelo(String modelo) { 
        this.modelo = modelo; 
    }

    public LocalDate getFechaMatriculacion() { 
        return fechaMatriculacion; 
    }
    
    public void setFechaMatriculacion(LocalDate fechaMatriculacion) { 
        this.fechaMatriculacion = fechaMatriculacion; 
    }

    public String getTipoDgt() { 
        return tipoDgt; 
    }
    
    public void setTipoDgt(String tipoDgt) { 
        this.tipoDgt = tipoDgt; 
    }

    /* 
    *  Método para devolver en una unidad marca + modelo para mostrar por pantalla en los paneles
    */

    public String getModeloCompleto() {
        String mca = getMarca();
        String mod = getModelo();
        if (mca.equals("Desconocido") && mod.equals("Desconocido")) {
            return "Desconocido";
        }
        return mca + " " + mod;
    }

}
