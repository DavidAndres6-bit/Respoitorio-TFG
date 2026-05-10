package controllers;

import clases.DAOS.UsuarioDAO;
import clases.POJOS.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utilities.Utilities;

public class PanelAdminController {

    /*
    *   Inicializar los elementos de la ventana
    */

    @FXML
    private TableView<Usuario> tablaUsuarios;
    

    /*
    *   Definimos las columnas de la tabla que corresponden con el id que les hemos asignado en Scene Builder
    */

    @FXML 
    private TableColumn<Usuario, String> colUsuario;
    
    @FXML 
    private TableColumn<Usuario, String> colCorreo;
    
    @FXML 
    private TableColumn<Usuario, Integer> colTelefono;
    
    @FXML 
    private TableColumn<Usuario, String> colRol;
    
    /*
    *   Label para el nombre del tecnico
    */
    
    @FXML 
    private Label lblNombreAdmin;


    /*
    *   Inicializar la tabla y rellenarla
    */

    @FXML
    public void initialize(){

        //Asociamos cada columna de la tabla con su atributo del POJO de Usuario correspondiente
        //setCellValueFactory -> le indica a la columna de donde va a sacar la informacion para mostrarla
        //new PropertyValueFactory -> accede al metodo get del POJO de Usuario y recupera el valor del atributo
        
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        //Lamamos al metodo que rellena la tabla
        rellenarTabla();

        // Mostramos el nombre del administrador
        // Comprobamos si hay un usuario en esta inspeccion
        if (utilities.Sesion.getUsuario() != null) {
            String nombre = utilities.Sesion.getUsuario().getNombre();
            lblNombreAdmin.setText(nombre);
        }

    }

    /*
    *   Rellenar la tabla con los usuarios de la aplicación
    */

    @FXML
    public void rellenarTabla(){

        //Instancia de usuario DAO para llamar al metodo que recupera los usuarios
        UsuarioDAO usuario = new UsuarioDAO();

        //Convertimos la lista devuelta por el DAO a ObservableList para que JavaFX pueda mostrarlo en la tabla
        ObservableList<Usuario> lista = FXCollections.observableArrayList(usuario.obtenerUsuarios());

        tablaUsuarios.setItems(lista);

    }

    /*
    *   Funcion para abrir la ventana de añadir usuario
    */

    @FXML
    private void abrirAniadirUsuario(ActionEvent event) {
        Utilities.abrirVentanaWait("/vistas/AniadirUsuario.fxml", "Añadir Nuevo Usuario");

        //Recargar la Tabla para que se reflejen los cambios al volver
        rellenarTabla();
    }


    /*
    *   Funcion para abrir la ventana de actualizar usuario
    */

    @FXML
    private void abrirActualizarUsuario(ActionEvent event) {
        Utilities.abrirVentanaWait("/vistas/ActualizarUsuario.fxml", "Modificar Usuario");   
        rellenarTabla();  
    }

    /*
    *   Funcion para abrir la ventana de eliminar usuario
    */
    
    @FXML
    private void abrirEliminarUsuario(ActionEvent event) {
       Utilities.abrirVentanaWait("/vistas/EliminarUsuario.fxml", "Dar de baja Usuario");
       rellenarTabla();
    }

    /*
    *   Función para cerrar la sesion
    */
   
    @FXML
    private void cerrarSesion(ActionEvent event){

        // Limpiamos el usuario de la sesion
        utilities.Sesion.setUsuario(null);

        // Volvemos al Login
        Utilities.abrirVentana("/vistas/Login.fxml", "Cyl-ITV Digital - Acceso");

        // Cerramos la ventana
        Utilities.cerrarVentana(event);
    }

}
