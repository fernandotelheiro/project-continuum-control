package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.Intervencao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntervencaoRepository extends JpaRepository<Intervencao, Long> {
}