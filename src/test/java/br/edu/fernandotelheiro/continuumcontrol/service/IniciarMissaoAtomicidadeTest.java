package br.edu.fernandotelheiro.continuumcontrol.service;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarMissaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.MissaoResponse;
import br.edu.fernandotelheiro.continuumcontrol.TestcontainersConfiguration;
import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.EventoHistorico;
import br.edu.fernandotelheiro.continuumcontrol.domain.Missao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImportanciaEvento;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;
import br.edu.fernandotelheiro.continuumcontrol.repository.AgenteRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AnomaliaRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.EventoHistoricoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.MissaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import jakarta.persistence.EntityManager;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class IniciarMissaoAtomicidadeTest {

    @Autowired
    private MissaoService missaoService;

    @Autowired
    private AgenteRepository agenteRepository;

    @Autowired
    private AnomaliaRepository anomaliaRepository;

    @Autowired
    private EventoHistoricoRepository eventoRepository;

    @Autowired
    private EntityManager entityManager;

    @MockitoSpyBean
    private MissaoRepository missaoRepository;

    @Test
    void falhaNoMeioDoIniciarDeveDesfazerTudo() {

        // ARRANGE

        EventoHistorico evento = eventoRepository.save(
                new EventoHistorico(
                        "Evento para teste de atomicidade",
                        "Evento histórico usado no teste transacional.",
                        OffsetDateTime.now().minusDays(1),
                        "Rio de Janeiro",
                        ImportanciaEvento.ALTA
                )
        );

        Anomalia anomalia = anomaliaRepository.save(
                new Anomalia(
                        evento,
                        "Anomalia para teste de atomicidade.",
                        OffsetDateTime.now(),
                        "Teste",
                        null,
                        RiscoAnomalia.BAIXO
                )
        );

        Agente agente1 = agenteRepository.save(
                new Agente(
                        "Agente Atomicidade 1",
                        "Operações temporais"
                )
        );

        Agente agente2 = agenteRepository.save(
                new Agente(
                        "Agente Atomicidade 2",
                        "Operações temporais"
                )
        );


        MissaoResponse missao = missaoService.criar(
                new CriarMissaoRequest(
                        anomalia.getId(),
                        "MIS-ATOMICIDADE-001",
                        "Validar rollback da operação de início",
                        OffsetDateTime.now().plusDays(1),
                        PrioridadeMissao.NORMAL
                )
        );

        missaoService.designarAgente(
                missao.id(),
                agente1.getId()
        );

        missaoService.designarAgente(
                missao.id(),
                agente2.getId()
        );

        Long missaoId = missao.id();
        Long anomaliaId = anomalia.getId();
        Long agente1Id = agente1.getId();
        Long agente2Id = agente2.getId();

        /*
         * Deixa o saveAndFlush real acontecer.
         * Só depois lança a exceção.
         *
         * Portanto, os UPDATEs já chegaram ao PostgreSQL,
         * mas ainda estão dentro da transação do service.
         */
        doAnswer(invocacao -> {
            entityManager.flush();

            throw new RuntimeException(
                    "Falha simulada após o flush"
            );
        }).when(missaoRepository).saveAndFlush(any());

        // ACT

        RuntimeException erro = assertThrows(
                RuntimeException.class,
                () -> missaoService.iniciar(missaoId)
        );

        assertEquals(
                "Falha simulada após o flush",
                erro.getMessage()
        );

        // ASSERT
        // Releitura real do banco depois do rollback.

        Missao missaoPersistida = missaoRepository
                .findById(missaoId)
                .orElseThrow();

        Agente agente1Persistido = agenteRepository
                .findById(agente1Id)
                .orElseThrow();

        Agente agente2Persistido = agenteRepository
                .findById(agente2Id)
                .orElseThrow();

        Anomalia anomaliaPersistida = anomaliaRepository
                .findById(anomaliaId)
                .orElseThrow();

        assertEquals(
                StatusMissao.PLANEJADA,
                missaoPersistida.getStatus()
        );

        assertEquals(
                StatusAgente.DISPONIVEL,
                agente1Persistido.getStatus()
        );

        assertEquals(
                StatusAgente.DISPONIVEL,
                agente2Persistido.getStatus()
        );

        assertEquals(
                StatusAnomalia.DETECTADA,
                anomaliaPersistida.getStatus()
        );
    }
}