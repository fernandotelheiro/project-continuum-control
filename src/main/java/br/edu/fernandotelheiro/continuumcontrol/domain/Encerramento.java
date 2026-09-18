package br.edu.fernandotelheiro.continuumcontrol.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Encerramento {

    @Column(columnDefinition = "text")
    private String resultado;

    @Column(columnDefinition = "text")
    private String resumo;

    @Column(name = "impacto_observado", columnDefinition = "text")
    private String impactoObservado;

    protected Encerramento() {
    }

    public Encerramento(
            String resultado,
            String resumo,
            String impactoObservado
    ) {
        this.resultado = resultado;
        this.resumo = resumo;
        this.impactoObservado = impactoObservado;
    }

    public String getResultado() {
        return resultado;
    }

    public String getResumo() {
        return resumo;
    }

    public String getImpactoObservado() {
        return impactoObservado;
    }
}