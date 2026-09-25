package br.edu.fernandotelheiro.continuumcontrol.api;

import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarEventoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarEventoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.EventoResponse;
import br.edu.fernandotelheiro.continuumcontrol.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public Page<EventoResponse> listar(Pageable pageable) {
        return eventoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable Long id) {
        return eventoService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criar(
            @Valid @RequestBody CriarEventoRequest request
    ) {
        EventoResponse response = eventoService.criar(request);

        return ResponseEntity
                .created(URI.create("/eventos/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public EventoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarEventoRequest request
    ) {
        return eventoService.atualizar(id, request);
    }
}