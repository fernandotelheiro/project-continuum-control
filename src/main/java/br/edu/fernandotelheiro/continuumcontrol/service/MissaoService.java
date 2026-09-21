package br.edu.fernandotelheiro.continuumcontrol.service;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.Autorizacao;
import br.edu.fernandotelheiro.continuumcontrol.domain.Encerramento;
import br.edu.fernandotelheiro.continuumcontrol.domain.Intervencao;
import br.edu.fernandotelheiro.continuumcontrol.domain.Missao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImpactoIntervencao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.RecursoNaoEncontradoException;
import br.edu.fernandotelheiro.continuumcontrol.repository.AgenteRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AnomaliaRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AutorizacaoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.IntervencaoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.MissaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class MissaoService {

    private final MissaoRepository missaoRepository;
    private final AnomaliaRepository anomaliaRepository;
    private final AgenteRepository agenteRepository;
    private final AutorizacaoRepository autorizacaoRepository;
    private final IntervencaoRepository intervencaoRepository;

    public MissaoService(
            MissaoRepository missaoRepository,
            AnomaliaRepository anomaliaRepository,
            AgenteRepository agenteRepository,
            AutorizacaoRepository autorizacaoRepository,
            IntervencaoRepository intervencaoRepository
    ) {
        this.missaoRepository = missaoRepository;
        this.anomaliaRepository = anomaliaRepository;
        this.agenteRepository = agenteRepository;
        this.autorizacaoRepository = autorizacaoRepository;
        this.intervencaoRepository = intervencaoRepository;
    }

    @Transactional
    public Missao criar(
            Long anomaliaId,
            String codigo,
            String objetivo,
            OffsetDateTime destinoTemporal,
            PrioridadeMissao prioridade
    ) {
        Anomalia anomalia = buscarAnomalia(anomaliaId);

        Missao missao = new Missao(
                anomalia,
                codigo,
                objetivo,
                destinoTemporal,
                prioridade
        );

        return missaoRepository.save(missao);
    }

    @Transactional
    public Missao designarAgente(
            Long missaoId,
            Long agenteId
    ) {
        Missao missao = buscarMissao(missaoId);
        Agente agente = buscarAgente(agenteId);

        missao.designar(agente);

        return missao;
    }

    @Transactional
    public Missao autorizar(
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

        return missao;
    }

    @Transactional
    public Missao iniciar(Long missaoId) {
        Missao missao = buscarMissao(missaoId);

        missao.iniciar();

        missaoRepository.saveAndFlush(missao);

        return missao;
    }

    @Transactional
    public Intervencao registrarIntervencao(
            Long missaoId,
            Long agenteId,
            String acao,
            String justificativa,
            ImpactoIntervencao impacto,
            String resultado
    ) {
        Missao missao = buscarMissao(missaoId);
        Agente agente = buscarAgente(agenteId);

        Intervencao intervencao = missao.registrarIntervencao(
                agente,
                acao,
                justificativa,
                impacto,
                resultado
        );

        return intervencaoRepository.save(intervencao);
    }

    @Transactional
    public Missao concluir(
            Long missaoId,
            Encerramento encerramento
    ) {
        Missao missao = buscarMissao(missaoId);

        missao.concluir(encerramento);

        return missao;
    }

    @Transactional
    public Missao falhar(
            Long missaoId,
            Encerramento encerramento
    ) {
        Missao missao = buscarMissao(missaoId);

        missao.falhar(encerramento);

        return missao;
    }

    @Transactional(readOnly = true)
    public Missao buscar(Long id) {
        return buscarMissao(id);
    }

    @Transactional(readOnly = true)
    public Page<Missao> listar(Pageable pageable) {
        return missaoRepository.findAll(pageable);
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