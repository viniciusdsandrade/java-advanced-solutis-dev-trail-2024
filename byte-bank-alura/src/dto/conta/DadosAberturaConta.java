package dto.conta;

import dto.cliente.DadosCadastroCliente;

/// O record `DadosAberturaConta` encapsula os dados necessários para abrir uma nova conta.
///
/// Este record armazena o número da conta e os dados do cliente, sendo utilizado durante
/// o processo de abertura de contas no sistema bancário.
public record DadosAberturaConta(Integer numero, DadosCadastroCliente dadosCliente) {
}
