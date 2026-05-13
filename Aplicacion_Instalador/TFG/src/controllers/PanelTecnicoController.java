package controllers;

import java.util.List;



import clases.DAOS.ClienteDAO;
import clases.DAOS.InspeccionDAO;
import clases.DAOS.VehiculoDAO;
import clases.POJOS.Cliente;
import clases.POJOS.Inspeccion;
import clases.POJOS.Vehiculo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import utilities.BufferInspeccion;
import utilities.Utilities;

public class PanelTecnicoController {

    /*
    *   Campos del formulario
    */

    @FXML
    private TextField txtBuscarMatricula;

    @FXML
    private Pane panelInicial, panelDatos, panelFormularioCliente;

    @FXML
    private Label lblMatricula, lblMarca, lblModelo, lblFechaMatriculacion, lblCliente, lblNombreTecnico;

    @FXML
    private Button btnIniciarInspeccion, btnRegistrarCliente;

    @FXML
    private Pane paneConHistorial, paneSinHistorial;
    
    @FXML
    private VBox vboxListaInspecciones;

    /*
    *   Variable para guardar el vehiculo si lo encontramos
    */

    private Vehiculo vehiculoEncontrado;

    /**
     * Funcion para buscar el vehículo por matrícula
    */

    /*
    *   Inicializamos el nombre del tecnico
    */

    @FXML
    public void initialize() {
        // Comprobamos si hay un usuario en esta inspeccion
        if (utilities.Sesion.getUsuario() != null) {
            String nombre = utilities.Sesion.getUsuario().getNombre();
            lblNombreTecnico.setText(nombre);
        }
    }

    @FXML
    private void accionBuscar(ActionEvent event) {

        //Recogemos la matricula que introduce el tecnico
        String matricula = txtBuscarMatricula.getText().trim().toUpperCase();

        if (matricula.isEmpty()) {

            //Si pulsa el boton sin introducir la matricula le informamos
            Utilities.mostrarAlerta("Campo vacío", "Por favor, introduce una matrícula.", Alert.AlertType.WARNING);

        } else {
            
            // Mostramos sus posibles inspecciones previas
            mostrarInspeccionesVehiculo(matricula);

            //Cremamos un instancia del DAO de vehiculo para usar el metodo de bucar vehiculo
            VehiculoDAO vehiculoDAO = new VehiculoDAO();

            //Buscamos el vehiculo con el metodo y recogemos lo que devuelve
            vehiculoEncontrado = vehiculoDAO.buscarVehiculo(matricula);

            //Si encuentra el vehiculo en la base de datos
            if (vehiculoEncontrado != null) {

                    // Rellenamos los labels
                    lblMatricula.setText(vehiculoEncontrado.getMatricula());
                    lblMarca.setText(vehiculoEncontrado.getMarca());
                    lblModelo.setText(vehiculoEncontrado.getModelo());
                    lblFechaMatriculacion.setText(vehiculoEncontrado.getFechaMatriculacion().toString());

                    // Buscamos el cliente asociado al vehiculo
                    Cliente clienteAsociado = ClienteDAO.buscarCliente(matricula);

                    if(clienteAsociado != null){
                        lblCliente.setText(clienteAsociado.getDni() + ", " +(clienteAsociado.getNombre()));
                       
                        // Guardar el cliente en el buffer de la inspeccion
                        BufferInspeccion.setClienteActual(clienteAsociado);
                    
                        btnIniciarInspeccion.setDisable(false);
                    } else {

                        // Si NO hay cliente, limpiamos labels y mostramos botón de añadir
                        lblCliente.setText("Sin asignar");
                        
                        BufferInspeccion.setClienteActual(null);
                        btnIniciarInspeccion.setDisable(true);  // Bloqueamos el boton hasta que exista el cliente
                    }

                        // Dejamos el boton activo
                        btnRegistrarCliente.setVisible(true);
                        
                        // Cambiamos la visibilidad de los paneles
                        panelInicial.setVisible(false);
                        panelDatos.setVisible(true);

                        // Buffer de inspección
                        Inspeccion inspeccionActual = BufferInspeccion.getInspeccionActual();
                        inspeccionActual.setMatriculaCoche(vehiculoEncontrado.getMatricula());
                        BufferInspeccion.setVehiculoActual(vehiculoEncontrado);
                    
                } else {
                    
                    // Si no existe volvemos al estado inicial
                    panelInicial.setVisible(true);
                    panelDatos.setVisible(false);
                    btnIniciarInspeccion.setDisable(true);
                    Utilities.mostrarAlerta("Sin resultados", "No se ha encontrado ningún vehículo con esa matrícula.", Alert.AlertType.INFORMATION);
                }
            }
        }

    /*
    *   Funcion para cargar las inspecciones previas de un vehiculo
    */

    public void mostrarInspeccionesVehiculo(String matricula){
      
        InspeccionDAO inspeccionDAO = new InspeccionDAO();

        // Lista para guardar las inspecciones
        List<String> inspecciones = inspeccionDAO.obtenerHistorialPorMatricula(matricula);

        // Si no hay inspecciones mostramos el panel que informa sobre ello
        if (inspecciones.isEmpty()) {
            paneSinHistorial.setVisible(true);
            paneConHistorial.setVisible(false);
        } else {
            paneSinHistorial.setVisible(false);
            paneConHistorial.setVisible(true);

            // Limpiamos primero el vbox para evitar mostrar posibles datos previos
            vboxListaInspecciones.getChildren().clear();

            // Recorremos la lista de Strings y creamos un Label para cada una
            for (String dato : inspecciones) {
                Label lblInspeccion = new Label(dato);

                // Estilo para que quede bien dentro del panel
                lblInspeccion.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-text-fill: #333333; -fx-alignment: center;");
                lblInspeccion.setMaxWidth(Double.MAX_VALUE);
                lblInspeccion.setAlignment(javafx.geometry.Pos.CENTER);
                // Añadimos el Label al VBox
                vboxListaInspecciones.getChildren().add(lblInspeccion);
            }
        }
    }


    /*
    *   Accion del boton añadir cliente
    */

    @FXML
    private void accionRegistrarCliente(ActionEvent event) {
        
        // Abrimos la ventana con show and wait para que se actualizen los valores una vez rellenados
        Utilities.abrirVentanaWait("/vistas/AltaCliente.fxml", "Registro de Cliente");

        // Al volver guardamos ese cliente en el buffer
        if (BufferInspeccion.getClienteActual() != null) {
            Cliente nuevo = BufferInspeccion.getClienteActual();
            lblCliente.setText(nuevo.getDni() + ", " + nuevo.getNombre());
            btnIniciarInspeccion.setDisable(false);
        }
    }

    /**
     * Función para pasar a la siguiente ventana y empezar con la inspeccion
    */
   
    @FXML
    private void accionIniciarInspeccion(ActionEvent event) {

        // Recogemos la matricula
        String matricula = txtBuscarMatricula.getText().toUpperCase().trim();

        // Comprobar que no este vacia
        if (matricula.isEmpty()) {
            Utilities.mostrarAlerta("Error", "Introduce una matrícula.", Alert.AlertType.ERROR);
        } else {
            if (InspeccionDAO.existeInspeccionHoy(matricula)) {
                Utilities.mostrarAlerta("Inspeccion ya realizada", 
                "El vehículo con matrícula " + matricula + " ya ha realizado una inspección en el día de hoy.\n\nNo se permiten duplicados diarios.", 
                Alert.AlertType.WARNING); 
            } else {
                Utilities.abrirVentana("/vistas/Identificacion.fxml", "Identificación del Vehiculo");

                Utilities.cerrarVentana(event);
            }
        }
    }

    /*
    *   Función para cerrar la sesion
    */
   
    @FXML
    private void cerrarSesion(ActionEvent event){

        // Limpiamos el usuario de la sesion
        utilities.Sesion.setUsuario(null);

        // Volvemos al Login
        Utilities.abrirVentana("/vistas/Login.fxml", "Cyl-ITV Digital - Acceso");

        // Cerramos la ventana
        Utilities.cerrarVentana(event);
    }
}