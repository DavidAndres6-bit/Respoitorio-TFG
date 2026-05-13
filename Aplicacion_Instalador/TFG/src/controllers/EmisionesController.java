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
     * Funcion que calcula los limites de contaminación siguiendo su distintivo y
     * antiguedad
     */

    private void calcularLimites(Vehiculo v) {
        String distintivo = v.getTipoDgt().toUpperCase();
        int anio = v.getFechaMatriculacion().getYear();

        // Segun los valores calculamos
        if (distintivo.contains("SIN DISTINTIVO")) {
            limiteOpacidad = (anio < 1980) ? 3.5 : 2.5;
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
     * Funcion que maneja para cada tipo de vehiculo cuales son las pruebas que se
     * le deben realizar
     */

    private void manejarPruebas(Vehiculo v) {
        String distintivo = v.getTipoDgt().toUpperCase();

        // Por defecto deshabilitamos todo
        habilitarCampo(txtOpacidad);
        habilitarCampo(txtRalenti);
        habilitarCampo(txtRalentiAcelerado);

        // Segun el distintivo habilitamos los correspondientes
        if (distintivo.contains("SIN DISTINTIVO")) {
            // Para los antiguos el acelerado no suele aplicar
            deshabilitarCampo(txtRalentiAcelerado);
        } else if (distintivo.contains("0 EMISIONES")) {
            // Para los electricos deshabilitamos todas las pruebas
            deshabilitarCampo(txtOpacidad);
            deshabilitarCampo(txtRalenti);
            deshabilitarCampo(txtRalentiAcelerado);
        }
    }

    /*
     * Funcion que comprueba si los valores indicados son mayores a los permitidos
     */

    private boolean nivelesCorrectos() {

        // Variable que almacena el resultado global
        boolean correcto = true;

        // Empezamos con todos correctos
        boolean c1 = true;
        boolean c2 = true;
        boolean c3 = true;

        // Estilos fijos para mantener el diseño
        String estiloFallo = "-fx-background-color: #ff9999; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";
        String estiloOk = "-fx-background-color: #99ff99; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";
        String estiloActivo = "-fx-background-color: white; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";
        String estiloBloqueado = "-fx-background-color: #eeeeee; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 20;";

        // Validamos cada campo por separado
        c1 = validarUnicoCampo(txtOpacidad, limiteOpacidad, "opacidad", "resOpacidad",
                estiloFallo, estiloOk, estiloActivo, estiloBloqueado,
                "04.01", "Opacidad excesiva (Diesel)");

        c2 = validarUnicoCampo(txtRalenti, limiteRalenti, "coRalenti", "resCoRalenti",
                estiloFallo, estiloOk, estiloActivo, estiloBloqueado,
                "04.02", "Emisiones CO ralentí superiores a lo permitido");

        c3 = validarUnicoCampo(txtRalentiAcelerado, limiteAcelerado, "coRalentiAcelerado", "resRalentiAcelerado",
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
        }

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
        }

        // Comprobamos el valor con su limite establecido por el tipo de etiqueta
        try {
            double emisiones = Double.parseDouble(texto);
            BufferInspeccion.getValoresEmisiones().put(valor, texto);

            if (emisiones > limite) {
                campo.setStyle(fallo); // Fondo rojo, borde dorado
                BufferInspeccion.getValoresEmisiones().put(resultado, "X");
                // Agregampos un defecto automatico
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
        }
        return correcto;
    }

    /*
     * Funcion para validar que campos debe rellenar obligatoriamente segun la
     * etiqueta
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
    private void accionSiguiente(ActionEvent event) {

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
        campo.setStyle(
                "-fx-background-color: #eeeeee; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    /*
     * Funcion para habilitar el campo
     */

    private void habilitarCampo(TextField campo) {
        campo.clear();
        campo.setDisable(false);
        // Restauramos el borde dorado original del diseño
        campo.setStyle(
                "-fx-background-color: white; -fx-border-color: #bf820d; -fx-border-radius: 5; -fx-background-radius: 5;");
    }
}