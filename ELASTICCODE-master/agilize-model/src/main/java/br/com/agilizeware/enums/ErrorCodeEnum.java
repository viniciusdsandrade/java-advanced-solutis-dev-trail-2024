package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ErrorCodeEnumDeserializer.class)
public enum ErrorCodeEnum implements IEnum<Integer> {
	
	DEFAULT_EXCEPTION(1001, "Erro Gen�rico", "msg.error.genneric"),
	REQUIREDS_FIELD(1002, "Campos Obrigatório Não Preenchidos", "msg.error.requireds.field"),
	REQUIRED_FIELD(1003, "Campo Obrigatório", "msg.error.required.field"),
	EMPTY_LIST(1004, "Lista Vazia", "msg.error.required.list"),
	EXPECTED_HTTP_REQUEST(1005, "Expecting a HTTP request", "msg.error.expeted.http.request"),
	BAD_CREDNTIALS(1006, "Nome N�o Localizado", "msg.error.bad.credentials"),
	WRONG_PASSWORD(1007, "Senha Inv�lida", "msg.error.wrong.password"),
	USER_DISABLED(1008, "Usu�rio Desabilitado", "msg.error.user.disabled"),
	CONFIG_WRONG_AMBIENT(1009, "Ambiente configurado de forma errada", "msg.error.config.wrong.ambient"),
	EXPIRED_ACCOUNT(1010, "Conta Expirada", "msg.error.expired.account"),
	LOCKED_ACCOUNT(1011, "Conta Bloqueada", "msg.error.locked.account"),
	INATIVE_ACCOUNT(1012, "Conta Inativa", "msg.error.inative.account"),
	PATH_NULL(1013, "Path N�o Informado", "msg.error.path.null"),
	APPLICATION_NULL(1014, "Aplica��o N�o Informada", "msg.error.application.null"),
	UNAUTHORIZED_PATH(1015, "Sem permiss�o de acesso ao path", "msg.error.unauthorized.path"),
	PASSWORD_ENCODER_EXCEPTION(1016, "Erro ao inicializar o password encoder", "msg.error.password.encoder"),
	UNAUTHORIZED_ACCESS(1017, "Sem permiss�o de acesso", "msg.error.unauthorized.access"),
	APPLICATION_NOT_FOUND(1018, "Aplica��o N�o Encontrada", "msg.error.application.not.found"),
	SERVICE_NOT_PROCESS(1019, "M�todo chamado sem retorno", "msg.error.service.not.process"),
	ERROR_JSON(1020, "Mapeamento Json com Problemas", "msg.error.json"),
	ERROR_FILTER(1021, "Falhas no processamento da pesquisa", "msg.error.filter"),
	ENUM_NOT_FOUND(1022, "Classe Enum Informada n�o encontrada", "msg.error.enum.not.found"),
	ENUM_INSTANTIATION(1023, "Classe Enum Informada n�o instanciada", "msg.error.enum.not.instantiation"),
	ID_NOT_FOUND(1024, "Chave de Registro Informada n�o encontrada", "msg.error.id.not.found"),
	ERROR_CONNECT_CIELLO_INIT_PAYMENT(1025, "Erro ao se conectar com o servidor da ciello para iniciar transa��o", "msg.error.connect.ciello.init.payment"),
	ERROR_STATUS_OPERATION_CIELLO_INVALID(1026, "Status de iniciar de transa��o retornado inv�lido", "msg.error.status.operation.ciello.invalid"),
	ERROR_STATUS_CAPTURE_CIELLO_INVALID(1027, "Status de captura de transa��o retornado inv�lido", "msg.error.status.capture.ciello.invalid"),
	ERROR_BD(1028, "Sql Error", "msg.error.sql"),
	ERROR_CONNECT_CIELLO_CANCEL_PAYMENT(1029, "Erro ao se conectar com o servidor da ciello para cancelar uma transa��o", "msg.error.connect.ciello.cancel.payment"),
	INVALID_FIELD(1030, "Campo Inv�lido", "msg.error.invalid.field"),
	INVALID_AMOUNT_SALE(1031, "Valor da Compra n�o pode ser inferior a 5 Reais", "msg.error.invalid.amount.sale"),
	INVALID_TOKEN(1032, "Token Inv�lido", "msg.error.invalid.token"),
	ERROR_PAGMTO_DENIED(1033, "Pagamento n�o permitido", "msg.error.pagmto.denied"),
	ERROR_PAGMTO_VOIDED(1034, "Pagamento Cancelado", "msg.error.pagmto.voided"),
	ERROR_PAGMTO_REFUNDED(1035, "Pagamento Cancelado/Estornado", "msg.error.pagmto.refunded"),
	ERROR_PAGMTO_PENDING(1036, "Pagamento N�o Processado pela Institui��o Financeira", "msg.error.pagmto.pending"),
	ERROR_READ_TMP_FILE(1037, "Erro ao ler arquivo tempor�rio", "msg.error.read.tmp.file"),
	INVALID_FORMAT_FIELD(1038, "Formato Inv�lido de Campo", "msg.error.invalid.format.field"),
	ERROR_DT_BEFORE_TODAY(1039, "Data menor que a data atual", "msg.error.dt.before.today"),
	INVALID_LENGTH_FIELD(1040, "Tamanho de Campo Inválido", "msg.error.length.field"),
	ERROR_SAVE_TMP_FILE(1041, "Erro ao salvar arquivo tempor�rio", "msg.error.save.tmp.file"),
	ERROR_CONECTION(1042, "Conex�o n�o estabelecida com o servidor", "msg.error.connection"),
	FILE_EMPTY(1043, "Arquivo Vazio", "msg.error.file.empty"),
	ERROR_CRUD_FILE(1044, "Erro na opera��o com Arquivos", "msg.error.crud.file"),
	ERROR_READ_FILE(1045, "Erro ao ler arquivo", "msg.error.read.file"),
	ERROR_INIT_FILE(1046, "Erro ao inicializar arquivo", "msg.error.init.file"),
	ERROR_SAME_USER(1047, "Erro ao salvar um usuário: mesmo cpf/email registrado", "msg.error.same.user"),
	ERROR_FIELDS(1048, "Problemas com Campos", "msg.error.fields.list"),
	ERROR_NOR_FIELDS(1049, "Ao menos um dos campos são obrigatório", "msg.error.nor.fields"),
	ERROR_LOCATION_SEARCH_NOT_FOUND(1050, "Localização de Endereço para pesquisa não informado", "msg.error.location.search.not.found"),
	ERROR_PRODUCT_NOT_SUFFICIENT(1051, "Quantidade de produto em estoque não suficiente", "msg.error.product.not.sufficient"),
	ERROR_VALIDATIONS_PRE_PAYMENT(1052, "Ocorreram problemas de validação do pedido antes de efetuar o pagamento", "msg.error.validations.pre.payment"),
	ERROR_PRODUCT_PRICE(1053, "Valor informado do produto menor que o cadastrado", "msg.error.product.price"),
	KEYS_DUPLICATEDS(1054, "Chaves informadas duplicadas", "msg.error.keys.duplicateds"),
	ERROR_DELETE_FILE(1055, "Erro ao deletar arquivo", "msg.error.delete.file"),
	ERROR_REGISTER_OPERATION(1056, "Erro ao relizar operação com o registro", "msg.error.register.operation"),
	ERROR_PARSE_DATE_EXCEPTION(1057, "Erro com formato de datas", "msg.error.parse.date.exception"),
	ERROR_MONGO_CONECTION(1058, "Conexão não estabelecida com o servidor de Banco de Dados não Relacional", "msg.error.mongo.connection"),
	;
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private ErrorCodeEnum(Integer codigo, String descricao, String label) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.label = label;
	}
	
	public Integer getId() {
		return codigo;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return descricao;
	}


	public static ErrorCodeEnum findByLabel(String label) {
		for (ErrorCodeEnum userType : ErrorCodeEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static ErrorCodeEnum findByCode(Integer code) {
		ErrorCodeEnum[] array = ErrorCodeEnum.values();
		for (ErrorCodeEnum codeEnum : array) {
			if(codeEnum.getId().equals(code)) {
				return codeEnum; 
			}
		}
		return null;
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}

}
