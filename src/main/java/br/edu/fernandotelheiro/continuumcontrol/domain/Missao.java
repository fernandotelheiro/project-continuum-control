package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImpactoIntervencao;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.RegraDeNegocioException;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.TransicaoInvalidaException;
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
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
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
        if (!anomalia.aceitaNovaMissao()) {
            throw new RegraDeNegocioException(
                    "Anomalia irreversível não aceita nova missão."
            );
        }

        this.anomalia = anomalia;
        this.codigo = codigo;
        this.objetivo = objetivo;
        this.destinoTemporal = destinoTemporal;
        this.prioridade = prioridade;
        this.status = StatusMissao.PLANEJADA;
    }


    public void atualizarDados(
            String objetivo,
            OffsetDateTime destinoTemporal,
            PrioridadeMissao prioridade
    ) {
        if (status != StatusMissao.PLANEJADA) {
            throw new TransicaoInvalidaException(
                    "Somente missão planejada pode ter seus dados alterados."
            );
        }

        this.objetivo = objetivo;
        this.destinoTemporal = destinoTemporal;
        this.prioridade = prioridade;
    }

    public boolean exigeAutorizacao() {
        return prioridade == PrioridadeMissao.EMERGENCIA
                || anomalia.isCritica();
    }

    public void designar(Agente agente) {
        if (status != StatusMissao.PLANEJADA
                && status != StatusMissao.AUTORIZADA) {

            throw new TransicaoInvalidaException(
                    "Não é possível designar agente no estado atual da missão."
            );
        }

        if (!agente.estaDisponivel()) {
            throw new RegraDeNegocioException(
                    "Somente agente disponível pode ser designado."
            );
        }

        boolean jaParticipa = participacoes.stream()
                .anyMatch(participacao -> participacao.getAgente() == agente);

        if (jaParticipa) {
            throw new RegraDeNegocioException(
                    "Agente já está designado para esta missão."
            );
        }

        participacoes.add(new ParticipacaoMissao(this, agente));
    }

    public void autorizar() {
        if (status != StatusMissao.PLANEJADA) {
            throw new TransicaoInvalidaException(
                    "Somente missão planejada pode ser autorizada."
            );
        }

        status = StatusMissao.AUTORIZADA;
    }

    public void iniciar() {
        if (status != StatusMissao.PLANEJADA
                && status != StatusMissao.AUTORIZADA) {

            throw new TransicaoInvalidaException(
                    "Missão não pode ser iniciada no estado atual."
            );
        }

        if (participacoes.isEmpty()) {
            throw new RegraDeNegocioException(
                    "Missão precisa de pelo menos um agente para iniciar."
            );
        }

        if (exigeAutorizacao()
                && status != StatusMissao.AUTORIZADA) {

            throw new TransicaoInvalidaException(
                    "Missão exige autorização antes de iniciar."
            );
        }

        boolean todosDisponiveis = participacoes.stream()
                .allMatch(participacao ->
                        participacao.getAgente().estaDisponivel()
                );

        if (!todosDisponiveis) {
            throw new RegraDeNegocioException(
                    "Todos os agentes precisam estar disponíveis para iniciar a missão."
            );
        }

        for (ParticipacaoMissao participacao : participacoes) {
            participacao.getAgente().entrarEmMissao();
        }

        anomalia.iniciarCorrecao();

        status = StatusMissao.EM_EXECUCAO;
    }

    public void concluir(Encerramento encerramento) {
        if (status != StatusMissao.EM_EXECUCAO) {
            throw new TransicaoInvalidaException(
                    "Somente missão em execução pode ser concluída."
            );
        }

        this.encerramento = encerramento;

        for (ParticipacaoMissao participacao : participacoes) {
            participacao.getAgente().liberar();
        }

        anomalia.estabilizar();

        status = StatusMissao.CONCLUIDA;
    }

    public void falhar(Encerramento encerramento) {
        if (status != StatusMissao.EM_EXECUCAO) {
            throw new TransicaoInvalidaException(
                    "Somente missão em execução pode falhar."
            );
        }

        this.encerramento = encerramento;

        for (ParticipacaoMissao participacao : participacoes) {
            participacao.getAgente().liberar();
        }

        anomalia.reabrir();

        status = StatusMissao.FALHOU;
    }

    public void cancelar() {
        if (status != StatusMissao.PLANEJADA
                && status != StatusMissao.AUTORIZADA) {

            throw new TransicaoInvalidaException(
                    "Missão não pode ser cancelada no estado atual."
            );
        }

        status = StatusMissao.CANCELADA;
    }

    public boolean aceitaIntervencao() {
        return status == StatusMissao.EM_EXECUCAO;
    }


    public boolean possuiAgente(Agente agente) {
        return participacoes.stream()
                .anyMatch(participacao ->
                        participacao.getAgente() == agente
                );
    }

    public Intervencao registrarIntervencao(
            Agente agente,
            String acao,
            String justificativa,
            ImpactoIntervencao impacto,
            String resultado
    ) {
        if (!aceitaIntervencao()) {
            throw new TransicaoInvalidaException(
                    "Intervenção só pode ser registrada em missão em execução."
            );
        }

        if (!possuiAgente(agente)) {
            throw new RegraDeNegocioException(
                    "O agente não participa desta missão."
            );
        }

        return new Intervencao(
                this,
                agente,
                acao,
                justificativa,
                impacto,
                resultado
        );
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