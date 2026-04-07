/* Tabla para controlar las funciones HASH y las matriculas */
CREATE TABLE maestro_entidad (
    -> hash VARCHAR (64) NOT NULL,
    -> matricula VARCHAR (7) UNIQUE NOT NULL,
    -> instancia INT (10)
    -> );


/* Tabla para almacenar los datos de los vehiculos */
CREATE TABLE vehiculos_cyl(
    -> MATRICULA VARCHAR (7) PRIMARY KEY,
    -> PROVINCIA VARCHAR (50),
    -> MARCA VARCHAR (100),
    -> MODELO VARCHAR (100),
    -> FECHA_MATR VARCHAR (20),
    -> PROCEDENCIA VARCHAR (50),
    -> ESTADO VARCHAR (20),
    -> TIPO_DGT VARCHAR (100),
    -> TIPO_DISTINTIVO VARCHAR (50),
    -> EMISIONES_CO2 VARCHAR (50),
    -> PLAZAS VARCHAR (10),
    -> FECHA_ULTIMA_MODIFICACION DATETIME,
    -> ACTIVO TINYINT(1) DEFAULT 1
    -> );

/* Añadir la relacion PF respecto a la tabla maestro_entidad */
ALTER TABLE vehiculos_cyl ADD CONSTRAINT fk_maestro_entidad FOREIGN KEY (MATRICULA) REFERENCES maestro_entidad (matricula) ON DELETE CASCADE;


/* Tabla para almacenar los usuarios de la aplicacion  */
CREATE TABLE usuarios (
    -> id INT AUTO_INCREMENT PRIMARY KEY,
    -> nombre_usuario VARCHAR (100),
    -> contrasenia VARCHAR (30),
    -> correo_electronico VARCHAR (200),
    -> telefono INT (9)
    -> administrador BOOLEAN
    -> );