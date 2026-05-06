package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class AlumbradoySenializacionController {


    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
    
    @FXML 
    private CheckBox chkCruce, chkLargas, chkIntermitentes, chkAntinieblas, chkPosicion, chkMarchaAtras, chkFreno;
    
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
        
        // Variable para almacenar si ha seleccionado alguno
        boolean fallos = false;

        // Comprobar si hay check seleccionados
        if(chkCruce.isSelected() || chkLargas.isSelected() || chkIntermitentes.isSelected() || chkAntinieblas.isSelected() || chkPosicion.isSelected() || chkMarchaAtras.isSelected() || chkFreno.isSelected()){
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
    *   Funcion para cambiar a la siguiente ventana
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

        // Si se han marcado checkboxs guardamos el resultado como false
        BufferInspeccion.getInspeccionActual().setAlumbradoSenializacion(!hayFallos());

        // Guardamos los checkboxs
        BufferInspeccion.getChecksMarcados().put("cruce", chkCruce.isSelected());
        BufferInspeccion.getChecksMarcados().put("largas", chkLargas.isSelected());
        BufferInspeccion.getChecksMarcados().put("intermitentes", chkIntermitentes.isSelected());
        BufferInspeccion.getChecksMarcados().put("antinieblas", chkAntinieblas.isSelected());
        BufferInspeccion.getChecksMarcados().put("posicion", chkPosicion.isSelected());
        BufferInspeccion.getChecksMarcados().put("marchaAtras", chkMarchaAtras.isSelected());
        BufferInspeccion.getChecksMarcados().put("freno", chkFreno.isSelected());

        // Guardamos las observaciones
        String texto = txtObservaciones.getText().trim();
    
        if (!texto.isEmpty()) {
            BufferInspeccion.guardarObservacion(2,"[ALUMBRADO Y SEÑIALIZACIÓN]: " +texto);
        } else {
            BufferInspeccion.guardarObservacion(2,"");
        }

        // Cambiamos de ventana        
        Utilities.abrirVentana("/vistas/EmisionesContaminantes.fxml", "Emisiones Contaminantes");

        Utilities.cerrarVentana(event);      
    }

}
