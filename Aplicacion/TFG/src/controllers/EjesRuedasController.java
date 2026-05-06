package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import utilities.BufferInspeccion;
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
    *   Funcion para cambiar a la siguiente ventana
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

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

        // --- DEBUG ---
System.out.println("========================================");
System.out.println("PANTALLA: Acondicionamiento Exterior");
System.out.println("¿APARTADO APTO?: " + !hayFallos()); // Si no hay fallos, es true (apto)
System.out.println("MAPA DE CHECKS (Tamaño): " + BufferInspeccion.getChecksMarcados().size());
System.out.println("DETALLE CHECKS: " + BufferInspeccion.getChecksMarcados().toString());
System.out.println("========================================");

        // Cambiamos de ventana
        Utilities.abrirVentana("/vistas/MotoryTransmision.fxml", "MOTOR Y TRANSMISIÓN");

        Utilities.cerrarVentana(event);
    }
}