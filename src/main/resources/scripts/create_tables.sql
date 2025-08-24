-- Creación de la base de datos (si no existe)
CREATE DATABASE crediya_solicitudes;

-- Conéctate a la base de datos recién creada
\c crediya_solicitudes;

-- -----------------------------------------------------
-- Tabla `tipo_prestamo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tipo_prestamo (
  id_tipo_prestamo UUID NOT NULL PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL UNIQUE,
  monto_minimo NUMERIC(15, 2) NOT NULL,
  monto_maximo NUMERIC(15, 2) NOT NULL,
  tasa_interes NUMERIC(5, 2) NOT NULL,
  validacion_automatica BOOLEAN NOT NULL DEFAULT FALSE
);

-- -----------------------------------------------------
-- Tabla `estados`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS estados (
  id_estado UUID NOT NULL PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL UNIQUE,
  descripcion VARCHAR(255)
);

-- -----------------------------------------------------
-- Tabla `solicitud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS solicitud (
  id_solicitud UUID NOT NULL PRIMARY KEY,
  monto NUMERIC(15, 2) NOT NULL,
  plazo INT NOT NULL,
  email VARCHAR(255) NOT NULL,
  fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  id_estado UUID NOT NULL,
  id_tipo_prestamo UUID NOT NULL,
  CONSTRAINT fk_solicitud_estados
    FOREIGN KEY (id_estado)
    REFERENCES estados (id_estado)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_solicitud_tipo_prestamo
    FOREIGN KEY (id_tipo_prestamo)
    REFERENCES tipo_prestamo (id_tipo_prestamo)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);