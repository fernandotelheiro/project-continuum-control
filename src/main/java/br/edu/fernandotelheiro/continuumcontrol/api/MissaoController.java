package br.edu.fernandotelheiro.continuumcontrol.api;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;
import br.edu.fernandotelheiro.continuumcontrol.dto.AtualizarMissaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.AutorizarMissaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.CriarMissaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.DesignarAgenteRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.EncerramentoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.IntervencaoRequest;
import br.edu.fernandotelheiro.continuumcontrol.dto.IntervencaoResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.MissaoResponse;
import br.edu.fernandotelheiro.continuumcontrol.service.MissaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.edu.fernandotelheiro.continuumcontrol.dto.HistoricoResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import java.net.URI;

@RestController
@RequestMapping("/missoes")
public class MissaoController {

    private final MissaoService missaoService;

    public MissaoController(MissaoService missaoService) {
        this.missaoService = missaoService;
    }

    @GetMapping
    public Page<MissaoResponse> listar(
            @RequestParam(required = false) StatusMissao status,
            Pageable pageable
    ) {
        return missaoService.listar(status, pageable);
    }

    @GetMapping("/{id}")
    public MissaoResponse buscar(@PathVariable Long id) {
        return missaoService.buscar(id);
    }

    @GetMapping("/{id}/historico")
    public List<HistoricoResponse> historico(
            @PathVariable Long id
    ) {
        return missaoService.historico(id);
    }
    @PostMapping
    public ResponseEntity<MissaoResponse> criar(
            @Valid @RequestBody CriarMissaoRequest request
    ) {
        MissaoResponse response = missaoService.criar(request);

        return ResponseEntity
                .created(URI.create("/missoes/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public MissaoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarMissaoRequest request
    ) {
        return missaoService.atualizar(id, request);
    }

    @PostMapping("/{id}/agentes")
    public MissaoResponse designarAgente(
            @PathVariable Long id,
            @Valid @RequestBody DesignarAgenteRequest request
    ) {
        return missaoService.designarAgente(
                id,
                request.agenteId()
        );
    }

    @PostMapping("/{id}/autorizacao")
    public MissaoResponse autorizar(
            @PathVariable Long id,
            @Valid @RequestBody AutorizarMissaoRequest request
    ) {
        return missaoService.autorizar(
                id,
                request.responsavel(),
                request.observacao()
        );
    }

    @PostMapping("/{id}/iniciar")
    public MissaoResponse iniciar(@PathVariable Long id) {
        return missaoService.iniciar(id);
    }


    @PreAuthorize(
            "hasRole('SUPERVISOR') or " +
                    "(hasRole('AGENTE') and " +
                    "@guardaMissao.podeIntervir(#id, authentication.name))"
    )

    @PostMapping("/{id}/intervencoes")
    public ResponseEntity<IntervencaoResponse> registrarIntervencao(
            @PathVariable Long id,
            @Valid @RequestBody IntervencaoRequest request
    ) {
        IntervencaoResponse response =
                missaoService.registrarIntervencao(id, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{id}/concluir")
    public MissaoResponse concluir(
            @PathVariable Long id,
            @Valid @RequestBody EncerramentoRequest request
    ) {
        return missaoService.concluir(id, request);
    }

    @PostMapping("/{id}/falhar")
    public MissaoResponse falhar(
            @PathVariable Long id,
            @Valid @RequestBody EncerramentoRequest request
    ) {
        return missaoService.falhar(id, request);
    }
}