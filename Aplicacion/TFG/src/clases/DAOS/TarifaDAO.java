package clases.DAOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import clases.CONNECTION.Conexion;

public class TarifaDAO {

    /*
    *   Metodo que recibe el distintivo medioambiental y devuelve la tarifa correspondiente
    */

    public double obtenerTarifaPorDistintivo(String distintivo){
        // Fijamos un precio base por si no encuentra el dato del distintivo
        double precio = 53.00;

        // Conexion a la base de datos
        Connection con = null;

        // Consulta
        String sql = "SELECT precio FROM tarifas WHERE tipo_vehiculo = ?";

        // ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;

        // Instancia de la clase conexion
        Conexion c = new Conexion();

        // Realizar la consulta
        try {

            con = c.conexion();
            select = con.prepareStatement(sql);

            // Pasamos el distintivo como parametro
            select.setString(1, distintivo);
            rs = select.executeQuery();

            // Recorrer el resultado
            if(rs.next()){
                precio = rs.getDouble("precio");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return precio;
    }
}