package controllers;


import java.io.File;
import java.time.LocalDate;


import clases.DAOS.ClienteDAO;
import clases.DAOS.InspeccionDAO;
import clases.POJOS.Cliente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import utilities.BufferInspeccion;
import utilities.GenerarInforme;
import utilities.Utilities;


public class ResultadoController {

    /*
     * Recoger los valores del formulario
     */

    @FXML
    private Label lblMatricula, lblModelo;


    @FXML
    private ComboBox<String> spResultado;


    @FXML
    private DatePicker dpFecha;


    @FXML
    private Button btnInforme;

    // Varaible para bloquear el boton cuando se este generando el informe
    private boolean procesando = false;

    // Instancia de la clase que genera el PDF
    GenerarInforme generador = new GenerarInforme();

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

        // Pasar los valores al spinner del resultado de la inspeccion
        if (spResultado != null) {
            spResultado.setItems(FXCollections.observableArrayList("FAVORABLE", "NO FAVORABLE"));
            spResultado.setStyle("-fx-font-size: 24px;"); // Aumenta el tamaño de la letra

            // Escuchamos el cambio para habilitar o deshabilitar el DatePicker
            spResultado.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    if (newV.equals("NO FAVORABLE")) {
                        dpFecha.setDisable(true);
                        dpFecha.setValue(null);
                    } else {
                        dpFecha.setDisable(false);
                    }
                }
            });
        }
    }


   /*
     * Funcion para generar un PDF con el informe de la inspección
     */

    @FXML
    private void accionGenerarInforme() {
        // Variable para comprobar que todo es correcto
        boolean todoCorrecto = true;
        String resultado = spResultado.getValue();

        // Si esta haciendo el pdf, bloqueamos el boton
        if(procesando){
           todoCorrecto = false;
        
        } else {
            // Validamos que el resultado no sea nulo
            if (resultado == null) {
                Utilities.mostrarAlerta("Error", "Selecciona un resultado para la inspección.", Alert.AlertType.ERROR);
                todoCorrecto = false;
            } else {     
                //  Validamos la fecha solo si es FAVORABLE
                if (resultado.equals("FAVORABLE") && dpFecha.getValue() == null) {
                    Utilities.mostrarAlerta("Error", "Para inspecciones FAVORABLES es obligatoria la fecha de próxima revisión.", Alert.AlertType.ERROR);
                    todoCorrecto = false;
                }
            }
        }

        // Si ha pasado todas las validaciones
        if (todoCorrecto) {
            // Ponemos procesando a True 
            procesando = true;
            
            // Bloqueamos el boton y cambiamos el texto al instante
            btnInforme.setDisable(true);
            btnInforme.setText("Generando Informe...");
           
            // Recoger los valores introducidos en esta pantalla
            LocalDate fechaSiguienteRevision = dpFecha.getValue();

            // Guardar los valores en el buffer
            BufferInspeccion.getInspeccionActual().setResultadoInspeccion(resultado);
            BufferInspeccion.getInspeccionActual().setFechaProximaInspeccion(fechaSiguienteRevision);

            // Creamos un hilo secundario para no congelar la interfaz grafica
            Thread hiloProceso = new Thread(() -> {
                
                // Insertamos todo en la base de datos
                boolean insertado = insertarInspeccion();

                // Si se ha insertado correctamente generamos el pdf
                if (insertado) {
                    // Obtener la carpeta de descargas del dispositivo en el que se ejecute la aplicacion
                    String carpetaDescargas = System.getProperty("user.home") + File.separator + "Downloads"; 

                    try{
                        // Pasamos los datos a la clase que genera el informe
                        generador.generarInformeITV(BufferInspeccion.getInspeccionActual(),BufferInspeccion.getDefectosActuales(), carpetaDescargas);

                        // Mostramos los elementos graficos de exito en el hilo principal de JavaFX
                        javafx.application.Platform.runLater(() -> {
                            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                            alerta.setTitle("Inspección Finalizada");
                            alerta.setHeaderText("PDF Generado con Éxito");
                            alerta.setContentText("El informe técnico se ha guardado en la carpeta 'Descargas'.\n\nAl aceptar, volverá al panel principal.");
                            alerta.setGraphic(null);

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
                        });
                    
                    } catch (Exception e) {
                        // Si falla la generacion del PDF, restauramos el boton en el hilo principal
                        javafx.application.Platform.runLater(() -> {
                            procesando = false;
                            btnInforme.setDisable(false);
                            btnInforme.setText("Generar Informe");
                            
                            Utilities.mostrarAlerta("Error Crítico PDF", "No se pudo crear el archivo.\n\nDetalle técnico: " + e.getMessage(), Alert.AlertType.ERROR);
                        });
                        e.printStackTrace();
                    } 
                } else {
                    // Si falla el insert de la base de datos, restauramos el boton en el hilo principal
                    javafx.application.Platform.runLater(() -> {
                        procesando = false;
                        btnInforme.setDisable(false);
                        btnInforme.setText("Generar Informe");
                        Utilities.mostrarAlerta("Error", "No se ha podido insertar la inspeccion.", Alert.AlertType.ERROR);
                    });
                }   
            });
            
            // Iniciamos el hilo secundario
            hiloProceso.start();
        }
    }
    
    /*
    * Función que realiza el insert en la base de datos
    */

    private boolean insertarInspeccion() {
        // Variable para recoger el resultado
        boolean insertado = false;

        // Guardamos el cliente de la inspeccion
        Cliente cliente = BufferInspeccion.getClienteActual();
        String matricula = BufferInspeccion.getVehiculoActual().getMatricula();

        // Obtenemos el id del cliente
        int idCliente = cliente.getId();

        // Si el id es 0 o -1, comprobamos si ya existe por DNI o lo añadimos
        if (idCliente <= 0) {
            // Instancia de cliente DAO para buscar por DNI
            ClienteDAO clienteDAO = new ClienteDAO();
            Cliente existente = clienteDAO.obtenerClientePorDni(cliente.getDni());

            if (existente != null) {
                // Si ya existe en la base de datos recuperamos su ID
                idCliente = existente.getId();
                cliente.setId(idCliente);
            } else {
                // Si no existe lo insertamos como cliente nuevo
                idCliente = ClienteDAO.aniadirCliente(cliente);

                if (idCliente != -1) {
                    cliente.setId(idCliente);
                } else {
                    return false;
                }
            }
        }

        // Guardamos el vehiculo con el cliente de esta inspeccion en su tabla
        ClienteDAO.vincularClienteConVehiculo(matricula, idCliente);

        // Realiza el insert de la inspeccion y sus defectos
        insertado = InspeccionDAO.guardarInspeccion(BufferInspeccion.getInspeccionActual(), BufferInspeccion.getDefectosActuales());

        // Devuelve el resultado
        return insertado;
    }
}
