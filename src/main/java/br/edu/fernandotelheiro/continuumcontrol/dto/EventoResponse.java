package br.edu.fernandotelheiro.continuumcontrol.dto;

import br.edu.fernandotelheiro.continuumcontrol.domain.EventoHistorico;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImportanciaEvento;

import java.time.OffsetDateTime;

public record EventoResponse(
        Long id,
        String titulo,
        String descricao,
        OffsetDateTime dataEvento,
        String localizacao,
        ImportanciaEvento importancia
) {

    public static EventoResponse de(EventoHistorico evento) {
        return new EventoResponse(
                evento.getId(),
                evento.getTitulo(),
                evento.getDescricao(),
                evento.getDataEvento(),
                evento.getLocalizacao(),
                evento.getImportancia()
        );
    }
}