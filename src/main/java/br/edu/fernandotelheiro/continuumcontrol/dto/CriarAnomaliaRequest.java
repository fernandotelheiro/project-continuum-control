package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CriarAnomaliaRequest(

        @NotNull
        Long eventoHistoricoId,

        @NotBlank
        String descricao,

        @NotNull
        OffsetDateTime detectadaEm,

        String origem,

        String observacoes,

        @NotNull
        RiscoAnomalia risco

) {
}