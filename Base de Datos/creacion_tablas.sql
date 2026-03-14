/*  Creacion tabla para almacenar los datos de los vehiculo  */

CREATE TABLE vehiculos_cyl (
    ->     id INT AUTO_INCREMENT PRIMARY KEY,
    ->     MATRICULA VARCHAR(7),
    ->     PROVINCIA VARCHAR(50),
    ->     MARCA VARCHAR(100),
    ->     MODELO VARCHAR(100),
    ->     FECHA_MATR VARCHAR(20),
    ->     PROCEDENCIA VARCHAR(50),
    ->     estado VARCHAR(20),
    ->     TIPO_DGT VARCHAR(100),
    ->     TIPO_DISTINTIVO VARCHAR(50),
    ->     EMISIONES_CO2 VARCHAR(20),
    ->     PLAZAS VARCHAR(10),
    ->     activo TINYINT(1) DEFAULT 1
    -> );


/* Creacion tabla para almacenae los usuarios de la aplicacion  */
CREATE TABLE usuarios (
    -> id INT AUTO_INCREMENT PRIMARY KEY,
    -> nombre_usuario VARCHAR (100),
    -> contrasenia VARCHAR (30),
    -> correo_electronico VARCHAR (200),
    -> telefono INT (9)
    -> );