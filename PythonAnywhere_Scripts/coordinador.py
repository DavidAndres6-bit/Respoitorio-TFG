import sys                          # Permite salir del script con un código de error
from datetime import datetime       # Para comprobar el día del mes actual
import descarga                     # Script de descarga del ZIP de la DGT
import procesarTXT                  # Script de extracción de TXT del ZIP
import procesarCSV                  # Script de procesado y generación de CSVs
import insertarBBDD                 # Script de inserción en base de datos

# Días del mes en los que se ejecuta el proceso completo
# Usamos dos fechas para asegurarnos de que la DGT ya ha publicado los datos
DIAS_EJECUCION = {5, 20}


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
    # Comprobamos si hoy es el día de ejecución mensual
    # Si no lo es, salimos sin hacer nada — la tarea está programada diariamente
    # pero solo actúa el día indicado en DIA_EJECUCION
    hoy = datetime.now().day
    if hoy != DIA_EJECUCION:
        print(f"[INFO] Hoy es día {hoy}. El proceso se ejecuta el día {DIA_EJECUCION}. Saliendo.")
        sys.exit(0)

    print("\n" + "="*60)
    print("  COORDINADOR DGT - CyL")
    print("  Inicio del proceso completo")
    print("="*60)

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
if __name__ == "__main__":
    main()