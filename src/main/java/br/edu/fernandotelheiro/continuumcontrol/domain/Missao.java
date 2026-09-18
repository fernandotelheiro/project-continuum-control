package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "missao")
public class Missao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anomalia_id", nullable = false)
    private Anomalia anomalia;

    @Column(nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(nullable = false, columnDefinition = "text")
    private String objetivo;

    @Column(name = "destino_temporal", nullable = false)
    private OffsetDateTime destinoTemporal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PrioridadeMissao prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusMissao status;

    @OneToMany(
            mappedBy = "missao",
            cascade = CascadeType.PERSIST
    )
    private List<ParticipacaoMissao> participacoes = new ArrayList<>();

    @Embedded
    private Encerramento encerramento;

    @Version
    private Long versao;

    protected Missao() {
    }

    public Missao(
            Anomalia anomalia,
            String codigo,
            String objetivo,
            OffsetDateTime destinoTemporal,
            PrioridadeMissao prioridade
    ) {
        this.anomalia = anomalia;
        this.codigo = codigo;
        this.objetivo = objetivo;
        this.destinoTemporal = destinoTemporal;
        this.prioridade = prioridade;
        this.status = StatusMissao.PLANEJADA;
    }

    public Long getId() {
        return id;
    }

    public Anomalia getAnomalia() {
        return anomalia;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public OffsetDateTime getDestinoTemporal() {
        return destinoTemporal;
    }

    public PrioridadeMissao getPrioridade() {
        return prioridade;
    }

    public StatusMissao getStatus() {
        return status;
    }

    public List<ParticipacaoMissao> getParticipacoes() {
        return List.copyOf(participacoes);
    }

    public Encerramento getEncerramento() {
        return encerramento;
    }

    public Long getVersao() {
        return versao;
    }
}