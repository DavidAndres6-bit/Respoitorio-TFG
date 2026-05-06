package utilities;

import javafx.event.ActionEvent;

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
        }
    }
}

