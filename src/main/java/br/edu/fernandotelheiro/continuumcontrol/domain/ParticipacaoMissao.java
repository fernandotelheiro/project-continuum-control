package br.edu.fernandotelheiro.continuumcontrol.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "participacao_missao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_participacao_missao_agente",
                        columnNames = {"missao_id", "agente_id"}
                )
        }
)
public class ParticipacaoMissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "missao_id", nullable = false)
    private Missao missao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agente_id", nullable = false)
    private Agente agente;

    @Column(name = "designado_em", nullable = false)
    private OffsetDateTime designadoEm;

    protected ParticipacaoMissao() {
    }

    public ParticipacaoMissao(
            Missao missao,
            Agente agente
    ) {
        this.missao = missao;
        this.agente = agente;
        this.designadoEm = OffsetDateTime.now();
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

    public OffsetDateTime getDesignadoEm() {
        return designadoEm;
    }
}