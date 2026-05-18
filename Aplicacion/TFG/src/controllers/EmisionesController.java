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
        // Limitación estricta de los campos de texto a tres cifras decimales
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
     * Funcion para limitar la cantidad decimales el input de las emisiones y la logntidud total maxima.
     */

    private void limitarDecimales(TextField textField) {
       textField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            // Recogemos lo que va a pasar en el cuadro de texto
            String nuevoTexto = change.getControlNewText();
            
            // Si escribe mas de 5 caracteres bloqueamos
            if (nuevoTexto.length() > 5) {
                change.setText(""); // Borra lo que el usuario acaba de teclear antes de que aparezca
            }
            
            // Si meten letras o no cumple el formato, bloqueamos la pulsación
            if (!nuevoTexto.isEmpty() && !nuevoTexto.matches("\\d*([.,]\\d{0,3})?")) {
                change.setText(""); // Borra la letra introducida antes de que aparezca
            }
            
            // Aplicamos el cambio 
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
        
        // Recogemos el vehiculo y buscamos su informacion
        Vehiculo v = BufferInspeccion.getVehiculoActual();
        if (v != null) {
            calcularLimites(v);
            manejarPruebas(v);
        }

        // Lee si el switch de arriba ha bloqueado o no las cajas de texto
        String infoOpacidad = "";
        if (txtOpacidad.isDisable()) {
            infoOpacidad = "No aplica (N/A)";
        } else {
            infoOpacidad = limiteOpacidad + " m⁻¹";
        }

        String infoRalenti = "";
        if (txtRalenti.isDisable()) {
            infoRalenti = "No aplica (N/A)";
        } else {
            infoRalenti = limiteRalenti + " %";
        }

        String infoAcelerado = "";
        if (txtRalentiAcelerado.isDisable()) {
            infoAcelerado = "No aplica (N/A)";
        } else {
            infoAcelerado = limiteAcelerado + " %";
        }

        String contenido = "De acuerdo con el distintivo ambiental del vehículo, los límites son:\n\n"
                + "• Opacidad Máxima: " + infoOpacidad + "\n"
                + "• CO en Ralentí Máximo: " + infoRalenti + "\n"
                + "• CO en Ralentí Acelerado Máximo: " + infoAcelerado + "\n\n"
                + "Nota: Las pruebas no aplicables se configuran automáticamente según su distintivo.";
           
        // Configuramos la ventana emergente
        alerta.setContentText(contenido);
        alerta.getDialogPane().setPrefSize(500, 260); // Tamaño
        alerta.setGraphic(null);  // Quitar el icono que sal epor defecto
        alerta.showAndWait();
    }

    /*
     * Funcion que calcula los limites de contaminación de un vehiculo segun su distintivo
     */

   private void calcularLimites(Vehiculo v) {
        // Recogemos el distintivo, y el año para los vehiculos sin distintivo ambiental
        String distintivo = v.getDistintivo().toUpperCase().trim();
        int anio = v.getFechaMatriculacion().getYear();

        // Calculamos segun el distintivo
        switch (distintivo) {
            case "SIN DISTINTIVO":
                if (anio < 1980) {
                    limiteOpacidad = 3.5;
                } else {
                    limiteOpacidad = 2.5;
                }
                limiteRalenti = 0.5;
                limiteAcelerado = 0.3;
                break;

            case "DISTINTIVO B":
                limiteOpacidad = 1.5;
                limiteRalenti = 0.3;
                limiteAcelerado = 0.2;
                break;

            case "DISTINTIVO C":
                limiteOpacidad = 0.5;
                limiteRalenti = 0.2;
                limiteAcelerado = 0.1;
                break;

            case "ECO":
                limiteOpacidad = 0.2;
                limiteRalenti = 0.1;
                limiteAcelerado = 0.1;
                break;

            case "CERO":
            default:
                limiteOpacidad = 0.0;
                limiteRalenti = 0.0;
                limiteAcelerado = 0.0;
                break;
        }
    }

    /*
     * Funcion que maneja para cada tipo de vehiculo cuales son las pruebas que se le deben realizar
     */

    private void manejarPruebas(Vehiculo v) {
        // Recogemos el distintivo
        String distintivo = v.getDistintivo().toUpperCase().trim();

        // Por defecto, dejamos todo libre para que el técnico trabaje
        habilitarCampo(txtOpacidad);
        habilitarCampo(txtRalenti);
        habilitarCampo(txtRalentiAcelerado);
        txtOpacidad.setPromptText("");
        txtRalenti.setPromptText("");
        txtRalentiAcelerado.setPromptText("");

        // Segun el distintivo configuramos los inputss
        switch (distintivo) {
            case "CERO":
                deshabilitarCampo(txtOpacidad);
                deshabilitarCampo(txtRalenti);
                deshabilitarCampo(txtRalentiAcelerado);
                txtOpacidad.setPromptText("N/A - Exento");
                txtRalenti.setPromptText("N/A");
                txtRalentiAcelerado.setPromptText("N/A");
                break;

            case "ECO":
                deshabilitarCampo(txtOpacidad);
                txtOpacidad.setPromptText("N/A - ECO");
                break;

            case "SIN DISTINTIVO":
                deshabilitarCampo(txtRalentiAcelerado);
                txtRalentiAcelerado.setPromptText("N/A");
                break;

            // Si son B o C pasan todas
            case "DISTINTIVO B":
            case "DISTINTIVO C":
                break;
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
     * Funcion para validar cada campo de forma individual
     */

    private boolean validarUnicoCampo(TextField campo, double limite, String valor, String resultado, String fallo,String ok, String activo, String bloqueado, String codigoDefecto, String descripcionDefecto) {
        // Variable para guardar el resultado
        boolean correcto = false;

        // Si esta deshabilitado por la etiqueta aplicamos el estilo
        if (campo.isDisable()) {
            campo.setStyle(bloqueado);
            correcto = true;
        } else {
            // Limpiamos el defecto por si acaso
            BufferInspeccion.borrarDefecto(codigoDefecto);

            // Recogemos el valor del campo
            String texto = campo.getText().replace(",", ".").trim();

            // Si el tecnico borra el texto volvemos al estilo original
            if (texto.isEmpty()) {
                campo.setStyle(activo); // Vuelve a blanco con borde dorado
                BufferInspeccion.getValoresEmisiones().put(valor, "");
                BufferInspeccion.getValoresEmisiones().put(resultado, "");
                correcto = true;
            } else {
                // Comparamos el valor con su limite establecido por el tipo de etiqueta
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
     * ActionEvent que maneja las opciones del menu 
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
            Utilities.mostrarAlerta("Campos incompletos","Debe introducir los valores de las pruebas habilitadas para este vehículo.", Alert.AlertType.WARNING);
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