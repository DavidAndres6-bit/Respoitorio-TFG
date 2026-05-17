import os                           # Trabaja con rutas y carpetas del sistema
import re                           # Permite buscar patrones en textos (expresiones regulares)
import requests                     # Permite descargar páginas web y archivos desde internet
from bs4 import BeautifulSoup       # Librería que permite interpretar y procesar HTML

# Carpeta donde se guardarán los archivos ZIP descargados
RUTA_TEMP = "/home/DJF/temp/zips"

# URL de la página oficial de la DGT donde se publican los listados mensuales
URL_DGT = "https://www.dgt.es/menusecundario/dgt-en-cifras/matraba-listados/parque-vehiculos-mensual.html"

# Patrón de la URL final del ZIP, donde se sustituye {fecha} por la fecha detectada
PATRON_ZIP = "https://www.dgt.es/microdatos/Parque/parque_vehiculos_{fecha}_PROVINCIA.zip"


# Función para asegurar que la carpeta temp existe
def asegurar_carpeta():
    if not os.path.exists(RUTA_TEMP):
        os.makedirs(RUTA_TEMP)
        print(f"[INFO] Carpeta creada: {RUTA_TEMP}")
    else:
        print(f"[INFO] Carpeta ya existe: {RUTA_TEMP}")


# Función para obtener el archivo más reciente
# Utilizamos la librería BeautifulSoup para poder leer el HTML después del request
def obtener_ultima_fecha():
    print("- Descargando página de la DGT para detectar última fecha...")
    respuesta = requests.get(URL_DGT)
    respuesta.raise_for_status()            # Lanza excepción si hay error en la descarga

    sopa = BeautifulSoup(respuesta.text, "html.parser")
    texto = sopa.get_text()                 # Extrae todo el texto visible de la página

    # Busca todas las fechas con formato YYYYMM usando expresión regular
    fechas = re.findall(r"20\d{4}", texto)

    if not fechas:
        raise Exception("- No se encontró ninguna fecha YYYYMM en la página.")

    fecha_mas_reciente = max(fechas)        # Toma la más reciente lexicográficamente
    print(f"- Fecha más reciente detectada: {fecha_mas_reciente}")
    return fecha_mas_reciente


# Función para descargar el ZIP
def descargar_zip(fecha):
    url_zip = PATRON_ZIP.format(fecha=fecha)
    nombre_archivo = f"parque_vehiculos_{fecha}_PROVINCIA.zip"
    ruta_archivo = os.path.join(RUTA_TEMP, nombre_archivo)

    print(f"- Descargando ZIP: {url_zip}")
    respuesta = requests.get(url_zip)
    respuesta.raise_for_status()  # Controla errores HTTP

    with open(ruta_archivo, "wb") as archivo:  # Guardamos en modo binario
        archivo.write(respuesta.content)

    print(f"- ZIP guardado en: {ruta_archivo}")
    return ruta_archivo


# Función principal que centraliza todo el flujo
def main():
    asegurar_carpeta()              # Nos aseguramos de que la carpeta exista
    fecha = obtener_ultima_fecha()  # Detectamos la última fecha disponible
    ruta_zip = descargar_zip(fecha) # Descargamos el ZIP correspondiente
    print(f"[INFO] Proceso de descarga completado. Archivo listo en: {ruta_zip}")


# Bloque principal para ejecutar solo si se ejecuta directamente
if __name__ == "__main__":
    main()