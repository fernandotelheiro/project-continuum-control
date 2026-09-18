package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnomaliaRepository extends JpaRepository<Anomalia, Long> {
}