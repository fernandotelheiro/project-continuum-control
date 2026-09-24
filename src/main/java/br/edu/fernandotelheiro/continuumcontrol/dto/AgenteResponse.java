package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;

public record AgenteResponse(
        Long id,
        String nome,
        String especialidade,
        StatusAgente status,
        Long versao
) {

    public static AgenteResponse de(Agente agente) {
        return new AgenteResponse(
                agente.getId(),
                agente.getNome(),
                agente.getEspecialidade(),
                agente.getStatus(),
                agente.getVersao()
        );
    }
}