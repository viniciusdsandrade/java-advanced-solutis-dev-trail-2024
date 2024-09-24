package conta;


import cliente.Cliente;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

public class Conta {

    private Integer numero;
    private BigDecimal saldo;
    private Cliente titular;

    public Conta(Integer numero, Cliente titular) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = ZERO;
    }

    public Conta(Integer numero, BigDecimal saldo, Cliente titular) {
        this.numero = numero;
        this.saldo = saldo;
        this.titular = titular;
    }

    public boolean possuiSaldo() {
        return this.saldo.compareTo(ZERO) != 0;
    }

    public void sacar(BigDecimal valor) {
        this.saldo = this.saldo.subtract(valor);
    }

    public void depositar(BigDecimal valor) {
        this.saldo = this.saldo.add(valor);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Conta that = (Conta) o;

        return numero.equals(that.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return "{\n" +
               "    \"numero\":\"" + numero + "\",\n" +
               "    \"saldo\":" + saldo + ",\n" +
               "    \"titular\":" + titular + "\n" +
               "}";
    }
}
