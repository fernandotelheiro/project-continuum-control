package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.*;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.RegraDeNegocioException;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.TransicaoInvalidaException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MissaoTest {

    @Test
    void naoDeveDesignarAgenteSuspenso() {
        Missao missao = criarMissaoNormal();
        Agente agente = new Agente("Agente 1", "Campo");

        agente.suspender();

        assertThrows(
                RegraDeNegocioException.class,
                () -> missao.designar(agente)
        );
    }

    @Test
    void naoDeveDesignarAgenteQueJaEstaEmMissao() {
        Missao missao = criarMissaoNormal();
        Agente agente = new Agente("Agente 1", "Campo");

        agente.entrarEmMissao();

        assertThrows(
                RegraDeNegocioException.class,
                () -> missao.designar(agente)
        );
    }

    @Test
    void anomaliaIrreversivelNaoDeveAceitarNovaMissao() {
        Anomalia anomalia = criarAnomalia(RiscoAnomalia.ALTO);
        anomalia.marcarIrreversivel();

        assertThrows(
                RegraDeNegocioException.class,
                () -> new Missao(
                        anomalia,
                        "MIS-001",
                        "Corrigir anomalia",
                        OffsetDateTime.now().plusDays(1),
                        PrioridadeMissao.NORMAL
                )
        );
    }

    @Test
    void missaoEmergencialDeveExigirAutorizacao() {
        Anomalia anomalia = criarAnomalia(RiscoAnomalia.ALTO);

        Missao missao = new Missao(
                anomalia,
                "MIS-001",
                "Corrigir anomalia",
                OffsetDateTime.now().plusDays(1),
                PrioridadeMissao.EMERGENCIA
        );

        Agente agente = new Agente("Agente 1", "Campo");
        missao.designar(agente);

        assertThrows(
                TransicaoInvalidaException.class,
                missao::iniciar
        );

        missao.autorizar();
        missao.iniciar();

        assertEquals(StatusMissao.EM_EXECUCAO, missao.getStatus());
    }

    @Test
    void anomaliaCriticaDeveExigirAutorizacao() {
        Anomalia anomalia = criarAnomalia(RiscoAnomalia.CRITICO);

        Missao missao = new Missao(
                anomalia,
                "MIS-001",
                "Corrigir anomalia crítica",
                OffsetDateTime.now().plusDays(1),
                PrioridadeMissao.NORMAL
        );

        Agente agente = new Agente("Agente 1", "Campo");
        missao.designar(agente);

        assertThrows(
                TransicaoInvalidaException.class,
                missao::iniciar
        );

        missao.autorizar();
        missao.iniciar();

        assertEquals(StatusMissao.EM_EXECUCAO, missao.getStatus());
    }

    @Test
    void missaoSemAgenteNaoDeveIniciar() {
        Missao missao = criarMissaoNormal();

        assertThrows(
                RegraDeNegocioException.class,
                missao::iniciar
        );
    }

    @Test
    void iniciarMissaoDeveAtualizarAgenteEAnomalia() {
        Anomalia anomalia = criarAnomalia(RiscoAnomalia.ALTO);

        Missao missao = new Missao(
                anomalia,
                "MIS-001",
                "Corrigir anomalia",
                OffsetDateTime.now().plusDays(1),
                PrioridadeMissao.NORMAL
        );

        Agente agente = new Agente("Agente 1", "Campo");

        missao.designar(agente);
        missao.iniciar();

        assertEquals(StatusMissao.EM_EXECUCAO, missao.getStatus());
        assertEquals(StatusAgente.EM_MISSAO, agente.getStatus());
        assertEquals(StatusAnomalia.EM_CORRECAO, anomalia.getStatus());
    }

    @Test
    void missaoPlanejadaNaoDeveAceitarIntervencao() {
        Missao missao = criarMissaoNormal();
        Agente agente = new Agente("Agente 1", "Campo");
        missao.designar(agente);

        assertThrows(
                TransicaoInvalidaException.class,
                () -> missao.registrarIntervencao(agente, "Ajuste", "Teste", ImpactoIntervencao.MINIMO, null)
        );
    }

    @Test
    void falharMissaoDeveLiberarAgentesEReabrirAnomalia() {
        Anomalia anomalia = criarAnomalia(RiscoAnomalia.ALTO);

        Missao missao = new Missao(
                anomalia,
                "MIS-FALHA-001",
                "Corrigir anomalia",
                OffsetDateTime.now().plusDays(1),
                PrioridadeMissao.NORMAL
        );

        Agente agente = new Agente(
                "Agente 1",
                "Operações temporais"
        );

        missao.designar(agente);
        missao.iniciar();

        missao.falhar(
                new Encerramento(
                        "FALHA",
                        "resumo",
                        "SIGNIFICATIVO"
                )
        );

        assertEquals(
                StatusMissao.FALHOU,
                missao.getStatus()
        );

        assertEquals(
                StatusAgente.DISPONIVEL,
                agente.getStatus()
        );

        assertEquals(
                StatusAnomalia.CONFIRMADA,
                anomalia.getStatus()
        );
    }

    @Test
    void naoDeveIniciarSeUmDosAgentesNaoEstiverMaisDisponivel() {
        Anomalia anomalia = criarAnomalia(RiscoAnomalia.ALTO);

        Missao missao = new Missao(
                anomalia,
                "MIS-002",
                "Corrigir anomalia",
                OffsetDateTime.now().plusDays(1),
                PrioridadeMissao.NORMAL
        );

        Agente agente1 = new Agente("Agente 1", "Campo");
        Agente agente2 = new Agente("Agente 2", "Campo");

        missao.designar(agente1);
        missao.designar(agente2);

        agente2.entrarEmMissao();

        assertThrows(
                RegraDeNegocioException.class,
                missao::iniciar
        );

        assertEquals(
                StatusAgente.DISPONIVEL,
                agente1.getStatus()
        );

        assertEquals(
                StatusMissao.PLANEJADA,
                missao.getStatus()
        );

        assertEquals(
                StatusAnomalia.DETECTADA,
                anomalia.getStatus()
        );
    }

    private Missao criarMissaoNormal() {
        return new Missao(
                criarAnomalia(RiscoAnomalia.ALTO),
                "MIS-001",
                "Corrigir anomalia",
                OffsetDateTime.now().plusDays(1),
                PrioridadeMissao.NORMAL
        );
    }

    private Anomalia criarAnomalia(RiscoAnomalia risco) {
        EventoHistorico evento = new EventoHistorico(
                "Evento temporal",
                "Alteração detectada na linha temporal.",
                OffsetDateTime.now().minusDays(1),
                "Rio de Janeiro",
                ImportanciaEvento.ALTA
        );

        return new Anomalia(
                evento,
                "Anomalia temporal detectada.",
                OffsetDateTime.now(),
                "Origem desconhecida",
                null,
                risco
        );
    }
}