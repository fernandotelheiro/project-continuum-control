package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.TransicaoInvalidaException;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "anomalia")
public class Anomalia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_historico_id", nullable = false)
    private EventoHistorico eventoHistorico;

    @Column(nullable = false, columnDefinition = "text")
    private String descricao;

    @Column(name = "detectada_em", nullable = false)
    private OffsetDateTime detectadaEm;

    @Column(length = 180)
    private String origem;

    @Column(columnDefinition = "text")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiscoAnomalia risco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusAnomalia status;

    protected Anomalia() {
    }

    public Anomalia(
            EventoHistorico eventoHistorico,
            String descricao,
            OffsetDateTime detectadaEm,
            String origem,
            String observacoes,
            RiscoAnomalia risco
    ) {
        this.eventoHistorico = eventoHistorico;
        this.descricao = descricao;
        this.detectadaEm = detectadaEm;
        this.origem = origem;
        this.observacoes = observacoes;
        this.risco = risco;
        this.status = StatusAnomalia.DETECTADA;
    }

    public boolean aceitaNovaMissao() {
        return status != StatusAnomalia.IRREVERSIVEL;
    }

    public boolean isCritica() {
        return risco == RiscoAnomalia.CRITICO;
    }

    public void iniciarCorrecao() {
        if (status != StatusAnomalia.DETECTADA
                && status != StatusAnomalia.EM_ANALISE
                && status != StatusAnomalia.CONFIRMADA) {

            throw new TransicaoInvalidaException(
                    "Anomalia não pode iniciar correção no estado atual."
            );
        }

        status = StatusAnomalia.EM_CORRECAO;
    }

    public void estabilizar() {
        if (status != StatusAnomalia.EM_CORRECAO) {
            throw new TransicaoInvalidaException(
                    "Somente anomalia em correção pode ser estabilizada."
            );
        }

        status = StatusAnomalia.ESTABILIZADA;
    }

    public void reabrir() {
        if (status != StatusAnomalia.EM_CORRECAO) {
            throw new TransicaoInvalidaException(
                    "Somente anomalia em correção pode ser reaberta."
            );
        }

        status = StatusAnomalia.CONFIRMADA;
    }

    public void marcarIrreversivel() {
        if (status == StatusAnomalia.ESTABILIZADA
                || status == StatusAnomalia.IRREVERSIVEL) {

            throw new TransicaoInvalidaException(
                    "Anomalia não pode ser marcada como irreversível no estado atual."
            );
        }

        status = StatusAnomalia.IRREVERSIVEL;
    }

    public Long getId() {
        return id;
    }

    public EventoHistorico getEventoHistorico() {
        return eventoHistorico;
    }

    public String getDescricao() {
        return descricao;
    }

    public OffsetDateTime getDetectadaEm() {
        return detectadaEm;
    }

    public String getOrigem() {
        return origem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public RiscoAnomalia getRisco() {
        return risco;
    }

    public StatusAnomalia getStatus() {
        return status;
    }
}