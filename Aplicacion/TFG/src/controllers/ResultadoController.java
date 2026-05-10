package controllers;



import java.io.File;
import java.time.LocalDate;

import clases.DAOS.ClienteDAO;
import clases.DAOS.InspeccionDAO;
import clases.POJOS.Cliente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import utilities.BufferInspeccion;
import utilities.GenerarInforme;
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

    //Instancia de la clase que genera el PDF
    GenerarInforme generador = new GenerarInforme();

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
        if(spResultado.getValue() == null || dpFecha.getValue() == null){
            Utilities.mostrarAlerta("Error", "Rellena todos los campos para poder generar el informe.", Alert.AlertType.ERROR);
        } else {

            // Recogemos las observaciones de la inspeccion
            String observaciones = BufferInspeccion.juntarObservaciones();

            // Las asignamos a la inspeccion
            BufferInspeccion.getInspeccionActual().setObservaciones(observaciones);
            
            // Recoger los valores introducidos en esta pantalla
            String resultado = spResultado.getValue();
            LocalDate fechaSiguienteRevision = dpFecha.getValue();
            
            // Guardar los valores en el buffer
            BufferInspeccion.getInspeccionActual().setResultadoInspeccion(resultado);
            BufferInspeccion.getInspeccionActual().setFechaProximaInspeccion(fechaSiguienteRevision);

            // Insertamos todo en la base de datos
            boolean insertado = insertarInspeccion();
            
            // Si se ha insertado correctamente generamos el pdf
            if(insertado){

                // Obtener la carpeta de descargas del dispositivo
                String carpetaDescargas = System.getProperty("user.home") + File.separator + "Downloads";
                
                // Pasamos los datos a la clase que genera el informe
                generador.generarInformeITV(BufferInspeccion.getInspeccionActual(), BufferInspeccion.getDefectosActuales(), carpetaDescargas);
        

                // Mostrarmos ventana emergente informando al tecnico y redirigimos al panel
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Inspección Finalizada");
                alerta.setHeaderText("PDF Generado con Éxito");
                alerta.setContentText("El informe técnico se ha guardado en la carpeta 'Descargas'.\n\nAl aceptar, volverá al panel principal.");
                
                // Si pulsa aceptar redirigimos al panel
                alerta.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    
                    // Limpiamos el buffer de la inspeccion actual
                    BufferInspeccion.limpiarBuffer();

                    // Abrimos el panel del tecnico
                    Utilities.abrirVentana("/vistas/PanelTecnico.fxml", "Panel de Gestión - ITV");

                    Utilities.cerrarVentanaComodin(dpFecha);
                }
            });
            } else {
                Utilities.mostrarAlerta("Error", "No se ha podido insertar la inspeccion.", Alert.AlertType.ERROR);
            }
        }
    }

    /*
    *   Función que realiza el insert en la base de datos
    */

    private boolean insertarInspeccion(){

        //Variable para recoger el resultado
        boolean insertado = false;

        // Guardamos el cliente de la inspeccion
        Cliente cliente = BufferInspeccion.getClienteActual();
        String matricula = BufferInspeccion.getVehiculoActual().getMatricula();

        // Insertamos el cliente
        int idCliente = cliente.getId();

        // Si el id es 0 o -1, significa que es el cliente nuevo que creamos en AltaCliente
        if (idCliente <= 0) {
            idCliente = ClienteDAO.aniadirCliente(cliente);

            if(idCliente == -1){
                insertado = false;
            } else {
                cliente.setId(idCliente);
            }
        }

        // Guardamos el vehiculo con el cliente de esta inspeccion en su tabla
        ClienteDAO.vincularClienteConVehiculo(matricula, idCliente);

        // Realiza el insert
        insertado = InspeccionDAO.guardarInspeccion(BufferInspeccion.getInspeccionActual(), BufferInspeccion.getDefectosActuales());

        // Devuelve el resultado
        return insertado;
    }
}