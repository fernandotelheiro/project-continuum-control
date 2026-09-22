package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.Missao;
import br.edu.fernandotelheiro.continuumcontrol.domain.ParticipacaoMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;

import java.time.OffsetDateTime;
import java.util.List;

public record MissaoResponse(

        Long id,
        String codigo,
        String objetivo,
        OffsetDateTime destinoTemporal,
        PrioridadeMissao prioridade,
        StatusMissao status,
        Long anomaliaId,
        List<Long> agenteIds,
        boolean exigeAutorizacao,
        Long versao

) {

    public static MissaoResponse de(Missao missao) {

        List<Long> agenteIds = missao.getParticipacoes()
                .stream()
                .map(ParticipacaoMissao::getAgente)
                .map(agente -> agente.getId())
                .toList();

        return new MissaoResponse(
                missao.getId(),
                missao.getCodigo(),
                missao.getObjetivo(),
                missao.getDestinoTemporal(),
                missao.getPrioridade(),
                missao.getStatus(),
                missao.getAnomalia().getId(),
                agenteIds,
                missao.exigeAutorizacao(),
                missao.getVersao()
        );
    }
}