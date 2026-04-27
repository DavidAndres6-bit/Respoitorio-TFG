package controllers;
import clases.POJOS.Vehiculo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    *   Funcion que comprueba si los valores indicados son mayores a los permitidos
    */

    private boolean nivelesCorrectos(){

        //Variable para guardar si son correctos
        boolean correctos = true;

        //Recoger los valores introducidos
        String opacidadStr = txtOpacidad.getText().replace(",", ".");
        String ralentiStr = txtRalenti.getText().replace(",", ".");
        String aceleradoStr = txtRalentiAcelerado.getText().replace(",", ".");

        //Convertir los valores a double controlando posibles valores vacios 
        double opacidad = 0;
        double coRalenti = 0;
        double coRalentiAcelerado = 0;

        if(!opacidadStr.isEmpty()){
            opacidad = Double.parseDouble(opacidadStr);
        }

        if(!ralentiStr.isEmpty()){
            coRalenti = Double.parseDouble(ralentiStr);
        }

        if(!aceleradoStr.isEmpty()){
            coRalentiAcelerado = Double.parseDouble(aceleradoStr);
        }


        //Comprobamos si se han superado los limites en algun valor
        if(opacidad > limiteOpacidad || coRalenti >  limiteRalenti || coRalentiAcelerado > limiteAcelerado){
            correctos = false;
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
        }
    }


    /*
    *   Funcion para cambiar a la siguiente ventana
    */

    @FXML
    private void accionSiguiente(ActionEvent event) {

        //Si se han introducido valores superiores a los maximos permitidos marcamos el resultado del apartado como false
        BufferInspeccion.getInspeccionActual().setEmisiones(nivelesCorrectos());

        //Guardamos las observaciones
        String texto = txtObservaciones.getText().trim();
    
        if (!texto.isEmpty()) {
            BufferInspeccion.getInspeccionActual().setObservaciones("[EMISIONES]: " + texto);
        } else {
            BufferInspeccion.getInspeccionActual().setObservaciones("");
        }

        //Cambiamos de ventana
        Utilities.abrirVentana("/vistas/Frenos.fxml", "Frenos");

        Utilities.cerrarVentana(event);
    }
}