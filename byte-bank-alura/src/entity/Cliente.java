package entity;

import dto.cliente.DadosCadastroCliente;

import java.util.Objects;

/// A classe `Cliente` representa um cliente em um sistema bancário.
///
/// Ela armazena informações como nome, CPF e email do cliente, além de fornecer
/// métodos para manipulação desses dados e para clonagem do objeto.
public class Cliente implements Cloneable {

    private Long id;
    private String nome;
    private String cpf;
    private String email;

    /// Construtor da classe `Cliente` que recebe os dados de um cliente.
    ///
    /// @param dados Dados para criação do cliente, como nome, CPF e email.
    public Cliente(DadosCadastroCliente dados) {
        this.nome = dados.nome();
        this.cpf = dados.cpf();
        this.email = dados.email();
    }

    /// Construtor de cópia para a classe `Cliente`.
    ///
    /// Cria uma cópia de um objeto `Cliente` existente, copiando todas as suas propriedades.
    ///
    /// @param outroCliente O cliente a ser copiado.
    public Cliente(Cliente outroCliente) {
        this.id = outroCliente.id;
        this.nome = outroCliente.nome;
        this.cpf = outroCliente.cpf;
        this.email = outroCliente.email;
    }

    /// Clona um objeto `Cliente`.
    ///
    /// Este metodo retorna uma cópia profunda do objeto `Cliente`, usando o construtor de cópia.
    /// Se houver alguma falha durante o processo de clonagem, o objeto original é retornado.
    ///
    /// @return Um clone do objeto `Cliente`.
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Cliente clone() {
        Cliente clone = null;
        try {
            clone = new Cliente(this);
        } catch (Exception ignore) {
        }
        return clone;
    }

    /// Métodos getters e setters para acessar e modificar os atributos do cliente.
    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getCpf() {
        return cpf;
    }
    public String getEmail() {
        return email;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    /// Verifica se dois objetos `Cliente` são iguais, comparando o CPF.
    ///
    /// @param o O objeto a ser comparado com o cliente atual.
    /// @return `true` se os CPFs forem iguais, `false` caso contrário.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Cliente that = (Cliente) o;

        return cpf.equals(that.cpf);
    }

    /// Gera o código hash para o objeto `Cliente` com base no CPF.
    ///
    /// @return O código hash gerado.
    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    /// Converte o objeto `Cliente` em uma string JSON formatada.
    ///
    /// @return Uma string representando o cliente no formato JSON.
    @Override
    public String toString() {
        return "\t{ \n" +
               "    \"nome\":\"" + nome + "\",\n" +
               "    \"cpf\":\"" + cpf + "\",\n" +
               "    \"email\":\"" + email + "\"\n" +
               "\t}";
    }
}
