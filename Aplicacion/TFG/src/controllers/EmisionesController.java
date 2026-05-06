package controllers;

import clases.POJOS.Vehiculo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utilities.BufferInspeccion;
import utilities.Utilities;

public class EmisionesController {

    
    /*
    *   Recoger los valores del formulario
    */

    @FXML 
    private Label lblMatricula, lblModelo;
    
    @FXML 
    private TextField txtOpacidad, txtRalenti, txtRalentiAcelerado;

    @FXML 
    private TextArea txtObservaciones;
    
    @FXML 
    private Button btnSiguiente;

    //Variables para almacenar los limites de emisiones en función del distintivo ambiental
    private double limiteOpacidad;
    private double limiteRalenti;
    private double limiteAcelerado;



    /*
    *   Metodo para inicializar los valores de matricula modelo
    */

    @FXML
    public void initialize() {

        // Recuperamos el vehiculo para coger el distintivo ambiental
        Vehiculo v = BufferInspeccion.getVehiculoActual();

        // Cargar datos del buffer
        if (BufferInspeccion.getVehiculoActual() != null) {
            lblMatricula.setText(BufferInspeccion.getVehiculoActual().getMatricula());
            lblModelo.setText(BufferInspeccion.getVehiculoActual().getModeloCompleto());

            //Llamamos al metodo que calcular los limites de emisiones
            calcularLimites(v);

            // Llamamos al metodo que controla que pruebas debe pasar
            manejarPruebas(v);
        }

        // Igual que hicimos en la ventana de identificación usamos estos escuchadores para habilitar o deshabilitar
        // para habilitar o deshabilitar el textarea
        txtOpacidad.textProperty().addListener((obs, oldV, newV) -> gestionarValores());
        txtRalenti.textProperty().addListener((obs, oldV, newV) -> gestionarValores());
        txtRalentiAcelerado.textProperty().addListener((obs, oldV, newV) -> gestionarValores());
    }


    /*
    *   Funcion que calcula los limites de contaminación siguiendo su distintivo y antiguedad
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
    *   Funcion que maneja para cada tipo de vehiculo cuales son las pruebas que se le deben realizar
    */

    private void manejarPruebas(Vehiculo v){
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
    *   Funcion que comprueba si los valores indicados son mayores a los permitidos
    */

    private boolean nivelesCorrectos(){

        //Variable para guardar si son correctos
        boolean correctos = true;

        //Recoger los valores introducidos
        String opacidadStr = txtOpacidad.getText().replace(",", ".");
        String ralentiStr = txtRalenti.getText().replace(",", ".");
        String aceleradoStr = txtRalentiAcelerado.getText().replace(",", ".");


        // COMPROBACIÓN DE OPACIDAD
        if(!opacidadStr.isEmpty() && !opacidadStr.equalsIgnoreCase("N/A")){
            double opacidad = Double.parseDouble(opacidadStr);
            BufferInspeccion.getValoresEmisiones().put("opacidad", opacidadStr);

            if(opacidad > limiteOpacidad){
                correctos = false;
                BufferInspeccion.getValoresEmisiones().put("resOpacidad", "X");
                txtOpacidad.setStyle("-fx-background-color: #ff9999;");
            } else {
                BufferInspeccion.getValoresEmisiones().put("resOpacidad", "S");
                txtOpacidad.setStyle("-fx-background-color: #99ff99;");
            }

        } else {
            BufferInspeccion.getValoresEmisiones().put("opacidad", "N/A");
            BufferInspeccion.getValoresEmisiones().put("resOpacidad", "N/A");
        }

        // COMPROBACIÓN DE CO RALENTÍ 
        if(!ralentiStr.isEmpty() && !ralentiStr.equalsIgnoreCase("N/A")){
            double coRalenti = Double.parseDouble(ralentiStr);
            BufferInspeccion.getValoresEmisiones().put("coRalenti", ralentiStr);

            if(coRalenti > limiteRalenti){
                correctos = false;
                BufferInspeccion.getValoresEmisiones().put("resCoRalenti", "X");
                txtRalenti.setStyle("-fx-background-color: #ff9999;");
            } else {
                BufferInspeccion.getValoresEmisiones().put("resCoRalenti", "S");
                txtRalenti.setStyle("-fx-background-color: #99ff99;");
            }

        } else {
            BufferInspeccion.getValoresEmisiones().put("coRalenti", "N/A");
            BufferInspeccion.getValoresEmisiones().put("resCoRalenti", "N/A");
        }

        // COMPROBACIÓN DE CO ACELERADO

        if(!aceleradoStr.isEmpty() && !aceleradoStr.equalsIgnoreCase("N/A")){
            double coRalentiAcelerado = Double.parseDouble(aceleradoStr);
            BufferInspeccion.getValoresEmisiones().put("coRalentiAcelerado", aceleradoStr);

            if(coRalentiAcelerado > limiteAcelerado){
                correctos = false;
                BufferInspeccion.getValoresEmisiones().put("resRalentiAcelerado", "X");
                txtRalentiAcelerado.setStyle("-fx-background-color: #ff9999;");
            } else {
                BufferInspeccion.getValoresEmisiones().put("resRalentiAcelerado", "S");
                txtRalentiAcelerado.setStyle("-fx-background-color: #99ff99;");
            }
        
        } else {
            BufferInspeccion.getValoresEmisiones().put("coRalentiAcelerado", "N/A");
            BufferInspeccion.getValoresEmisiones().put("resRalentiAcelerado", "N/A");
        }

        return correctos;
    }

    /*
    *   Funcion para gestionar lo niveles introducidos por el tecnico
    */

    private void gestionarValores(){

        //Variable para almacenar si hay algun valor superior al permitido
        boolean superado = nivelesCorrectos();

       // Si se ha superado algun valor habilitamos el campo observaciones
        if(!superado){
            txtObservaciones.setDisable(false);
        } else {
            txtObservaciones.setDisable(true);
            txtObservaciones.clear();
        }
    }

    /*
    *   Funcion para validar que campos debe rellenar obligatoriamente segun la etiqueta
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

    /*
    *   Funcion para cambiar a la siguiente ventana
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

        // Validamos si ha rellenado los campos que debe rellenar
        boolean puedeAvanzar = validarCamposSegunEtiqueta();

        // Si los ha rellenado todos
        if(puedeAvanzar){
            //Si se han introducido valores superiores a los maximos permitidos marcamos el resultado del apartado como false
            BufferInspeccion.getInspeccionActual().setEmisiones(nivelesCorrectos());

            //Guardamos las observaciones
            String texto = txtObservaciones.getText().trim();
        
            if (!texto.isEmpty()) {
                BufferInspeccion.guardarObservacion(3,"[EMISIONES]: " +texto);
            } else {
                BufferInspeccion.guardarObservacion(3,"");
            }

            //Cambiamos de ventana
            Utilities.abrirVentana("/vistas/Frenos.fxml", "Frenos");

            Utilities.cerrarVentana(event);
        } else {
            Utilities.mostrarAlerta("Campos incompletos",   "Debe introducir los valores de las pruebas habilitadas para este vehículo.", Alert.AlertType.WARNING);
        }
        
    }

    /*
    *   Funcion para deshabilitar el campo
    */

    private void deshabilitarCampo(TextField campo){
        campo.setText("N/A");
        campo.setDisable(true);
        campo.setStyle("-fx-background-color: #eeeeee;");
    }

    /*
    *   Funcion para habilitar el campo
    */

    private void habilitarCampo(TextField campo){
        campo.clear();
        campo.setDisable(false);
        campo.setStyle(" ");
    }
}