-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.5.16 - MySQL Community Server (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             8.0.0.4396
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for dbviews
DROP DATABASE IF EXISTS `dbviews`;
CREATE DATABASE IF NOT EXISTS `dbviews` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `dbviews`;


-- Dumping structure for table dbviews.dbv_connection
DROP TABLE IF EXISTS `dbv_connection`;
CREATE TABLE IF NOT EXISTS `dbv_connection` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `url` varchar(5000) NOT NULL COMMENT 'a database url of the form jdbc:subprotocol:subname',
  `username` varchar(255) NOT NULL COMMENT 'the database user on whose behalf the connection is being made',
  `password` varchar(255) NOT NULL COMMENT 'the user''s password',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Dumping data for table dbviews.dbv_connection: ~3 rows (approximately)
/*!40000 ALTER TABLE `dbv_connection` DISABLE KEYS */;
REPLACE INTO `dbv_connection` (`id`, `description`, `url`, `username`, `password`) VALUES
	(1, 'Oracle HR', 'jdbc:oracle:thin:@localhost:1521:XE', 'hr', 'hr'),
	(2, 'MySQL DbViews', 'jdbc:mysql://localhost:3306/dbviews', 'dbviews', 'dbviews1'),
	(3, 'UDIMA DW', 'jdbc:mysql://localhost:3306/default_schema', 'udima', 'udima1');
/*!40000 ALTER TABLE `dbv_connection` ENABLE KEYS */;


-- Dumping structure for table dbviews.dbv_graph
DROP TABLE IF EXISTS `dbv_graph`;
CREATE TABLE IF NOT EXISTS `dbv_graph` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `view_id` int(10) NOT NULL,
  `description` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `sql_query` varchar(5000) NOT NULL COMMENT 'SQL query that generates the graph',
  `tab_index` int(10) NOT NULL DEFAULT '0',
  `graph_type` varchar(50) NOT NULL DEFAULT 'pie' COMMENT '"pie" or "bars" or "lines" or "points"',
  `serie_column` varchar(255) DEFAULT NULL,
  `xaxis_column` varchar(255) NOT NULL,
  `yaxis_column` varchar(255) NOT NULL,
  `xmode` varchar(50) DEFAULT NULL COMMENT '"categories" or "time"',
  `ymode` varchar(50) DEFAULT NULL COMMENT '"categories" or "time"',
  `width` int(10) NOT NULL DEFAULT '400',
  `height` int(10) NOT NULL DEFAULT '400',
  `legend_position` varchar(50) DEFAULT 'ne' COMMENT '"ne" or "nw" or "se" or "sw"',
  PRIMARY KEY (`id`),
  KEY `fk_graph_view` (`view_id`),
  CONSTRAINT `fk_graph_view` FOREIGN KEY (`view_id`) REFERENCES `dbv_view` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- Dumping data for table dbviews.dbv_graph: ~7 rows (approximately)
/*!40000 ALTER TABLE `dbv_graph` DISABLE KEYS */;
REPLACE INTO `dbv_graph` (`id`, `view_id`, `description`, `label`, `sql_query`, `tab_index`, `graph_type`, `serie_column`, `xaxis_column`, `yaxis_column`, `xmode`, `ymode`, `width`, `height`, `legend_position`) VALUES
	(1, 1, 'Employees grouping by department', 'Employees by department', 'select department_id, count(*) as employees from employees group by department_id', 3, 'pie', 'department_id', 'employees', '', '', '', 600, 400, ''),
	(2, 3, 'Group by modelo.abandono', 'Abandono Pie', 'select if(abandono=1, \'Si\', \'No\') as "Abandono", count(*) as "Cantidad" from modelo group by abandono', 4, 'pie', 'abandono', 'cantidad', '', '', '', 400, 400, 'ne'),
	(4, 3, 'Group by asignatura.creditos', 'Asignaturas Pie', 'select creditos as "Créditos", count(1) as "Cantidad" from asignatura group by creditos', 2, 'pie', 'créditos', 'cantidad', '', '', '', 400, 400, 'ne'),
	(5, 3, 'Group by curso.duracion', 'Curso Pie', 'select duracion as "Duración", count(1) as "Cantidad" from curso group by duracion', 6, 'pie', 'duración', 'cantidad', '', '', '', 800, 650, 'ne'),
	(6, 3, 'Group by curso.duracion', 'Curso Bars', 'select duracion as "Duración", count(1) as "Cantidad" from curso group by duracion', 7, 'points,lines', '', 'duración', 'cantidad', '', '', 600, 400, 'ne'),
	(7, 3, 'Cursos agrupados por asignatura, semestre', 'Barra de Cursos', 'select concat(\'Semestre \', Semestre) as Semestre, Asignatura_idNombre, count(*) as cursos from curso group by concat(\'Semestre \', Semestre), Asignatura_idNombre', 8, 'points,lines', 'Semestre', 'Asignatura_idNombre', 'cursos', '', '', 600, 400, 'ne'),
	(8, 1, 'Employees grouping by department', 'Employees Bars', 'select department_id, count(*) as employees from employees group by department_id order by department_id, employees', 4, 'points,lines', '', 'department_id', 'employees', '', '', 800, 600, 'ne');
/*!40000 ALTER TABLE `dbv_graph` ENABLE KEYS */;


-- Dumping structure for table dbviews.dbv_html_block
DROP TABLE IF EXISTS `dbv_html_block`;
CREATE TABLE IF NOT EXISTS `dbv_html_block` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `view_id` int(10) NOT NULL,
  `description` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `sql_query` varchar(5000) NOT NULL COMMENT 'SQL query that generates the table',
  `tab_index` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_html_block_view` (`view_id`),
  CONSTRAINT `fk_html_block_view` FOREIGN KEY (`view_id`) REFERENCES `dbv_view` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Dumping data for table dbviews.dbv_html_block: ~1 rows (approximately)
/*!40000 ALTER TABLE `dbv_html_block` DISABLE KEYS */;
REPLACE INTO `dbv_html_block` (`id`, `view_id`, `description`, `label`, `sql_query`, `tab_index`) VALUES
	(1, 3, 'Alerta de Abandono', 'Abandono', 'select \'<p style="color:red">Usted está en peligro de abandono</p>\' from dual', 9);
/*!40000 ALTER TABLE `dbv_html_block` ENABLE KEYS */;


-- Dumping structure for table dbviews.dbv_table
DROP TABLE IF EXISTS `dbv_table`;
CREATE TABLE IF NOT EXISTS `dbv_table` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `view_id` int(10) NOT NULL,
  `description` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `sql_query` varchar(5000) NOT NULL COMMENT 'SQL query that generates the table',
  `tab_index` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_table_view` (`view_id`),
  CONSTRAINT `fk_table_view` FOREIGN KEY (`view_id`) REFERENCES `dbv_view` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- Dumping data for table dbviews.dbv_table: ~9 rows (approximately)
/*!40000 ALTER TABLE `dbv_table` DISABLE KEYS */;
REPLACE INTO `dbv_table` (`id`, `view_id`, `description`, `label`, `sql_query`, `tab_index`) VALUES
	(1, 1, 'Employees table', 'Employees', 'select * from employees where first_name like \'%\' || {emp} || \'%\'', 1),
	(2, 1, 'Employees grouping by department', 'Employees by department', 'select department_id, count(*) from employees group by department_id', 2),
	(3, 2, 'Vistas', 'Vistas', 'select * from dbv_view', 2),
	(4, 2, 'Conexiones', 'Conexiones', 'select * from dbv_connection', 1),
	(5, 2, 'Tablas', 'Tablas', 'select * from dbv_table', 3),
	(6, 3, 'Tabla Asignatura', 'Asignaturas', 'select * from asignatura', 1),
	(7, 3, 'Tabla Modelo', 'Modelos', 'select * from modelo', 3),
	(8, 3, 'Tabla Curso', 'Cursos', 'select c.*, a.creditos from curso c inner join asignatura a on a.idNombre = c.Asignatura_idNombre', 5),
	(9, 2, 'Gráficas', 'Gráficas', 'select * from dbv_graph', 4);
/*!40000 ALTER TABLE `dbv_table` ENABLE KEYS */;


-- Dumping structure for table dbviews.dbv_table_field
DROP TABLE IF EXISTS `dbv_table_field`;
CREATE TABLE IF NOT EXISTS `dbv_table_field` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `table_id` int(10) NOT NULL COMMENT 'Id of the table to which it belongs',
  `name` varchar(64) NOT NULL COMMENT 'Name of field',
  `column_name` varchar(64) NOT NULL COMMENT 'Name of column in database',
  `type` int(11) NOT NULL DEFAULT '12' COMMENT 'java.sql.Types',
  `width` varchar(8) DEFAULT NULL COMMENT 'Width of column in table',
  `align` varchar(8) DEFAULT NULL COMMENT 'Left, Center, Right',
  `valign` varchar(8) DEFAULT NULL COMMENT 'Top, Middle, Bottom',
  `field_order` varchar(8) DEFAULT NULL COMMENT 'Asc, Desc, None, Locked',
  `visible` varchar(8) DEFAULT NULL COMMENT 'If the field should be displayed in the table',
  `exportable` varchar(8) DEFAULT NULL COMMENT 'If the field should be exportable',
  PRIMARY KEY (`id`),
  KEY `fk_table_field` (`table_id`),
  CONSTRAINT `fk_table_field` FOREIGN KEY (`table_id`) REFERENCES `dbv_table` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table dbviews.dbv_table_field: ~0 rows (approximately)
/*!40000 ALTER TABLE `dbv_table_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `dbv_table_field` ENABLE KEYS */;


-- Dumping structure for table dbviews.dbv_view
DROP TABLE IF EXISTS `dbv_view`;
CREATE TABLE IF NOT EXISTS `dbv_view` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `connection_id` int(10) NOT NULL,
  `description` varchar(255) NOT NULL,
  `auth_principals` varchar(5000) DEFAULT NULL COMMENT 'Users and groups (separated by comma)',
  `jqui_plugin` varchar(50) NOT NULL DEFAULT 'tabs' COMMENT '"tabs" or "accordion"',
  `jqui_plugin_options` varchar(5000) DEFAULT NULL COMMENT 'JSON syntax as plugin argument',
  PRIMARY KEY (`id`),
  KEY `fk_connection` (`connection_id`),
  CONSTRAINT `fk_connection` FOREIGN KEY (`connection_id`) REFERENCES `dbv_connection` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Dumping data for table dbviews.dbv_view: ~3 rows (approximately)
/*!40000 ALTER TABLE `dbv_view` DISABLE KEYS */;
REPLACE INTO `dbv_view` (`id`, `connection_id`, `description`, `auth_principals`, `jqui_plugin`, `jqui_plugin_options`) VALUES
	(1, 1, 'HR Employees', '', 'accordion', '{ "collapsible": true, "heightStyle": "content" }'),
	(2, 2, 'DbViews', 'Teachers,Students', 'tabs', '{ "collapsible": true, "heightStyle": "content"  }'),
	(3, 3, 'UDIMA Data Warehouse', 'Teachers', 'tabs', '{ "collapsible": true }');
/*!40000 ALTER TABLE `dbv_view` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
