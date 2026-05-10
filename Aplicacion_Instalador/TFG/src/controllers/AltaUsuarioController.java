package controllers;

import clases.DAOS.UsuarioDAO;
import clases.POJOS.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utilities.Utilities;

public class AltaUsuarioController {


    /*
    *  Campos del Formulario
    */

    @FXML 
    private TextField txtNombre, txtCorreo, txtTelefono;
    
    @FXML
    private PasswordField txtContrasenia;

    @FXML 
    private ComboBox<String> spRolUsuario;


    /*
    *   Inicializamos el spinner con los roles disponibles al cargar la aplicacion
    */

    public void initialize(){

        //Pasar los valores
        spRolUsuario.getItems().addAll("administrador","tecnico");
    }

    /*
    *   Funcion del boton guardarUsuario
    */
    
    @FXML
    private void accionGuardar(ActionEvent event) {
        
        //Validamos que haya rellenado todos los campos
        if (spRolUsuario.getValue() == null || txtNombre.getText().isEmpty() || txtContrasenia.getText().isEmpty() || txtCorreo.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
            Utilities.mostrarAlerta("Campos vacíos", "Por favor, rellena todos los campos del formulario.", Alert.AlertType.WARNING);
        } else {

            //Validamos que los campos cumplan las condiciones requeridas
            String mensajeError = validarFormatos();
    
            if (mensajeError != null) {
                //Mostramos el mensaje correspondiente
                Utilities.mostrarAlerta("Error de formato", mensajeError, Alert.AlertType.ERROR);
            } else {
                // Si no ha habido errores lo guardamos
                guardarUsuario();
            }
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
    *   Metodo para guardar el usuario en la base de datos
    */

    @FXML
    private void guardarUsuario() {

        //Recogemos los valores de los campos del formulario
        String nombre = txtNombre.getText();
        String contrasenia = txtContrasenia.getText();
        String email = txtCorreo.getText();
        int telefono = Integer.parseInt(txtTelefono.getText());
        String rol = spRolUsuario.getValue();

        //Creamos un objeto de tipo usuario con los valores recogidos
        Usuario usuario = new Usuario(nombre, contrasenia, email, telefono, rol);

        //Creamos una intancia del DAO de usuario y usamos su metodo para realizar el insert en la base de datos
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean insertado = usuarioDAO.insertar(usuario);


        //Comprobamos si se ha insertado correctamente
        if(insertado){
            //Mostramos una ventana emergente indicando que se ha insertado y cerramos la ventana
            Utilities.mostrarAlerta("Éxito", "Usuario añadido correctamente.", Alert.AlertType.INFORMATION);
            Utilities.cerrarVentanaComodin(txtCorreo);
    
        } else {
            //Mostramos una ventana emergente indicando el error
            Utilities.mostrarAlerta("Error", "No se pudo añadir el usuario.", Alert.AlertType.ERROR);
        }
    }

    /*
    *   Metodo para validar formatos de los datos
    */

    private String validarFormatos() {
        String respuesta = null;
        
        // Validamos el correo
        if (!Utilities.correoValido(txtCorreo.getText())) {
            return "El formato del correo electrónico no es válido (ejemplo@dominio.com).";
        }

        // Validamos el teléfono
        if (!Utilities.telefonoValido(txtTelefono.getText())) {
            return "El teléfono no es válido. Debe tener 9 dígitos y empezar por 6, 7, 8 o 9.";
        }

        // Validamos la contraseña 
        if (!Utilities.contraseniaValida(txtContrasenia.getText())) {
            return "La contraseña es demasiado corta. Debe tener al menos 6 caracteres.";
        }

        return respuesta; 
    }
}