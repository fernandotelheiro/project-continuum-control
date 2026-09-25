package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImportanciaEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record AtualizarEventoRequest(

        @NotBlank
        String titulo,

        @NotBlank
        String descricao,

        @NotNull
        OffsetDateTime dataEvento,

        @NotBlank
        String localizacao,

        @NotNull
        ImportanciaEvento importancia

) {
}