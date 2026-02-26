#Imports necesarios
import requests # Para descargar el archivo y enviarlo a Supabase
import zipfile  # Para abrir el ZIP de la DGT
import pandas as pd # Para filtrar los datos


# Configurar el acceso a SupaBase con nuestra URL Y Key de la base de datos

# URL de nuestra API de SupaBase añadiendole la ruta hasta nuestra tabla VehiculosCyL
URL_BASE = "https://vpwzjmlyyqizyizvyhhb.supabase.co"
URL_API = "/rest/v1/"
URL_TABLA = "parque_vehiculos"

URL_SUPABASE = URL_BASE + URL_API + URL_TABLA

# Key de la base de datos VehiculosCyL
KEY_TABLA = "sb_publishable_X70lS7zspegk2Nka3aDRWQ_tanq6N9w"

# Autentificacion para poder conectarse a la base de datos (como lo que hacemos en java mas o menos en JDBC de user, puerto,password...)
AUTENTIFICACION = {

    "apikey" : KEY_TABLA,    #Identificar el proyecto con su key

    "Authorization" :  f"Bearer {KEY_TABLA}",  #Acceso a la base de datos

    "Content-Type": "application/json",  #Aunque nosotros pasamos a CSV SupaBase lee en json

    "Prefer": "return=minimal"  #Es opcional lo he buscado, es para que sea mas rapida la subida de datos

}


#   ---- FUNCIONES PARA EL SCRIPT COMPLETO ----
def descargar_datos_dgt(mes_anio):

    return


def filtrar_datosCyl(archivo_zip):



    return

def transformar_JSON(archivo_limpio):


    return


def subir_a_supabase(datos_limpios):


    return


def main():

    #LLamar a las funciones

    # Descargar
    # zip_descargado = descargar_datos_dgt("202501")
    
    # Filtrar
    # datos_cyl = filtrar_datosCyl(zip_descargado)
    
    # Transformar
    # json_datos = transformar_JSON(datos_cyl)
    
    # Subir
    # subir_a_supabase(json_datos)

    return


#Llamar al Main
main()

