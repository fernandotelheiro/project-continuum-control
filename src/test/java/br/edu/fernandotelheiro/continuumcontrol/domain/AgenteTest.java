package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.TransicaoInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgenteTest {

    @Test
    void agenteNovoDeveEstarDisponivel() {
        Agente agente = new Agente("Fernando", "Operações temporais");

        assertTrue(agente.estaDisponivel());
        assertEquals(StatusAgente.DISPONIVEL, agente.getStatus());
    }

    @Test
    void agenteDisponivelPodeEntrarEmMissao() {
        Agente agente = new Agente("Fernando", "Operações temporais");

        agente.entrarEmMissao();

        assertEquals(StatusAgente.EM_MISSAO, agente.getStatus());
    }

    @Test
    void agenteEmMissaoPodeSerLiberado() {
        Agente agente = new Agente("Fernando", "Operações temporais");

        agente.entrarEmMissao();
        agente.liberar();

        assertEquals(StatusAgente.DISPONIVEL, agente.getStatus());
    }

    @Test
    void agenteSuspensoNaoPodeEntrarEmMissao() {
        Agente agente = new Agente("Fernando", "Operações temporais");

        agente.suspender();

        assertThrows(
                TransicaoInvalidaException.class,
                agente::entrarEmMissao
        );
    }
}