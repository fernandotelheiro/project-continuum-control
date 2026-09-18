package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImpactoIntervencao;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "intervencao")
public class Intervencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "missao_id", nullable = false)
    private Missao missao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agente_id", nullable = false)
    private Agente agente;

    @Column(nullable = false)
    private OffsetDateTime instante;

    @Column(nullable = false, columnDefinition = "text")
    private String acao;

    @Column(nullable = false, columnDefinition = "text")
    private String justificativa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ImpactoIntervencao impacto;

    @Column(columnDefinition = "text")
    private String resultado;

    protected Intervencao() {
    }

    public Intervencao(
            Missao missao,
            Agente agente,
            String acao,
            String justificativa,
            ImpactoIntervencao impacto,
            String resultado
    ) {
        this.missao = missao;
        this.agente = agente;
        this.instante = OffsetDateTime.now();
        this.acao = acao;
        this.justificativa = justificativa;
        this.impacto = impacto;
        this.resultado = resultado;
    }

    public Long getId() {
        return id;
    }

    public Missao getMissao() {
        return missao;
    }

    public Agente getAgente() {
        return agente;
    }

    public OffsetDateTime getInstante() {
        return instante;
    }

    public String getAcao() {
        return acao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public ImpactoIntervencao getImpacto() {
        return impacto;
    }

    public String getResultado() {
        return resultado;
    }
}