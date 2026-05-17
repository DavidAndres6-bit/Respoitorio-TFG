package clases.DAOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import clases.CONNECTION.Conexion;
import clases.POJOS.Cliente;


public class ClienteDAO {

    /*
    *   Metodo que busca el utimo cliente asociado a una matricula especifica
    */

    public static Cliente buscarCliente(String matriculaCoche){
        // Variable que guardara el cliente encontrado
        Cliente cliente = null;

        // Conexion a la base de datos
        Connection con = null;

        // Instancia de la clase conexion
        Conexion c = new Conexion();

        // Consulta
        String sql = "SELECT c.* FROM clientes c " +
                    "JOIN vehiculo_cliente vc ON c.id = vc.id_cliente " +
                    "WHERE vc.matricula = ? " +
                    "ORDER BY vc.id DESC LIMIT 1";

        // ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;

        // Recorremos la consulta y buscamos el cliente
        try {
            con = c.conexion();
            if (con != null) {
                select = con.prepareStatement(sql);
                select.setString(1, matriculaCoche);
                rs = select.executeQuery();

                // Si lo encuentra devolvemos ese cliente
                if(rs.next()){
                    cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setNombre(rs.getString("nombre"));
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
        return cliente;
    }
    
    /*
    *   Metodo para añadir un cliente
    */

    public static int aniadirCliente(Cliente cliente) {
        // Id generado para el insert en la segunda tabla
        int id = -1;

        // Conexion a la base de datos
        Connection con = null;

        // PreparedStatement para el insert
        PreparedStatement ps = null;
    
        // Instancia de la clase conexion
        Conexion c = new Conexion();
        
        // Sentencia del insert 
        String sql = "INSERT INTO clientes (dni, nombre) VALUES (?,?)";
        
        // Realizar el insert con los valores de los atributos
         try {
           con = c.conexion();
            if(con != null){
                // Usamos esto para poder quedarnos con la pk para el segundo insert
                ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                // Pasar los valores
                ps.setString(1, cliente.getDni());
                ps.setString(2  , cliente.getNombre());

                // Ejecutar el insert
                int filasActualizadas = ps.executeUpdate();
                
                // Comprobar si se ha actualizado
                if(filasActualizadas > 0){
                   ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar el insert: " + e.getMessage());
        } finally{
            // Cerrar los recursos abiertos
           try {
                // Cerrar el preparedStatement
                if (ps != null){
                    ps.close();
                }
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                } 
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return id;
    }

    /*
    *   Metodo para asociar el cliente a un vehiculo
    */

    public static void vincularClienteConVehiculo(String matricula, int idCliente) {

        // Conexion a la base de datos
        Connection con = null;

        // PreparedStatement para el insert
        PreparedStatement psComprobar = null;
        PreparedStatement psInsert = null;

        // ResultSet para la consulta
        ResultSet rs = null;

        // Instancia de la clase conexion
        Conexion c = new Conexion();
        
        // Sentencia para buscar si ya existe ese cliente asociado al vehiculo
        String sqlComprobacion = "SELECT id FROM vehiculo_cliente WHERE matricula = ? AND id_cliente = ?"; 
        String sqlInsert = "INSERT INTO vehiculo_cliente (matricula, id_cliente) VALUES (?, ?)";

        try {
            con = c.conexion();
            if (con != null) {
                // Buscamos si ya existe un cliente asociado a ese vehiculo con los datos insertados
                psComprobar = con.prepareStatement(sqlComprobacion);
                psComprobar.setString(1, matricula);
                psComprobar.setInt(2, idCliente);
                rs = psComprobar.executeQuery();

                // Si no existe insertamos
                if (!rs.next()) {
                    psInsert = con.prepareStatement(sqlInsert);
                    psInsert.setString(1, matricula);
                    psInsert.setInt(2, idCliente);
                    psInsert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al vincular vehículo y cliente: " + e.getMessage());
        } finally {
         // Cerrar los recursos abiertos
           try {  
                // Cerrar el ResultSet
                if (rs != null) {
                    rs.close();
                }
                
                // Cerrar los preparedStatement
                if (psComprobar != null){
                    psComprobar.close();
                }

                if(psInsert != null){
                    psInsert.close();
                }
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }    
    }

    /*
    * Metodo para buscar un cliente por su DNI
    */

    public Cliente obtenerClientePorDni(String dni) {

        // Conexion a la base de datos
        Connection con = null;

        // PreparedStatement para la consulta
        PreparedStatement ps = null;

        // ResultSet para recoger los datos
        ResultSet rs = null;

        // Objeto cliente que devolveremos
        Cliente cliente = null;

        // Instancia de la clase conexion
        Conexion c = new Conexion();

        // Sentencia SQL para buscar el cliente
        String sql = "SELECT id, dni, nombre FROM clientes WHERE dni = ?";

        try {
            con = c.conexion();
            if (con != null) {
                // Preparamos la consulta
                ps = con.prepareStatement(sql);
                ps.setString(1, dni);
                rs = ps.executeQuery();

                // Si existe el cliente recogemos sus datos
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setNombre(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el cliente por DNI: " + e.getMessage());
        } finally {
            // Cerrar los recursos abiertos
            try {
                // Cerrar el ResultSet
                if (rs != null) {
                    rs.close();
                }

                // Cerrar el preparedStatement
                if (ps != null) {
                    ps.close();
                }

                // Cerrar la conexion
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return cliente;
    }
}