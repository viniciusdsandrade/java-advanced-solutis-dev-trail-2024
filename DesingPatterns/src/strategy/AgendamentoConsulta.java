package strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import strategy.exception.ConsultaValidationException;
import strategy.exception.DataHoraInvalidaException;
import strategy.exception.MedicoNaoDisponivelException;
import strategy.exception.PacienteNaoCadastradoException;
import strategy.validacoes.ConsultaValidationStrategy;
import strategy.validacoes.DataHoraValidaStrategy;
import strategy.validacoes.MedicoDisponivelStrategy;
import strategy.validacoes.PacienteCadastradoStrategy;

/// Classe responsável por agendar consultas, aplicando estratégias de
/// validação para garantir a integridade dos dados.
///
/// Utiliza o padrão Strategy para permitir a definição de diferentes critérios
/// de validação para uma consulta de forma flexível e extensível.
///
/// @see strategy.validacoes.ConsultaValidationStrategy
public class AgendamentoConsulta {

    /// Lista de estratégias de validação a serem aplicadas às consultas.
    private final List<ConsultaValidationStrategy> validationStrategies = new ArrayList<>();

    /// Construtor da classe AgendamentoConsulta.
    ///
    /// Inicializa a lista de estratégias de validação, buscando
    /// automaticamente classes que implementam a interface
    /// `ConsultaValidationStrategy` no pacote `strategy`.
    public AgendamentoConsulta() {
        // Adicionar automaticamente as estratégias de validação
        Reflections reflections = new Reflections("strategy");
        Set<Class<? extends ConsultaValidationStrategy>> strategyClasses =
                reflections.getSubTypesOf(ConsultaValidationStrategy.class);

        for (Class<? extends ConsultaValidationStrategy> strategyClass : strategyClasses) {
            try {
                ConsultaValidationStrategy strategy = strategyClass.getDeclaredConstructor().newInstance();
                addValidationStrategy(strategy);
            } catch (Exception e) {
                // Lidar com exceções (ex: classe abstrata, construtor privado)
                System.err.println("Erro ao instanciar estratégia: " + strategyClass.getName());
            }
        }
    }

    /// Adiciona uma estratégia de validação à lista de estratégias.
    ///
    /// @param strategy A estratégia de validação a ser adicionada.
    public void addValidationStrategy(ConsultaValidationStrategy strategy) {
        validationStrategies.add(strategy);
    }

    /// Agenda uma consulta, aplicando as estratégias de validação definidas.
    ///
    /// Se todas as estratégias de validação forem satisfeitas, a consulta é
    /// agendada com sucesso. Caso contrário, as exceções correspondentes às
    /// validações que falharam são lançadas.
    ///
    /// @param consulta A consulta a ser agendada.
    public void agendar(Consulta consulta) {
        List<ConsultaValidationException> exceptions = new ArrayList<>();

        for (ConsultaValidationStrategy strategy : validationStrategies) {
            if (!strategy.isValid(consulta)) {
                switch (strategy) {
                    case PacienteCadastradoStrategy ignored -> exceptions.add(new PacienteNaoCadastradoException());
                    case MedicoDisponivelStrategy ignored -> exceptions.add(new MedicoNaoDisponivelException());
                    case DataHoraValidaStrategy ignored -> exceptions.add(new DataHoraInvalidaException());
                    default -> throw new IllegalStateException("Unexpected value: " + strategy);
                }
            }
        }

        if (exceptions.isEmpty()) {
            // Lógica para agendar a consulta (exemplo)
            System.out.println("Consulta agendada com sucesso!");
        } else {
            // Imprime as mensagens de erro de todas as exceções capturadas
            for (ConsultaValidationException e : exceptions) {
                System.err.println("Erro de validação: " + e.getMessage());
            }
        }
    }
}