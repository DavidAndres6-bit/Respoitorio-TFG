package controllers;

import clases.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    //Recoger el valor de los campos usuario y contraseña del Login
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasenia;

    //Instanciamos la claase usuario dao para poder llamar al metodo que valida si existe el usuario en la base de datos
    private UsuarioDAO usuario = new UsuarioDAO();


    //Evento action del boton de Iniciar sesion
    @FXML
    private void inicioSesion() {

        //Recoger el valor de las variables
        String nomUsuario = txtUsuario.getText().trim();
        String contra = txtContrasenia.getText().trim();

        //Primero validamos que los campos no esten vacios
        if(nomUsuario.isEmpty() || contra.isEmpty()){
            //Mostramos una ventana emergente informando de la situacion
            mostrarVentana("Campos Vacios", "Debes introducir usuario y contraseña para poder iniciar sesion");
        } else {
            //Llamamos a la funcion que realiza la consulta y comprobamos si encuentra al usuario en la base de datos
            boolean esta = usuario.validarInicio(nomUsuario, contra);

            //Si esta lo redirigimos a su panel
            if(esta){
                //Si esta pasamos al panel y cerramos esta ventana
                cambioVentana();
            } else {
                //Si no esta mostramos una ventana emergente informando del fallo
                mostrarVentana("Error de acceso", "Usuario o contraseña incorrectos.");
            }
        }
    }


    //Metodo que lanza una ventana emergente si los campos estan vacios o los datos introducidos son incorrectos
    private void mostrarVentana(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    //Metodo para cambiar a la ventana del panel del tecnico
    private void cambioVentana(){

        try {
            
            //Cargamos el fxml de la ventana del tecnico 
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/PanelTecnico.fxml"));
            Parent root = loader.load();
        
            //Crear una escena para esta ventana
            Scene scene = new Scene(root);

            Stage stage = new Stage();
        
            //Asignar la escena
            stage.setScene(scene);
            
            //Le asignamos un titulo de ventana
            stage.setTitle("Cyl-ITV Digital - Panel de Técnico");

            //Mostramos la escena
            stage.show();
        
            //Cerramos la ventana del login obteniendo el Stage actual (el del login) y cerrandolo
            Stage loginStage = (Stage) txtUsuario.getScene().getWindow();
            loginStage.close();
        
        } catch (Exception e) {
            System.err.println("Error al cambiar de ventana:");
            e.printStackTrace();
        }
   
    }
}
