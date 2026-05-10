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

public class AlumbradoySenializacionController {


    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
    
    @FXML 
    private CheckBox chkCruce, chkIntermitentes, chkAntinieblas, chkPosicion, chkMarchaAtras, chkFreno;
    
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
        chkCruce.setSelected(checks.getOrDefault("cruce", false));
        chkIntermitentes.setSelected(checks.getOrDefault("intermitentes", false));
        chkAntinieblas.setSelected(checks.getOrDefault("antinieblas", false));
        chkPosicion.setSelected(checks.getOrDefault("posicion", false));
        chkMarchaAtras.setSelected(checks.getOrDefault("marchaAtras", false));
        chkFreno.setSelected(checks.getOrDefault("freno",false));


        // Recuperamos las posibles observaciones que ubiese escrito quitando el prefijo
        String obsGuardada = BufferInspeccion.getObservacionPosicion(2);
        if (obsGuardada != null && !obsGuardada.isEmpty()) {
            String textoLimpio = obsGuardada.replace("[ALUMBRADO Y SEÑALIZACIÓN]: ", "");
            txtObservaciones.setText(textoLimpio);
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
        if(chkCruce.isSelected() || chkIntermitentes.isSelected() || chkAntinieblas.isSelected() || chkPosicion.isSelected() || chkMarchaAtras.isSelected() || chkFreno.isSelected()){
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

        // Guardamos el estado de la inspección
        BufferInspeccion.getInspeccionActual().setAlumbradoSenializacion(!hayFallos());

        // Guardar estado de cada check
        BufferInspeccion.getChecksMarcados().put("cruce", chkCruce.isSelected());
        BufferInspeccion.getChecksMarcados().put("largas", chkCruce.isSelected()); // Unificado
        BufferInspeccion.getChecksMarcados().put("intermitentes", chkIntermitentes.isSelected());
        BufferInspeccion.getChecksMarcados().put("antinieblas", chkAntinieblas.isSelected());
        BufferInspeccion.getChecksMarcados().put("posicion", chkPosicion.isSelected());
        BufferInspeccion.getChecksMarcados().put("marchaAtras", chkMarchaAtras.isSelected());
        BufferInspeccion.getChecksMarcados().put("freno", chkFreno.isSelected());

        // Guardar observaciones
        String texto = txtObservaciones.getText().trim();
        if (!texto.isEmpty()) {
            BufferInspeccion.guardarObservacion(2, "[ALUMBRADO Y SEÑALIZACIÓN]: " + texto);
        } else {
            BufferInspeccion.guardarObservacion(2, "");
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

        // Guardamos los datos
        guardarDatosEnBuffer();

        // Cambiamos de ventana        
        Utilities.abrirVentana("/vistas/EmisionesContaminantes.fxml", "Emisiones Contaminantes");

        Utilities.cerrarVentana(event);      
    }

}
