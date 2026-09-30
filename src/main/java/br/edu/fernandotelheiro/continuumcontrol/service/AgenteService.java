package br.edu.fernandotelheiro.continuumcontrol.service;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.RecursoNaoEncontradoException;
import br.edu.fernandotelheiro.continuumcontrol.dto.AgenteResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarAgenteRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarAgenteRequest;
import br.edu.fernandotelheiro.continuumcontrol.repository.AgenteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class AgenteService {

    private final AgenteRepository agenteRepository;

    public AgenteService(AgenteRepository agenteRepository) {
        this.agenteRepository = agenteRepository;
    }

    @Transactional
    @CacheEvict(
            cacheNames = {"estado-operacional", "estabilidade"},
            allEntries = true
    )
    public AgenteResponse criar(CriarAgenteRequest request) {
        Agente agente = new Agente(
                request.nome(),
                request.especialidade()
        );

        agenteRepository.save(agente);

        return AgenteResponse.de(agente);
    }

    @Transactional
    public AgenteResponse atualizar(
            Long id,
            AtualizarAgenteRequest request
    ) {
        Agente agente = buscarAgente(id);

        agente.atualizarCadastro(
                request.nome(),
                request.especialidade()
        );

        return AgenteResponse.de(agente);
    }

    @Transactional(readOnly = true)
    public AgenteResponse buscar(Long id) {
        return AgenteResponse.de(buscarAgente(id));
    }

    @Transactional(readOnly = true)
    public Page<AgenteResponse> listar(
            StatusAgente status,
            Pageable pageable
    ) {
        Page<Agente> agentes;

        if (status != null) {
            agentes = agenteRepository.findByStatus(
                    status,
                    pageable
            );
        } else {
            agentes = agenteRepository.findAll(pageable);
        }

        return agentes.map(AgenteResponse::de);
    }

    @Transactional
    @CacheEvict(
            cacheNames = {"estado-operacional", "estabilidade"},
            allEntries = true
    )
    public void remover(Long id) {
        Agente agente = buscarAgente(id);

        agenteRepository.delete(agente);
        agenteRepository.flush();
    }

    private Agente buscarAgente(Long id) {
        return agenteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Agente " + id + " não encontrado."
                        )
                );
    }
}