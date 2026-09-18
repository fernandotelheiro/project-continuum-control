package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.ParticipacaoMissao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipacaoMissaoRepository
        extends JpaRepository<ParticipacaoMissao, Long> {
}