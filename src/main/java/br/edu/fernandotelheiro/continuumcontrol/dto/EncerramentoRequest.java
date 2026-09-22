package br.edu.fernandotelheiro.continuumcontrol.dto;

import jakarta.validation.constraints.NotBlank;

public record EncerramentoRequest(

        @NotBlank
        String resultado,

        @NotBlank
        String resumo,

        String impactoObservado

) {
}