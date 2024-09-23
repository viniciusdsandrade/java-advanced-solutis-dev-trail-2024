use agilize_security;

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

-- Associação entre o grupo e o papel
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

-- Associação entre o papel e a regra
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

alter table user add UNIQUE KEY uk_user_username (username);

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

-- Aplicações
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

-- Autorizações para os Paths
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

alter table agilize_security.application add column active bit not null default true;
alter table agilize_security.application add column flg_environment_pay_prod bit not null default false;
alter table agilize_security.application add column merchant_order_id varchar(300) null;
alter table agilize_security.application add column merchant_order_key varchar(300) null;
alter table agilize_security.application add column payment_url_return varchar(300) null;

use agilize_security;

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

insert into user 
(dt_creation, fk_user_creation, name, cpf, dt_birth, password, username, email)
values
(STR_TO_DATE('12/31/2011', '%m/%d/%Y'), null, 'Administrador da Ferramenta', '13005553787', STR_TO_DATE('10/31/2001', '%m/%d/%Y'), '2025bbb88f44dd9c7f496a5bc3e5fbb8cb823202b5159bea12745dfbfb84d897', '13005553787', null);

-- Triger para validar a integridade dos dados a serem incluidos na tabela function_feature
DELIMITER $$
create trigger trg_user_function_ins before insert on function_feature 
	for each row
		begin
			declare count_func int(12);
			
			if NEW.fk_feature is not null and NEW.fk_role is not null then
                signal sqlstate '45000' set message_text = 'Não é permitido informar Funcionalidade e Regra no mesmo registro';
			end if;
				
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_feature = NEW.fk_feature
			and fk_feature is not null;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45001' set message_text = 'Já existe registro para o Papel e Funcionalidade Informados';
			end if;

			set count_func = null;
			
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_role = NEW.fk_role
			and fk_role is not null;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45002' set message_text = 'Já existe registro para o Papel e Regra Informados';
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
                signal sqlstate '45000' set message_text = 'Não é permitido informar Funcionalidade e Regra no mesmo registro';
			end if;
				
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_feature = NEW.fk_feature
			and fk_feature is not null
            and id <> NEW.id;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45001' set message_text = 'Já existe registro para o Papel e Funcionalidade Informados';
			end if;

			set count_func = null;
			
			select count(id) into count_func from function_feature
			where fk_function = NEW.fk_function
			and fk_role = NEW.fk_role
			and fk_role is not null
            and id <> NEW.id;
			
			if count_func is not null and count_func > 0 then
                signal sqlstate '45002' set message_text = 'Já existe registro para o Papel e Regra Informados';
			end if;
		end;
$$

CREATE TABLE agilize_security.user_profile (
  fk_user INT(12) NOT NULL,
  user_id varchar(80) NOT NULL,
  first_name varchar(80) NULL,
  last_name varchar(80) NULL,
  image_url varchar(80) NULL,
  PRIMARY KEY (fk_user),
  CONSTRAINT fk_user_uspr FOREIGN KEY (fk_user) REFERENCES user (id)
);

use agilize_security;
alter table user MODIFY cpf varchar(15) NULL;
alter table user MODIFY dt_birth date NULL;
alter table user MODIFY password varchar(300) NULL;

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
(1, 1, 'Permissão para pesquisa de Entidades');

insert into role
(name)
values
('ROLE_CRUD_ENTITY');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Inclusão de Entidades', 'entity', 2);

insert into path_role
(fk_application_path, fk_role, description)
values
(2, 2, 'Permissão para Inclusão de Entidades');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Alteração de Entidades', 'entity/{id}', 3);

insert into path_role
(fk_application_path, fk_role, description)
values
(3, 2, 'Permissão para Alteração de Entidades');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Exclusão de Entidades', 'entity/{id}', 5);

insert into path_role
(fk_application_path, fk_role, description)
values
(4, 2, 'Permissão para Exclusão de Entidades');

insert into application_path
(fk_application, description, path, http_method)
values
(1, 'Pesquisa por PK de Entidades', 'entity/{id}', 0);

insert into path_role
(fk_application_path, fk_role, description)
values
(5, 1, 'Permissão para Exclusão de Entidades');
