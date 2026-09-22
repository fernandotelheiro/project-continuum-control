package br.edu.fernandotelheiro.continuumcontrol.dto;

import jakarta.validation.constraints.NotBlank;

public record AutorizarMissaoRequest(

        @NotBlank
        String responsavel,

        @NotBlank
        String observacao

) {
}