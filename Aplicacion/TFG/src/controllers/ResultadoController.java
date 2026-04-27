package controllers;



import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class ResultadoController {

    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
   
    @FXML 
    private ComboBox<String> spResultado; 
    
    @FXML 
    private DatePicker dpFecha;

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

        // Pasar los valores al spinner del resultado de la inspeccion
        if (spResultado != null) {
            spResultado.setItems(FXCollections.observableArrayList("FAVORABLE", "NO FAVORABLE"));
        }

    }


    /*
    *   Funcion para generar un PDF con el informe de la inspección
    */

    @FXML
    private void accionGenerarInforme() {

        // Validar que los campos esten completos
        if(spResultado == null || dpFecha == null){
            Utilities.mostrarAlerta("Error", "Rellena todos los campos para poder generar el informer.", Alert.AlertType.ERROR);
        } else {

            // Recoger los valores introducidos
            String resultado = spResultado.getValue();
            LocalDate fechaSiguienteRevision = dpFecha.getValue();

            // Guardar los valores en el buffer
            BufferInspeccion.getInspeccionActual().setResultadoInspeccion(resultado);
            BufferInspeccion.getInspeccionActual().setFechaProximaInspeccion(fechaSiguienteRevision);

            // Insertamos todo en la base de datos
            boolean insertado = insertarInspeccion();

            
        }
    }
}
