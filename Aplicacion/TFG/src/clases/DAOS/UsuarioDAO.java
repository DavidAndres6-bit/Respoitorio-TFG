package clases.DAOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clases.CONNECTION.Conexion;
import clases.POJOS.Usuario;


public class UsuarioDAO {

    //Instancia de la clase conexion
    Conexion c = new Conexion();

    /*
    *  Metodo para recuperar los usuarios de la base de datos
    */
   
    public List<Usuario> obtenerUsuarios(){

        // Lista para guardar los usuarios
        List<Usuario> lista = new ArrayList<>();

        // Conexion a la base de datos
        Connection con = null;

        // Consulta
        String sql = "SELECT * FROM usuarios";

        // ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;
    
        // Recorremos la consulta y añadimos los usuarios a la lista
        try {
        con = c.conexion();
            if (con != null) {
                select = con.prepareStatement(sql);
                rs = select.executeQuery();

                // Mientras encuentre resultados añadimos los usuarios a la lista
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre_usuario"));
                    u.setContrasenia(rs.getString("contrasenia"));
                    u.setCorreo(rs.getString("correo_electronico"));
                    u.setTelefono(rs.getInt("telefono"));
                    u.setRol(rs.getString("rol"));
                    lista.add(u);
                }
            }
        }  catch (SQLException e) {
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
        return lista;
    }

    /*
     * Metodo para comprobar los datos del Login
    */

    public Usuario validarInicio(String nombre, String contrasenia){
        // Variable para guardar el usuario
        Usuario u = null;
       
        // Conexion a la base de datos
        Connection con = null;

        // Consulta
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasenia = ?";

        //ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;

        // Recorrer la consulta y buscar
        try {
           con = c.conexion();
           if(con != null){
                select = con.prepareStatement(sql);

                // Settear los valores a la consulta
                select.setString(1, nombre);
                select.setString(2, contrasenia);

                // Ejecutar la consulta
                rs = select.executeQuery();

                // Comprobar si ha encontrado el usuario
                if(rs.next()){
                    // Crear objeto usuario con estos valores
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre_usuario"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setContrasenia(rs.getString("contrasenia"));
                    u = usuario;
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
        return u;
    }

   /*
     * Metodo para insertar un nuevo usuario en la base de datos
    */

    public boolean insertar(Usuario u){
        // Variable para recoger el resultado del insert
        boolean insertado = false;

        // Conexion a la base de datos
        Connection con = null;

        // PreparedStatement para el delete
        PreparedStatement ps = null;
    
        // Sentencia del insert 
        String sql = "INSERT INTO usuarios (nombre_usuario,contrasenia,correo_electronico,telefono,rol) VALUES (?,?,?,?,?)";
    
        // Realizar el insert con los valores de los atributos
         try {
           con = c.conexion();
            if(con != null){
                ps = con.prepareStatement(sql);

                // Pasar los valores al insert
                ps.setString(1, u.getNombre());
                ps.setString(2, u.getContrasenia());
                ps.setString(3, u.getCorreo());
                ps.setInt(4, u.getTelefono());
                ps.setString(5, u.getRol());

                // Ejecutar el insert
                int filasActualizadas = ps.executeUpdate();
                
                // Comprobar si se ha actualizado
                if(filasActualizadas > 0){
                    insertado = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar el insert: " + e.getMessage());
        } finally{
            // Cerrar los recursos abiertos
           try {
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                }
                
                // Cerrar el preparedStatement
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return insertado;
    }

    /*
     * Metodo para eliminar un usuario de la base de datos
    */

    public boolean eliminarUsuario(int id){
        // Variable para almacenar el resultado de la operacion
        boolean eliminado = false;

        // Sentencia para hacer la eliminacion
        String sql = "DELETE FROM usuarios WHERE id = ?";

        // Conexion a la base de datos
        Connection con = null;

        // PreparedStatement para el delete
        PreparedStatement ps = null;
    
        // Realizar la operacion
         try {
            con = c.conexion();
           
            if(con != null){
                ps = con.prepareStatement(sql);
                ps.setInt(1, id);

                // Ejecutar
                int filasEliminadas = ps.executeUpdate();
                
                if(filasEliminadas > 0){
                    eliminado = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar el borrado: " + e.getMessage());
        } finally{
           // Cerrar los recursos abiertos
           try {
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                }
                
                // Cerrar el preparedStatement
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return eliminado;
    }

    /*
     * Metodo para actualizar el rol de un usuario en la base de datos
    */

    public boolean actualizarRol(int id, String nuevoRol, String nuevaContrasenia){
        boolean actualizado = false;

        // Comprobamos si hemos recibido rol y contraseña
        boolean rol = false;
        boolean contra = false;

        if(nuevoRol != null){
            rol = true;
        }

        if(nuevaContrasenia != null && !nuevaContrasenia.trim().isEmpty()){
            contra = true;
        }

        // Montar la sentencia en funcion de lo que tenemos
        String sql = "";

        if (rol && contra) {
            sql = "UPDATE usuarios SET rol = ?, contrasenia = ? WHERE id = ?";
        } else if (rol) {
            sql = "UPDATE usuarios SET rol = ? WHERE id = ?";
        } else if (contra) {
            sql = "UPDATE usuarios SET contrasenia = ? WHERE id = ?";
        }

        // Conexion a la base de datos
        Connection con = null;

        // ResultSet y PreparedStatement
        ResultSet rs = null;
        PreparedStatement update = null;

        try {
            con = c.conexion();
            if (con != null) {
                update = con.prepareStatement(sql);
                
                // Pasamos los parametros a los update
                if (rol && contra) {
                    update.setString(1, nuevoRol);
                    update.setString(2, nuevaContrasenia);
                    update.setInt(3, id);
                } else if (rol) {
                    update.setString(1, nuevoRol);
                    update.setInt(2, id);
                } else if (contra) {
                    update.setString(1, nuevaContrasenia);
                    update.setInt(2, id);
                }

                // Ejecutamos el update y comprobamos si se han actualizado filas
                int filasActualizadas = update.executeUpdate();

                if(filasActualizadas > 0){
                    actualizado = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        } finally {

            // Cerrar los recursos abiertos
           try {
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                }
                
                // Cerrar el preparedStatement
                if (update != null) {
                    update.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return actualizado;
    }

    /*
    *   Metodo que recibe el correo electronico y devuelve su id
    */
    
    public int obtenerIdUsuario(String correo) {
        
        // Variable para devolver si se ha encontrado    
        int id = -1;

        // Conexion a la base de datos
        Connection con = null;

        // Consulta
        String sql = "SELECT id FROM usuarios WHERE correo_electronico = ?";
        
        //ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;

        // Recorrer la consulta y buscar
        try {
           con = c.conexion();
           if(con != null){
                
                select = con.prepareStatement(sql);

                // Settear los valores a la consulta
                select.setString(1, correo);

                // Ejecutar la consulta
                rs = select.executeQuery();

                //Comprobar si ha encontrado el usuario
                if(rs.next()){
                    id = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar la busqueda: " + e.getMessage());
        } finally{
            // Cerrar los recursos abiertos
           try {
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                }
                
                // Cerrar el preparedStatement
                if (select != null) {
                    select.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return id;
    }

    /*
    *  Metodo para validar si un usuario ya existe con el valor indicado
    */

    public boolean existeDato(String columna, String valor) {
        boolean existe = false;

        // Conexion a la base de datos
        Connection con = null;

        // Consulta
        String sql = "SELECT COUNT(*) FROM usuarios WHERE " + columna + " = ?";
        
        //ResultSet y PreparedStatement para la consulta
        ResultSet rs = null;
        PreparedStatement select = null;

        // Recorrer la consulta y buscar
        try {
           con = c.conexion();
           if(con != null){
                
                select = con.prepareStatement(sql);

                // Settear los valores a la consulta
                select.setString(1, valor);

                // Ejecutar la consulta
                rs = select.executeQuery();

                //Comprobar si ha encontrado el usuario
                if(rs.next()){
                    if(rs.getInt(1) > 0){
                        existe =  true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar la busqueda: " + e.getMessage());
        } finally{
            // Cerrar los recursos abiertos
           try {
                
                // Cerrar la conexion
                if (con != null) {
                  con.close();
                }
                
                // Cerrar el preparedStatement
                if (select != null) {
                    select.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return existe;
    }
}