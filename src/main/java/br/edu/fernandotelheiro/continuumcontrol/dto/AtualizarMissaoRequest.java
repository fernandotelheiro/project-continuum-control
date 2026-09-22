package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record AtualizarMissaoRequest(

        @NotBlank
        String objetivo,

        @NotNull
        OffsetDateTime destinoTemporal,

        @NotNull
        PrioridadeMissao prioridade

) {
}