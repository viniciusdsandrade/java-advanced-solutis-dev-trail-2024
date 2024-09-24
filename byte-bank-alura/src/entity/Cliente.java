package entity;

import dto.cliente.DadosCadastroCliente;

import java.util.Objects;

public class Cliente implements Cloneable {

    private Long id;
    private String nome;
    private String cpf;
    private String email;

    public Cliente(DadosCadastroCliente dados) {
        this.nome = dados.nome();
        this.cpf = dados.cpf();
        this.email = dados.email();
    }

    // Construtor de cópia
    public Cliente(Cliente outroCliente) {
        this.id = outroCliente.id;
        this.nome = outroCliente.nome;
        this.cpf = outroCliente.cpf;
        this.email = outroCliente.email;
    }

    @Override
    public Cliente clone() {
        Cliente clone = null;
        try {
            clone = new Cliente(this);
        } catch (Exception ignore) {
        }
        return clone;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Cliente that = (Cliente) o;

        return cpf.equals(that.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return "\t{ \n" +
               "    \"nome\":\"" + nome + "\",\n" +
               "    \"cpf\":\"" + cpf + "\",\n" +
               "    \"email\":\"" + email + "\"\n" +
               "\t}";
    }
}
