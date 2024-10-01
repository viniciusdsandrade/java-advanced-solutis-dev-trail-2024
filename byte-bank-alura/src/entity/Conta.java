package entity;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;
import static util.ShallowOrDeepCopy.verifyAndCopy;

/// A classe 'Conta' representa uma conta bancária com saldo e um titular.
///
/// Esta classe permite operações de saque, depósito e possui suporte para clonagem de contas,
/// além de verificar o saldo disponível.
public class Conta implements Cloneable {

    private Integer numero;
    private BigDecimal saldo;
    private Cliente titular;

    /// Construtor da classe `Conta` que recebe o número, saldo e o titular da conta.
    ///
    /// @param numero O número da conta.
    /// @param saldo O saldo inicial da conta.
    /// @param titular O titular associado à conta.
    public Conta(Integer numero, BigDecimal saldo, Cliente titular) {
        this.numero = numero;
        this.saldo = saldo;
        this.titular = titular;
    }

    /// Construtor da classe `Conta` que recebe o número e o titular, inicializando o saldo como zero.
    ///
    /// @param numero O número da conta.
    /// @param titular O titular da conta.
    public Conta(Integer numero, Cliente titular) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = ZERO;
    }

    /// Construtor de cópia para a classe 'Conta'.
    ///
    /// Cria uma cópia de uma conta existente, copiando todos os seus atributos, incluindo o titular.
    ///
    /// @param outraConta A conta a ser copiada.
    public Conta(Conta outraConta) {
        this.numero = outraConta.numero;
        this.saldo = outraConta.saldo;
        this.titular = (Cliente) verifyAndCopy(outraConta.titular);
    }

    /// Verifica se a conta possui saldo disponível.
    ///
    /// @return `true` se o saldo for diferente de zero, `false` caso contrário.
    public boolean possuiSaldo() {
        return this.saldo.compareTo(ZERO) != 0;
    }

    /// Realiza o saque de um valor da conta.
    ///
    /// Este metodo subtrai o valor do saldo da conta.
    ///
    /// @param valor O valor a ser sacado.
    public void sacar(BigDecimal valor) {
        this.saldo = this.saldo.subtract(valor);
    }

    /// Realiza o depósito de um valor na conta.
    ///
    /// Este metodo adiciona o valor ao saldo da conta.
    ///
    /// @param valor O valor a ser depositado.
    public void depositar(BigDecimal valor) {
        this.saldo = this.saldo.add(valor);
    }

    /// Métodos getters e setters para acessar e modificar os atributos da conta.
    public void setNumero(Integer numero) {
        this.numero = numero;
    }
    public Integer getNumero() {
        return numero;
    }
    public BigDecimal getSaldo() {
        return saldo;
    }
    public Cliente getTitular() {
        return titular;
    }

    /// Clona uma 'Conta'.
    ///
    /// Cria uma cópia da conta atual, incluindo o titular. O titular é copiado profundamente.
    ///
    /// @return Um clone da conta atual.
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Conta clone() {
        Conta clone = null;
        try {
            clone = new Conta(this);
            clone.titular = new Cliente(this.titular);
        } catch (Exception ignore) {
        }
        return clone;
    }

    /// Verifica se duas contas são iguais, comparando o número da conta, saldo e titular.
    ///
    /// @param o O objeto a ser comparado com a conta atual.
    /// @return 'true' se as contas forem iguais, 'false' caso contrário.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Conta that = (Conta) o;

        return this.numero.equals(that.numero)
               && this.saldo.equals(that.saldo)
               && this.titular.equals(that.titular);
    }

    /// Gera o código hash para a conta, baseado no número da conta, saldo e titular.
    ///
    /// @return O código hash gerado.
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;

        hash *= prime + Objects.hashCode(numero);
        hash *= prime + Objects.hashCode(saldo);
        hash *= prime + Objects.hashCode(titular);

        if (hash < 0) hash *= -1;

        return hash;
    }

    /// Converte o objeto 'Conta' em uma string JSON formatada.
    ///
    /// @return Uma string representando a conta no formato JSON.
    @Override
    public String toString() {
        return "{\n" +
               "    \"numero\":\"" + numero + "\",\n" +
               "    \"saldo\":" + saldo + ",\n" +
               "    \"titular\":" + titular + "\n" +
               "}";
    }
}
