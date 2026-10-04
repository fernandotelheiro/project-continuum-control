package br.edu.fernandotelheiro.continuumcontrol.seguranca;

import br.edu.fernandotelheiro.continuumcontrol.repository.ParticipacaoMissaoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component("guardaMissao")
public class GuardaMissao {

    private final UsuarioRepository usuarioRepository;
    private final ParticipacaoMissaoRepository participacaoRepository;

    public GuardaMissao(
            UsuarioRepository usuarioRepository,
            ParticipacaoMissaoRepository participacaoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.participacaoRepository = participacaoRepository;
    }

    public boolean podeIntervir(
            Long missaoId,
            String login
    ) {
        return usuarioRepository
                .findByLogin(login)
                .filter(usuario ->
                        usuario.getPerfil() == Perfil.AGENTE
                )
                .map(Usuario::getAgente)
                .filter(agente -> agente != null)
                .map(agente ->
                        participacaoRepository
                                .existsByMissao_IdAndAgente_Id(
                                        missaoId,
                                        agente.getId()
                                )
                )
                .orElse(false);
    }
}