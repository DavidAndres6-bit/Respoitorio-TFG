package controllers;

import java.util.Map;

import clases.POJOS.Defecto;
import clases.POJOS.Vehiculo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import utilities.BufferInspeccion;
import utilities.MenuController;
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
        
        // Recuperar los posibles defectos que ya hubiese
        if (BufferInspeccion.getDefectosActuales() != null) {
            // Limpiamos primero para evitar posibles duplicados
            listaDefectos.clear(); 
            listaDefectos.addAll(BufferInspeccion.getDefectosActuales());
        }   


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
    *   Funcion para guardar los datos en el buffer
    */

    private void guardarDatosEnBuffer() {
        // Limpiamos lo que hubiera en el buffer
        BufferInspeccion.getDefectosActuales().clear();
        
        // Metemos todo lo que hay en la tabla actualmente
        BufferInspeccion.getDefectosActuales().addAll(listaDefectos);
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
    *   Accion del boton siguiente
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {
        
        // Recuperamos los valores de las emisiones
        Map<String, String> emisiones = BufferInspeccion.getValoresEmisiones();
        Vehiculo v = BufferInspeccion.getVehiculoActual();        

        // Comprobamos que no este vacio el mapa
        boolean tieneEmisiones = false;
        if (emisiones != null) {
            
            // Recogemos los campos
            String opacidad = emisiones.getOrDefault("opacidad", "").trim();
            String coRalenti = emisiones.getOrDefault("coRalenti", "").trim();
            String coAcelerado = emisiones.getOrDefault("coRalentiAcelerado", "").trim();

            // Comprobar que ha rellenado valores
            if (!opacidad.isEmpty() && !opacidad.equalsIgnoreCase("N/A") || !coRalenti.isEmpty()  && !coRalenti.equalsIgnoreCase("N/A")|| !coAcelerado.isEmpty()  && !coAcelerado.equalsIgnoreCase("N/A")) {
                tieneEmisiones = true;
            }
        }

        // Comprobamos que no sea un coche electrico
        boolean electrico = false;

        if(v.getTipoDgt().equalsIgnoreCase("0 EMISIONES")){
            electrico = true;
        }

        // Si no es electrico y no ha rellenado las emisiones le informamos que no puede terminar la inspeccion sin hacerlo
        if (!tieneEmisiones && !electrico) {
            Utilities.mostrarAlerta("Inspección Incompleta","No se han registrado los datos de emisiones. Por favor, vuelva a la sección de Emisiones antes de finalizar.", 
            Alert.AlertType.WARNING);
        } else {
        
            // Guardar los datos en el buffer
            guardarDatosEnBuffer();
        
            //Cambiamos de ventana
            Utilities.abrirVentana("/vistas/Resultado.fxml", "Resultado de la Inspección");

            Utilities.cerrarVentana(event);     
        }
    }
}

