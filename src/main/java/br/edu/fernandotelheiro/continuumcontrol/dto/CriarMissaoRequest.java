package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CriarMissaoRequest(

        @NotNull
        Long anomaliaId,

        @NotBlank
        @Size(max = 50)
        String codigo,

        @NotBlank
        String objetivo,

        @NotNull
        OffsetDateTime destinoTemporal,

        @NotNull
        PrioridadeMissao prioridade

) {
}