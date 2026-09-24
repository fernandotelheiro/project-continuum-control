package br.edu.fernandotelheiro.continuumcontrol.api;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import br.edu.fernandotelheiro.continuumcontrol.dto.AgenteResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarAgenteRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarAgenteRequest;
import br.edu.fernandotelheiro.continuumcontrol.service.AgenteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/agentes")
public class AgenteController {

    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    @GetMapping
    public Page<AgenteResponse> listar(
            @RequestParam(required = false) StatusAgente status,
            Pageable pageable
    ) {
        return agenteService.listar(status, pageable);
    }

    @GetMapping("/{id}")
    public AgenteResponse buscar(@PathVariable Long id) {
        return agenteService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AgenteResponse> criar(
            @Valid @RequestBody CriarAgenteRequest request
    ) {
        AgenteResponse response = agenteService.criar(request);

        return ResponseEntity
                .created(URI.create("/agentes/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public AgenteResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarAgenteRequest request
    ) {
        return agenteService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Long id
    ) {
        agenteService.remover(id);

        return ResponseEntity.noContent().build();
    }
}