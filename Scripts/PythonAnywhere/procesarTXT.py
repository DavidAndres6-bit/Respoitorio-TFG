import os                                       # Trabaja con rutas y carpetas del sistema
import zipfile                                  # Trabaja con archivos comprimidos
import unicodedata                              # Normaliza caracteres para eliminar tildes
from descarga import obtener_ultima_fecha       # Detecta la última fecha publicada en la DGT

# Carpeta donde se guardan los ZIP descargados
RUTA_ZIPS = "/home/DJF/temp/zips"

# Carpeta donde se guardarán los TXT extraídos
RUTA_TXT = "/home/DJF/temp/txt"

# Todas las provincias de Castilla y León
# Las escribimos sin tildes porque normalizamos antes de comparar
PROVINCIAS_CYL = [
    "05.AVILA",
    "09.BURGOS",
    "24.LEON",
    "34.PALENCIA",
    "37.SALAMANCA",
    "40.SEGOVIA",
    "42.SORIA",
    "47.VALLADOLID",
    "49.ZAMORA",
]


def quitar_tildes(texto):
    """
    Eliminamos tildes para no depender de cómo lo ponga la DGT ese mes
    """
    return ''.join(
        c for c in unicodedata.normalize('NFD', texto)
        if unicodedata.category(c) != 'Mn'
    )


def asegurar_carpeta_txt():
    """Nos aseguramos de que la carpeta de TXT existe antes de extraer."""
    if not os.path.exists(RUTA_TXT):
        os.makedirs(RUTA_TXT)
        print(f"[INFO] Carpeta creada: {RUTA_TXT}")
    else:
        print(f"[INFO] Carpeta ya existe: {RUTA_TXT}")


def descomprimir_zip(ruta_zip):
    """
    Abrimos el ZIP, recorremos su contenido y extraemos solo los TXT
    de las provincias de CyL. Normalizamos los nombres sin tildes antes
    de comparar para cubrir cualquier variante que use la DGT.
    Al terminar eliminamos el ZIP para liberar espacio.
    """
    print(f"- Abriendo ZIP: {ruta_zip}")
    extraidos = set()  # Evita extraer un mismo archivo varias veces

    with zipfile.ZipFile(ruta_zip, 'r') as zip_ref:
        for zip_info in zip_ref.infolist():
            # Normalizamos el nombre del fichero dentro del ZIP: mayúsculas y sin tildes
            nombre_normalizado = quitar_tildes(zip_info.filename.upper())

            for provincia in PROVINCIAS_CYL:
                if provincia in nombre_normalizado and nombre_normalizado not in extraidos:
                    print(f"- Extrayendo: {zip_info.filename}")
                    zip_ref.extract(zip_info, RUTA_TXT)
                    extraidos.add(nombre_normalizado)

    print(f"- Extracción completada. Archivos en: {RUTA_TXT}")

    # Eliminamos el ZIP para liberar espacio
    if os.path.exists(ruta_zip):
        os.remove(ruta_zip)
        print(f"- ZIP eliminado: {ruta_zip}")
    else:
        print(f"- No se encontró el ZIP para eliminar: {ruta_zip}")


def main():
    """Lanzamos el flujo completo: carpeta → fecha → ZIP → extracción."""
    asegurar_carpeta_txt()
    fecha = obtener_ultima_fecha()
    nombre_zip = f"parque_vehiculos_{fecha}_PROVINCIA.zip"
    ruta_zip = os.path.join(RUTA_ZIPS, nombre_zip)
    descomprimir_zip(ruta_zip)


# Solo ejecutamos main() si llamamos directamente a este fichero
if __name__ == "__main__":
    main()