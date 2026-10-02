package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.historico.RegistroHistorico;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record HistoricoResponse(
        String id,
        Long missaoId,
        String codigoMissao,
        Long anomaliaId,
        List<Long> agenteIds,
        String tipo,
        String statusMissao,
        Instant instante,
        Map<String, Object> detalhes
) {

    public static HistoricoResponse de(
            RegistroHistorico registro
    ) {
        return new HistoricoResponse(
                registro.getId(),
                registro.getMissaoId(),
                registro.getCodigoMissao(),
                registro.getAnomaliaId(),
                registro.getAgenteIds(),
                registro.getTipo(),
                registro.getStatusMissao(),
                registro.getInstante(),
                registro.getDetalhes()
        );
    }
}