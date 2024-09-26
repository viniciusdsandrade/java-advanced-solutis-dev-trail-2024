package dao;

import entity.Cliente;
import dto.conta.DadosAberturaConta;
import dto.cliente.DadosCadastroCliente;
import entity.Conta;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private final Connection conn;

    /// Construtor da classe `ContaDAO`.
    ///
    /// Este metodo constrói um novo objeto `ContaDAO`, recebendo uma conexão
    /// com o banco de dados para realizar operações de manipulação de contas.
    ///
    /// @param connection Conexão com o banco de dados a ser usada para as operações.
    public ContaDAO(Connection connection) {
        this.conn = connection;
    }

    /// Salva uma nova conta no banco de dados.
    ///
    /// Este metodo recebe os dados de abertura da conta, que incluem as informações
    /// do cliente e da conta. Ele realiza a inserção do cliente e, em seguida, da
    /// conta associada a esse cliente no banco de dados.
    ///
    /// @param dadosDaConta Dados da conta a ser criada, incluindo os dados do cliente.
    public void salvar(DadosAberturaConta dadosDaConta) {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);

        try {
            // Inserir o cliente no banco de dados
            String sqlCliente = "INSERT INTO cliente (nome, cpf, email) VALUES (?, ?, ?)";
            PreparedStatement psCliente = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS);
            psCliente.setString(1, cliente.getNome());
            psCliente.setString(2, cliente.getCpf());
            psCliente.setString(3, cliente.getEmail());
            psCliente.execute();

            // Recuperar o ID do cliente recém-inserido
            ResultSet rsCliente = psCliente.getGeneratedKeys();
            if (rsCliente.next()) {
                cliente.setId(rsCliente.getLong(1));
            }

            // Inserir a conta associada ao cliente
            String sqlConta = "INSERT INTO conta (numero, saldo, titular_id) VALUES (?, ?, ?)";
            PreparedStatement psConta = conn.prepareStatement(sqlConta);
            psConta.setInt(1, conta.getNumero());
            psConta.setBigDecimal(2, BigDecimal.ZERO);  // Saldo inicial
            psConta.setLong(3, cliente.getId());
            psConta.execute();

            psConta.close();
            psCliente.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /// Lista todas as contas cadastradas no banco de dados.
    ///
    /// Este metodo consulta todas as contas existentes no banco de dados,
    /// retornando uma lista com os dados de cada conta e seu respectivo titular.
    ///
    /// @return Um conjunto de contas cadastradas no banco de dados.
    public Set<Conta> listar() {
        PreparedStatement ps;
        ResultSet resultSet;
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT c.numero, c.saldo, cl.nome, cl.cpf, cl.email " +
                     "FROM conta c " +
                     "JOIN cliente cl ON c.titular_id = cl.id;";

        try {
            ps = conn.prepareStatement(sql);
            resultSet = ps.executeQuery();

            // Itera sobre os resultados e cria as contas
            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente =
                        new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                contas.add(new Conta(numero, saldo, cliente));
            }
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    /// Busca uma conta pelo número no banco de dados.
    ///
    /// Este metodo procura uma conta no banco de dados pelo seu número. Caso a conta seja encontrada,
    /// retorna os detalhes da conta e do titular. Caso contrário, retorna `null`.
    ///
    /// @param numero O número da conta a ser buscada.
    /// @return A conta correspondente ao número informado ou `null` se não encontrada.
    public Conta listarPorNumero(Integer numero) {
        String sql = "SELECT c.numero, c.saldo, cl.nome, cl.cpf, cl.email " +
                     "FROM conta c " +
                     "JOIN cliente cl ON c.titular_id = cl.id " +
                     "WHERE c.numero = ?";

        PreparedStatement ps;
        ResultSet resultSet;
        Conta conta = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, numero);
            resultSet = ps.executeQuery();

            // Se encontrar a conta, cria um objeto Conta com os dados recuperados
            if (resultSet.next()) {
                Integer numeroRecuperado = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente =
                        new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numeroRecuperado, saldo, cliente);
            }
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    /// Realiza o saque de um valor de uma conta.
    ///
    /// Este metodo subtrai o valor informado do saldo da conta correspondente ao número.
    ///
    /// @param numeroDaConta O número da conta de onde o valor será sacado.
    /// @param valor O valor a ser sacado da conta.
    public void sacar(Integer numeroDaConta, BigDecimal valor) {
        String sql = "UPDATE conta SET saldo = saldo - ? WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, valor);
            ps.setInt(2, numeroDaConta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /// Realiza o depósito de um valor em uma conta.
    ///
    /// Este metodo adiciona o valor informado ao saldo da conta correspondente ao número.
    ///
    /// @param numeroDaConta O número da conta onde o valor será depositado.
    /// @param valor O valor a ser depositado na conta.
    public void depositar(Integer numeroDaConta, BigDecimal valor) {
        String sql = "UPDATE conta SET saldo = saldo + ? WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, valor);
            ps.setInt(2, numeroDaConta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /// Encerra uma conta removendo-a do banco de dados.
    ///
    /// Este metodo exclui uma conta do banco de dados, com base no número da conta informado.
    ///
    /// @param numeroDaConta O número da conta a ser encerrada.
    public void encerrar(Integer numeroDaConta) {
        String sql = "DELETE FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroDaConta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /// Atualiza o saldo de uma conta no banco de dados.
    ///
    /// Este metodo atualiza o saldo da conta no banco de dados para o valor informado.
    ///
    /// @param conta A conta que terá o saldo atualizado.
    public void atualizarSaldo(Conta conta) {
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, conta.getSaldo());
            ps.setInt(2, conta.getNumero());
            ps.executeUpdate();

            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
