package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.Intervencao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImpactoIntervencao;

import java.time.OffsetDateTime;

public record IntervencaoResponse(

        Long id,
        Long missaoId,
        Long agenteId,
        OffsetDateTime instante,
        String acao,
        String justificativa,
        ImpactoIntervencao impacto,
        String resultado

) {

    public static IntervencaoResponse de(Intervencao intervencao) {
        return new IntervencaoResponse(
                intervencao.getId(),
                intervencao.getMissao().getId(),
                intervencao.getAgente().getId(),
                intervencao.getInstante(),
                intervencao.getAcao(),
                intervencao.getJustificativa(),
                intervencao.getImpacto(),
                intervencao.getResultado()
        );
    }
}