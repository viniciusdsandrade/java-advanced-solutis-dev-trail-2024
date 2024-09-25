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

    public ContaDAO(Connection connection) {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);

        try {
            // Inserir o cliente
            String sqlCliente = "INSERT INTO cliente (nome, cpf, email) VALUES (?, ?, ?)";
            PreparedStatement psCliente = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS);
            psCliente.setString(1, cliente.getNome());
            psCliente.setString(2, cliente.getCpf());
            psCliente.setString(3, cliente.getEmail());
            psCliente.execute();

            // Recuperar o ID do cliente
            ResultSet rsCliente = psCliente.getGeneratedKeys();
            if (rsCliente.next()) {
                cliente.setId(rsCliente.getLong(1));
            }

            // Inserir a conta
            String sqlConta = "INSERT INTO conta (numero, saldo, titular_id) VALUES (?, ?, ?)";
            PreparedStatement psConta = conn.prepareStatement(sqlConta);
            psConta.setInt(1, conta.getNumero());
            psConta.setBigDecimal(2, BigDecimal.ZERO);
            psConta.setLong(3, cliente.getId());
            psConta.execute();

            psConta.close();
            psCliente.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

    public void encerrar(Integer numeroDaConta) {
        String sql = "DELETE FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroDaConta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
