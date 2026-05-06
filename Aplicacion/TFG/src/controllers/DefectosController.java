package controllers;

import clases.POJOS.Defecto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class DefectosController {

    /*
    *   Inicializar los elementos de la ventana
    */

    @FXML
    private TableView<Defecto> tablaDefectos;
    
    @FXML 
    private Label lblMatricula, lblModelo;

    @FXML
    private TextField txtUnidad, txtDescripcion;

    @FXML
    private ChoiceBox<String> spCalificacion;

    /*
    *   Definimos las columnas de la tabla que corresponden con el id que les hemos asignado en Scene Builder
    */

    @FXML 
    private TableColumn<Defecto, String> colUnidad;
   
    @FXML 
    private TableColumn<Defecto, String> colDescripcion;
    
    @FXML 
    private TableColumn<Defecto, String> colCalificacion;

    /*
    *   Tipo de lista de JavaFx para poder ir alamancenando los defectos y mostrarlos en la tabla
    */

    private ObservableList<Defecto> listaDefectos = FXCollections.observableArrayList();
    
    /*
    *   Inicializar la tabla y rellenarla
    */

    @FXML
    public void initialize(){

        // Settear matricula y modelo
        lblMatricula.setText(BufferInspeccion.getVehiculoActual().getMatricula());
        lblModelo.setText(BufferInspeccion.getVehiculoActual().getModeloCompleto());
     
        //Cargar los valores en el deplegable de calificacion del defecto
        spCalificacion.getItems().addAll("LEVE", "MEDIO", "GRAVE");

        // Seteamos leve como valor por defecto
        spCalificacion.setValue("LEVE");
        
        //Asociamos cada columna de la tabla con su atributo del POJO de Defecto correspondiente
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacion"));
        
        //Asociamos la lista de defectos a la tabla
        tablaDefectos.setItems(listaDefectos);

        // DEBUG
        System.out.println("OBSERVACIONES: " + BufferInspeccion.getInspeccionActual().getObservaciones());
    }

    /*
    *   Funcion que recoge los valores introducidos en los inputs al pulsar el boton añiadir
    */


    @FXML
    private void accionAniadirDefecto(ActionEvent event) {

        //Recoger los valores de los inputs
        String unidad = txtUnidad.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String calificacion = spCalificacion.getValue();

        //Comprobamos que haya rellenado todos los campos
        if (!unidad.isEmpty() && !descripcion.isEmpty() && !calificacion.isEmpty()) {
            
            // Creamos un objeto de tipo defecto y lo añadimos a la lista
            Defecto d = new Defecto(unidad, descripcion, calificacion);
            listaDefectos.add(d);

            // Añadir el defecto al buffer
            BufferInspeccion.getDefectosActuales().add(d);

            // Limpiamos los inputs para el siguiente defecto que se introduzca
            txtUnidad.clear();
            txtDescripcion.clear();
        
            System.out.println("Defecto añadido correctamente: " + d.getDescripcion());
            System.out.println("Total defectos para el PDF: " + BufferInspeccion.getDefectosActuales().size());
        } else {
            Utilities.mostrarAlerta("Error", "Rellena todos los campos del defecto", Alert.AlertType.WARNING);
        }


    }

    /*
    *   Accion del boton siguiente
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {
        
        
        //Cambiamos de ventana
        Utilities.abrirVentana("/vistas/Resultado.fxml", "Resultado de la Inspección");

        Utilities.cerrarVentana(event);
    }

}

