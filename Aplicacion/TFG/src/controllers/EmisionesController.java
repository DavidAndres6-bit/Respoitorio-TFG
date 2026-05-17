package controllers;

import java.util.Map;

import clases.POJOS.Defecto;
import clases.POJOS.Vehiculo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utilities.BufferInspeccion;
import utilities.MenuController;
import utilities.Utilities;

public class EmisionesController {

    /*
     * Recoger los valores del formulario
     */

    @FXML
    private Label lblMatricula, lblModelo;

    @FXML
    private TextField txtOpacidad, txtRalenti, txtRalentiAcelerado;

    // Variables para almacenar los limites de emisiones en función del distintivo ambiental
    private double limiteOpacidad;
    private double limiteRalenti;
    private double limiteAcelerado;

    /*
     * Metodo para inicializar los valores de matricula modelo
     */

    @FXML
    public void initialize() {
        // Añadido: Limitación estricta de los campos de texto a tres cifras decimales
        limitarDecimales(txtOpacidad);
        limitarDecimales(txtRalenti);
        limitarDecimales(txtRalentiAcelerado);

        // Recuperamos el vehiculo para coger el distintivo ambiental
        Vehiculo v = BufferInspeccion.getVehiculoActual();

        // Cargar datos del buffer
        if (v != null) {
            lblMatricula.setText(v.getMatricula());
            lblModelo.setText(v.getModeloCompleto());

            // Llamamos al metodo que calcular los limites de emisiones
            calcularLimites(v);

            // Llamamos al metodo que controla que pruebas debe pasar
            manejarPruebas(v);

            // Recuperamos las posibles observaciones anteriores y los valores de emisiones
            Map<String, String> valoresPrevios = BufferInspeccion.getValoresEmisiones();

            if (valoresPrevios != null) {
                // Rellenamos los campos solo si el campo no está deshabilitado por la etiqueta
                if (!txtOpacidad.isDisable()) {
                    String val = valoresPrevios.getOrDefault("opacidad", "");
                    txtOpacidad.setText(val);
                }
                if (!txtRalenti.isDisable()) {
                    String val = valoresPrevios.getOrDefault("coRalenti", "");
                    txtRalenti.setText(val);
                }
                if (!txtRalentiAcelerado.isDisable()) {
                    String val = valoresPrevios.getOrDefault("coRalentiAcelerado", "");
                    txtRalentiAcelerado.setText(val);
                }
            }

            // Listeners para poder controlar el cambio de color de los inputs
            txtOpacidad.textProperty().addListener((obs, old, newValue) -> nivelesCorrectos());
            txtRalenti.textProperty().addListener((obs, old, newValue) -> nivelesCorrectos());
            txtRalentiAcelerado.textProperty().addListener((obs, old, newValue) -> nivelesCorrectos());

            nivelesCorrectos();
        }
    }

    /*
     * Funcion para limitar a 3 decimales el input de las emisiones y longitud maxima de  5 caracteres.
     */

    private void limitarDecimales(TextField textField) {
       textField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            // Recogemos lo que va a pasar en el cuadro de texto
            String nuevoTexto = change.getControlNewText();
            
            // Si el texto resultante se pasa de 5 caracteres, bloqueamos la pulsación
            if (nuevoTexto.length() > 5) {
                change.setText(""); // Borra lo que el usuario acaba de teclear antes de que aparezca
            }
            
            // Si meten letras o no cumple el formato, bloqueamos la pulsación
            if (!nuevoTexto.isEmpty() && !nuevoTexto.matches("\\d*([.,]\\d{0,3})?")) {
                change.setText(""); // Borra la letra introducida antes de que aparezca
            }
            
            // Aplica el cambio limpio 
            return change; 
        }));
    }

    /*
     * Funcion para mostrar una ventana emergente informativa con los límites calculados para el coche.
     */
    
    @FXML
    public void accionMostrarInfoLimites(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Límites de Emisiones Vigentes");
        alerta.setHeaderText("Criterios de Inspección para este Vehículo");
        
        Vehiculo v = BufferInspeccion.getVehiculoActual();
        if (v != null) {
            calcularLimites(v);
        }

        String infoOpacidad = txtOpacidad.isDisable() ? "No aplica (N/A)" : limiteOpacidad + " m⁻¹";
        String infoRalenti = txtRalenti.isDisable() ? "No aplica (N/A)" : limiteRalenti + " %";
        String infoAcelerado = txtRalentiAcelerado.isDisable() ? "No aplica (N/A)" : limiteAcelerado + " %";

        String contenido = "De acuerdo con el distintivo ambiental del vehículo, los límites son:\n\n"
                + "• Opacidad Máxima (Diésel): " + infoOpacidad + "\n"
                + "• CO en Ralentí Máximo: " + infoRalenti + "\n"
                + "• CO en Ralentí Acelerado Máximo: " + infoAcelerado + "\n\n"
                + "Nota: Las pruebas no aplicables se configuran automáticamente.";
                
        alerta.setContentText(contenido);
        alerta.getDialogPane().setPrefSize(500, 260);
        alerta.setGraphic(null); 
        alerta.showAndWait();
    }

    /*
     * Funcion que calcula los limites de contaminación siguiendo su distintivo y antiguedad
     */

    private void calcularLimites(Vehiculo v) {
        String distintivo = v.getTipoDgt().toUpperCase();
        int anio = v.getFechaMatriculacion().getYear();

        // Segun los valores calculamos
        if (distintivo.contains("SIN DISTINTIVO")) {
            if (anio < 1980) {
                limiteOpacidad = 3.5;
            } else {
                limiteOpacidad = 2.5;
            }
            limiteRalenti = 0.5;
            limiteAcelerado = 0.3;
        } else if (distintivo.contains("DISTINTIVO B")) {
            limiteOpacidad = 1.5;
            limiteRalenti = 0.3;
            limiteAcelerado = 0.2;
        } else if (distintivo.contains("DISTINTIVO C")) {
            limiteOpacidad = 0.5;
            limiteRalenti = 0.2;
            limiteAcelerado = 0.1;
        } else { // ECO o 0 Emisiones
            limiteOpacidad = 0.2;
            limiteRalenti = 0.1;
            limiteAcelerado = 0.0;
        }
    }

    /*
     * Funcion que maneja para cada tipo de vehiculo cuales son las pruebas que se le deben realizar
     */
    private void manejarPruebas(Vehiculo v) {
        String distintivo = v.getTipoDgt().toUpperCase();

        // Por defecto, habilitamos los de Gasolina (CO), que es lo más común
        habilitarCampo(txtRalenti);
        habilitarCampo(txtRalentiAcelerado);

        // Pero la opacidad (Diesel) suele estar deshabilitada a menos que sea un coche viejo o Diesel
        deshabilitarCampo(txtOpacidad);

        // Segun el distintivo habilitamos los correspondientes
        if (distintivo.contains("CERO") || distintivo.contains("0 EMISIONES")) {
            // Eléctricos no pasan pruebas de emisiones
            deshabilitarCampo(txtOpacidad);
            deshabilitarCampo(txtRalenti);
            deshabilitarCampo(txtRalentiAcelerado);

            // Informamos de ello
            txtOpacidad.setPromptText("N/A - Eléctrico");
            txtRalenti.setPromptText("N/A");
            txtRalentiAcelerado.setPromptText("N/A");
        } else if (distintivo.contains("ECO")) {
            // Híbridos: Se mide CO (Ralentí) pero NO opacidad
            deshabilitarCampo(txtOpacidad);
            txtOpacidad.setPromptText("N/A - ECO");
        } else if (distintivo.contains("SIN DISTINTIVO")) {
            // Coches antiguos: Solo Ralentí (normalmente)
            deshabilitarCampo(txtRalentiAcelerado);
            deshabilitarCampo(txtOpacidad);
        }
    }

    /*
     * Funcion que comprueba si los valores indicados son mayores a los permitidos
     */

    private boolean nivelesCorrectos() {
        // Variable que almacena el resultado global
        boolean correcto = true;

        // Estilos fijos para mantener el diseño
        String estiloFallo = "-fx-background-color: #ff9999; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";
        String estiloOk = "-fx-background-color: #99ff99; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";
        String estiloActivo = "-fx-background-color: white; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";
        String estiloBloqueado = "-fx-background-color: #eeeeee; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";

        // Validamos cada campo por separado
        boolean c1 = validarUnicoCampo(txtOpacidad, limiteOpacidad, "opacidad", "resOpacidad",
                estiloFallo, estiloOk, estiloActivo, estiloBloqueado,
                "04.01", "Opacidad excesiva (Diesel)");

        boolean c2 = validarUnicoCampo(txtRalenti, limiteRalenti, "coRalenti", "resCoRalenti",
                estiloFallo, estiloOk, estiloActivo, estiloBloqueado,
                "04.02", "Emisiones CO ralentí superiores a lo permitido");

        boolean c3 = validarUnicoCampo(txtRalentiAcelerado, limiteAcelerado, "coRalentiAcelerado", "resRalentiAcelerado",
                estiloFallo, estiloOk, estiloActivo, estiloBloqueado,
                "04.03", "Emisiones CO ralentí acelerado superiores a lo permitido");

        // Si alguno falla devolvemos false
        if (c1 == false || c2 == false || c3 == false) {
            correcto = false;
        }

        return correcto;
    }

    /*
     * Funcion para validar cada campo
     */

    private boolean validarUnicoCampo(TextField campo, double limite, String valor, String resultado, String fallo,
            String ok, String activo, String bloqueado, String codigoDefecto, String descripcionDefecto) {

        // Variable para guardar el resultado
        boolean correcto = false;

        // Si esta deshabilitado por la etiqueta aplicamos el estilo
        if (campo.isDisable()) {
            campo.setStyle(bloqueado);
            correcto = true;
        } else {
            // Limpiamos el defecto por si acaso
            BufferInspeccion.borrarDefecto(codigoDefecto);

            // Recogemos el valore del campo
            String texto = campo.getText().replace(",", ".").trim();

            // Si el tecnico borra el texto volvemos al estilo original
            if (texto.isEmpty()) {
                campo.setStyle(activo); // Vuelve a blanco con borde dorado
                BufferInspeccion.getValoresEmisiones().put(valor, "");
                BufferInspeccion.getValoresEmisiones().put(resultado, "");
                correcto = true;
            } else {
                // Comprobamos el valor con su limite establecido por el tipo de etiqueta
                try {
                    double emisiones = Double.parseDouble(texto);
                    BufferInspeccion.getValoresEmisiones().put(valor, texto);

                    if (emisiones > limite) {
                        campo.setStyle(fallo); // Fondo rojo, borde dorado
                        BufferInspeccion.getValoresEmisiones().put(resultado, "X");

                        // Agregamos un defecto automatico
                        BufferInspeccion.getDefectosActuales().add(new Defecto(codigoDefecto, descripcionDefecto, "GRAVE"));
                        correcto = false;
                    } else {
                        campo.setStyle(ok); // Fondo verde, borde dorado
                        BufferInspeccion.getValoresEmisiones().put(resultado, "S");
                        correcto = true;
                    }
                } catch (Exception e) {
                    // Si mete letras o caracteres raros, lo dejamos en blanco
                    campo.setStyle(activo);
                    correcto = false;
                }
            }
        }
        return correcto;
    }

    /*
     * Funcion para validar que campos debe rellenar obligatoriamente segun la etiqueta
     */

    private boolean validarCamposSegunEtiqueta() {
        boolean todoRelleno = true;

        // Validación de Opacidad
        if (!txtOpacidad.isDisable()) {
            String val = txtOpacidad.getText().trim();
            if (val.isEmpty() || val.equalsIgnoreCase("N/A")) {
                todoRelleno = false;
            }
        }

        // Validación de Ralentí
        if (todoRelleno && !txtRalenti.isDisable()) {
            String val = txtRalenti.getText().trim();
            if (val.isEmpty() || val.equalsIgnoreCase("N/A")) {
                todoRelleno = false;
            }
        }

        // Validación de Ralentí Acelerado
        if (todoRelleno && !txtRalentiAcelerado.isDisable()) {
            String val = txtRalentiAcelerado.getText().trim();
            if (val.isEmpty() || val.equalsIgnoreCase("N/A")) {
                todoRelleno = false;
            }
        }

        return todoRelleno;
    }

    private void guardarDatosEnBuffer() {
        // Comprobamos el resultado del apartado
        boolean resultadoGlobal = nivelesCorrectos();

        // Guardamos el resultado del apartado
        BufferInspeccion.getInspeccionActual().setEmisiones(resultadoGlobal);
    }

    /*
     * ActionEvent que maneja las opciones del menu (Mantiene tu firma intacta con mayúscula)
     */

    @FXML
    public void CambiarVentana(ActionEvent event) {
        // Guardar los datos en el buffer
        guardarDatosEnBuffer();

        // Obtenemos el botón
        Button btnPulsado = (Button) event.getSource();

        // Obtenemos su ID
        String seccion = btnPulsado.getId();

        // Llamamos al metodo de utilities
        MenuController.abrirVentana(seccion, event);
    }

    /*
     * Funcion para cambiar a la siguiente ventana
     */

    @FXML
    public void accionSiguiente(ActionEvent event) {
        // Validamos si ha rellenado los campos que debe rellenar
        boolean puedeAvanzar = validarCamposSegunEtiqueta();

        // Si los ha rellenado todos
        if (puedeAvanzar) {
            // Guardar los datos en el buffer
            guardarDatosEnBuffer();

            // Cambiamos de ventana
            Utilities.abrirVentana("/vistas/Frenos.fxml", "Frenos");
            Utilities.cerrarVentana(event);
        } else {
            Utilities.mostrarAlerta("Campos incompletos",
                    "Debe introducir los valores de las pruebas habilitadas para este vehículo.",
                    Alert.AlertType.WARNING);
        }
    }

    /*
     * Funcion para deshabilitar el campo
     */

    private void deshabilitarCampo(TextField campo) {
        campo.setText("N/A");
        campo.setDisable(true);

        // Mantenemos el borde gris suave para campos deshabilitados
        campo.setStyle("-fx-background-color: #eeeeee; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    /*
     * Funcion para habilitar el campo
     */

    private void habilitarCampo(TextField campo) {
        campo.clear();
        campo.setDisable(false);

        // Restauramos el borde dorado original del diseño
        campo.setStyle("-fx-background-color: white; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5;");
    }
}