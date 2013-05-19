SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `default_schema` DEFAULT CHARACTER SET utf8 ;
USE `default_schema` ;

-- -----------------------------------------------------
-- Table `default_schema`.`Modelo`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Modelo` (
  `idModelo` INT NOT NULL AUTO_INCREMENT ,
  `Nombre` VARCHAR(45) NOT NULL ,
  `Descripcion` VARCHAR(80) NULL DEFAULT NULL ,
  `Fecha_creac` DATETIME NOT NULL ,
  `Fecha_modif` DATETIME NOT NULL ,
  `Abandono` TINYINT(1) NULL DEFAULT NULL ,
  PRIMARY KEY (`idModelo`) ,
  UNIQUE INDEX `Nombre_UNIQUE` (`Nombre` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `default_schema`.`Datos_Semanales`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Datos_Semanales` (
  `idDatos_Semanales` INT NOT NULL AUTO_INCREMENT ,
  `Num_visitas` INT NOT NULL ,
  `Num_dias_diferentes` INT NOT NULL ,
  `Lunes` FLOAT NOT NULL ,
  `Martes` FLOAT NOT NULL ,
  `Miercoles` FLOAT NOT NULL ,
  `Jueves` FLOAT NOT NULL ,
  `Viernes` FLOAT NOT NULL ,
  `Sabado` FLOAT NOT NULL ,
  `Domingo` FLOAT NOT NULL ,
  `Num_semana` INT NOT NULL ,
  `Modelo_idModelo` INT NOT NULL ,
  PRIMARY KEY (`idDatos_Semanales`) ,
  INDEX `fk_Datos_Semanales_Modelo1_idx` (`Modelo_idModelo` ASC) ,
  UNIQUE INDEX `Num_semana_UNIQUE` (`Num_semana` ASC) ,
  CONSTRAINT `fk_Datos_Semanales_Modelo1`
    FOREIGN KEY (`Modelo_idModelo` )
    REFERENCES `default_schema`.`Modelo` (`idModelo` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `default_schema`.`Asignatura`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Asignatura` (
  `idNombre` VARCHAR(45) NOT NULL ,
  `Creditos` FLOAT NULL DEFAULT NULL ,
  PRIMARY KEY (`idNombre`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `default_schema`.`Curso`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Curso` (
  `Nombre_Curso` VARCHAR(50) NOT NULL ,
  `Curso_Academico` VARCHAR(50) NOT NULL ,
  `Semestre` INT(11) NOT NULL ,
  `Duracion` INT NULL DEFAULT NULL ,
  `Fecha_Comienzo` DATETIME NULL DEFAULT NULL ,
  `Asignatura_idNombre` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`Nombre_Curso`, `Curso_Academico`, `Semestre`) ,
  INDEX `fk_Curso_Asignatura_idx` (`Asignatura_idNombre` ASC) ,
  CONSTRAINT `fk_Curso_Asignatura`
    FOREIGN KEY (`Asignatura_idNombre` )
    REFERENCES `default_schema`.`Asignatura` (`idNombre` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `default_schema`.`Recurso`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Recurso` (
  `idRecurso` INT NOT NULL AUTO_INCREMENT ,
  `Nombre` VARCHAR(45) NULL DEFAULT NULL ,
  `Tipo` VARCHAR(45) NULL DEFAULT NULL ,
  `Curso_Nombre_Curso` VARCHAR(50) NOT NULL ,
  `Curso_Curso_Academico` VARCHAR(50) NOT NULL ,
  `Curso_Semestre` INT(11) NOT NULL ,
  PRIMARY KEY (`idRecurso`) ,
  INDEX `fk_Recurso_Curso1_idx` (`Curso_Nombre_Curso` ASC, `Curso_Curso_Academico` ASC, `Curso_Semestre` ASC) ,
  CONSTRAINT `fk_Recurso_Curso1`
    FOREIGN KEY (`Curso_Nombre_Curso` , `Curso_Curso_Academico` , `Curso_Semestre` )
    REFERENCES `default_schema`.`Curso` (`Nombre_Curso` , `Curso_Academico` , `Semestre` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '			';


-- -----------------------------------------------------
-- Table `default_schema`.`Accedido`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Accedido` (
  `Datos_Semanales_idDatos_Semanales` INT NOT NULL ,
  `Recurso_idRecurso` INT NOT NULL ,
  `Num_total_accesos` INT NOT NULL ,
  `Num_medio_accesos` INT NOT NULL ,
  `Moda_accesos` INT NOT NULL ,
  `Primer_acceso` INT NULL DEFAULT NULL ,
  `Primer_acceso_tipico` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`Datos_Semanales_idDatos_Semanales`, `Recurso_idRecurso`) ,
  INDEX `fk_Datos_Semanales_has_Recurso_Recurso1_idx` (`Recurso_idRecurso` ASC) ,
  INDEX `fk_Datos_Semanales_has_Recurso_Datos_Semanales_idx` (`Datos_Semanales_idDatos_Semanales` ASC) ,
  CONSTRAINT `fk_Datos_Semanales_has_Recurso_Datos_Semanales`
    FOREIGN KEY (`Datos_Semanales_idDatos_Semanales` )
    REFERENCES `default_schema`.`Datos_Semanales` (`idDatos_Semanales` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Datos_Semanales_has_Recurso_Recurso1`
    FOREIGN KEY (`Recurso_idRecurso` )
    REFERENCES `default_schema`.`Recurso` (`idRecurso` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `default_schema`.`Estudiante`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Estudiante` (
  `idNombre` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idNombre`) );


-- -----------------------------------------------------
-- Table `default_schema`.`Accede`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Accede` (
  `Fecha` DATE NOT NULL ,
  `Tipo` VARCHAR(45) NULL DEFAULT NULL ,
  `Estudiante_idNombre` VARCHAR(45) NOT NULL ,
  `Recurso_idRecurso` INT NOT NULL ,
  PRIMARY KEY (`Fecha`, `Estudiante_idNombre`, `Recurso_idRecurso`) ,
  INDEX `fk_Accede_Estudiante1_idx` (`Estudiante_idNombre` ASC) ,
  INDEX `fk_Accede_Recurso1_idx` (`Recurso_idRecurso` ASC) ,
  CONSTRAINT `fk_Accede_Estudiante1`
    FOREIGN KEY (`Estudiante_idNombre` )
    REFERENCES `default_schema`.`Estudiante` (`idNombre` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Accede_Recurso1`
    FOREIGN KEY (`Recurso_idRecurso` )
    REFERENCES `default_schema`.`Recurso` (`idRecurso` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `default_schema`.`Tiene_matriculado`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Tiene_matriculado` (
  `Abandono` TINYINT(1) NULL DEFAULT NULL ,
  `Calificacion` FLOAT NULL DEFAULT NULL ,
  `Peligrosidad` INT(11) NULL DEFAULT NULL ,
  `Curso_Nombre_Curso` VARCHAR(50) NOT NULL ,
  `Curso_Curso_Academico` VARCHAR(50) NOT NULL ,
  `Curso_Semestre` INT(11) NOT NULL ,
  `Estudiante_idNombre` VARCHAR(45) NOT NULL ,
  INDEX `fk_Tiene_matriculado_Estudiante1_idx` (`Estudiante_idNombre` ASC) ,
  PRIMARY KEY (`Curso_Nombre_Curso`, `Curso_Curso_Academico`, `Curso_Semestre`, `Estudiante_idNombre`) ,
  CONSTRAINT `fk_Tiene_matriculado_Curso1`
    FOREIGN KEY (`Curso_Nombre_Curso` , `Curso_Curso_Academico` , `Curso_Semestre` )
    REFERENCES `default_schema`.`Curso` (`Nombre_Curso` , `Curso_Academico` , `Semestre` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Tiene_matriculado_Estudiante1`
    FOREIGN KEY (`Estudiante_idNombre` )
    REFERENCES `default_schema`.`Estudiante` (`idNombre` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `default_schema`.`Representa`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `default_schema`.`Representa` (
  `Modelo_idModelo` INT NOT NULL ,
  `Curso_Nombre_Curso` VARCHAR(50) NOT NULL ,
  `Curso_Curso_Academico` VARCHAR(50) NOT NULL ,
  `Curso_Semestre` INT(11) NOT NULL ,
  PRIMARY KEY (`Modelo_idModelo`, `Curso_Nombre_Curso`, `Curso_Curso_Academico`, `Curso_Semestre`) ,
  INDEX `fk_Modelo_has_Curso_Curso1_idx` (`Curso_Nombre_Curso` ASC, `Curso_Curso_Academico` ASC, `Curso_Semestre` ASC) ,
  INDEX `fk_Modelo_has_Curso_Modelo1_idx` (`Modelo_idModelo` ASC) ,
  CONSTRAINT `fk_Modelo_has_Curso_Modelo1`
    FOREIGN KEY (`Modelo_idModelo` )
    REFERENCES `default_schema`.`Modelo` (`idModelo` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Modelo_has_Curso_Curso1`
    FOREIGN KEY (`Curso_Nombre_Curso` , `Curso_Curso_Academico` , `Curso_Semestre` )
    REFERENCES `default_schema`.`Curso` (`Nombre_Curso` , `Curso_Academico` , `Semestre` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `default_schema` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
