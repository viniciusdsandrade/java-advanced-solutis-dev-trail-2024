package service;

import config.ConnectionFactory;
import dao.ContaDAO;
import dto.conta.DadosAberturaConta;
import entity.Conta;
import exception.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaService {

    private Set<Conta> contas = new HashSet<>();

    private final ConnectionFactory connection;

    public ContaService() {
        this.connection = new ConnectionFactory();
    }

    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            // Chama o metodo salvar da classe ContaDAO
            ContaDAO contaDAO = new ContaDAO(conn);
            contaDAO.salvar(dadosDaConta);

            conn.commit(); // Confirma a transação
        } catch (SQLException e) {
            try {
                conn.rollback(); // Desfaz a transação em caso de erro
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void encerrar(Integer numeroDaConta) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            var conta = buscarContaPorNumero(numeroDaConta);
            if (conta.possuiSaldo()) {
                throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
            }

            // Chama o metodo encerrar da classe ContaDAO
            ContaDAO contaDAO = new ContaDAO(conn);
            contaDAO.encerrar(numeroDaConta);

            conn.commit(); // Confirma a transação
        } catch (SQLException | RegraDeNegocioException e) {
            try {
                conn.rollback(); // Desfaz a transação em caso de erro
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            var conta = buscarContaPorNumero(numeroDaConta);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
            }

            if (valor.compareTo(conta.getSaldo()) > 0) {
                throw new RegraDeNegocioException("Saldo insuficiente!");
            }

            // Chama o metodo sacar da classe ContaDAO
            ContaDAO contaDAO = new ContaDAO(conn);
            contaDAO.sacar(numeroDaConta, valor);

            conn.commit(); // Confirma a transação
        } catch (RegraDeNegocioException | SQLException e) {
            try {
                conn.rollback(); // Desfaz a transação em caso de erro
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }


    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraDeNegocioException("Valor do depósito deve ser superior a zero!");
            }

            // Chama o metodo depositar da classe ContaDAO
            ContaDAO contaDAO = new ContaDAO(conn);
            contaDAO.depositar(numeroDaConta, valor);

            conn.commit(); // Confirma a transação
        } catch (SQLException | RegraDeNegocioException e) {
            try {
                conn.rollback(); // Desfaz a transação em caso de erro
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {

            }
        }
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Connection conn = connection.recuperarConexao();
        Conta conta = new ContaDAO(conn).listarPorNumero(numero);
        if (conta != null) {
            return conta;
        } else {
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
        }
    }


    public Set<Conta> getContas() {
        return contas;
    }

    public ConnectionFactory getConnection() {
        return connection;
    }
}