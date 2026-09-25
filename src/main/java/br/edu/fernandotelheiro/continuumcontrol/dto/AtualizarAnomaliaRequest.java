package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarAnomaliaRequest(

        @NotBlank
        String descricao,

        String origem,

        String observacoes,

        @NotNull
        RiscoAnomalia risco

) {
}