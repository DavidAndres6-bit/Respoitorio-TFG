package controllers;

import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import utilities.BufferInspeccion;
import utilities.MenuController;
import utilities.Utilities;

public class AcondicionamientoInteriorController {

    
    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
    
    @FXML 
    private CheckBox chkCinturones, chkVelocidad, chkAsientos, chkCarga;
    
    @FXML 
    private TextArea txtObservaciones;
    

    /*
    *   Metodo para inicializar los valores de matricula modelo
    */

    @FXML
    public void initialize() {
        // Cargar datos del buffer
        if (BufferInspeccion.getVehiculoActual() != null) {
            lblMatricula.setText(BufferInspeccion.getVehiculoActual().getMatricula());
            lblModelo.setText(BufferInspeccion.getVehiculoActual().getModeloCompleto());
        }

        // Recuperamos los posibles checks marcados previamente para que no se pierda la informacion al volver
        Map<String, Boolean> checks = BufferInspeccion.getChecksMarcados();
        chkAsientos.setSelected(checks.getOrDefault("asientos", false));
        chkCinturones.setSelected(checks.getOrDefault("cinturones", false));
        chkVelocidad.setSelected(checks.getOrDefault("velocidad", false));
        chkCarga.setSelected(checks.getOrDefault("carga", false));
        
        // Recuperamos las observaciones quitandole el prefijo
        String obsGuardada = BufferInspeccion.getObservacionPosicion(1);
        if (obsGuardada != null && !obsGuardada.isEmpty()) {
            String textoLimpio = obsGuardada.replace("[ACONDICIONAMIENTO INTERIOR]: ", "");
            txtObservaciones.setText(textoLimpio);
            System.out.println("DEBUG: Observación interior recuperada: " + textoLimpio);
        }

        gestionarCheck();
    }

    /*
    *   Funcion que comprueba si hay checkboxs marcados
    */

    private boolean hayFallos(){
        
        // Variable para almacenar si ha seleccionado alguno
        boolean fallos = false;

        // Comprobar si hay check seleccionados
        if(chkCinturones.isSelected() || chkVelocidad.isSelected() || chkAsientos.isSelected() || chkCarga.isSelected()){
            fallos = true;
        }

        return fallos;
    }

    /*
    *   Metodo para gestionar cuando el tecnico marca los checkboxs
    */

    @FXML
    private void gestionarCheck() {

        // Variable para almacenar si ha seleccionado alguno
        boolean fallos = hayFallos();

        // Si hay fallos habilitamos la caja de texto
        if(fallos){
            txtObservaciones.setDisable(false);
        }

        // Si desmarca todos los checkboxs volvemos a deshabilitar
        if(!fallos){
            txtObservaciones.setDisable(true);
        }
    }

    /*
    *   Metodo para guardar los datos en el Buffer al cambiar de ventana desde el boton o desde el menu
    */

    private void guardarDatosEnBuffer() {
        System.out.println("DEBUG: Guardando datos de Interior en el Buffer...");
        
        // Guardamos el estado general de esta sección
        BufferInspeccion.getInspeccionActual().setAcondicionamientoInterior(!hayFallos());

        // Guardamos los checks individuales
        BufferInspeccion.getChecksMarcados().put("cinturones", chkCinturones.isSelected());
        BufferInspeccion.getChecksMarcados().put("velocidad", chkVelocidad.isSelected());
        BufferInspeccion.getChecksMarcados().put("asientos", chkAsientos.isSelected());
        BufferInspeccion.getChecksMarcados().put("carga", chkCarga.isSelected());
        
        // Guardamos las observaciones
        String texto = txtObservaciones.getText().trim();
        if (!texto.isEmpty()) {
            BufferInspeccion.guardarObservacion(1, "[ACONDICIONAMIENTO INTERIOR]: " + texto);
        } else {
            BufferInspeccion.guardarObservacion(1, "");
        }
    }
    
    /*
    *   ActionEvent que maneja las opciones del menu
    */
    
    @FXML
    public void CambiarVentana(ActionEvent event){
        
        // Guardar los datos en el buffer
        guardarDatosEnBuffer();
        
        // Obtenemos el botón 
        Button btnPulsado = (Button) event.getSource();
        
        // Obtenemos su ID
        String seccion = btnPulsado.getId(); 
        
        // Llamamos al metodo de utilities
        MenuController.abrirVentana(seccion, event);
    }

    /*
    *   Funcion para cambiar a la siguiente ventana
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

        // Guardamos de ventana
        guardarDatosEnBuffer();

        // Cambiamos de ventana
        Utilities.abrirVentana("/vistas/AlumbradoySenializacion.fxml", "Alumbrado y Señalización");
        Utilities.cerrarVentana(event);    
    }
}