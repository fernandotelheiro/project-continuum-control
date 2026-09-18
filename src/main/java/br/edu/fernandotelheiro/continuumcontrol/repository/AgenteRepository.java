package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenteRepository extends JpaRepository<Agente, Long> {
}