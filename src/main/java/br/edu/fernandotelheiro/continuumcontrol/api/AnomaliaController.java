package br.edu.fernandotelheiro.continuumcontrol.api;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.dto.AnomaliaResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarAnomaliaRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarAnomaliaRequest;
import br.edu.fernandotelheiro.continuumcontrol.service.AnomaliaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/anomalias")
public class AnomaliaController {

    private final AnomaliaService anomaliaService;

    public AnomaliaController(AnomaliaService anomaliaService) {
        this.anomaliaService = anomaliaService;
    }

    @GetMapping
    public Page<AnomaliaResponse> listar(
            @RequestParam(required = false) RiscoAnomalia risco,
            @RequestParam(required = false) StatusAnomalia status,
            Pageable pageable
    ) {
        return anomaliaService.listar(
                risco,
                status,
                pageable
        );
    }

    @GetMapping("/{id}")
    public AnomaliaResponse buscar(
            @PathVariable Long id
    ) {
        return anomaliaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AnomaliaResponse> criar(
            @Valid @RequestBody CriarAnomaliaRequest request
    ) {
        AnomaliaResponse response =
                anomaliaService.criar(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/anomalias/" + response.id()
                        )
                )
                .body(response);
    }

    @PutMapping("/{id}")
    public AnomaliaResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarAnomaliaRequest request
    ) {
        return anomaliaService.atualizar(id, request);
    }
}