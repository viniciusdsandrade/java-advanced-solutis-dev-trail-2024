import dto.cliente.DadosCadastroCliente;
import entity.Conta;
import service.ContaService;
import dto.conta.DadosAberturaConta;
import exception.RegraDeNegocioException;

import java.math.BigDecimal;
import java.util.Set;

import static util.Teclado.*;

/// A classe 'ByteBankApplication' é a classe principal que controla o fluxo da aplicação ByteBank.
///
/// Esta aplicação permite listar contas, abrir novas contas, encerrar contas, consultar saldo, realizar saques,
/// depósitos e transferências entre contas. Todas as operações são realizadas por meio de interação com o usuário.
public class ByteBankApplication {

    /// Instância do serviço 'ContaService', que gerencia as operações bancárias.
    private static final ContaService service = new ContaService();

    /// Metodo principal da aplicação.
    ///
    /// Inicia o menu de operações bancárias e permite ao usuário escolher e executar diferentes funcionalidades.
    ///
    /// @param args Argumentos de linha de comando (não utilizados nesta aplicação).
    public static void main(String[] args) {
        iniciar();
    }

    /// Inicia o menu de navegação da aplicação.
    ///
    /// O usuário interage com o menu para selecionar diferentes opções, como listar contas, abrir conta, encerrar,
    /// consultar saldo, realizar saques, depósitos e transferências.
    private static void iniciar() {
        var opcao = exibirMenu();
        while (opcao != 8) {
            try {
                switch (opcao) {
                    case 1 -> listarContas();
                    case 2 -> abrirConta();
                    case 3 -> encerrarConta();
                    case 4 -> consultarSaldo();
                    case 5 -> realizarSaque();
                    case 6 -> realizarDeposito();
                    case 7 -> realizarTransferencia();  // Nova opção de transferência
                }
            } catch (RegraDeNegocioException e) {
                System.out.println("Erro: " + e.getMessage());
                aguardarEnter();
            }
            opcao = exibirMenu();
        }

        System.out.println("Finalizando a aplicação.");
    }

    /// Exibe o menu de opções da aplicação e captura a escolha do usuário.
    ///
    /// @return A opção escolhida pelo usuário.
    private static int exibirMenu() {
        System.out.println("""
                BYTEBANK - ESCOLHA UMA OPÇÃO:
                1 - Listar contas abertas
                2 - Abertura de conta
                3 - Encerramento de conta
                4 - Consultar saldo de uma conta
                5 - Realizar saque em uma conta
                6 - Realizar depósito em uma conta
                7 - Realizar transferência entre contas
                8 - Sair
                """);
        return lerInt();
    }

    /// Lista todas as contas cadastradas no sistema.
    ///
    /// As contas abertas são obtidas através do serviço 'ContaService' e exibidas no console.
    private static void listarContas() {
        System.out.println("Contas cadastradas:");
        Set<Conta> contas = service.listarContasAbertas();
        contas.forEach(System.out::println);

        aguardarEnter();
    }

    /// Realiza a abertura de uma nova conta.
    ///
    /// O usuário insere os dados necessários, como número da conta, nome, CPF e email,
    /// e o serviço 'ContaService' cria a nova conta.
    private static void abrirConta() {
        String nome, cpf, email;
        int numeroDaConta;

        System.out.print("Digite o número da conta: ");
        numeroDaConta = lerInt();

        System.out.print("Digite o nome do cliente: ");
        nome = lerString();

        System.out.print("Digite o cpf do cliente: ");
        cpf = lerString();

        System.out.print("Digite o email do cliente: ");
        email = lerString();

        service.abrir(new DadosAberturaConta(numeroDaConta, new DadosCadastroCliente(nome, cpf, email)));

        System.out.println("Conta aberta com sucesso!");
        aguardarEnter();
    }

    /// Encerra uma conta existente.
    ///
    /// O usuário insere o número da conta, e o serviço 'ContaService' encerra a conta, desde que não possua saldo.
    private static void encerrarConta() {
        int numeroDaConta;

        System.out.print("Digite o número da conta: ");
        numeroDaConta = lerInt();

        service.encerrar(numeroDaConta);

        System.out.println("Conta encerrada com sucesso!");
        aguardarEnter();
    }

    /// Consulta o saldo de uma conta.
    ///
    /// O usuário insere o número da conta e o saldo é exibido no console.
    private static void consultarSaldo() {
        BigDecimal saldo;
        int numeroDaConta;

        System.out.print("Digite o número da conta: ");
        numeroDaConta = lerInt();
        saldo = service.consultarSaldo(numeroDaConta);
        System.out.println("Saldo da conta: " + saldo);

        aguardarEnter();
    }

    /// Realiza um saque de uma conta.
    ///
    /// O usuário insere o número da conta e o valor a ser sacado, e o serviço 'ContaService' processa o saque.
    private static void realizarSaque() {
        BigDecimal valor;
        int numeroDaConta;

        System.out.print("Digite o número da conta: ");
        numeroDaConta = lerInt();

        System.out.print("Digite o valor do saque: ");
        valor = lerBigDecimal();

        service.realizarSaque(numeroDaConta, valor);
        System.out.println("Saque realizado com sucesso!");
        aguardarEnter();
    }

    /// Realiza um depósito em uma conta.
    ///
    /// O usuário insere o número da conta e o valor a ser depositado, e o serviço 'ContaService' processa o depósito.
    private static void realizarDeposito() {
        BigDecimal valor;
        int numeroDaConta;

        System.out.print("Digite o número da conta: ");
        numeroDaConta = lerInt();

        System.out.print("Digite o valor do depósito: ");
        valor = lerBigDecimal();

        service.realizarDeposito(numeroDaConta, valor);

        System.out.println("Depósito realizado com sucesso!");
        aguardarEnter();
    }

    /// Realiza uma transferência entre duas contas.
    ///
    /// O usuário insere o número da conta de origem, o número da conta de destino e o valor da transferência.
    /// O serviço 'ContaService' processa a transferência entre as contas.
    private static void realizarTransferencia() {
        int numeroContaOrigem, numeroContaDestino;
        BigDecimal valor;

        System.out.print("Digite o número da sua conta (origem): ");
        numeroContaOrigem = lerInt();

        System.out.print("Digite o número da conta destino: ");
        numeroContaDestino = lerInt();

        System.out.print("Digite o valor da transferência: ");
        valor = lerBigDecimal();

        service.realizarTransferencia(numeroContaOrigem, numeroContaDestino, valor);

        System.out.println("Transferência realizada com sucesso!");
        aguardarEnter();
    }
}
