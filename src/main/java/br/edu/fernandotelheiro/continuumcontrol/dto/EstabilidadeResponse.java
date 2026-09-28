package br.edu.fernandotelheiro.continuumcontrol.dto;

import java.io.Serializable;

public record EstabilidadeResponse(
        int indice,
        String rotulo,
        long anomaliasCriticasAtivas,
        long anomaliasAtivas,
        long missoesEmExecucao,
        long missoesFalhas,
        long intervencoesSeverasUltimos30Dias
) implements Serializable {
}