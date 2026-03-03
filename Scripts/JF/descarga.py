import os                           # Trabaja con rutas y archivos del sistema operativo
import re                           # Permite buscar patrones dentro de texto (por ejemplo YYYYMM)
import requests                     # Permite descargar páginas web y archivos desde internet
from bs4 import BeautifulSoup       # Librería que permite interpretar y procesar HTML

# ------------------------------------------------------------
# CONFIGURACIÓN DE RUTAS (ADAPTADO A WINDOWS Y TU USUARIO)
# ------------------------------------------------------------

# Carpeta base en el escritorio donde guardaremos los ZIP
# Resultado: C:/Users/herre/Desktop/DGT/zips
RUTA_BASE = os.path.join("C:\\Users", "herre", "Desktop", "DGT", "zips")

# Página oficial de la DGT
URL_DGT = "https://www.dgt.es/menusecundario/dgt-en-cifras/matraba-listados/parque-vehiculos-mensual.html"

# Patrón fijo del ZIP final que queremos descargar
PATRON_ZIP = "https://www.dgt.es/microdatos/Parque/parque_vehiculos_{fecha}_PROVINCIA.zip"


# ------------------------------------------------------------
# Crear carpetas necesarias en el escritorio
# ------------------------------------------------------------
def asegurar_carpetas():
    if not os.path.exists(RUTA_BASE):
        os.makedirs(RUTA_BASE)      # Crea todas las carpetas necesarias


# ------------------------------------------------------------
# Obtenemos la última fecha (patrón YYYYMM) de la página de la DGT
# ------------------------------------------------------------
def obtener_ultima_fecha():
    # Descargamos el HTML de la página
    respuesta = requests.get(URL_DGT)
    respuesta.raise_for_status()            # Lanza una excepción automática si la descarga falla

    # Analizamos el HTML
    sopa = BeautifulSoup(respuesta.text, "html.parser")

    # Extraemos todo el texto visible
    texto = sopa.get_text()

    # Buscamos el patrón con una expresión regular (ej: 202602, 202503, etc.)
    fechas = re.findall(r"20\d{4}", texto)

    # Controlamos el error si no ha encontrado nada
    if not fechas:
        raise Exception("No se encontró ninguna fecha YYYYMM en la página.")

    # Ordenamos las fechas y nos quedamos con la más reciente
    # Como están en formato YYYYMM, la comparación lexicográfica funciona
    fecha_mas_reciente = max(fechas)

    return fecha_mas_reciente



# ------------------------------------------------------------
# Descargamos el ZIP usando la fecha detectada
# ------------------------------------------------------------
def descargar_zip(fecha):
    # Construimos la URL final del ZIP
    url_zip = PATRON_ZIP.format(fecha=fecha)

    # Nombre del archivo ZIP
    nombre_archivo = f"parque_vehiculos_{fecha}_PROVINCIA.zip"

    # Ruta completa donde lo guardaremos
    ruta_archivo = os.path.join(RUTA_BASE, nombre_archivo)

    print(f"Descargando: {url_zip}")

    # Descargamos el archivo
    respuesta = requests.get(url_zip)
    respuesta.raise_for_status()            # Si falla la descarga, lanza error

    # Guardamos el ZIP en disco
    with open(ruta_archivo, "wb") as archivo:
        archivo.write(respuesta.content)

    print(f"Guardado en: {ruta_archivo}")
    return ruta_archivo


# ------------------------------------------------------------
# Función principal
# ------------------------------------------------------------
def main():
    asegurar_carpetas()
    fecha = obtener_ultima_fecha()
    print(f"Última fecha detectada: {fecha}")
    descargar_zip(fecha)


# ------------------------------------------------------------
# Punto de entrada del script
# ------------------------------------------------------------
# Esta condición garantiza que main() solo se ejecuta cuando este archivo
# se ejecuta directamente. Si otro script lo importa, NO se ejecutará.
if __name__ == "__main__":
    main()
