package controllers;

import clases.DAOS.InspeccionDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class IdentificacionController {

    /*
    *   Recoger los campos del formulario
    */

    @FXML
    private Label lblMatricula, lblModelo;

    @FXML
    private TextField txtKmAnteriores, txtKmActuales;

    @FXML
    private Button btnSiguiente;

    /*
    *   Metodo para inicializar los valores de matricula modelo y km anteriores si ya habia pasado una itv previamente
    */

    @FXML
    public void initialize() {

        // Recuperamos el vehículo del buffer
        if (BufferInspeccion.getVehiculoActual() != null) {
            lblMatricula.setText(BufferInspeccion.getVehiculoActual().getMatricula());
            lblModelo.setText(BufferInspeccion.getVehiculoActual().getModeloCompleto());
            
            // Recuperamos los kilometros de la anterior revision y los setteamos
            // si es la primera vez que pasa inspeccion dejamos en disable el input

            int kilometros_previos = InspeccionDAO.obtenerKmUltimaInspeccion(lblMatricula.getText());

            if(kilometros_previos > 0){
                // Lo ponemos como valor    
                txtKmAnteriores.setText(String.valueOf(kilometros_previos));

                txtKmAnteriores.setEditable(false);
            } else {
               txtKmAnteriores.setText("0");
               txtKmAnteriores.setDisable(true);
            }

            txtKmActuales.setText("");
            txtKmActuales.requestFocus(); // Ponemos el cursor aquí para ahorrar un clic al técnico

            // Esto lo hemos buscado son propiedades para que los campos hagan un efecto parecido al del autocompleteTextView
            // y se mantenga validación si borra y vuelve a escribir

            txtKmAnteriores.textProperty().addListener((observable, oldValue, newValue) -> validarCampos());
            txtKmActuales.textProperty().addListener((observable, oldValue, newValue) -> validarCampos());

            validarCampos();
        }
    }

    /*
    *   Funcion para comprobar si ha rellenado los campos y activar el boton para avanzar
    */

    private void validarCampos() {
    
        // Recogemos los valores de los campos de texto
        String antStr = txtKmAnteriores.getText().trim();
        String actStr = txtKmActuales.getText().trim();

        try {
            
            // El campo actual siempre debe tener contenido para poder avanzar
            if (actStr.isEmpty()) {
                btnSiguiente.setDisable(true);
                btnSiguiente.setOpacity(0.5);
            } else {
                
                // Parseamos el valor actual
                int kmAct = Integer.parseInt(actStr);
                int kmAnt = 0;

                // Solo parseamos el anterior si tiene algo y no es N/A
                if (!antStr.isEmpty() && !antStr.equalsIgnoreCase("N/A")) {
                    kmAnt = Integer.parseInt(antStr);
                }

                // Aplicamos la lógica de coherencia y el límite de 999.999
                if (kmAct >= kmAnt && kmAct <= 999999) {
                    btnSiguiente.setDisable(false);
                    btnSiguiente.setOpacity(1.0);
                } else {
                    btnSiguiente.setDisable(true);
                    btnSiguiente.setOpacity(0.5);
                }
            }
            
        } catch (NumberFormatException e) {
            
            // Si hay letras en los campos, el botón se deshabilita
            btnSiguiente.setDisable(true);
            btnSiguiente.setOpacity(0.5);
        }
    }

    /*
    *   Funcion para recoger los kilometros y guardarloe en nuestro buffer
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

        try {

            //Recoger los valores de los campos
            int kmAnt = 0;
            
            if (txtKmAnteriores.isDisable() || txtKmAnteriores.getText().trim().isEmpty()) {
                kmAnt = 0; // Si está deshabilitado o vacío, asumimos 0
            } else {
                kmAnt = Integer.parseInt(txtKmAnteriores.getText().trim());
            }

            int kmAct = Integer.parseInt(txtKmActuales.getText());

            //Validar que los datos introducidos sean coherentes
            if (kmAct < kmAnt) {
                Utilities.mostrarAlerta("Error", "Los KM actuales no pueden ser menores a los de la inpección anterior.", Alert.AlertType.WARNING);
            } else if(kmAct > 999999) {
                Utilities.mostrarAlerta("Valor excesivo", "Los kilómetros no pueden superar 999.999", Alert.AlertType.ERROR);
            } else {
                // Guardar y pasar de página
                BufferInspeccion.getInspeccionActual().setKmActuales(kmAct);
                BufferInspeccion.getInspeccionActual().setKmAnteriorInspeccion(kmAnt);

                // Guardamos la fecha de la inspeccion
                BufferInspeccion.getInspeccionActual().setFechaInspeccion(java.time.LocalDate.now());
                
                Utilities.abrirVentana("/vistas/AcondicionamientoExterior.fxml", "Acondicionamiento Exterior, Carroceria y Chasis");
                Utilities.cerrarVentana(event);
            }

        } catch (NumberFormatException e) {
            Utilities.mostrarAlerta("Error", "Introduce solo números.", Alert.AlertType.ERROR);
        }
    }
}
