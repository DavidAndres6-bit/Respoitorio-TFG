import os                                       # Trabaja con rutas y carpetas del sistema
import zipfile                                  # Trabaja con archivos comprimidos
from descarga import obtener_ultima_fecha       # Importamos la función obtener_ultima_fecha() de descarga.py y usarla para construir la ruta del ZIP automáticamente

# Carpeta donde están los ZIP descargados y donde se extraerán los TXT
RUTA_TEMP = "/home/DJF/temp/zips"

# Lista de provincias de Castilla y León que nos interesan
# Nota: Ávila puede aparecer con tilde o sin tilde en distintos ZIPs, o no aparecer
PROVINCIAS_CYL = [
    "05.ÁVILA", "05.AVILA",
    "09.BURGOS",
    "24.LEÓN", "24.LEON",
    "34.PALENCIA",
    "37.SALAMANCA",
    "40.SEGOVIA",
    "42.SORIA",
    "47.VALLADOLID",
    "49.ZAMORA"
]

# Convertimos a mayúsculas para la comparación
PROVINCIAS_CYL_UPPER = [p.upper() for p in PROVINCIAS_CYL]


# Función para descomprimir el ZIP, recorrerlo y quedarnos sólo con los TXT de CyL
# Evitamos sobrecargar la memoria de PythonAnywhere
def descomprimir_zip(ruta_zip):
    print(f"- Abriendo ZIP: {ruta_zip}")
    extraidos = set()  # Evita extraer un mismo archivo varias veces

    with zipfile.ZipFile(ruta_zip, 'r') as zip_ref:
        for zip_info in zip_ref.infolist():
            nombre_upper = zip_info.filename.upper()

            for provincia in PROVINCIAS_CYL_UPPER:
                if provincia in nombre_upper and nombre_upper not in extraidos:
                    print(f"- Extrayendo: {zip_info.filename}")
                    zip_ref.extract(zip_info, RUTA_TEMP)
                    extraidos.add(nombre_upper)

    print(f"- Extracción completada. Archivos en: {RUTA_TEMP}")

    # Eliminamos el ZIP original para liberar espacio
    if os.path.exists(ruta_zip):
        os.remove(ruta_zip)
        print(f"- ZIP eliminado: {ruta_zip}")
    else:
        print(f"- No se encontró el ZIP para eliminar: {ruta_zip}")


# Función principal que centraliza todo el flujo de procesado
def main():
    fecha = obtener_ultima_fecha()                  # Detecta la última fecha publicada en la web
    nombre_zip = f"parque_vehiculos_{fecha}_PROVINCIA.zip"
    ruta_zip = os.path.join(RUTA_TEMP, nombre_zip)
    descomprimir_zip(ruta_zip)                      # Descomprime solo los TXT de CyL y elimina el ZIP


# Bloque principal para ejecutar solo si se ejecuta directamente
if __name__ == "__main__":
    main()