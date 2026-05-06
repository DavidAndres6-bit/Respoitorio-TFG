package clases.DAOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import clases.CONNECTION.Conexion;
import clases.POJOS.Defecto;
import clases.POJOS.Inspeccion;

public class InspeccionDAO {

    /*
    *   Metodo que guarda la inspeccion en la base de datos recibe la inspeccion y los defectos
    */

    public static boolean guardarInspeccion(Inspeccion inspeccion, List<Defecto> defectos){

        //Variable para guardar el resultado del insert
        boolean insertada = false;

        //Conexion a la base de datos
        Connection con = null;

        //PreparedStatement para el delete
        PreparedStatement ps = null;
    
        //Instancia de la clase conexion
        Conexion c = new Conexion();

        //Sentencia del insert 
        String sql = "INSERT INTO inspecciones (matricula_coche, km_anterior_inspeccion, km_actuales, "
            + "acondicionamiento_exterior, acondicionamiento_interior, alumbrado_senializacion, "
            + "emisiones, frenos, ejes_ruedas_neumaticos, motor_transmision, "
            + "resultado_inspeccion, fecha_proxima_inspeccion, observaciones, fecha_inspeccion) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //Realizar el insert con los valores de los atributos
        try{
           con = c.conexion();
           if(con != null){

                // Iniciamos transacción para toda la operación
                con.setAutoCommit(false);
                
                //Realizamos el insert en la tabla de inspecciones

                //Usamos PreparedStatement.RETURN_GENERATED_KEYS para obtener el id de la inspeccion y poder insertar los defectos
                ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, inspeccion.getMatriculaCoche());
                ps.setInt(2, inspeccion.getKmAnteriorInspeccion());
                ps.setInt(3, inspeccion.getKmActuales());
                ps.setBoolean(4, inspeccion.isAcondicionamientoExterior());
                ps.setBoolean(5, inspeccion.isAcondicionamientoInterior());
                ps.setBoolean(6, inspeccion.isAlumbradoSenializacion());
                ps.setBoolean(7, inspeccion.isEmisiones()); 
                ps.setBoolean(8, inspeccion.isFrenos());
                ps.setBoolean(9, inspeccion.isEjesRuedasNeumaticos());
                ps.setBoolean(10, inspeccion.isMotorTransmision());
                ps.setString(11, inspeccion.getResultadoInspeccion());
                ps.setDate(12, inspeccion.getFechaProximaInspeccion());
                ps.setString(13, inspeccion.getObservaciones());
               
                // Tratamos la fecha de la inspeccion (la del sistema)
                if (inspeccion.getFechaInspeccion() != null) {
                    ps.setDate(14, java.sql.Date.valueOf(inspeccion.getFechaInspeccion()));
                } else {
                    // Si por lo que sea llega null, le metemos la de hoy por seguridad
                    ps.setDate(14, java.sql.Date.valueOf(LocalDate.now()));
                }

                int filasActualizadas = ps.executeUpdate();
                
                //Comprobar si se ha actualizado
                if (filasActualizadas > 0) {

                    // Recuperamos el ID generado
                    ResultSet rs = ps.getGeneratedKeys();
                    int idGenerado = 0;
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                        inspeccion.setId(idGenerado);
                    }

                    // Insertar los defectos
                    if (idGenerado > 0 && defectos != null && !defectos.isEmpty()) {
                        String sqlDef = "INSERT INTO inspeccion_defectos (id_inspeccion, unidad, descripcion, calificacion) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement psDef = con.prepareStatement(sqlDef)) {
                            for (Defecto defecto : defectos) {
                                psDef.setInt(1, idGenerado);
                                psDef.setString(2, defecto.getUnidad());
                                psDef.setString(3, defecto.getDescripcion());
                                psDef.setString(4, defecto.getCalificacion());
                                psDef.addBatch();
                            }
                            psDef.executeBatch();
                        }
                    }
                    
                    con.commit(); // Hacer el commit finalmente
                    insertada = true;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error al insertar la inspeccion: " + e.getMessage());

            try {
                if (con != null) {
                    con.rollback(); // Deshacer todo si algo falla
                } 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally{
            //Cerrar los recursos abiertos
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.setAutoCommit(true); // Volvemos a activar el autocommit
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
        return insertada;
    }   
    
    /*
    *   Metodo para recuperar los kilometros que tenia el vehiculo en su anterior inspeccion
    */

    public static int obtenerKmUltimaInspeccion(String matricula){

        // Variable para guardar los kilometros
        int km = 0;

        //Conexion a la base de datos
        Connection con = null;

        //Instancia de la clase conexion
        Conexion c = new Conexion();

        //Consulta
        //String sql = "SELECT km_actuales FROM inspecciones WHERE matricula_coche = ? ORDER BY fecha_inspeccion DESC LIMIT 1";
        String sql = "SELECT km_actuales FROM inspecciones WHERE matricula_coche = ? ORDER BY id DESC LIMIT 1";
        //ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;

        //Recorremos la consulta y buscamos los kilometros
        try {
            con = c.conexion();

            if (con != null) {
                select = con.prepareStatement(sql);
                select.setString(1, matricula);
                rs = select.executeQuery();

                //Si los encontramos los guardamos en la variable
                if(rs.next()){
                   km = rs.getInt("km_actuales");
                }
            }
        
            
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        } finally{
            //Cerrar los recursos abiertos
           try {
                
            //Cerrar el ResultSet
            if (rs != null) {
                 rs.close();
            }
                
            //Cerrar el preparedStatement
            if (select != null){
                select.close();
            }
                
            //Cerrar la conexion
            if (con != null) {
                con.close();
            }
                
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }    
        return km;
    }
}