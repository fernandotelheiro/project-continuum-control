package br.edu.fernandotelheiro.continuumcontrol.service;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.Autorizacao;
import br.edu.fernandotelheiro.continuumcontrol.domain.Encerramento;
import br.edu.fernandotelheiro.continuumcontrol.domain.Intervencao;
import br.edu.fernandotelheiro.continuumcontrol.domain.Missao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.RecursoNaoEncontradoException;
import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarMissaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarMissaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.EncerramentoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.HistoricoResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.IntervencaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.IntervencaoResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.MissaoResponse;
import br.edu.fernandotelheiro.continuumcontrol.evento.MissaoMudouDeEstado;
import br.edu.fernandotelheiro.continuumcontrol.historico.HistoricoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AgenteRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AnomaliaRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AutorizacaoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.IntervencaoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.MissaoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MissaoService {

    private final MissaoRepository missaoRepository;
    private final AnomaliaRepository anomaliaRepository;
    private final AgenteRepository agenteRepository;
    private final AutorizacaoRepository autorizacaoRepository;
    private final IntervencaoRepository intervencaoRepository;
    private final HistoricoRepository historicoRepository;
    private final ApplicationEventPublisher publisher;

    public MissaoService(
            MissaoRepository missaoRepository,
            AnomaliaRepository anomaliaRepository,
            AgenteRepository agenteRepository,
            AutorizacaoRepository autorizacaoRepository,
            IntervencaoRepository intervencaoRepository,
            HistoricoRepository historicoRepository,
            ApplicationEventPublisher publisher
    ) {
        this.missaoRepository = missaoRepository;
        this.anomaliaRepository = anomaliaRepository;
        this.agenteRepository = agenteRepository;
        this.autorizacaoRepository = autorizacaoRepository;
        this.intervencaoRepository = intervencaoRepository;
        this.historicoRepository = historicoRepository;
        this.publisher = publisher;
    }

    @Transactional
    public MissaoResponse criar(CriarMissaoRequest request) {
        Anomalia anomalia = buscarAnomalia(request.anomaliaId());

        Missao missao = new Missao(
                anomalia,
                request.codigo(),
                request.objetivo(),
                request.destinoTemporal(),
                request.prioridade()
        );

        missaoRepository.save(missao);

        publicar(
                missao,
                "MISSAO_CRIADA",
                Map.of()
        );

        return MissaoResponse.de(missao);
    }

    @Transactional
    public MissaoResponse atualizar(
            Long id,
            AtualizarMissaoRequest request
    ) {
        Missao missao = buscarMissao(id);

        missao.atualizarDados(
                request.objetivo(),
                request.destinoTemporal(),
                request.prioridade()
        );

        return MissaoResponse.de(missao);
    }

    @Transactional
    public MissaoResponse designarAgente(
            Long missaoId,
            Long agenteId
    ) {
        Missao missao = buscarMissao(missaoId);
        Agente agente = buscarAgente(agenteId);

        missao.designar(agente);

        publicar(
                missao,
                "AGENTE_DESIGNADO",
                Map.of(
                        "agenteId",
                        agenteId
                )
        );

        return MissaoResponse.de(missao);
    }

    @Transactional
    public MissaoResponse autorizar(
            Long missaoId,
            String responsavel,
            String observacao
    ) {
        Missao missao = buscarMissao(missaoId);

        missao.autorizar();

        Autorizacao autorizacao = new Autorizacao(
                missao,
                responsavel,
                observacao
        );

        autorizacaoRepository.save(autorizacao);

        publicar(
                missao,
                "MISSAO_AUTORIZADA",
                Map.of(
                        "responsavel",
                        responsavel
                )
        );

        return MissaoResponse.de(missao);
    }

    @Transactional
    public MissaoResponse iniciar(Long missaoId) {
        Missao missao = buscarMissao(missaoId);

        missao.iniciar();

        missaoRepository.saveAndFlush(missao);

        publicar(
                missao,
                "MISSAO_INICIADA",
                Map.of()
        );

        return MissaoResponse.de(missao);
    }

    @Transactional
    public IntervencaoResponse registrarIntervencao(
            Long missaoId,
            IntervencaoRequest request
    ) {
        Missao missao = buscarMissao(missaoId);
        Agente agente = buscarAgente(request.agenteId());

        Intervencao intervencao = missao.registrarIntervencao(
                agente,
                request.acao(),
                request.justificativa(),
                request.impacto(),
                request.resultado()
        );

        intervencaoRepository.save(intervencao);

        publicar(
                missao,
                "INTERVENCAO_REGISTRADA",
                Map.of(
                        "agenteId",
                        request.agenteId(),
                        "acao",
                        request.acao(),
                        "impacto",
                        request.impacto().name()
                )
        );

        return IntervencaoResponse.de(intervencao);
    }

    @Transactional
    public MissaoResponse concluir(
            Long missaoId,
            EncerramentoRequest request
    ) {
        Missao missao = buscarMissao(missaoId);

        Encerramento encerramento = new Encerramento(
                request.resultado(),
                request.resumo(),
                request.impactoObservado()
        );

        missao.concluir(encerramento);

        publicar(
                missao,
                "MISSAO_CONCLUIDA",
                Map.of(
                        "resultado",
                        request.resultado()
                )
        );

        return MissaoResponse.de(missao);
    }

    @Transactional
    public MissaoResponse falhar(
            Long missaoId,
            EncerramentoRequest request
    ) {
        Missao missao = buscarMissao(missaoId);

        Encerramento encerramento = new Encerramento(
                request.resultado(),
                request.resumo(),
                request.impactoObservado()
        );

        missao.falhar(encerramento);

        publicar(
                missao,
                "MISSAO_FALHOU",
                Map.of(
                        "resultado",
                        request.resultado()
                )
        );

        return MissaoResponse.de(missao);
    }

    @Transactional(readOnly = true)
    public MissaoResponse buscar(Long id) {
        Missao missao = buscarMissao(id);

        return MissaoResponse.de(missao);
    }

    @Transactional(readOnly = true)
    public Page<MissaoResponse> listar(
            StatusMissao status,
            Pageable pageable
    ) {
        Page<Missao> missoes;

        if (status != null) {
            missoes = missaoRepository.findByStatus(
                    status,
                    pageable
            );
        } else {
            missoes = missaoRepository.findAll(pageable);
        }

        return missoes.map(MissaoResponse::de);
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponse> historico(Long missaoId) {

        buscarMissao(missaoId);

        return historicoRepository
                .findByMissaoIdOrderByInstanteAsc(missaoId)
                .stream()
                .map(HistoricoResponse::de)
                .toList();
    }

    private void publicar(
            Missao missao,
            String tipo,
            Map<String, Object> detalhes
    ) {
        List<Long> agenteIds = missao.getParticipacoes()
                .stream()
                .map(participacao ->
                        participacao.getAgente().getId()
                )
                .toList();

        MissaoMudouDeEstado evento =
                new MissaoMudouDeEstado(
                        missao.getId(),
                        missao.getCodigo(),
                        missao.getAnomalia().getId(),
                        agenteIds,
                        tipo,
                        missao.getStatus(),
                        OffsetDateTime.now(),
                        detalhes
                );

        publisher.publishEvent(evento);
    }

    private Missao buscarMissao(Long id) {
        return missaoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Missão " + id + " não encontrada."
                        )
                );
    }

    private Agente buscarAgente(Long id) {
        return agenteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Agente " + id + " não encontrado."
                        )
                );
    }

    private Anomalia buscarAnomalia(Long id) {
        return anomaliaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Anomalia " + id + " não encontrada."
                        )
                );
    }
}