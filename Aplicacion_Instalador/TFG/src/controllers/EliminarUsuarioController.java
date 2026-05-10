package controllers;

import java.util.Optional;

import clases.DAOS.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import utilities.Sesion;
import utilities.Utilities;


public class EliminarUsuarioController {

    /*
    *  Campos del Formulario
    */

    @FXML 
    private TextField txtCorreo, txtContrasenia;

    /*
    *   Funcion del boton eliminar usuario
    */

    @FXML
    private void accionEliminar(){
       
        //Validamos que haya rellenado todos los campos
        if(txtCorreo.getText().isEmpty()|| txtContrasenia.getText().isEmpty()){
            //Mostrar ventana emergente
            Utilities.mostrarAlerta("Campos vacíos", "Por favor, rellena todos los campos del formulario.", Alert.AlertType.WARNING);
        
        } else {
            eliminarUsuario();
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
    *   Metodo para eliminar el usuario de la base de datos
    */

    @FXML
    private void eliminarUsuario() {

        //Recoger los valores del formulario
        String correo = txtCorreo.getText();
        String contrasenia = txtContrasenia.getText();

        //Contraseña del administrador registrado
        String contraAdmin = Sesion.getUsuario().getContrasenia();

        //Instancia de la clase UsuarioDAO para utilizar el metodo de eliminar usuario
        UsuarioDAO usuarioDAO = new UsuarioDAO();


        //Validamos que los datos del formulario no esten vacios
        if(correo.isEmpty() || contrasenia.isEmpty()){
            
            //Mostrar ventana emergente informando
            Utilities.mostrarAlerta("Campos vacíos", "Por favor, rellena todos los campos del formulario.", Alert.AlertType.WARNING);

        } else {
          
            //Comprobamos que la contraseña sea la del administrador
            if(contrasenia.equals(contraAdmin)){
                
                //Utilizamos el metodo del dao para obtener el usuario que vamos a borrar
                int id = usuarioDAO.obtenerIdUsuario(correo);

                //Si lo hemos encontrado
                if(id != -1){

                    //Mostrar ventana emergente para pedir al usuario que confirme antes de eliminar
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText("¿Estás seguro de eliminar al usuario?");
                    confirm.setContentText("Correo: " + correo + "\nEsta acción no se puede deshacer.");

                    Optional<ButtonType> resultado = confirm.showAndWait();

                   if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    
                    // Borramos el usuario de la base de datos
                    if (usuarioDAO.eliminarUsuario(id)) {
                        Utilities.mostrarAlerta("Éxito", "Usuario eliminado correctamente.", Alert.AlertType.INFORMATION);
                        Utilities.cerrarVentanaComodin(txtCorreo);

                    } else {
                        Utilities.mostrarAlerta("Error", "No se pudo eliminar el registro.", Alert.AlertType.ERROR);
                    }
                }
           
            } else {
                // El correo no existe
                Utilities.mostrarAlerta("No encontrado", "No existe ningún usuario con ese correo electrónico.", Alert.AlertType.ERROR);
            }
            
            } else {
                // La contraseña no es la del administrador
                Utilities.mostrarAlerta("Error de seguridad", "La contraseña de administrador es incorrecta.", Alert.AlertType.ERROR);
            }
        }
    }
}