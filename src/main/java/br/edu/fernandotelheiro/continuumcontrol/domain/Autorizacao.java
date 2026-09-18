package br.edu.fernandotelheiro.continuumcontrol.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "autorizacao")
public class Autorizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "missao_id",
            nullable = false,
            unique = true
    )
    private Missao missao;

    @Column(nullable = false, length = 120)
    private String responsavel;

    @Column(nullable = false)
    private OffsetDateTime instante;

    @Column(nullable = false, columnDefinition = "text")
    private String observacao;

    protected Autorizacao() {
    }

    public Autorizacao(
            Missao missao,
            String responsavel,
            String observacao
    ) {
        this.missao = missao;
        this.responsavel = responsavel;
        this.observacao = observacao;
        this.instante = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Missao getMissao() {
        return missao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public OffsetDateTime getInstante() {
        return instante;
    }

    public String getObservacao() {
        return observacao;
    }
}