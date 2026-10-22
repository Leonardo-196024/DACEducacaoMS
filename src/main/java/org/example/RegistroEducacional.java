
package org.example;

public class RegistroEducacional {

    private final int anoReferencia;
    private final String nomeMunicipio;
    private final String nomeEscola;

    private final Integer matriculasTotal;
    private final Integer matriculaInicial;
    private final Integer matriculaAposCenso;
    private final Integer transferidos;
    private final Integer cancelados;
    private final Integer falecido;
    private final Integer abandono;
    private final Integer aprovados;
    private final Integer reprovados;
    private final Integer cursando;
    private final Integer outrasSituacoes;

    public RegistroEducacional(
            int anoReferencia,
            String nomeMunicipio,
            String nomeEscola,
            Integer matriculasTotal,
            Integer matriculaInicial,
            Integer matriculaAposCenso,
            Integer transferidos,
            Integer cancelados,
            Integer falecido,
            Integer abandono,
            Integer aprovados,
            Integer reprovados,
            Integer cursando,
            Integer outrasSituacoes) {

        this.anoReferencia = anoReferencia;
        this.nomeMunicipio = nomeMunicipio;
        this.nomeEscola = nomeEscola;
        this.matriculasTotal = matriculasTotal;
        this.matriculaInicial = matriculaInicial;
        this.matriculaAposCenso = matriculaAposCenso;
        this.transferidos = transferidos;
        this.cancelados = cancelados;
        this.falecido = falecido;
        this.abandono = abandono;
        this.aprovados = aprovados;
        this.reprovados = reprovados;
        this.cursando = cursando;
        this.outrasSituacoes = outrasSituacoes;
    }

    public int getAnoReferencia() {
        return anoReferencia;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public String getNomeEscola() {
        return nomeEscola;
    }

    public Integer getMatriculasTotal() {
        return matriculasTotal;
    }

    public Integer getMatriculaInicial() {
        return matriculaInicial;
    }

    public Integer getMatriculaAposCenso() {
        return matriculaAposCenso;
    }

    public Integer getTransferidos() {
        return transferidos;
    }

    public Integer getCancelados() {
        return cancelados;
    }

    public Integer getFalecido() {
        return falecido;
    }

    public Integer getAbandono() {
        return abandono;
    }

    public Integer getAprovados() {
        return aprovados;
    }

    public Integer getReprovados() {
        return reprovados;
    }

    public Integer getCursando() {
        return cursando;
    }

    public Integer getOutrasSituacoes() {
        return outrasSituacoes;
    }
}
