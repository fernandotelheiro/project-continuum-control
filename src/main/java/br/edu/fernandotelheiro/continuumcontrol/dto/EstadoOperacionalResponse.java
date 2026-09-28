package br.edu.fernandotelheiro.continuumcontrol.dto;

import java.io.Serializable;

public record EstadoOperacionalResponse(
        long agentesDisponiveis,
        long agentesEmMissao,
        long missoesEmExecucao,
        long anomaliasAtivas,
        long anomaliasCriticas,
        int indiceEstabilidade,
        String rotulo
) implements Serializable {
}