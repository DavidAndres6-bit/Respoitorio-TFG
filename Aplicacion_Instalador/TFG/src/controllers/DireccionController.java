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

public class DireccionController {

    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
    
    @FXML 
    private CheckBox chkDesviacion, chkDireccion, chkVolanteColumna, chkServoDireccion;
    
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
        chkDesviacion.setSelected(checks.getOrDefault("desviacion", false));
        chkDireccion.setSelected(checks.getOrDefault("direccion", false));
        chkVolanteColumna.setSelected(checks.getOrDefault("volanteColumna", false));
        chkServoDireccion.setSelected(checks.getOrDefault("servoDireccion", false));
    
        // Recuperamos las posibles observaciones que ubiese escrito
        String obsGuardada = BufferInspeccion.getObservacionPosicion(5);
        if (obsGuardada != null && !obsGuardada.isEmpty()) {
            String textoLimpio = obsGuardada.replace("[DIRECCION]: ", "");
            txtObservaciones.setText(textoLimpio);
        }
    }

    /*
    *   Funcion que comprueba si hay checkboxs marcados
    */

    private boolean hayFallos(){
        
        // Variable para almacenar si ha seleccionado alguno
        boolean fallos = false;

        // Comprobar si hay check seleccionados
        if(chkDesviacion.isSelected() || chkDireccion.isSelected() || chkVolanteColumna.isSelected() || chkServoDireccion.isSelected()){
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
       
        // Si se han marcado checkboxs guardamos el resultado como false
        BufferInspeccion.getInspeccionActual().setDireccion(!hayFallos());

        // Guardamos los checkboxs
        BufferInspeccion.getChecksMarcados().put("desviacion", chkDesviacion.isSelected());
        BufferInspeccion.getChecksMarcados().put("direccion", chkDireccion.isSelected());
        BufferInspeccion.getChecksMarcados().put("volanteColumna", chkVolanteColumna.isSelected());
        BufferInspeccion.getChecksMarcados().put("servoDireccion", chkServoDireccion.isSelected());

        
        // Guardamos las observaciones
        String texto = txtObservaciones.getText().trim();
    
        if (!texto.isEmpty()) {
            BufferInspeccion.guardarObservacion(5,"[DIRECCION]: " +texto);
        } else {
            BufferInspeccion.guardarObservacion(5,"");
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

        // Guardar los datos en el buffer
        guardarDatosEnBuffer();

        // Cambiamos de ventana
        Utilities.abrirVentana("/vistas/EjesRuedasNeumaticosSuspension.fxml", "Ejes Ruedas Neumaticos y Suspensión");

        Utilities.cerrarVentana(event);   
    }
}