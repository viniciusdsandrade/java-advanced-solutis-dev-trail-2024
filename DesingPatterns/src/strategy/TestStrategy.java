package strategy;

import java.util.ArrayList;
import java.util.List;


public class TestStrategy {
    public static void main(String[] ignoredArgs) {
        // Criar as consultas
        Consulta consultaValida = new Consulta("João Silva", "Dr. José", "2023-12-31", "10:00");
        Consulta consultaSemPaciente = new Consulta(null, "Dr. José", "2023-12-31", "10:00");
        Consulta consultaSemMedico = new Consulta("João Silva", null, "2023-12-31", "10:00");
        Consulta consultaSemData = new Consulta("João Silva", "Dr. José", null, "10:00");
        Consulta consultaSemHora = new Consulta("João Silva", "Dr. José", "2023-12-31", null);

        // Adicionar as consultas a uma lista
        List<Consulta> consultas = new ArrayList<>();
        consultas.add(consultaValida);
        consultas.add(consultaSemPaciente);
        consultas.add(consultaSemMedico);
        consultas.add(consultaSemData);
        consultas.add(consultaSemHora);

        // Criar o contexto de agendamento (as estratégias são carregadas automaticamente)
        AgendamentoConsulta agendamento = new AgendamentoConsulta();

        // Agendar as consultas usando um foreach
        consultas.forEach(agendamento::agendar);
    }
}