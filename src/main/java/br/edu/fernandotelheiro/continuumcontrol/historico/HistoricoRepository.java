package br.edu.fernandotelheiro.continuumcontrol.historico;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoricoRepository
        extends MongoRepository<RegistroHistorico, String> {

    List<RegistroHistorico>
    findByMissaoIdOrderByInstanteAsc(Long missaoId);
}