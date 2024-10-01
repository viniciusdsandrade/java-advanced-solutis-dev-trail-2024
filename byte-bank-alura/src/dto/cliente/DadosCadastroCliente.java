package dto.cliente;

/// O record 'DadosCadastroCliente' encapsula as informações necessárias para o cadastro de um cliente.
///
/// Este record armazena o nome, CPF e email de um cliente, simplificando o transporte desses dados
/// dentro do sistema bancário.
public record DadosCadastroCliente(String nome, String cpf, String email) {
}
