SET SQL_SAFE_UPDATES = 0;
CREATE DATABASE asyncronous_agilize;
UPDATE mysql.user SET Grant_priv='Y', Super_priv='Y' WHERE User='root';
FLUSH PRIVILEGES;
GRANT ALL ON *.* TO 'root'@'localhost';
GRANT ALL ON asyncronous_agilize.* TO 'elastic_2'@'localhost';
use asyncronous_agilize;

CREATE TABLE queue (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  name_entity varchar(120) NULL,
  fk_application INT(12) not null,
  queue int(2) NOT NULL,
  url varchar(150) NULL,
  parameters LONGTEXT NULL,
  priority int(1) NOT NULL default 2,
  historic bit not null default false,
  count int(1) NOT NULL default 0,
  executed bit not null default false,
  error varchar(1000) null,
  dt_execution timestamp null,
  PRIMARY KEY (id),
  CONSTRAINT fk_queue_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_queue_applc FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.application (id)
);

SET SQL_SAFE_UPDATES = 1;