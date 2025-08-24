CREATE DATABASE IF NOT EXISTS crediya_solicitudes;
USE crediya_solicitudes;

-- -----------------------------------------------------
-- Tabla `tipo_prestamo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tipo_prestamo` (
  `id_tipo_prestamo` BINARY(16) NOT NULL,
  `nombre` VARCHAR(255) NOT NULL,
  `monto_minimo` DECIMAL(15, 2) NOT NULL,
  `monto_maximo` DECIMAL(15, 2) NOT NULL,
  `tasa_interes` DECIMAL(5, 2) NOT NULL,
  `validacion_automatica` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_tipo_prestamo`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC) VISIBLE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Tabla `estados`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `estados` (
  `id_estado` BINARY(16) NOT NULL,
  `nombre` VARCHAR(255) NOT NULL,
  `descripcion` VARCHAR(255) NULL,
  PRIMARY KEY (`id_estado`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC) VISIBLE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Tabla `solicitud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `solicitud` (
  `id_solicitud` BINARY(16) NOT NULL,
  `monto` DECIMAL(15, 2) NOT NULL,
  `plazo` INT NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_estado` BINARY(16) NOT NULL,
  `id_tipo_prestamo` BINARY(16) NOT NULL,
  PRIMARY KEY (`id_solicitud`),
  INDEX `fk_solicitud_estados_idx` (`id_estado` ASC) VISIBLE,
  INDEX `fk_solicitud_tipo_prestamo_idx` (`id_tipo_prestamo` ASC) VISIBLE,
  CONSTRAINT `fk_solicitud_estados`  FOREIGN KEY (`id_estado`)  REFERENCES `estados` (`id_estado`)  ON DELETE NO ACTION  ON UPDATE NO ACTION,
  CONSTRAINT `fk_solicitud_tipo_prestamo`  FOREIGN KEY (`id_tipo_prestamo`)  REFERENCES `tipo_prestamo` (`id_tipo_prestamo`)  ON DELETE NO ACTION  ON UPDATE NO ACTION
) ENGINE = InnoDB;