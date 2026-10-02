package br.edu.fernandotelheiro.continuumcontrol.evento;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record MissaoMudouDeEstado(
        Long missaoId,
        String codigo,
        Long anomaliaId,
        List<Long> agenteIds,
        String tipo,
        StatusMissao statusMissao,
        OffsetDateTime instante,
        Map<String, Object> detalhes
) {
}