package utilities;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Utilities {

    /*
     * Expresiones regulares para validar formatos de correo electronico y
     * contraseña
     */

    private static final String REGEX_EMAIL = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    private static final String REGEX_TELEFONO = "^[6789]\\d{8}$";

    /*
     * Metodo para abrir una nueva ventana para el admin
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
     * Metodo para abrir una nueva ventana para el tecnico
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
     * Metodo para cerrar una ventana desde un boton
     */

    public static void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /*
     * Metodo para cerrar una ventana sin estar causado por un boton
     */

    public static void cerrarVentanaComodin(Node nodo) {
        Stage stage = (Stage) nodo.getScene().getWindow();
        stage.close();
    }

    /*
     * Metodo para mostrar alertas
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

    /*
     * Metodo para configurar los choicebox
     */

    public static void configurarChoiceBox(ChoiceBox<String> cb, String codigoUnidad) {

        // Cargamos los posibles valores previos
        cb.getItems().addAll("SIN FALLOS", "LEVE", "MEDIANO", "GRAVE");

        // Estilos para el choicebox
        cb.setStyle("-fx-font-size: 18px; " +
                "-fx-pref-height: 42px; " +
                "-fx-pref-width: 354px; " +
                "-fx-cursor: hand;");

        // Hemos preguntado a la IA como centrar el texto sin padding para que se vea bien con el 
        // texto de LEVE... tambien y nos ha echo estado
        // Esperamos a que el ChoiceBox se dibuje para encontrar la etiqueta interna y centrarla
        cb.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Label label = (Label) cb.lookup(".label");
                if (label != null) {
                    label.setAlignment(javafx.geometry.Pos.CENTER);
                    // Hacemos que la etiqueta interna ocupe casi todo el ancho para que el centro sea real
                    label.setPrefWidth(cb.getPrefWidth() - 30); 
                }
            }
        });

        // Solo lo ejecutamos si el usuario hace click para evitar problemas con la ventana emergente
        cb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // Guardamos la seleccion del usuario
                String seleccion = cb.getValue();

                // Si no hay seleccion o se selecciona sin fallos quitamos el defecto
                if (seleccion != null && seleccion.equals("SIN FALLOS")) {
                    BufferInspeccion.borrarDefecto(codigoUnidad);
                }

                // Funcionamiento de la ventana emergente
                if (cb.isFocused()) {
                
                    // Si ha seleccionado una distinta a sin fallos mostramos la ventana emergente
                    if (seleccion != null && !seleccion.equals("SIN FALLOS")) {

                        // Borramos lo que hubiera antes para actualizar con lo nuevo
                        BufferInspeccion.borrarDefecto(codigoUnidad);

                        // Creamos una ventana emergente de tipo input
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Detalle Defecto");
                        dialog.setHeaderText("Código: " + codigoUnidad + " | Grado: " + seleccion);
                        dialog.setContentText("Detalla el defecto:");

                        dialog.getDialogPane().setMinWidth(600);
                        dialog.getDialogPane()
                            .setStyle("-fx-font-size: 14pt; -fx-border-color: #bf820d; -fx-border-width: 2;");
                            
                        // Mostramos la ventana emergente y recogemos la respuesta
                        Optional<String> resultado = dialog.showAndWait();

                        // Si el usuario ha introducido una observacion
                        if (resultado.isPresent()) {

                            // Obtenemos la observacion
                            String defecto = resultado.get();

                            // Guardamos el defecto
                            BufferInspeccion.aniadirDefecto(codigoUnidad, defecto, seleccion);
                            
                        } 
                    }
                }
            }
        });

        // Usamos runLater para que el seteo ocurra justo después de cargar la vista
            javafx.application.Platform.runLater(() -> {
                if (cb.getValue() == null) {
                    cb.setValue("SIN FALLOS");
                }
            });
        }
    }