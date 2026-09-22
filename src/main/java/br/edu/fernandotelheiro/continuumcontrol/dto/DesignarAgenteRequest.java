package br.edu.fernandotelheiro.continuumcontrol.dto;

import jakarta.validation.constraints.NotNull;

public record DesignarAgenteRequest(

        @NotNull
        Long agenteId

) {
}