import dto.cliente.DadosCadastroCliente;
import entity.Conta;
import service.ContaService;
import dto.conta.DadosAberturaConta;
import exception.RegraDeNegocioException;

import java.math.BigDecimal;
import java.util.Set;

import static util.Teclado.*;

public class ByteBankApplication {

    private static final ContaService service = new ContaService();

    public static void main(String[] args) {
        iniciar();
    }

    private static void iniciar() {
        var opcao = exibirMenu();
        while (opcao != 7) {
            try {
                switch (opcao) {
                    case 1 -> listarContas();
                    case 2 -> abrirConta();
                    case 3 -> encerrarConta();
                    case 4 -> consultarSaldo();
                    case 5 -> realizarSaque();
                    case 6 -> realizarDeposito();
                }
            } catch (RegraDeNegocioException e) {
                System.out.println("Erro: " + e.getMessage());
                aguardarEnter();
            }
            opcao = exibirMenu();
        }

        System.out.println("Finalizando a aplicação.");
    }

    private static int exibirMenu() {
        System.out.println("""
                BYTEBANK - ESCOLHA UMA OPÇÃO:
                1 - Listar contas abertas
                2 - Abertura de conta
                3 - Encerramento de conta
                4 - Consultar saldo de uma conta
                5 - Realizar saque em uma conta
                6 - Realizar depósito em uma conta
                7 - Sair
                """);
        return lerInt();
    }

    private static void listarContas() {
        System.out.println("Contas cadastradas:");
        Set<Conta> contas = service.listarContasAbertas();
        contas.forEach(System.out::println);

        aguardarEnter();
    }

    private static void abrirConta() {
        System.out.print("Digite o número da conta: ");
        int numeroDaConta = lerInt();

        System.out.print("Digite o nome do cliente: ");
        String nome = lerString();

        System.out.print("Digite o cpf do cliente: ");
        String cpf = lerString();

        System.out.print("Digite o email do cliente: ");
        String email = lerString();

        service.abrir(new DadosAberturaConta(numeroDaConta, new DadosCadastroCliente(nome, cpf, email)));

        System.out.println("Conta aberta com sucesso!");
        aguardarEnter();
    }

    private static void encerrarConta() {
        System.out.print("Digite o número da conta: ");
        int numeroDaConta = lerInt();

        service.encerrar(numeroDaConta);

        System.out.println("Conta encerrada com sucesso!");
        aguardarEnter();
    }

    private static void consultarSaldo() {
        System.out.print("Digite o número da conta: ");
        int numeroDaConta = lerInt();
        BigDecimal saldo = service.consultarSaldo(numeroDaConta);
        System.out.println("Saldo da conta: " + saldo);

        aguardarEnter();
    }

    private static void realizarSaque() {
        System.out.print("Digite o número da conta: ");
        int numeroDaConta = lerInt();

        System.out.print("Digite o valor do saque: ");
        BigDecimal valor = lerBigDecimal();

        service.realizarSaque(numeroDaConta, valor);
        System.out.println("Saque realizado com sucesso!");
        aguardarEnter();
    }

    private static void realizarDeposito() {
        System.out.print("Digite o número da conta: ");
        int numeroDaConta = lerInt();

        System.out.print("Digite o valor do depósito: ");
        BigDecimal valor = lerBigDecimal();

        service.realizarDeposito(numeroDaConta, valor);

        System.out.println("Depósito realizado com sucesso!");
        aguardarEnter();
    }
}