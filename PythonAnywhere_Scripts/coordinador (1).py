import os                           # Trabaja con rutas y carpetas del sistema
import sys                          # Permite salir del script con un código de error
import shutil                       # Permite eliminar y recrear carpetas completas
from datetime import datetime       # Para comprobar el día del mes actual
import descarga                     # Script de descarga del ZIP de la DGT
import procesarTXT                  # Script de extracción de TXT del ZIP
import procesarCSV                  # Script de procesado y generación de CSVs
import insertarBBDD                 # Script de inserción en base de datos

# Carpetas que vaciamos al inicio de cada ejecución para garantizar
# que siempre trabajamos con datos frescos del mes actual
CARPETA_TEMP = "/home/DJF/temp"


# Días del mes en los que se ejecuta el proceso completo
# Usamos dos fechas para asegurarnos de que la DGT ya ha publicado los datos
DIAS_EJECUCION = {5, 20}


def limpiar_carpetas():
    """
    Vaciamos la carpeta temp completa antes de empezar el proceso.
    Así garantizamos que no se procesan ficheros del mes anterior,
    incluyendo ZIPs viejos que pudieran haber quedado de ejecuciones anteriores.
    """
    if os.path.exists(CARPETA_TEMP):
        shutil.rmtree(CARPETA_TEMP)
        print(f"[INFO] Carpeta eliminada: {CARPETA_TEMP}")
    os.makedirs(CARPETA_TEMP)
    print(f"[INFO] Carpeta recreada: {CARPETA_TEMP}")


def ejecutar_paso(nombre, funcion):
    """
    Ejecutamos un paso del proceso. Si falla, informamos del error y
    detenemos el coordinador — los scripts son un todo y no tienen
    sentido unos sin los otros.
    """
    print(f"\n{'='*60}")
    print(f"  PASO: {nombre}")
    print(f"{'='*60}\n")
    try:
        funcion()
        print(f"\n[OK] {nombre} completado correctamente.")
    except Exception as e:
        print(f"\n[ERROR] {nombre} ha fallado: {e}")
        print("[ERROR] Proceso detenido. Corrija el error y vuelva a ejecutar.")
        sys.exit(1)


def main():
    # Comprobamos si hoy es uno de los días de ejecución mensual
    # Si no lo es, salimos sin hacer nada — la tarea está programada diariamente
    # pero solo actúa los días indicados en DIAS_EJECUCION
    hoy = datetime.now().day
    if '--forzar' not in sys.argv and hoy not in DIAS_EJECUCION:
        print(f"[INFO] Hoy es día {hoy}. El proceso se ejecuta los días {sorted(DIAS_EJECUCION)}. Saliendo.")
        sys.exit(0)

    print("\n" + "="*60)
    print("  COORDINADOR DGT - CyL")
    print("  Inicio del proceso completo")
    print("="*60)

    # Paso 0: vaciamos las carpetas para trabajar siempre con datos frescos
    ejecutar_paso("0. Limpieza de carpetas", limpiar_carpetas)

    # Paso 1: descargamos el ZIP más reciente de la DGT
    ejecutar_paso("1. Descarga del ZIP", descarga.main)

    # Paso 2: extraemos los TXT de CyL del ZIP descargado
    ejecutar_paso("2. Extracción de TXT", procesarTXT.main)

    # Paso 3: procesamos los TXT y generamos los CSVs con matrículas
    ejecutar_paso("3. Procesado y generación de CSVs", procesarCSV.main)

    # Paso 4: insertamos los CSVs en la base de datos
    ejecutar_paso("4. Inserción en base de datos", insertarBBDD.main)

    print("\n" + "="*60)
    print("  Proceso completo finalizado correctamente.")
    print("="*60 + "\n")


# Solo ejecutamos main() si llamamos directamente a este fichero
# Si otro módulo nos importa, este bloque no se ejecuta
if __name__ == "__main__":
    main()