import csv

#Configurar el archivo de entrada y el de salida
archivo_entrada = '47.VALLADOLID.txt'  
archivo_salida = '47.VALLADOLID.csv'

#Funcion que convierte el documento a csv
def convertir():

    #Abrir el archivo
    try:

        #Abrirlo en modo lectura y el encoding porque el archivo tiene ñ y otros caracteres delicados
        with open(archivo_entrada, 'r', encoding="latin-1") as f_in:

            #Recorrer el fichero delimitado por el separador el txt la  |
            reader = csv.reader(f_in, delimiter='|')

            #Crear el archivo csv de salida cambiando el separador
            with open(archivo_salida, 'w', newline='', encoding='utf-8') as f_out:

                #Escribir en el archiv cambiando el separador
                #Con el quoting se consigue que si hay comas dentro del txt no se confunda con una 
                #division de columnas en el csv
                writer = csv.writer(f_out,delimiter=',', quoting=csv.QUOTE_MINIMAL)

                contador = 0
                for fila in reader:
                    # Escribir la fila en el nuevo archivo
                    writer.writerow(fila)
                    
                    # Mostrar el progeso cada ciertas lineas no una a una
                    contador += 1
                    if contador % 100000 == 0:
                        print(f"Líneas procesadas: {contador}...")

        print(f"\nConversión realizada con éxito.")
        print(f"Total de registros: {contador}")
        print(f"Archivo generado: {archivo_salida}")

    except FileNotFoundError:
        print(f"Error: No se encuentra el archivo '{archivo_entrada}' en esta carpeta.")
    except Exception as e:
        print(f"Ocurrió un error: {e}")

convertir()