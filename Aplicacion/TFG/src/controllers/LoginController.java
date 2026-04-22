package controllers;

import clases.DAOS.UsuarioDAO;
import clases.POJOS.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import utilities.Sesion;
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
            Usuario u = usuario.validarInicio(nomUsuario, contra);

            //Si esta lo redirigimos a su panel
            if(u != null){
                //Guardamos el usuario en la Sesion
                Sesion.setUsuario(u);
                
                //Si esta pasamos al panel y cerramos esta ventana
                cambioVentana(u);
            } else {
               
                //Reiniciamos los valores de los campos 
                txtUsuario.clear();
                txtContrasenia.clear(); 

                //Quitar el foco del boton de inicio de sesion para que no se quede pillada la consulta
                txtUsuario.requestFocus();

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
    private void cambioVentana(Usuario usuario){

        try {
            
            //Variables para las ventanas
            String fxml = "";
            String titulo = "";

            //Comprobamos cual es el rol del usuario para redirigirle a su panel correspondiente
            if(usuario.getRol().equalsIgnoreCase("ADMINISTRADOR")){
                fxml ="/vistas/PanelAdministrador.fxml";
                titulo = "Cyl-ITV Digital - Panel de Administrador";
            } else {
                fxml ="/vistas/PanelTecnico.fxml";
                titulo = "Cyl-ITV Digital - Panel de Técnico";
            }

            //Cargamos la ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

            //Configuramos las ventanas con opciones comunes para los dos paneles
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.setTitle(titulo);  //Titulo de la ventana
            stage.setResizable(false); //Quitamos el resizable
            
            stage.show();

            //Cerrar el login
            Stage loginStage = (Stage) txtUsuario.getScene().getWindow();
            loginStage.close();

            
        } catch (Exception e) {
            System.err.println("Error al cambiar de ventana:");
            e.printStackTrace();
        }
   
    }
}
