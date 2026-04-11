package clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UsuarioDAO {

    //Instancia de la clase conexion
    Conexion c = new Conexion();


    /*
     * Metodo para comprobar los datos del Login
    */
      public boolean validarInicio(String nombre, String contrasenia){
        boolean encontrado = false;
       
        //Conexion a la base de datos
        Connection con = null;

        //Consulta
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasenia = ?";

        //Recorrer la consulta y buscar
        try {
           con = c.conexion();
           if(con != null){
                
                PreparedStatement select = con.prepareStatement(sql);

                //Settear los valores a la consulta
                select.setString(1, nombre);
                select.setString(2, contrasenia);

                //Ejecutar la consulta
                ResultSet rs = select.executeQuery();


                //Comprobar si ha encontrado el usuario
                if(rs.next()){
                    encontrado = true;
                }
           } 

        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
            // TODO: handle exception
        }

        return encontrado;
    }


    /*   METODO DE DEBUG PARA PROBAR SI FUNCIONA  */
   public static void main(String[] args) {
    UsuarioDAO dao = new UsuarioDAO();
    // Prueba con un usuario que sepas que existe en tu script de Python
    boolean resultado = dao.validarInicio("david", "1234");
    
    if(resultado) {
        System.out.println("✅ LOGIN FUNCIONA: Usuario encontrado en la nube.");
    } else {
        System.out.println("❌ LOGIN FALLA: Usuario no encontrado o datos incorrectos.");
    }
} 
}
