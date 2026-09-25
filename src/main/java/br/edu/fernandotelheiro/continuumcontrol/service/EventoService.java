package br.edu.fernandotelheiro.continuumcontrol.service;

import br.edu.fernandotelheiro.continuumcontrol.domain.EventoHistorico;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.RecursoNaoEncontradoException;
import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarEventoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarEventoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.EventoResponse;
import br.edu.fernandotelheiro.continuumcontrol.repository.EventoHistoricoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoHistoricoRepository eventoRepository;

    public EventoService(
            EventoHistoricoRepository eventoRepository
    ) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public EventoResponse criar(CriarEventoRequest request) {
        EventoHistorico evento = new EventoHistorico(
                request.titulo(),
                request.descricao(),
                request.dataEvento(),
                request.localizacao(),
                request.importancia()
        );

        eventoRepository.save(evento);

        return EventoResponse.de(evento);
    }

    @Transactional
    public EventoResponse atualizar(
            Long id,
            AtualizarEventoRequest request
    ) {
        EventoHistorico evento = buscarEvento(id);

        evento.atualizar(
                request.titulo(),
                request.descricao(),
                request.dataEvento(),
                request.localizacao(),
                request.importancia()
        );

        return EventoResponse.de(evento);
    }

    @Transactional(readOnly = true)
    public EventoResponse buscar(Long id) {
        return EventoResponse.de(buscarEvento(id));
    }

    @Transactional(readOnly = true)
    public Page<EventoResponse> listar(Pageable pageable) {
        return eventoRepository
                .findAll(pageable)
                .map(EventoResponse::de);
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