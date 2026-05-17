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

public class AcondicionamientoInteriorController {

    /*
     * Recoger los valores del formulario
     */

    @FXML
    private Label lblMatricula, lblModelo;

    @FXML
    private ChoiceBox<String> cbAsientos, cbCinturones, cbVelocidad, cbCarga;

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
        Utilities.configurarChoiceBox(cbAsientos, "02.01");
        Utilities.configurarChoiceBox(cbCinturones, "02.02");
        Utilities.configurarChoiceBox(cbVelocidad, "02.03");
        Utilities.configurarChoiceBox(cbCarga, "02.04");

        // Cargamos los posibles valores previos
        recuperarValoresPrevios();
    }

    /*
     * Metodo para recuperar los posibles defectos previos
     */

    private void recuperarValoresPrevios() {
        
        // Recorremos los defectos actuales y comprobamos si hay alguno por su codigo
        for (Defecto d : BufferInspeccion.getDefectosActuales()) {

            if (d.getUnidad().equals("02.01")) {
                cbAsientos.setValue(d.getCalificacion());
            }

            if (d.getUnidad().equals("02.02")) {
                cbCinturones.setValue(d.getCalificacion());
            }

            if (d.getUnidad().equals("02.03")) {
                cbVelocidad.setValue(d.getCalificacion());
            }

            if (d.getUnidad().equals("02.04")) {
                cbCarga.setValue(d.getCalificacion());
            }
        }
    }

    /*
     * Metodo para guardar los datos en el Buffer al cambiar de ventana desde el
     * boton o desde el menu
     */

    private void guardarDatosEnBuffer() {
        // Comprobar si hay fallos
        boolean fallos = false;

        if (BufferInspeccion.getDefectos().isEmpty()) {
            fallos = false;
        } else {
            fallos = true;
        }

        // Guardamos el estado de la inspeccion
        BufferInspeccion.getInspeccionActual().setAcondicionamientoInterior(!fallos);
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
        // Guardamos de ventana
        guardarDatosEnBuffer();

        // Cambiamos de ventana
        Utilities.abrirVentana("/vistas/AlumbradoySenializacion.fxml", "Alumbrado y Señalización");
        Utilities.cerrarVentana(event);
    }
}