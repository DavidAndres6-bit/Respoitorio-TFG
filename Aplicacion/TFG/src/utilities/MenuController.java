package utilities;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {

    /*
    *   Metodo para controlar a donde redirige cada boton del menu
    */
    
    public static void abrirVentana(String ventana, ActionEvent event){
        String ruta = "";
        String titulo = "";

        // Segun la ventana abrimos el fxml correspondiente
        switch(ventana){
            case "exterior":
                ruta = "/vistas/AcondicionamientoExterior.fxml";
                titulo = "Acondicionamiento Exterior";
                break;
            case "interior":
                ruta = "/vistas/AcondicionamientoInterior.fxml";
                titulo = "Acondicionamiento Interior";
                break;
            case "luces":
                ruta = "/vistas/AlumbradoySenializacion.fxml";
                titulo = "Alumbrado y Señalización";
                break;
            case "emisiones":
                ruta = "/vistas/EmisionesContaminantes.fxml";
                titulo = "Emisiones Contaminantes";
                break;
            case "frenos":
                ruta = "/vistas/Frenos.fxml";
                titulo = "Frenos";
                break;
            case "direccion":
                ruta = "/vistas/Direccion.fxml";
                titulo = "Dirección";
                break;
            case "ejes":
                ruta = "/vistas/EjesRuedasNeumaticosSuspension.fxml";
                titulo = "Ejes y Suspensión";
                break;
            case "motor":
                ruta = "/vistas/MotoryTransmision.fxml";
                titulo = "Motor y Transmisión";
                break;
            case "defectos":
                ruta = "/vistas/Defectos.fxml";
                titulo = "Defectos Encontrados";
                break;
        }
    
    
        // Abrimos la ventana correspondiente
        try {
        
            // Cargar el nuevo FXML
            FXMLLoader loader = new FXMLLoader(MenuController.class.getResource(ruta));
            Parent root = loader.load();
            
            // Conseguir el Stage desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Cambiar la escena
            stage.setScene(new Scene(root));
            stage.setTitle("ITV - " + titulo);
            stage.show();
            
        } catch (IOException e) {
            System.out.println("Error al cargar la ventana " + ventana + ": " + e.getMessage());
        }
    }
}

