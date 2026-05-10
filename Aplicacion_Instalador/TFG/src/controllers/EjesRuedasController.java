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

public class EjesRuedasController {

    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
    
    @FXML 
    private CheckBox chkEjes, chkNeumaticos, chkRuedas, chkSuspension;
    
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
        chkEjes.setSelected(checks.getOrDefault("ejes", false));
        chkNeumaticos.setSelected(checks.getOrDefault("neumaticos", false));
        chkRuedas.setSelected(checks.getOrDefault("ruedas", false));
        chkSuspension.setSelected(checks.getOrDefault("suspension", false));
    
        // Recuperamos las posibles observaciones que ubiese escrito
        String obsGuardada = BufferInspeccion.getObservacionPosicion(6);
        if (obsGuardada != null && !obsGuardada.isEmpty()) {
            String textoLimpio = obsGuardada.replace("[EJES RUEDAS NEUMATICOS SUSPENSION]: ", "");
            txtObservaciones.setText(textoLimpio);
        }
    }

    /*
    *   Funcion que comprueba si hay checkboxs marcados
    */

    private boolean hayFallos(){
        
        //Variable para almacenar si ha seleccionado alguno
        boolean fallos = false;

        //Comprobar si hay check seleccionados
        if(chkEjes.isSelected() || chkNeumaticos.isSelected() || chkRuedas.isSelected() || chkSuspension.isSelected()){
            fallos = true;
        }

        return fallos;
    }

    /*
    *   Metodo para gestionar cuando el tecnico marca los checkboxs
    */

    @FXML
    private void gestionarCheck() {

        //Variable para almacenar si ha seleccionado alguno
        boolean fallos = hayFallos();

        //Si hay fallos habilitamos la caja de texto
        if(fallos){
            txtObservaciones.setDisable(false);
        }

        //Si desmarca todos los checkboxs volvemos a deshabilitar
        if(!fallos){
            txtObservaciones.setDisable(true);
        }
    }

    /*
    *   Metodo para guardar los datos en el Buffer al cambiar de ventana desde el boton o desde el menu
    */

    private void guardarDatosEnBuffer() {
       
        // Si se han marcado checkboxs guardamos el resultado como false
        BufferInspeccion.getInspeccionActual().setEjesRuedasNeumaticos(!hayFallos());

        // Guardamos los Checkboxs
        BufferInspeccion.getChecksMarcados().put("ejes", chkEjes.isSelected());
        BufferInspeccion.getChecksMarcados().put("neumaticos", chkNeumaticos.isSelected());
        BufferInspeccion.getChecksMarcados().put("ruedas", chkRuedas.isSelected());
        BufferInspeccion.getChecksMarcados().put("suspension", chkSuspension.isSelected());

        // Guardamos las observaciones
        String texto = txtObservaciones.getText().trim();
    
        if (!texto.isEmpty()) {
            BufferInspeccion.guardarObservacion(6,"[EJES RUEDAS NEUMATICOS SUSPENSION]: " +texto);
        } else {
            BufferInspeccion.guardarObservacion(6,"");
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

        // Guardar en el buffer
        guardarDatosEnBuffer();

        // Cambiamos de ventana
        Utilities.abrirVentana("/vistas/MotoryTransmision.fxml", "MOTOR Y TRANSMISIÓN");

        Utilities.cerrarVentana(event);
    }
}