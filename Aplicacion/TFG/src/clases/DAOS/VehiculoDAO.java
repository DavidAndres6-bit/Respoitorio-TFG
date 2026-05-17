package clases.DAOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import clases.CONNECTION.Conexion;
import clases.POJOS.Vehiculo;

public class VehiculoDAO {

    // Instancia de la clase conexion
    Conexion c = new Conexion();

    /* 
      * Método para buscar un coche por su Matricula 
    */

    public Vehiculo buscarVehiculo(String matricula){

        // Variable para almacenar el vehiculo encontrado
        Vehiculo v = null;
        
        // Conexion a la base de datos
        Connection con = null;

        // Consulta
        String sql = "SELECT * FROM vehiculos_cyl WHERE matricula = ?";

        // ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;

        // Recorrer la consulta y buscar
        try {
           con = c.conexion();
           if(con != null){ 
                select = con.prepareStatement(sql);

                // Settear la matricula a la consulta
                select.setString(1, matricula);
                
                // Ejecutar la consulta
                rs = select.executeQuery();

                //Comprobar si ha encontrado el usuario
                if(rs.next()){

                    // Crear objeto vehiculo con los atributos
                    Vehiculo vehiculo = new Vehiculo();

                    // Recoger los valores de la consulta y asignarlos a los atributos del vehiculo
                    vehiculo.setMatricula(rs.getString("MATRICULA"));
                    vehiculo.setMarca(rs.getString("MARCA"));
                    vehiculo.setModelo(rs.getString("MODELO"));
                    vehiculo.setTipoDgt(rs.getString("TIPO_DGT"));
                    vehiculo.setFechaMatriculacion(rs.getDate("FECHA_MATR").toLocalDate()); //La guardamos como localdate
                    vehiculo.setDistintivo(rs.getString("TIPO_DISTINTIVO"));
                    v = vehiculo;
                }
           } 
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        } finally{
           // Cerrar los recursos abiertos
           try {
    
                // Cerrar el ResultSet
                if (rs != null) {
                    rs.close();
                }
                
                // Cerrar el preparedStatement
                if (select != null){
                    select.close();
                }
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return v;
    }
}