package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImpactoIntervencao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IntervencaoRequest(

        @NotNull
        Long agenteId,

        @NotBlank
        String acao,

        @NotBlank
        String justificativa,

        @NotNull
        ImpactoIntervencao impacto,

        String resultado

) {
}