package controllers;

import clases.DAOS.ClienteDAO;
import clases.POJOS.Cliente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class AltaClienteController {

    /*
    *   Variables del formulario
    */

    @FXML
    private TextField txtMatricula, txtDni, txtNombre;

    /*
    *   Obtenemos la matricula del vehiculo y la mostramos
    */

    @FXML
    public void initialize() {
        // Recogemos la matricula del panel del tecnico
        String matricula = BufferInspeccion.getVehiculoActual().getMatricula();
        
        // Deshabilitamos la edicion en el input
        if(matricula != null){
            txtMatricula.setText(matricula);

            txtMatricula.setEditable(false);
        }
    }

   /*
    * Accion del boton registrar cliente
    */

    @FXML
    private void accionRegistrar(ActionEvent event) {
        // Recogemos los datos del formulario
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();

        // Validmaos que dni y mombre no esten vacioes
        if (dni.isEmpty() || nombre.isEmpty()) {
            Utilities.mostrarAlerta("Atención", "Todos los campos son obligatorios.", AlertType.WARNING);
        } else {
            // Si el formato del DNI no es correcto
            if (Utilities.dniValido(dni) == false) {
                Utilities.mostrarAlerta("Error de formato", "El DNI introducido no es válido o la letra no corresponde.", AlertType.ERROR);
            } else {
                // Instancia de cliente DAO
                ClienteDAO clienteDAO = new ClienteDAO();

                // Comprobamos si ya existe ese cliente
                Cliente clienteExistente = clienteDAO.obtenerClientePorDni(dni);
                
                if (clienteExistente != null) {
                    // Si existe lo guardamos en el buffer y cerramos
                    BufferInspeccion.setClienteActual(clienteExistente);
                    Utilities.cerrarVentana(event);
                } else {
                    // Si no existe creamos un objeto cliente nuevo
                    Cliente clienteBuffer = new Cliente();
                    clienteBuffer.setDni(dni.toUpperCase());
                    clienteBuffer.setNombre(nombre);
                    
                    // Lo guardamos en el buffer
                    BufferInspeccion.setClienteActual(clienteBuffer);
                    Utilities.cerrarVentana(event);
                }
            }  
        }
    }
    
    /*
    *   Accion del boton cancelar
    */

    @FXML
    private void accionCancelar(ActionEvent event) {
        Utilities.cerrarVentana(event);
    }
}
