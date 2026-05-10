package controllers;

import clases.DAOS.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utilities.Utilities;

public class ActualizarUsuarioController {

    /*
    *  Campos del Formulario
    */

    @FXML 
    private TextField txtCorreo, txtContrasenia;
    
    @FXML 
    private ChoiceBox<String> spRolUsuario;


    /*
    *   Inicializamos el spinner con los roles disponibles al cargar la aplicacion
    */

    public void initialize(){

        //Pasar los valores
        spRolUsuario.getItems().addAll("administrador","tecnico");
    }

    /*
    *   Funcion del boton actualizarUsuario
    */
    
    @FXML
    private void accionActualizar(ActionEvent event) {
        
        //Validamos que haya al menos un campo para actualizar
        if (txtCorreo.getText().isEmpty() || (spRolUsuario.getValue() == null && txtContrasenia.getText().isEmpty())) {
            Utilities.mostrarAlerta("Campos vacíos", "Por favor, debes seleccionar al menos un elemneto para actualizar.", Alert.AlertType.WARNING);
        } else {
            actualizarUsuario();
        }
    }

    /*
    *   Funcion del boton cancelar
    */
    
    @FXML
    private void accionCancelar(ActionEvent event) {
        Utilities.cerrarVentana(event);
    }

    /*
    *   Metodo para actualizar el usuario en la base de datos
    */

     @FXML
    private void actualizarUsuario() {

        //Recogemos los valores de los campos del formulario
        String correo = txtCorreo.getText();
        String rolSeleccionado = spRolUsuario.getValue();
        String nuevaContrasenia = txtContrasenia.getText();


        //Creamos una instancia de usuarioDAO para usar el metodo de actualizar
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        
        //Obtenemos el id del usuario
        int id = usuarioDAO.obtenerIdUsuario(correo);

        //Comprobamos que se ha encontrado ese usuario
        if (id != -1) {
            //Llammamos al metodo que actualiza
            boolean actualizado = usuarioDAO.actualizarRol(id, rolSeleccionado, nuevaContrasenia);

            //Comprobamos si se ha actualizado
            if(actualizado){
               Utilities.mostrarAlerta("Éxito", "Usuario actualizado correctamente.", AlertType.INFORMATION);
               Utilities.cerrarVentanaComodin(txtCorreo);
            } else {
                Utilities.mostrarAlerta("Información", "No se han realizado cambios.", AlertType.WARNING);
            }
        } else {
            Utilities.mostrarAlerta("Error", "No se encontró el usuario.", AlertType.ERROR);
        }        
    }
}
