package controllers;

import clases.DAOS.VehiculoDAO;
import clases.POJOS.Inspeccion;
import clases.POJOS.Vehiculo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class PanelTecnicoController {

    /*
    *   Campos del formulario
    */

    @FXML
    private TextField txtBuscarMatricula;

    @FXML
    private Pane panelInicial, panelDatos;

    @FXML
    private Label lblMatricula, lblMarca, lblModelo, lblFechaMatriculacion;

    @FXML
    private Button btnIniciarInspeccion;


    /*
    *   Variable para guardar el vehiculo si lo encontramos
    */

    private Vehiculo vehiculoEncontrado;

    /**
     * Funcion para buscar el vehículo por matrícula
    */

    @FXML
    private void accionBuscar(ActionEvent event) {

        //Recogemos la matricula que introduce el tecnico
        String matricula = txtBuscarMatricula.getText().trim().toUpperCase();

        if (matricula.isEmpty()) {

            //Si pulsa el boton sin introducir la matricula le informamos
            Utilities.mostrarAlerta("Campo vacío", "Por favor, introduce una matrícula.", Alert.AlertType.WARNING);

        } else {
            
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

                    // Cambiamos la visibilidad de los paneles
                    panelInicial.setVisible(false);
                    panelDatos.setVisible(true);
                    
                    // Habilitamos el botón de inspección
                    btnIniciarInspeccion.setDisable(false);
                    
                    // Inicializamos la clase que va a hacer la funcion de "buffer" durante la inspeccion del vehiculo
                    Inspeccion inspeccionActual = BufferInspeccion.getInspeccionActual();

                    //Guardamos los la matricula y el vehiculo que hemos obtenido en ese buffer
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

    /**
     * Función para pasar a la siguiente ventana y empezar con la inspeccion
    */
   
    @FXML
    private void accionIniciarInspeccion(ActionEvent event) {
        Utilities.abrirVentana("/vistas/Identificacion.fxml", "Identificación del Vehiculo");

        Utilities.cerrarVentana(event);
    }
}
