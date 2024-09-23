use service_agilize;

/* insert into mysql.user (Host, User, Password, Select_priv, Insert_priv, Update_priv, Delete_priv, Create_priv, Drop_priv, Reload_priv, Shutdown_priv, Process_priv, File_priv, Grant_priv, References_priv, Index_priv, Alter_priv, Show_db_priv, Super_priv, Create_tmp_table_priv, Lock_tables_priv, Execute_priv, Repl_slave_priv, Repl_client_priv, Create_view_priv, Show_view_priv, Create_routine_priv, Alter_routine_priv, Create_user_priv, ssl_type, ssl_cipher, x509_issuer, x509_subject, max_questions, max_updates, max_connections, max_user_connections) 
values('localhost','root','','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','','','','','0','0','0','0'); */


CREATE TABLE entity (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  name varchar(120) NOT NULL,
  description varchar(300) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_entty_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id)
);

CREATE TABLE field (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  name varchar(120) NOT NULL,
  type_field int(2) NOT NULL,
  fk_entity INT(12) NOT NULL,
  fk_entity_ref INT(12) null,
  type_relation INT(2) null,
  PRIMARY KEY (id),
  CONSTRAINT fk_field_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_field_entity FOREIGN KEY (fk_entity) REFERENCES entity (id),
  CONSTRAINT fk_field_entref FOREIGN KEY (fk_entity_ref) REFERENCES entity (id)
);

CREATE TABLE field_value (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  fk_field INT(12) NOT NULL,
  value text null,
  PRIMARY KEY (id),
  CONSTRAINT fk_fivlue_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_fivlue_field FOREIGN KEY (fk_field) REFERENCES field (id)
);

/**
CREATE TABLE service_agilize.relationship (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  fk_entity_ref INT(12) not null,
  fk_field INT(12) not null,
  type_relation INT(2) not null,
  PRIMARY KEY (id),
  CONSTRAINT fk_relat_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_relat_entit FOREIGN KEY (fk_entity_ref) REFERENCES entity (id),
  CONSTRAINT fk_relat_field FOREIGN KEY (fk_field) REFERENCES field (id)
);
*/

alter table field add column description varchar(300) NULL;

-- Tabela que irá guardar qual(is) aplicacoes determinada entidade pertence
CREATE TABLE entity_application (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  fk_entity INT(12) not null,
  fk_application INT(12) not null,
  PRIMARY KEY (id),
  CONSTRAINT fk_entapp_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_entapp_entit FOREIGN KEY (fk_entity) REFERENCES entity (id),
  CONSTRAINT fk_entapp_applic FOREIGN KEY (fk_application) REFERENCES agilize_security.application (id),
  CONSTRAINT UNIQUE KEY uk_ent_app (fk_entity, fk_application)
);

