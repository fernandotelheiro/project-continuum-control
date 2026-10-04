package br.edu.fernandotelheiro.continuumcontrol.seguranca;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.repository.AgenteRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuariosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AgenteRepository agenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final String senhaInicial;

    public UsuariosIniciais(
            UsuarioRepository usuarioRepository,
            AgenteRepository agenteRepository,
            PasswordEncoder passwordEncoder,
            @Value("${continuum.senha-inicial}")
            String senhaInicial
    ) {
        this.usuarioRepository = usuarioRepository;
        this.agenteRepository = agenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.senhaInicial = senhaInicial;
    }

    @Override
    public void run(String... args) {

        criarSeNaoExistir(
                "operador",
                Perfil.OPERADOR,
                null
        );

        criarSeNaoExistir(
                "supervisor",
                Perfil.SUPERVISOR,
                null
        );

        criarSeNaoExistir(
                "admin",
                Perfil.ADMINISTRADOR,
                null
        );

        agenteRepository.findById(1L)
                .ifPresent(agente ->
                        criarSeNaoExistir(
                                "agente1",
                                Perfil.AGENTE,
                                agente
                        )
                );
    }

    private void criarSeNaoExistir(
            String login,
            Perfil perfil,
            Agente agente
    ) {
        if (usuarioRepository.findByLogin(login).isPresent()) {
            return;
        }

        Usuario usuario = new Usuario(
                login,
                passwordEncoder.encode(senhaInicial),
                perfil,
                agente
        );

        usuarioRepository.save(usuario);
    }
}