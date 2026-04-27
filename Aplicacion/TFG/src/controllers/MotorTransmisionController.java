package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class MotorTransmisionController {

   
    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
    
    @FXML 
    private CheckBox chkEstadoMotor, chkSistemaEscape, chkTransmision, chkAlimentacion;
    
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
    }

    /*
    *   Funcion que comprueba si hay checkboxs marcados
    */

    private boolean hayFallos(){
        
        //Variable para almacenar si ha seleccionado alguno
        boolean fallos = false;

        //Comprobar si hay check seleccionados
        if(chkEstadoMotor.isSelected() || chkSistemaEscape.isSelected() || chkTransmision.isSelected() || chkAlimentacion.isSelected()){
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
    *   Funcion para cambiar a la siguiente ventana
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

        //Si se han marcado checkboxs guardamos el resultado como false
        BufferInspeccion.getInspeccionActual().setMotorTransmision(hayFallos());

        //Guardamos las observaciones
        String texto = txtObservaciones.getText().trim();
    
        if (!texto.isEmpty()) {
            BufferInspeccion.getInspeccionActual().setObservaciones("[MOTOR Y TRANSMISIÓN]: " + texto);
        } else {
            BufferInspeccion.getInspeccionActual().setObservaciones("");
        }

        //Cambiamos de ventana
        Utilities.abrirVentana("/vistas/Defectos.fxml", "Defectos Encontrados");

        Utilities.cerrarVentana(event);
    }
}