import os                           # Trabaja con rutas y archivos del sistema operativo
import re                           # Permite buscar patrones dentro de texto (por ejemplo YYYYMM)
import requests                     # Permite descargar páginas web y archivos desde internet
import zipfile                      # Permite descomprimir archivos zip
from bs4 import BeautifulSoup       # Librería que permite interpretar y procesar HTML

# Carpeta base en el escritorio donde guardaremos los ZIP
# Resultado: C:/Users/herre/Desktop/DGT/zips
# Cambia 'herre' por tu usuario y así funciona sin tocar nada más
RUTA_BASE = os.path.join("C:\\Users", "herre", "Desktop", "DGT", "zips")

# Página oficial de la DGT
URL_DGT = "https://www.dgt.es/menusecundario/dgt-en-cifras/matraba-listados/parque-vehiculos-mensual.html"

# Patrón fijo del ZIP final que queremos descargar
PATRON_ZIP = "https://www.dgt.es/microdatos/Parque/parque_vehiculos_{fecha}_PROVINCIA.zip"


# Crear carpetas necesarias en el escritorio
def asegurar_carpetas():
    if not os.path.exists(RUTA_BASE):
        os.makedirs(RUTA_BASE)
        print(f"[INFO] Carpeta creada: {RUTA_BASE}")
    else:
        print(f"[INFO] Carpeta ya existe: {RUTA_BASE}")


# Obtenemos la última fecha (patrón YYYYMM) de la página de la DGT
def obtener_ultima_fecha():
    print("[INFO] Descargando página de la DGT para obtener la última fecha...")
    respuesta = requests.get(URL_DGT)
    respuesta.raise_for_status()
    
    sopa = BeautifulSoup(respuesta.text, "html.parser")
    texto = sopa.get_text()
    
    fechas = re.findall(r"20\d{4}", texto)
    if not fechas:
        raise Exception("[ERROR] No se encontró ninguna fecha YYYYMM en la página.")
    
    fecha_mas_reciente = max(fechas)
    print(f"[INFO] Última fecha detectada: {fecha_mas_reciente}")
    return fecha_mas_reciente


# Descargamos el ZIP usando la fecha detectada
def descargar_zip(fecha):
    url_zip = PATRON_ZIP.format(fecha=fecha)
    nombre_archivo = f"parque_vehiculos_{fecha}_PROVINCIA.zip"
    ruta_archivo = os.path.join(RUTA_BASE, nombre_archivo)

    print(f"[INFO] Descargando ZIP desde: {url_zip}")
    respuesta = requests.get(url_zip)
    respuesta.raise_for_status()

    with open(ruta_archivo, "wb") as archivo:
        archivo.write(respuesta.content)

    print(f"[INFO] ZIP guardado en: {ruta_archivo}")
    return ruta_archivo


def descomprimir_zip(ruta_zip):
    print(f"[INFO] Descomprimiendo ZIP: {ruta_zip}")
    with zipfile.ZipFile(ruta_zip, 'r') as zip_ref:
        for zip_info in zip_ref.infolist():
            # Nombre original
            nombre_original = zip_info.filename
            # Reemplazamos caracteres inválidos en Windows
            nombre_seguro = re.sub(r'[<>:"/\\|?*\x00-\x1F]', '_', nombre_original)
            # Extraemos
            zip_info.filename = nombre_seguro
            zip_ref.extract(zip_info, RUTA_BASE)
            print(f"[INFO] Extraído: {nombre_seguro}")
    print(f"[INFO] Descompresión completada en: {RUTA_BASE}")


# Función principal
def main():
    print("[INFO] Iniciando script de descarga y descompresión de la DGT...")
    asegurar_carpetas()
    fecha = obtener_ultima_fecha()
    ruta_zip = descargar_zip(fecha)
    descomprimir_zip(ruta_zip)
    print("[INFO] Script completado correctamente.")


# Punto de entrada del script
if __name__ == "__main__":
    main()