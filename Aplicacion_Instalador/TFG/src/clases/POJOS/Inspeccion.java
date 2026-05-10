package clases.POJOS;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Inspeccion {

    /*
     * Atributos de la inspeccion
    */


    private int id;
    private String matriculaCoche;
    private int kmAnteriorInspeccion;
    private int kmActuales;
    private boolean acondicionamientoExterior;
    private boolean acondicionamientoInterior;
    private boolean alumbradoSenializacion;
    private boolean emisiones;
    private boolean frenos;
    private boolean direccion;
    private boolean ejesRuedasNeumaticos;
    private boolean motorTransmision;
    private String resultadoInspeccion; 
    private java.sql.Date fechaProximaInspeccion;  //Para evitar errores con la base de datos con el formato de las fechas
    private LocalDate fechaInspeccion;
    private String observaciones;

    // Lista para almacenar los defectos
    private List<Defecto> listaDefectos;


   /*
    * Constructor vacío
    */
   
    public Inspeccion() {
        this.listaDefectos = new ArrayList<>();
    }

    /*
     * Getters y Setters
    */
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMatriculaCoche() {
        return matriculaCoche;
    }

    public void setMatriculaCoche(String matriculaCoche) {
        this.matriculaCoche = matriculaCoche;
    }

    public int getKmAnteriorInspeccion() {
        return kmAnteriorInspeccion;
    }

    public void setKmAnteriorInspeccion(int kmAnteriorInspeccion) {
        this.kmAnteriorInspeccion = kmAnteriorInspeccion;
    }

    public int getKmActuales() {
        return kmActuales;
    }

    public void setKmActuales(int kmActuales) {
        this.kmActuales = kmActuales;
    }

    public boolean isAcondicionamientoExterior() {
        return acondicionamientoExterior;
    }

    public void setAcondicionamientoExterior(boolean acondicionamientoExterior) {
        this.acondicionamientoExterior = acondicionamientoExterior;
    }

    public boolean isAcondicionamientoInterior() {
        return acondicionamientoInterior;
    }

    public void setAcondicionamientoInterior(boolean acondicionamientoInterior) {
        this.acondicionamientoInterior = acondicionamientoInterior;
    }

    public boolean isAlumbradoSenializacion() {
        return alumbradoSenializacion;
    }

    public void setAlumbradoSenializacion(boolean alumbradoSenializacion) {
        this.alumbradoSenializacion = alumbradoSenializacion;
    }

    public boolean isEmisiones() {
        return emisiones;
    }

    public void setEmisiones(boolean emisiones) {
        this.emisiones = emisiones;
    }

    public boolean isFrenos() {
        return frenos;
    }

    public void setFrenos(boolean frenos) {
        this.frenos = frenos;
    }

    public boolean isDireccion() {
        return direccion;
    }

    public void setDireccion(boolean direccion) {
        this.direccion = direccion;
    }

    public boolean isEjesRuedasNeumaticos() {
        return ejesRuedasNeumaticos;
    }

    public void setEjesRuedasNeumaticos(boolean ejesRuedasNeumaticos) {
        this.ejesRuedasNeumaticos = ejesRuedasNeumaticos;
    }

    public boolean isMotorTransmision() {
        return motorTransmision;
    }

    public void setMotorTransmision(boolean motorTransmision) {
        this.motorTransmision = motorTransmision;
    }

    public String getResultadoInspeccion() {
        return resultadoInspeccion;
    }

    public void setResultadoInspeccion(String resultadoInspeccion) {
        this.resultadoInspeccion = resultadoInspeccion;
    }

    public Date getFechaProximaInspeccion() {
        return fechaProximaInspeccion;
    }

    public void setFechaProximaInspeccion(LocalDate fechaSiguienteRevision) {
        if (fechaSiguienteRevision != null) {
            // Convertimos LocalDate a java.sql.Date
            this.fechaProximaInspeccion = java.sql.Date.valueOf(fechaSiguienteRevision);
        }
    }
    
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Defecto> getListaDefectos() {
        return listaDefectos;
    }

    public void setListaDefectos(List<Defecto> listaDefectos) {
        this.listaDefectos = listaDefectos;
    }
    
    public LocalDate getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(LocalDate fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }
}