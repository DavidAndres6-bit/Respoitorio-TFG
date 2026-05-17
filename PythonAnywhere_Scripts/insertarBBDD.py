import os                           # Trabaja con rutas y carpetas del sistema
import pandas as pd                 # Lectura de los CSVs generados
import mysql.connector              # Conexión y operaciones con MySQL
from datetime import datetime          # Conversión de fechas al formato que espera MySQL

# ─── CONFIGURACIÓN DE LA BASE DE DATOS ───────────────────────────────────────

DB_CONFIG = {
    'host':     'DJF.mysql.pythonanywhere-services.com',
    'user':     'DJF',
    'password': 'DJFTFG1234',
    'database': 'DJF$TFG',
}

# Carpeta donde están los CSVs generados por procesarCSV.py
RUTA_CSV = "/home/DJF/temp/csv"

# Nombre de la tabla en la base de datos
TABLA = "vehiculos_cyl"


# ─── CONEXIÓN ────────────────────────────────────────────────────────────────

def conectar():
    """Establecemos la conexión con la base de datos y la devolvemos."""
    try:
        conexion = mysql.connector.connect(**DB_CONFIG)
        print("[INFO] Conexión establecida con la base de datos.")
        return conexion
    except mysql.connector.Error as e:
        print(f"[ERROR] No se pudo conectar a la base de datos: {e}")
        raise


# ─── CONVERSIÓN DE FECHAS ────────────────────────────────────────────────────

def convertir_fecha(fecha_str, con_hora=False):
    """
    Convertimos una fecha al formato que espera MySQL.
    Acepta tanto DD/MM/YYYY como DD/MM/YYYY HH:MM:SS.
    Si con_hora=False → DATE (YYYY-MM-DD)
    Si con_hora=True  → DATETIME (YYYY-MM-DD HH:MM:SS)
    Devolvemos None si el valor es inválido o vacío.
    """
    try:
        valor = str(fecha_str).strip()
        # Intentamos primero con hora y luego sin ella
        for fmt in ("%d/%m/%Y %H:%M:%S", "%d/%m/%Y"):
            try:
                fecha = datetime.strptime(valor, fmt)
                if con_hora:
                    return fecha.strftime("%Y-%m-%d %H:%M:%S")
                return fecha.strftime("%Y-%m-%d")
            except ValueError:
                continue
        return None
    except (ValueError, TypeError):
        return None


# ─── CARGA DE MATRÍCULAS EXISTENTES ──────────────────────────────────────────

def cargar_matriculas_bbdd(cursor):
    """
    Consultamos todas las matrículas que ya existen en la base de datos
    y las devolvemos como un set para poder hacer búsquedas rápidas.
    """
    cursor.execute(f"SELECT MATRICULA FROM {TABLA}")
    matriculas = {fila[0] for fila in cursor.fetchall()}
    print(f"[INFO] {len(matriculas)} matrículas encontradas en base de datos.")
    return matriculas


# ─── PROCESADO DE CADA CSV ────────────────────────────────────────────────────

def procesar_csv(ruta_csv, cursor, matriculas_bbdd, matriculas_csv_global):
    """
    Leemos un CSV de provincia y para cada registro decidimos:
      - Si la matrícula YA está en BBDD → no insertamos (ya existe)
      - Si la matrícula NO está en BBDD → insertamos el registro nuevo
    También acumulamos todas las matrículas del CSV en matriculas_csv_global
    para poder detectar después las bajas.

    Devuelve el número de registros insertados.
    """
    print(f"  · Procesando: {os.path.basename(ruta_csv)}")

    df = pd.read_csv(ruta_csv, sep=";", encoding="utf-8-sig")

    insertados = 0

    for _, fila in df.iterrows():
        matricula = str(fila['MATRICULA']).strip()

        # Acumulamos la matrícula en el set global de CSVs
        matriculas_csv_global.add(matricula)

        if matricula in matriculas_bbdd:
            # La matrícula ya existe en BBDD → no insertamos
            continue

        # La matrícula no existe en BBDD → insertamos el registro nuevo
        sql = f"""
            INSERT IGNORE INTO {TABLA}
            (MATRICULA, PROVINCIA, MARCA, MODELO, FECHA_MATR, PROCEDENCIA,
             NUEVO_USADO, TIPO_DGT, TIPO_DISTINTIVO, EMISIONES_CO2, PLAZAS,
             FECHA_ACTUALIZACION, ESTADO_ACTUAL)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        valores = (
            matricula,
            str(fila['PROVINCIA']).strip(),
            str(fila['MARCA']).strip(),
            str(fila['MODELO']).strip(),
            convertir_fecha(fila['FECHA_MATR']),                  # DATE → YYYY-MM-DD
            str(fila['PROCEDENCIA']).strip(),
            str(fila['NUEVO_USADO']).strip(),
            str(fila['TIPO_DGT']).strip(),
            str(fila['TIPO_DISTINTIVO']).strip(),
            str(fila['EMISIONES_CO2']).strip(),
            str(fila['PLAZAS']).strip(),
            convertir_fecha(fila['FECHA_ACTUALIZACION'], con_hora=True),  # DATETIME → YYYY-MM-DD 00:00:00
            str(fila['ESTADO_ACTUAL']).strip(),
        )
        cursor.execute(sql, valores)
        insertados += 1

    print(f"    → {insertados} registros insertados")
    return insertados


# ─── DETECCIÓN DE BAJAS ───────────────────────────────────────────────────────

def marcar_bajas(cursor, matriculas_bbdd, matriculas_csv_global):
    """
    Comparamos las matrículas que hay en BBDD con las que hay en los CSVs.
    Las que están en BBDD pero NO en los CSVs significa que el vehículo
    ya no aparece en los datos de la DGT → lo marcamos como Baja (B).
    """
    bajas = matriculas_bbdd - matriculas_csv_global

    if not bajas:
        print("[INFO] No se detectaron bajas.")
        return 0

    # Actualizamos el ESTADO_ACTUAL a 'B' para todas las matrículas de baja
    # Usamos placeholders para evitar SQL injection
    placeholders = ', '.join(['%s'] * len(bajas))
    sql = f"UPDATE {TABLA} SET ESTADO_ACTUAL = 'B' WHERE MATRICULA IN ({placeholders})"
    cursor.execute(sql, tuple(bajas))

    print(f"[INFO] {len(bajas)} vehículo(s) marcado(s) como Baja (B).")
    return len(bajas)


# ─── FLUJO PRINCIPAL ──────────────────────────────────────────────────────────

def main():
    print("=== INSERCIÓN EN BASE DE DATOS - DGT CyL ===\n")

    # Recogemos todos los CSVs de provincia (excluimos cualquier otro fichero)
    archivos = sorted([
        os.path.join(RUTA_CSV, f)
        for f in os.listdir(RUTA_CSV)
        if f.startswith("datos_") and f.endswith(".csv")
    ])

    if not archivos:
        print(f"[AVISO] No se encontraron CSVs en: {RUTA_CSV}")
        return

    print(f"[INFO] {len(archivos)} CSV(s) encontrado(s):\n")
    for ruta in archivos:
        print(f"  · {os.path.basename(ruta)}")
    print()

    conexion = conectar()
    cursor   = conexion.cursor()

    try:
        # Cargamos las matrículas que ya existen en BBDD
        matriculas_bbdd = cargar_matriculas_bbdd(cursor)

        # Set donde acumulamos todas las matrículas de los CSVs actuales
        # Lo usamos al final para detectar bajas
        matriculas_csv_global = set()

        # Procesamos cada CSV
        total_insertados = 0
        for ruta in archivos:
            total_insertados += procesar_csv(
                ruta, cursor, matriculas_bbdd, matriculas_csv_global
            )

        # Detectamos y marcamos las bajas
        total_bajas = marcar_bajas(cursor, matriculas_bbdd, matriculas_csv_global)

        # Confirmamos todos los cambios en la base de datos
        # Sin este commit, ninguna inserción ni actualización se guarda
        conexion.commit()

        print(f"\n[INFO] Proceso completado.")
        print(f"  · Registros insertados : {total_insertados}")
        print(f"  · Vehículos dados de baja: {total_bajas}")

    except Exception as e:
        # Si algo falla, deshacemos todos los cambios para no dejar la BBDD a medias
        conexion.rollback()
        print(f"[ERROR] Proceso abortado: {e}")
        raise

    finally:
        # Cerramos siempre la conexión, haya habido error o no
        cursor.close()
        conexion.close()
        print("[INFO] Conexión cerrada.")


# Solo ejecutamos main() si llamamos directamente a este fichero
if __name__ == "__main__":
    main()