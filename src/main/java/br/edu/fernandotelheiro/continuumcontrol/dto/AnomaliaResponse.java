package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAnomalia;

import java.time.OffsetDateTime;

public record AnomaliaResponse(
        Long id,
        Long eventoHistoricoId,
        String descricao,
        OffsetDateTime detectadaEm,
        String origem,
        String observacoes,
        RiscoAnomalia risco,
        StatusAnomalia status
) {

    public static AnomaliaResponse de(Anomalia anomalia) {
        return new AnomaliaResponse(
                anomalia.getId(),
                anomalia.getEventoHistorico().getId(),
                anomalia.getDescricao(),
                anomalia.getDetectadaEm(),
                anomalia.getOrigem(),
                anomalia.getObservacoes(),
                anomalia.getRisco(),
                anomalia.getStatus()
        );
    }
}