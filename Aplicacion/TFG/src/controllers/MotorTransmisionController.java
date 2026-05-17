package controllers;

import clases.POJOS.Defecto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import utilities.BufferInspeccion;
import utilities.MenuController;
import utilities.Utilities;

public class MotorTransmisionController {

    /*
     * Recoger los valores del formulario
     */

    @FXML
    private Label lblMatricula, lblModelo;

    @FXML
    private ChoiceBox<String> cbEstadoMotor, cbSistemaEscape, cbTransmision, cbAlimentacion;

    /*
     * Metodo para inicializar los valores de matricula modelo
     */

    @FXML
    public void initialize() {
        // Cargar datos del buffer
        if (BufferInspeccion.getVehiculoActual() != null) {
            lblMatricula.setText(BufferInspeccion.getVehiculoActual().getMatricula());
            lblModelo.setText(BufferInspeccion.getVehiculoActual().getModeloCompleto());
        }

        // Configuramos los choicebox con sus codigos de defecto
        Utilities.configurarChoiceBox(cbEstadoMotor, "08.01");
        Utilities.configurarChoiceBox(cbSistemaEscape, "08.02");
        Utilities.configurarChoiceBox(cbTransmision, "08.03");
        Utilities.configurarChoiceBox(cbAlimentacion, "08.04");

        // Cargamos los posibles valores previos
        recuperarValoresPrevios();
    }

    /*
     * Metodo para recuperar los posibles defectos previos
     */

    private void recuperarValoresPrevios() {

        // Recorremos los defectos actuales y comprobamos si hay alguno por su codigo
        for (Defecto d : BufferInspeccion.getDefectosActuales()) {

            if (d.getUnidad().equals("08.01")) {
                cbEstadoMotor.setValue(d.getCalificacion());
            }

            if (d.getUnidad().equals("08.02")) {
                cbSistemaEscape.setValue(d.getCalificacion());
            }

            if (d.getUnidad().equals("08.03")) {
                cbTransmision.setValue(d.getCalificacion());
            }

            if (d.getUnidad().equals("08.04")) {
                cbAlimentacion.setValue(d.getCalificacion());
            }
        }
    }

    /*
     * Metodo para guardar los datos en el Buffer al cambiar de ventana desde el
     * boton o desde el menu
     */

    private void guardarDatosEnBuffer() {
        // Variable para alamacenar su hay fallos
        boolean fallos = false;

        if (BufferInspeccion.getDefectos().isEmpty()) {
            fallos = false;
        } else {
            fallos = true;
        }

        // Guardamos el estado de la inspeccion
        BufferInspeccion.getInspeccionActual().setMotorTransmision(!fallos);
    }

    /*
     * ActionEvent que maneja las opciones del menu
     */

    @FXML
    public void CambiarVentana(ActionEvent event) {
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
     * Funcion para cambiar a la siguiente ventana
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