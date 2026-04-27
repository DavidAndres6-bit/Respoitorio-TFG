package controllers;

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
            
            // Aquí podrías llamar a un DAO para buscar la última inspección de este coche
            // y poner los KM en txtKmAnteriores. De momento lo dejamos a 0 o vacío.
            txtKmAnteriores.setText("0"); 

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
       
        //Recogemos los valores
        String antStr = txtKmAnteriores.getText().trim();
        String actStr = txtKmActuales.getText().trim();

        try {
        
            // Comprobamos que no estén vacíos
            if (antStr.isEmpty() || actStr.isEmpty()) {
                btnSiguiente.setDisable(true); 
            } else {
                // Comprobamos que sean números válidos
                int kmAnt = Integer.parseInt(antStr);
                int kmAct = Integer.parseInt(actStr);

                // Solo habilitar si el actual es >= al anterior
                if (kmAct >= kmAnt) {
                    btnSiguiente.setDisable(false);
                    btnSiguiente.setOpacity(1.0); // Se ve brillante
                } else {
                    btnSiguiente.setDisable(true);
                    btnSiguiente.setOpacity(0.5); // Se ve "apagado"
                }
            }
        } catch (NumberFormatException e) {
            // Si meten letras, deshabilitamos el botón
            btnSiguiente.setDisable(true);
        }
    }

    /*
    *   Funcion para recoger los kilometros y guardarloe en nuestro buffer
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

        try {

            //Recoger los valores de los campos
            int kmAnt = Integer.parseInt(txtKmAnteriores.getText());
            int kmAct = Integer.parseInt(txtKmActuales.getText());

            //Validar que los datos introducidos sean coherentes
            if (kmAct < kmAnt) {
                Utilities.mostrarAlerta("Error", "Los KM actuales no pueden ser menores a los de la inpección anterior.", Alert.AlertType.WARNING);
            } else {
                // Guardar y pasar de página
                BufferInspeccion.getInspeccionActual().setKmActuales(kmAct);
                
                Utilities.abrirVentana("/vistas/AcondicionamientoExterior.fxml", "Acondicionamiento Exterior, Carroceria y Chasis");
            
                Utilities.cerrarVentana(event);
            }

        } catch (NumberFormatException e) {
            Utilities.mostrarAlerta("Error", "Introduce solo números.", Alert.AlertType.ERROR);
        }

    }
}
