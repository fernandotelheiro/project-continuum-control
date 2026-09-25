package br.edu.fernandotelheiro.continuumcontrol.service;

import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.EventoHistorico;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.RecursoNaoEncontradoException;
import br.edu.fernandotelheiro.continuumcontrol.dto.AnomaliaResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarAnomaliaRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarAnomaliaRequest;
import br.edu.fernandotelheiro.continuumcontrol.repository.AnomaliaRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.EventoHistoricoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnomaliaService {

    private final AnomaliaRepository anomaliaRepository;
    private final EventoHistoricoRepository eventoRepository;

    public AnomaliaService(
            AnomaliaRepository anomaliaRepository,
            EventoHistoricoRepository eventoRepository
    ) {
        this.anomaliaRepository = anomaliaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public AnomaliaResponse criar(
            CriarAnomaliaRequest request
    ) {
        EventoHistorico evento = buscarEvento(
                request.eventoHistoricoId()
        );

        Anomalia anomalia = new Anomalia(
                evento,
                request.descricao(),
                request.detectadaEm(),
                request.origem(),
                request.observacoes(),
                request.risco()
        );

        anomaliaRepository.save(anomalia);

        return AnomaliaResponse.de(anomalia);
    }

    @Transactional
    public AnomaliaResponse atualizar(
            Long id,
            AtualizarAnomaliaRequest request
    ) {
        Anomalia anomalia = buscarAnomalia(id);

        anomalia.reavaliarRisco(
                request.descricao(),
                request.origem(),
                request.observacoes(),
                request.risco()
        );

        return AnomaliaResponse.de(anomalia);
    }

    @Transactional(readOnly = true)
    public AnomaliaResponse buscar(Long id) {
        return AnomaliaResponse.de(buscarAnomalia(id));
    }

    @Transactional(readOnly = true)
    public Page<AnomaliaResponse> listar(
            RiscoAnomalia risco,
            StatusAnomalia status,
            Pageable pageable
    ) {
        Page<Anomalia> anomalias;

        if (risco != null && status != null) {
            anomalias = anomaliaRepository.findByRiscoAndStatus(
                    risco,
                    status,
                    pageable
            );
        } else if (risco != null) {
            anomalias = anomaliaRepository.findByRisco(
                    risco,
                    pageable
            );
        } else if (status != null) {
            anomalias = anomaliaRepository.findByStatus(
                    status,
                    pageable
            );
        } else {
            anomalias = anomaliaRepository.findAll(pageable);
        }

        return anomalias.map(AnomaliaResponse::de);
    }

    private Anomalia buscarAnomalia(Long id) {
        return anomaliaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Anomalia " + id + " não encontrada."
                        )
                );
    }

    private EventoHistorico buscarEvento(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Evento histórico " + id + " não encontrado."
                        )
                );
    }
}