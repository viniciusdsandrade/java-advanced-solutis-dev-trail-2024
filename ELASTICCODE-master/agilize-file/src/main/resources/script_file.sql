create database file_agilize;

FLUSH PRIVILEGES;

SET SQL_SAFE_UPDATES = 0;	

GRANT ALL PRIVILEGES ON file_agilize.* TO 'elastic_2'@'localhost';

use file_agilize;

CREATE TABLE file (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) null,
  name varchar(180) NOT NULL,
  name_entity varchar(180) NOT NULL,
  path_physical varchar(256) NOT NULL,
  path_logical varchar(180) NULL,
  content_type varchar(180) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id)
);

commit;