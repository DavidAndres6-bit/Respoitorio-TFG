package utilities;

import java.io.IOException;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Utilities {

    /*
    *   Expresiones regulares para validar formatos de correo electronico y contraseña
    */

    private static final String REGEX_EMAIL = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    private static final String REGEX_TELEFONO = "^[6789]\\d{8}$";


    /*
    *   Metodo para abrir una nueva ventana para el admin
    */
   
    public static void abrirVentanaWait(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(Utilities.class.getResource(rutaFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait(); // Pausar el codigo para que se reflejen los cambios en la tabla
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    *   Metodo para abrir una nueva ventana para el tecnico
    */

    public static void abrirVentana(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(Utilities.class.getResource(rutaFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("ITV - " + titulo);

            // Añadir el logo de la aplicación a las ventanas
            Image icon = new Image(Utilities.class.getResourceAsStream("/img/Logo_app.png"));
            stage.getIcons().add(icon);

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *   Metodo para cerrar una ventana desde un boton
    */

    public static void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    /*
    *   Metodo para cerrar una ventana sin estar causado por un boton
    */

    public static void cerrarVentanaComodin(Node nodo) {
        Stage stage = (Stage) nodo.getScene().getWindow();
        stage.close();
    }


    /*
    *   Metodo para mostrar alertas
    */
   
    public static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Valida si un email tiene formato correcto
    */

    public static boolean correoValido(String email) {
        return email != null && Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * Valida si un teléfono tiene 9 dígitos y empieza por 6, 7, 8 o 9
    */

    public static boolean telefonoValido(String telefono) {
        return telefono != null && Pattern.matches(REGEX_TELEFONO, telefono);
    }

    /**
     * Valida si la contraseña cumple con lo que indicamos
    */

    public static boolean contraseniaValida(String pass) {
        return pass != null && pass.length() >= 7;
    }
}