package br.edu.fernandotelheiro.continuumcontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarAgenteRequest(

        @NotBlank
        @Size(max = 120)
        String nome,

        @NotBlank
        @Size(max = 120)
        String especialidade

) {
}