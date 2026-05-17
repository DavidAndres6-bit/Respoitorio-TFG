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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utilities.BufferInspeccion;
import utilities.MenuController;
import utilities.Utilities;

public class DefectosController {

    /*
     * Inicializar los elementos de la ventana
     */

    @FXML
    private TableView<Defecto> tablaDefectos;

    @FXML
    private Label lblMatricula, lblModelo;

    /*
     * Definimos las columnas de la tabla que corresponden con el id que les hemos
     * asignado en Scene Builder
     */

    @FXML
    private TableColumn<Defecto, String> colUnidad, colDescripcion, colCalificacion;

    /*
     * Tipo de lista de JavaFx para poder mostrarlos en la tabla
     */

    private ObservableList<Defecto> listaDefectos = FXCollections.observableArrayList();

    /*
     * Inicializar la tabla y rellenarla
     */

    @FXML
    public void initialize() {
        // Settear matricula y modelo
        if (BufferInspeccion.getVehiculoActual() != null) {
            lblMatricula.setText(BufferInspeccion.getVehiculoActual().getMatricula());
            lblModelo.setText(BufferInspeccion.getVehiculoActual().getModeloCompleto());
        }

        // Asociamos cada columna de la tabla con su atributo del POJO de Defecto
        // correspondiente
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacion"));

        // Recuperar los defectos de la inspeccion
        if (BufferInspeccion.getDefectosActuales() != null) {
            listaDefectos.setAll(BufferInspeccion.getDefectosActuales());

            // Asociamos la lista de defectos a la tabla
            tablaDefectos.setItems(listaDefectos);
        }

        // Hacemos que la tabla no sea editable solo informativa
        tablaDefectos.setEditable(false);
    }

    /*
     * ActionEvent que maneja las opciones del menu
     */

    @FXML
    public void CambiarVentana(ActionEvent event) {
        // Obtenemos el botón
        Button btnPulsado = (Button) event.getSource();

        // Obtenemos su ID
        String seccion = btnPulsado.getId();

        // Llamamos al metodo de utilities
        MenuController.abrirVentana(seccion, event);
    }

    /*
     * Accion del boton siguiente
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
            if (!opacidad.isEmpty() && !opacidad.equalsIgnoreCase("N/A")|| !coRalenti.isEmpty() && !coRalenti.equalsIgnoreCase("N/A")|| !coAcelerado.isEmpty() && !coAcelerado.equalsIgnoreCase("N/A")) {
                tieneEmisiones = true;
            }
        }

        // Comprobamos que no sea un coche electrico
        boolean electrico = false;

        if (v.getTipoDgt().equalsIgnoreCase("0 EMISIONES")) {
            electrico = true;
        }

        // Si no es electrico y no ha rellenado las emisiones le informamos que no puede
        // terminar la inspeccion sin hacerlo
        if (!tieneEmisiones && !electrico) {
            Utilities.mostrarAlerta("Inspección Incompleta","No se han registrado los datos de emisiones. Por favor, vuelva a la sección de Emisiones antes de finalizar.",Alert.AlertType.WARNING);
        } else {
            // Cambiamos de ventana
            Utilities.abrirVentana("/vistas/Resultado.fxml", "Resultado de la Inspección");
            Utilities.cerrarVentana(event);
        }
    }

    /*
    *   Accion del boton abortar inspeccion
    */

    @FXML
    private void accionAbortar(ActionEvent event) {
        // Creamos la alerta de confirmación
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Cancelación");
        alerta.setHeaderText("¿Estás seguro de que deseas ABORTAR la inspección?");
        alerta.setContentText("Esta acción borrará todos los datos introducidos y no se podrá deshacer.");
        alerta.setGraphic(null); // Quitamos el icono que viene por defecto
        // Si pulsa aceptar redirigimos al panel
        alerta.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Limpiamos el buffer de la inspeccion actual
                BufferInspeccion.limpiarBuffer();

                // Abrimos el panel del tecnico
                Utilities.abrirVentana("/vistas/PanelTecnico.fxml", "Panel de Gestión - ITV");
                Utilities.cerrarVentana(event);
            }
        });
    }
}