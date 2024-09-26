package service;

import config.ConnectionFactory;
import dao.ContaDAO;
import dto.conta.DadosAberturaConta;
import entity.Conta;
import exception.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class ContaService {

    private final ConnectionFactory connection;

    /// Construtor da classe `ContaService`.
    ///
    /// Inicializa o serviço de contas com uma fábrica de conexões ao banco de dados.
    /// A `ConnectionFactory` será usada para criar novas conexões durante as operações.
    public ContaService() {
        this.connection = new ConnectionFactory();
    }

    /// Lista todas as contas abertas.
    ///
    /// Este metodo retorna um conjunto de todas as contas abertas no sistema,
    /// utilizando o `ContaDAO` para realizar a consulta no banco de dados.
    ///
    /// @return Um conjunto de contas abertas.
    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).listar();
    }

    /// Consulta o saldo de uma conta específica.
    ///
    /// Dado o número da conta, este metodo busca a conta no banco de dados e
    /// retorna o saldo disponível.
    ///
    /// @param numeroDaConta O número da conta a ser consultada.
    /// @return O saldo da conta.
    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    /// Abre uma nova conta no sistema.
    ///
    /// Este metodo cria uma nova conta e insere seus dados no banco de dados.
    /// Utiliza uma transação para garantir que a criação do cliente e da conta
    /// ocorra de forma atômica. Caso algum erro ocorra durante o processo,
    /// a transação é desfeita.
    ///
    /// @param dadosDaConta Os dados necessários para abertura da conta, incluindo informações do cliente.
    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            // Chama o método salvar da classe ContaDAO
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

    /// Encerra uma conta existente.
    ///
    /// Este metodo encerra uma conta desde que ela não tenha saldo pendente.
    /// O processo é transacional, garantindo que, em caso de falha, o estado
    /// do banco de dados não seja alterado. Caso a conta tenha saldo, uma exceção
    /// será lançada.
    ///
    /// @param numeroDaConta O número da conta a ser encerrada.
    public void encerrar(Integer numeroDaConta) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            var conta = buscarContaPorNumero(numeroDaConta);
            if (conta.possuiSaldo()) throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");

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

    /// Realiza o saque em uma conta.
    ///
    /// Este metodo permite realizar o saque de um valor específico de uma conta,
    /// verificando se o valor é válido e se há saldo suficiente na conta.
    /// Em caso de erro, a transação é desfeita.
    ///
    /// @param numeroDaConta O número da conta de onde o valor será sacado.
    /// @param valor O valor a ser sacado.
    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            Conta conta = buscarContaPorNumero(numeroDaConta);

            if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
            if (valor.compareTo(conta.getSaldo()) > 0) throw new RegraDeNegocioException("Saldo insuficiente!");

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

    /// Realiza o depósito em uma conta.
    ///
    /// Este metodo permite realizar o depósito de um valor específico em uma conta.
    /// Se o valor for inválido, uma exceção será lançada e a transação será desfeita.
    ///
    /// @param numeroDaConta O número da conta onde o valor será depositado.
    /// @param valor O valor a ser depositado.
    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        Connection conn = connection.recuperarConexao();
        try {
            conn.setAutoCommit(false); // Inicia a transação

            if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new RegraDeNegocioException("Valor do depósito deve ser superior a zero!");

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

    /// Realiza a transferência entre contas.
    ///
    /// Este metodo realiza a transferência de um valor de uma conta de origem
    /// para uma conta de destino. Ele valida se ambas as contas existem, se o
    /// saldo da conta de origem é suficiente e se as contas são diferentes.
    /// A transação é controlada para garantir a atomicidade.
    ///
    /// @param numeroDaContaOrigem O número da conta de onde o valor será transferido.
    /// @param numeroDaContaDestino O número da conta para onde o valor será transferido.
    /// @param valor O valor a ser transferido.
    public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
        Connection conn = connection.recuperarConexao();

        try {
            conn.setAutoCommit(false); // Inicia a transação

            // Validações de valor
            if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new RegraDeNegocioException("Valor da transferência deve ser superior a zero!");

            // Verifica se as contas são diferentes
            if (numeroDaContaOrigem.equals(numeroDaContaDestino)) throw new RegraDeNegocioException("A conta de origem e destino não podem ser a mesma!");

            // Buscar e validar se ambas as contas existem
            var contaOrigem = buscarContaPorNumero(numeroDaContaOrigem); // Se a conta não existir, lança uma exceção
            var contaDestino = buscarContaPorNumero(numeroDaContaDestino); // Se a conta não existir, lança uma exceção

            // Verificar saldo da conta de origem
            if (valor.compareTo(contaOrigem.getSaldo()) > 0) throw new RegraDeNegocioException("Saldo insuficiente na conta de origem!");

            // Realizar transferência
            ContaDAO contaDAO = new ContaDAO(conn);
            contaDAO.sacar(numeroDaContaOrigem, valor);   // Saca o valor da conta de origem
            contaDAO.depositar(numeroDaContaDestino, valor); // Deposita o valor na conta de destino

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

    /// Busca uma conta pelo número.
    ///
    /// Este metodo busca uma conta no banco de dados utilizando o número informado.
    /// Caso a conta não seja encontrada, uma exceção será lançada.
    ///
    /// @param numero O número da conta a ser buscada.
    /// @return A conta correspondente ao número informado.
    private Conta buscarContaPorNumero(Integer numero) {
        Connection conn = connection.recuperarConexao();
        Conta conta = new ContaDAO(conn).listarPorNumero(numero);
        if (conta != null) {
            return conta;
        } else {
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
        }
    }
}
