package clases.UI;

import javafx.application.Application; 
import javafx.fxml.FXMLLoader; 
import javafx.scene.Parent; 
import javafx.scene.Scene; 
import javafx.stage.Stage;
import controllers.LoginController; 

public class Login extends Application{

    @Override
    public void start(Stage arg0) throws Exception {
        try {
            
            //Cargamos la ventana del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            //Usamos el controlador del login desde el cual se gestiona la logica y funciones de esta ventana
            LoginController controller = loader.getController();


            //Configuramos la ventana con algunas opciones
            Scene scene = new Scene(root);

            //titulo de la ventana
            arg0.setTitle("Cyl-ITV Digital - Acceso");
            arg0.setScene(scene);
            
            //Para que no se pueda aumentar el tamaño quitamos el resizable
            arg0.setResizable(false);

            //Mostramos la ventana
            arg0.show();

        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    //Funcion para mostrar la ventana de login
    public static void main(String[] args) {
        launch(args);
    }

}
