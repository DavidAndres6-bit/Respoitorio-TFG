package controllers;


import clases.DAOS.UsuarioDAO;
import clases.POJOS.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import utilities.Sesion;
import utilities.Utilities;
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


    /*
    *   Evento action del boton de Iniciar sesion
    */

    @FXML
    private void inicioSesion(ActionEvent event) {

        // Recoger el valor de las variables
        String nomUsuario = txtUsuario.getText().trim();
        String contra = txtContrasenia.getText().trim();


        //Primero validamos que los campos no esten vacios
        if(nomUsuario.isEmpty() || contra.isEmpty()){
            
            // Mostramos una ventana emergente informando de la situacion
            Utilities.mostrarAlerta("Campos Vacíos", "Debes introducir usuario y contraseña.", Alert.AlertType.WARNING);
         } else {

            // Llamamos a la funcion que realiza la consulta y comprobamos si encuentra al usuario en la base de datos
            Usuario u = usuario.validarInicio(nomUsuario, contra);

            // Si esta lo redirigimos a su panel
            if(u != null){

                // Guardamos el usuario en la Sesion
                Sesion.setUsuario(u);
                
                // Definimos la ruta segun el rol
                String fxml = "";
                String titulo = "";
            
                if(u.getRol().equalsIgnoreCase("ADMINISTRADOR")){
                    fxml ="/vistas/PanelAdministrador.fxml";
                    titulo = "Cyl-ITV Digital - Panel de Administrador";
                } else {
                    fxml ="/vistas/PanelTecnico.fxml";
                    titulo = "Cyl-ITV Digital - Panel de Técnico";
                }

                Utilities.abrirVentana(fxml, titulo);
        
                Utilities.cerrarVentana(event);
            } else {
               
                // Reiniciamos los valores de los campos 
                txtUsuario.clear();
                txtContrasenia.clear(); 

                // Quitar el foco del boton de inicio de sesion para que no se quede pillada la consulta
                txtUsuario.requestFocus();

                // Si no esta mostramos una ventana emergente informando del fallo
                Utilities.mostrarAlerta("Error de acceso", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
            }
        }    
    }
}