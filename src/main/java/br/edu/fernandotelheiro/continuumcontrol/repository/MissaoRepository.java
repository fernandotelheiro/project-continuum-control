package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.Missao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissaoRepository extends JpaRepository<Missao, Long> {

    Page<Missao> findByStatus(
            StatusMissao status,
            Pageable pageable
    );
}