package strategy;

/// Classe que representa uma consulta médica.
///
/// Contém informações sobre o paciente, médico, data e hora da consulta.
public class Consulta implements Cloneable {
    private String paciente;
    private String medico;
    private String data;
    private String hora;

    public Consulta(String paciente, String medico, String data, String hora) {
        this.paciente = paciente;
        this.medico = medico;
        this.data = data;
        this.hora = hora;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }
    public void setMedico(String medico) {
        this.medico = medico;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPaciente() {
        return paciente;
    }
    public String getMedico() {
        return medico;
    }
    public String getData() {
        return data;
    }
    public String getHora() {
        return hora;
    }

    /// Construtor de cópia da classe Consulta.
    ///
    /// Cria uma nova instância de Consulta com os mesmos dados de outra
    /// instância.
    ///
    /// @param consulta A instância de Consulta a ser copiada.
    public Consulta(Consulta consulta) {
        this.paciente = consulta.paciente;
        this.medico = consulta.medico;
        this.data = consulta.data;
        this.hora = consulta.hora;
    }

    @Override
    public Object clone() {
        Consulta clone = null;
        try {
            clone = new Consulta(this);
        } catch (Exception ignore) {
        }
        return clone;
    }

    /// Sobrescreve o metodo `equals` para comparar duas instâncias de Consulta.
    ///
    /// Duas instâncias de Consulta são consideradas iguais se tiverem o mesmo
    /// paciente, médico, data e hora.
    ///
    /// @param obj O objeto a ser comparado.
    /// @return `true` se as instâncias forem iguais,`false` caso contrário.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Consulta that = (Consulta) obj;

        return this.paciente.equals(that.paciente) &&
               this.medico.equals(that.medico) &&
               this.data.equals(that.data) &&
               this.hora.equals(that.hora);
    }

    /// Sobrescreve o metodo `hashCode` para gerar um código hash para a
    /// instância de Consulta.
    ///
    /// O código hash é baseado nos atributos paciente, médico, data e hora.
    ///
    /// @return O código hash da instância.
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;

        hash *= prime + paciente.hashCode();
        hash *= prime + medico.hashCode();
        hash *= prime + data.hashCode();
        hash *= prime + hora.hashCode();

        if (hash < 0) hash = -hash;

        return hash;
    }

    /// Sobrescreve o metodo `toString` para retornar uma representação em
    /// string da instância de Consulta.
    ///
    /// @return A representação em string da instância.
    @Override
    public String toString() {
        return "Consulta{" +
               "paciente='" + paciente + '\'' +
               ", medico='" + medico + '\'' +
               ", data='" + data + '\'' +
               ", hora='" + hora + '\'' +
               '}';
    }
}

