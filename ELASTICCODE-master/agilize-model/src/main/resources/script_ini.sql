/* insert into mysql.user (Host, User, Password, Select_priv, Insert_priv, Update_priv, Delete_priv, Create_priv, Drop_priv, Reload_priv, Shutdown_priv, Process_priv, File_priv, Grant_priv, References_priv, Index_priv, Alter_priv, Show_db_priv, Super_priv, Create_tmp_table_priv, Lock_tables_priv, Execute_priv, Repl_slave_priv, Repl_client_priv, Create_view_priv, Show_view_priv, Create_routine_priv, Alter_routine_priv, Create_user_priv, ssl_type, ssl_cipher, x509_issuer, x509_subject, max_questions, max_updates, max_connections, max_user_connections) 
values('localhost','root','','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','Y','','','','','0','0','0','0'); */

SET SQL_SAFE_UPDATES = 0;
UPDATE mysql.user SET Grant_priv = 1, Super_priv = 1 WHERE user = 'root';
UPDATE mysql.user SET Grant_priv = 1, Super_priv = 1 WHERE user = 'elastic_2';
FLUSH PRIVILEGES;

CREATE DATABASE agilize_security;
GRANT ALL ON agilize_security.* TO 'elastic_2'@'localhost';
use agilize_security;

CREATE TABLE user (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) null,
  name varchar(180) NOT NULL,
  cpf varchar(15) NOT NULL,
  dt_birth date NOT NULL,
  password varchar(300) NOT NULL,
  username varchar(60) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_user FOREIGN KEY (fk_user_creation) REFERENCES user (id)
);

alter table user add non_expired bit not null default 1;
alter table user add non_locked bit not null default 1;
alter table user add credential_non_expired bit not null default 1;
alter table user add enabled bit not null default 1;
alter table user add email varchar(200) null;
alter table user add dt_remove timestamp null;
alter table user add is_super_adm bit not null default 0;
alter table user add UNIQUE KEY uk_user (cpf,email);
alter table user add UNIQUE KEY uk_user_username (username);
alter table user add fk_file INT(12) NULL;
alter table user add CONSTRAINT fk_user_file FOREIGN KEY (fk_file) REFERENCES file_agilize.file (id);

CREATE TABLE user_access (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  type_device int(2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_usacc_user FOREIGN KEY (fk_user_creation) REFERENCES user (id)
);

-- Regras
CREATE TABLE role (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  name varchar(180) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_role_user FOREIGN KEY (fk_user_creation) REFERENCES user (id)
);

-- Funcionalidades
CREATE TABLE feature (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  name varchar(180) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_feat_user FOREIGN KEY (fk_user_creation) REFERENCES user (id)
);

-- Papeis
CREATE TABLE function (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  name varchar(180) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_func_user FOREIGN KEY (fk_user_creation) REFERENCES user (id)
);

-- Agrupamento de Papeis
CREATE TABLE groupment (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  name varchar(180) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_grou_user FOREIGN KEY (fk_user_creation) REFERENCES user (id)
);

-- Associa��o entre o grupo e o papel
CREATE TABLE function_group (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_groupment INT(12) NOT NULL,
  fk_function INT(12) NOT NULL,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  PRIMARY KEY (id),
  UNIQUE KEY uk_grou_func (fk_groupment, fk_function),
  CONSTRAINT fk_fcgr_user FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_fcgr_grou FOREIGN KEY (fk_groupment) REFERENCES groupment (id),
  CONSTRAINT fk_fcgr_func FOREIGN KEY (fk_function) REFERENCES function (id)
);

-- Associa��o entre o papel e a regra
CREATE TABLE feature_role (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_feature INT(12) NOT NULL,
  fk_role INT(12) NOT NULL,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  PRIMARY KEY (id),
  UNIQUE KEY uk_feat_role (fk_feature, fk_role),
  CONSTRAINT fk_feto_user FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_feto_feat FOREIGN KEY (fk_feature) REFERENCES feature (id),
  CONSTRAINT fk_feto_role FOREIGN KEY (fk_role) REFERENCES role (id)
);

CREATE TABLE function_feature (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_function INT(12) NOT NULL,
  fk_feature INT(12) NULL,
  fk_role INT(12) NULL,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  PRIMARY KEY (id),
  CONSTRAINT fk_fufe_user FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_fufe_func FOREIGN KEY (fk_function) REFERENCES function (id),
  CONSTRAINT fk_fufe_feat FOREIGN KEY (fk_feature) REFERENCES feature (id),
  CONSTRAINT fk_fufe_role FOREIGN KEY (fk_role) REFERENCES role (id)
);

CREATE TABLE user_function (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_user INT(12) NOT NULL,
  fk_function INT(12) NOT NULL,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_func (fk_user, fk_function),
  CONSTRAINT fk_usfc_uscr FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_usfc_user FOREIGN KEY (fk_user) REFERENCES user (id),
  CONSTRAINT fk_usfc_func FOREIGN KEY (fk_function) REFERENCES function (id)
);

CREATE TABLE user_role (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_user INT(12) NOT NULL,
  fk_role INT(12) NOT NULL,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role (fk_user, fk_role),
  CONSTRAINT fk_usrl_uscr FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_usrl_user FOREIGN KEY (fk_user) REFERENCES user (id),
  CONSTRAINT fk_usrl_func FOREIGN KEY (fk_role) REFERENCES role (id)
);

CREATE TABLE user_group (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_user INT(12) NOT NULL,
  fk_groupment INT(12) NOT NULL,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_grou (fk_user, fk_groupment),
  CONSTRAINT fk_usgr_uscr FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_usgr_user FOREIGN KEY (fk_user) REFERENCES user (id),
  CONSTRAINT fk_usgr_grou FOREIGN KEY (fk_groupment) REFERENCES groupment (id)
);

alter table function add constraint UNIQUE KEY uk_function (name);

-- Aplica��es
CREATE TABLE application (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  host varchar(180) NOT NULL,
  name varchar(180) NOT NULL,
  password_service varchar(300) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_appl_user FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT UNIQUE KEY uk_nm_application (name)
);

-- Paths com Autorizacao Requerida
CREATE TABLE application_path (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_application INT(12) not null,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  description varchar(300) NULL,
  path varchar(220) NOT NULL,
  http_method int(2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_appt_user FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_appt_appl FOREIGN KEY (fk_application) REFERENCES application (id),
  CONSTRAINT UNIQUE KEY uk_pt_application (path,http_method)
);

-- Autoriza��es para os Paths
CREATE TABLE path_role (
  id INT(12) NOT NULL AUTO_INCREMENT,
  fk_application_path INT(12) not null,
  fk_role INT(12) not null,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) null,
  description varchar(300) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_ptro_user FOREIGN KEY (fk_user_creation) REFERENCES user (id),
  CONSTRAINT fk_ptro_appt FOREIGN KEY (fk_application_path) REFERENCES application_path (id),
  CONSTRAINT fk_ptro_role FOREIGN KEY (fk_role) REFERENCES role (id),
  CONSTRAINT UNIQUE KEY uk_apro_path_role (fk_application_path, fk_role)
);

CREATE TABLE user_profile (
  fk_user INT(12) NOT NULL,
  user_id varchar(80) NOT NULL,
  first_name varchar(80) NULL,
  last_name varchar(80) NULL,
  image_url varchar(80) NULL,
  PRIMARY KEY (fk_user),
  CONSTRAINT fk_user_uspr FOREIGN KEY (fk_user) REFERENCES user (id)
);

create table UserConnection (
  userId varchar(255) not null,
  providerId varchar(255) not null,
  providerUserId varchar(255),
  rank int not null,
  displayName varchar(255),
  profileUrl varchar(512),
  imageUrl varchar(512),
  accessToken varchar(1024) not null,
  secret varchar(255),
  refreshToken varchar(255),
  expireTime bigint,
  primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);

create table UserProfile (
  userId varchar(255) not null,
  email varchar(255),
  firstName varchar(255),
  lastName varchar(255),
  name  varchar(255),
  username varchar(255),
  primary key (userId));
create unique index UserProfilePK on UserProfile(userId);

alter table user MODIFY cpf varchar(15) NULL;
alter table user MODIFY dt_birth date NULL;
alter table user MODIFY password varchar(300) NULL;


-- Triger para validar a integridade dos dados a serem incluidos na tabela function_feature
DELIMITER $$
create trigger trg_user_function_ins before insert on function_feature 
	for each row
		begin
			declare count_func int(12);
			
			if NEW.fk_feature is not null and NEW.fk_role is not null then
                signal sqlstate '45000' set message_text = 'N�o � permitido informar Funcionalidade e Regra no mesmo registro';
			end if;
				
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_feature = NEW.fk_feature
			and fk_feature is not null;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45001' set message_text = 'J� existe registro para o Papel e Funcionalidade Informados';
			end if;

			set count_func = null;
			
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_role = NEW.fk_role
			and fk_role is not null;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45002' set message_text = 'J� existe registro para o Papel e Regra Informados';
			end if;
		end;
$$

-- Triger para validar a integridade dos dados a serem alterados na tabela function_feature
DELIMITER $$
create trigger trg_user_function_upd before update on function_feature 
	for each row
		begin
			declare count_func int(12);
			
			if NEW.fk_feature is not null and NEW.fk_role is not null then
                signal sqlstate '45000' set message_text = 'N�o � permitido informar Funcionalidade e Regra no mesmo registro';
			end if;
				
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_feature = NEW.fk_feature
			and fk_feature is not null
            and id <> NEW.id;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45001' set message_text = 'J� existe registro para o Papel e Funcionalidade Informados';
			end if;

			set count_func = null;
			
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_role = NEW.fk_role
			and fk_role is not null
            and id <> NEW.id;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45002' set message_text = 'J� existe registro para o Papel e Regra Informados';
			end if;
		end;
$$

alter table agilize_security.application add column active bit not null default true;
alter table agilize_security.application add column flg_environment_pay_prod bit not null default false;
alter table agilize_security.application add column merchant_order_id varchar(300) null;
alter table agilize_security.application add column merchant_order_key varchar(300) null;
alter table agilize_security.application add column payment_url_return varchar(300) null;

alter table agilize_security.application add column paths_free varchar(1000) null;
alter table agilize_security.application add column url_validation_pre_payment varchar(200) null;
alter table agilize_security.application add column url_process_pos_payment varchar(200) null;

alter table agilize_security.application add column fk_background_image int(12) null;
alter table agilize_security.application add column title_application varchar(180) null;










CREATE DATABASE service_agilize;
GRANT ALL ON service_agilize.* TO 'elastic_2'@'localhost';
use service_agilize;

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


CREATE TABLE entity (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) not null,
  name varchar(120) NOT NULL,
  description varchar(300) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_entty_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id)
);

CREATE TABLE service_agilize.field (
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

alter table service_agilize.field add column description varchar(300) NULL;

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

-- Tabela que ir� guardar qual(is) aplicacoes determinada entidade pertence
CREATE TABLE entity_application (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null default now(),
  fk_user_creation INT(12) not null,
  fk_entity INT(12) not null,
  fk_application INT(12) not null,
  PRIMARY KEY (id),
  CONSTRAINT fk_entapp_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_entapp_entit FOREIGN KEY (fk_entity) REFERENCES entity (id),
  CONSTRAINT fk_entapp_applic FOREIGN KEY (fk_application) REFERENCES agilize_security.application (id),
  CONSTRAINT UNIQUE KEY uk_ent_app (fk_entity, fk_application)
);









CREATE DATABASE payment_agilize;
GRANT ALL ON payment_agilize.* TO 'elastic_2'@'localhost';
use payment_agilize;

CREATE TABLE payment_agilize.payment (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) null,
  fk_application INT(12) not null,
  amount decimal(10,2) not null,
  customer_name varchar(300) not null,
  type_card int(2) not null,
  card_number varchar(20) not null,
  name_at_card varchar(30) not null,
  month_expiration_date_card int(2) not null,
  year_expiration_date_card int(4) not null,
  security_code_card varchar(5) not null,
  brand int(2) not null,
  merchant_order_id varchar(50) not null,
  nsu varchar(20) null,
  tid varchar(40) null,
  authorization_code varchar(300) null,
  payment_id varchar(40) null,
  eci varchar(2) null,
  status int(2) not null,
  debit_url_return varchar(300) null,
  PRIMARY KEY (id),
  CONSTRAINT fk_paym_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_paym_appl FOREIGN KEY (fk_application) REFERENCES agilize_security.application (id)
);

CREATE TABLE payment_agilize.historic_payment (
  id INT(12) NOT NULL AUTO_INCREMENT,
  dt_creation timestamp not null,
  fk_user_creation INT(12) null,
  fk_application INT(12) not null,
  fk_payment INT(12) not null,
  amount decimal(10,2) not null,
  customer_name varchar(300) not null,
  type_card int(2) not null,
  card_number varchar(20) not null,
  name_at_card varchar(30) not null,
  month_expiration_date_card int(2) not null,
  year_expiration_date_card int(4) not null,
  security_code_card varchar(5) not null,
  brand int(2) not null,
  merchant_order_id varchar(50) not null,
  nsu varchar(20) null,
  tid varchar(40) null,
  authorization_code varchar(300) null,
  payment_id varchar(40) null,
  eci varchar(2) null,
  status int(2) not null,
  debit_url_return varchar(300) null,
  PRIMARY KEY (id),
  CONSTRAINT fk_hist_user FOREIGN KEY (fk_user_creation) REFERENCES agilize_security.user (id),
  CONSTRAINT fk_hist_appl FOREIGN KEY (fk_application) REFERENCES agilize_security.application (id),
  CONSTRAINT fk_hist_paym FOREIGN KEY (fk_payment) REFERENCES payment (id)
);








create database file_agilize;
FLUSH PRIVILEGES;
SET SQL_SAFE_UPDATES = 0;
CREATE USER 'elastic_2'@'localhost' IDENTIFIED BY '';
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





use agilize_security;

insert into user 
(dt_creation, fk_user_creation, name, cpf, dt_birth, password, username, email)
values
(STR_TO_DATE('12/31/2011', '%m/%d/%Y'), null, 'Administrador da Ferramenta', '13005553787', STR_TO_DATE('10/31/2001', '%m/%d/%Y'), '2025bbb88f44dd9c7f496a5bc3e5fbb8cb823202b5159bea12745dfbfb84d897', '13005553787', null);

insert into agilize_security.application
(host, name, password_service)
values
('http://localhost:8086/agilize', 'ELASTICCODE', 'f96d88d1494817750c5577215f85e2f4b645fb23e07eb476b9deee819f29127e');

insert into agilize_security.application
(host, name, password_service, merchant_order_id, merchant_order_key)
values
('http://localhost:8088/agilize/merci', 'PAYMENT', 'ersoqkscmatri82a9IJJU982ssfesnmcnndjesfessncm3D9D9D0F0iiq2sd7jHX', 'bfaf470d-b7e6-4595-adf6-9c24e3de8d13', 'VZFBACSEHZMXADMYNLZPZABKTKAKGOZDIDNMWKBZ');

insert into agilize_security.application
(host, name, password_service, merchant_order_id, merchant_order_key)
values
('http://localhost:8085/agilize/isobrou', 'ISOBROU', 'ws873UJ7jh*($tFyj&7798203293jshmnxwJ7H)0OlkjqamndesU81I3kMj9433K', 'bfaf470d-b7e6-4595-adf6-9c24e3de8d13', 'VZFBACSEHZMXADMYNLZPZABKTKAKGOZDIDNMWKBZ');

insert into agilize_security.application
(host, name, password_service, merchant_order_id, merchant_order_key)
values
('http://localhost:8087/agilize/fileserver', 'FILESERVER', '12wsmvkmLD03Llkdmco84dm9k120Io01Okmsjhk84fks9mv799skc8jGG09282J8J6G0kd', 'bfaf470d-b7e6-4595-adf6-9c24e3de8d13', 'VZFBACSEHZMXADMYNLZPZABKTKAKGOZDIDNMWKBZ');

insert into agilize_security.application
(host, name, password_service, merchant_order_id, merchant_order_key)
values
('http://localhost:8089/agilize/async', 'ASYNCROUNOUS', 'KSI98kmuybIWn8102N9n9N9UIS910J23hj8g0a(SLiux)JNhh721aAbbbuUU19432l0lI1I1', 'bfaf470d-b7e6-4595-adf6-9c24e3de8d13', 'VZFBACSEHZMXADMYNLZPZABKTKAKGOZDIDNMWKBZ');

insert into agilize_security.application
(host, name, password_service, merchant_order_id, merchant_order_key)
values
('http://localhost:9082/agilize/flow', 'WORKFLOW', 'jfskfjku8J0Loi394$%UinbU$0o0LjkwIJLnbB7y989@1!kjsiKICUmeiL0KJU$dR%', 'bfaf470d-b7e6-4595-adf6-9c24e3de8d13', 'VZFBACSEHZMXADMYNLZPZABKTKAKGOZDIDNMWKBZ');



update application
set merchant_order_id='52ab881a-b9d1-4713-8988-e8c6c39f36f8',
merchant_order_key='HfwVf7h4ZJsebExGk6v9XXj0iMqfh44GKJFmwFIL',
payment_url_return='www.elasticcode.com.br/agilize/merci',
flg_environment_pay_prod=1,
paths_free='store;customer',
host = 'http://localhost:8085/agilize/isobrou',
url_validation_pre_payment='http://localhost:8085/agilize/isobrou/customer/validate/preOrder',
url_process_pos_payment='http://localhost:8085/agilize/isobrou/customer/process/posPayment'
where name = 'ISOBROU';


insert into role
(name)
values
('ROLE_FILTER_ENTITY');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Pesquisa de Entidades por Filtro', '/entity/filter', 0);

insert into path_role
(fk_application_path, fk_role, description)
values
(1, 1, 'Permiss�o para pesquisa de Entidades');

insert into role
(name)
values
('ROLE_CRUD_ENTITY');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Inclus�o de Entidades', 'entity', 2);

insert into path_role
(fk_application_path, fk_role, description)
values
(2, 2, 'Permiss�o para Inclus�o de Entidades');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Altera��o de Entidades', 'entity/{id}', 3);

insert into path_role
(fk_application_path, fk_role, description)
values
(3, 2, 'Permiss�o para Altera��o de Entidades');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Exclus�o de Entidades', 'entity/{id}', 5);

insert into path_role
(fk_application_path, fk_role, description)
values
(4, 2, 'Permiss�o para Exclus�o de Entidades');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Pesquisa por PK de Entidades', 'entity/{id}', 0);

insert into path_role
(fk_application_path, fk_role, description)
values
(5, 1, 'Permiss�o para Exclus�o de Entidades');


CREATE TABLE uf(
	id INT(12) NOT NULL AUTO_INCREMENT,
    dt_creation timestamp null,
    fk_user_creation INT(12) NULL,
    `name` varchar(80) NOT NULL,
    sigla varchar(2) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_user_creation FOREIGN KEY (fk_user_creation) REFERENCES user (id)
);


INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Acre', 'AC');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Alagoas', 'AL');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Amapá', 'AP');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Amazonas', 'AM');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Bahia', 'BA');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Ceará', 'CE');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Distrito Federal', 'DF');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Espírito Santo', 'ES');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Goiás', 'GO');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Maranhão', 'MA');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Mato Grosso', 'MT');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Mato Grosso do Sul', 'MS');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Minas Gerais', 'MG');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Pará', 'PA');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Paraíba', 'PB');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Paraná', 'PR');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Pernambuco', 'PE');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Piauí', 'PI');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Rio de Janeiro', 'RJ');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Rio Grande do Norte', 'RN');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Rio Grande do Sul', 'RS');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Rondônia', 'RO');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Roraima', 'RR');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Santa Catarina', 'SC');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'São Paulo', 'SP');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Sergipe', 'SE');
INSERT INTO `agilize_security`.`uf` (`dt_creation`,`name`, `sigla`) VALUES (NOW(),'Tocantins', 'TO');