package clases.UI;

import java.io.InputStream;

import javafx.application.Application; 
import javafx.fxml.FXMLLoader; 
import javafx.scene.Parent; 
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage; 

public class Login extends Application{

    @Override
    public void start(Stage arg0) throws Exception {
        try {
            
            //Cargamos la ventana del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            //Configuramos la ventana con algunas opciones
            Scene scene = new Scene(root);

            //Añadir el log0 de la aplicacion
            InputStream is = getClass().getResourceAsStream("/img/Logo_app.png");
            if (is != null) {
                arg0.getIcons().add(new Image(is));
            } else {
                System.out.println("No se pudo encontrar el logo en: /img/Logo_app.png");
            }
        
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
