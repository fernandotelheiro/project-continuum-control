package br.edu.fernandotelheiro.continuumcontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AutorizarMissaoRequest(

        @NotBlank
        @Size(max = 120)
        String responsavel,

        @NotBlank
        String observacao


) {
}