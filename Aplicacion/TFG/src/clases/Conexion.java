package clases;

import java.sql.Connection;
import java.sql.DriverManager;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Conexion {

    /* Por defecto PythonAnywere bloquea las conexiones externas por el puerto 3306
       Necesitamos abrir un tunel para la comunicacion
       Informacion sacada de la pagina de ayuda de PythonAnywere
       https://help.pythonanywhere.com/pages/AccessingMySQLFromOutsidePythonAnywhere/ */

    //Guardar la sesion para habrir un unico tunel
    private static Session session;

    //Configuracion del tunel para el puerto de la conexion
    private static final String SSH_HOST = "ssh.pythonanywhere.com";
    private static final String SSH_USER = "DJF";
    private static final String SSH_PASS = "Dam1234@";

    
    //Configuracion de la parte de Mysql de python
    private static final String HOST = "DJF.mysql.pythonanywhere-services.com";
    private static final String USER= "DJF";
    private static final String PASS= "DJFTFG1234";
    private static final String DB= "DJF$TFG";
    

    //Crear la conexion
    //En la funcion se crea ese tunel de comunicacion alternativo y una vez creado hacemos la conexion a mysql usando JDBC 
    public static Connection conexion() {
		Connection con = null;
        
        try {
            
            //Abrimos el tunel para poder conectarnos a pythonAnywere sin usar el puerto 3306

            //Primero comprobamos  que no se haya creado ya un puerto de comunicacion y este el canal ocupado
            if (session == null || !session.isConnected()) {
                JSch jsch = new JSch();

                //Configuramos la conexion ssh usando el puerto 22
                session = jsch.getSession(SSH_USER, SSH_HOST, 22);
                session.setPassword(SSH_PASS);
                
                //Configuracion para la capa de seguridad de la conexion
                java.util.Properties config = new java.util.Properties();

                //Nos saltamos la validacion de 'Known Hosts' para permitir la conexión desde distintas redes
                config.put("StrictHostKeyChecking", "no");

                session.setConfig(config);
                
                //Establecemos la conexion con el tunel
                session.connect();


                /* Una vez que se ha abierto el tunel de comunicacion con el puerto 3307
                   volvemos a usar el puerto 3306 para estableces la comunicacion con mysql
                   y que este nos pueda escuchar y dar acceso 
                */

                session.setPortForwardingL(3307, HOST, 3306);

                //Configuracion de la conexion con mysql a pythonAnywere usando JDBC
                String url = "jdbc:mysql://127.0.0.1:3307/" + DB + "?serverTimezone=UTC";
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(url, USER, PASS);
                System.out.println("Túnel SSH establecido.");
            }
        

        /* Tratamiento de errores extenso en el tunel  */

        } catch (com.jcraft.jsch.JSchException e) {
            System.err.println("ERROR EN EL TÚNEL SSH:");
            if (e.getMessage().contains("Auth fail")) {
                System.err.println("   -> Las credenciales de PythonAnywhere (SSH) son incorrectas.");
            } else if (e.getMessage().contains("java.net.UnknownHostException")) {
                System.err.println("   -> No se encuentra el servidor. Revisa tu conexión a internet.");
            } else {
                System.err.println("   -> Detalle: " + e.getMessage());
            }
            
        } catch (java.sql.SQLException e) {
            System.err.println("ERROR EN LA BASE DE DATOS (MySQL):");
            System.err.println("   -> Código de error: " + e.getErrorCode());
            if (e.getErrorCode() == 1045) {
                System.err.println("   -> Usuario o contraseña de la Base de Datos incorrectos.");
            } else {
                System.err.println("   -> Detalle: " + e.getMessage());
            }

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR DE DRIVER:");
            System.err.println("   -> No se encontró el conector de MySQL (Connector/J).");

        } catch (Exception e) {
            System.err.println("ERROR INESPERADO:");
            e.printStackTrace();
        }

        return con;
    }

    /* Metodo debug para probar la conexion */
    public static void main(String[] args) {
        conexion(); 
    }
}
    

