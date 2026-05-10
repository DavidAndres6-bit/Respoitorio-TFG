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

        System.out.println("Datos en buffer: " + BufferInspeccion.getInspeccionActual().getObservaciones());
         System.out.println("Datos en buffer: " + BufferInspeccion.getInspeccionActual().getListaDefectos());

        // Cargar datos del buffer
        if (BufferInspeccion.getVehiculoActual() != null) {
            lblMatricula.setText(BufferInspeccion.getVehiculoActual().getMatricula());
            lblModelo.setText(BufferInspeccion.getVehiculoActual().getModeloCompleto());
        }
        
        // Recuperamos los posibles checks marcados previamente para que no se pierda la informacion al volver
        Map<String, Boolean> checks = BufferInspeccion.getChecksMarcados();
        chkEstadoMotor.setSelected(checks.getOrDefault("estadoMotor", false));
        chkSistemaEscape.setSelected(checks.getOrDefault("sistemaEscape", false));
        chkTransmision.setSelected(checks.getOrDefault("transmision", false));
        chkAlimentacion.setSelected(checks.getOrDefault("alimentacion", false));
    
        // Recuperamos las posibles observaciones que ubiese escrito
        String obsGuardada = BufferInspeccion.getObservacionPosicion(7);
        if (obsGuardada != null && !obsGuardada.isEmpty()) {
            String textoLimpio = obsGuardada.replace("[MOTOR Y TRANSMISION]: ", "");
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
    *   Metodo para guardar los datos en el Buffer al cambiar de ventana desde el boton o desde el menu
    */

    private void guardarDatosEnBuffer() {
       
       
        // Si se han marcado checkboxs guardamos el resultado como false
        BufferInspeccion.getInspeccionActual().setMotorTransmision(!hayFallos());

        // Guardamos los checkboxs
        BufferInspeccion.getChecksMarcados().put("estadoMotor", chkEstadoMotor.isSelected());
        BufferInspeccion.getChecksMarcados().put("sistemaEscape", chkSistemaEscape.isSelected());
        BufferInspeccion.getChecksMarcados().put("transmision", chkTransmision.isSelected());
        BufferInspeccion.getChecksMarcados().put("alimentacion", chkAlimentacion.isSelected());


        // Guardamos las observaciones
        String texto = txtObservaciones.getText().trim();
    
        if (!texto.isEmpty()) {
            BufferInspeccion.guardarObservacion(7,"[MOTOR Y TRANSMISION]: " +texto);
        } else {
            BufferInspeccion.guardarObservacion(7,"");
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
        Utilities.abrirVentana("/vistas/Defectos.fxml", "Defectos Encontrados");

        Utilities.cerrarVentana(event);
    }
}