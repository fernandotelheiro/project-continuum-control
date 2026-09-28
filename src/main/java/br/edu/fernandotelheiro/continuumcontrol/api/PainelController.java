package br.edu.fernandotelheiro.continuumcontrol.api;

import br.edu.fernandotelheiro.continuumcontrol.dto.EstabilidadeResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.EstadoOperacionalResponse;
import br.edu.fernandotelheiro.continuumcontrol.service.PainelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PainelController {

    private final PainelService painelService;

    public PainelController(PainelService painelService) {
        this.painelService = painelService;
    }

    @GetMapping("/estado-operacional")
    public EstadoOperacionalResponse estadoOperacional() {
        return painelService.estadoOperacional();
    }

    @GetMapping("/estabilidade")
    public EstabilidadeResponse estabilidade() {
        return painelService.estabilidade();
    }
}