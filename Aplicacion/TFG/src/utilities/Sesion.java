package utilities;

import clases.POJOS.Usuario;

public class Sesion {

    // Variable para guardar al usuario hasta que se cierre el programa para si se tienen que acceder a sus datos
    private static Usuario usuarioLogueado;

    /**
     * Guarda el usuario al hacer login
    */
    
    public static void setUsuario(Usuario usuario) {
        usuarioLogueado = usuario;
    }

    /**
     * Devuelve el objeto Usuario completo
    */
    
    public static Usuario getUsuario() {
        return usuarioLogueado;
    }

    /**
     * Limpia la sesión
    */
    
    public static void cerrarSesion() {
        usuarioLogueado = null;
    }

    /**
     * Verifica si hay alguien logueado (Seguridad)
    */

    public static boolean estaLogueado() {
        return usuarioLogueado != null;
    }
}