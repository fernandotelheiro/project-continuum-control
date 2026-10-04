package br.edu.fernandotelheiro.continuumcontrol.seguranca;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "login",
            nullable = false,
            unique = true,
            length = 100
    )
    private String login;

    @Column(
            name = "senha_hash",
            nullable = false,
            length = 100
    )
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "perfil",
            nullable = false,
            length = 30
    )
    private Perfil perfil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "agente_id",
            unique = true
    )
    private Agente agente;

    protected Usuario() {
    }

    public Usuario(
            String login,
            String senhaHash,
            Perfil perfil,
            Agente agente
    ) {
        this.login = login;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.agente = agente;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Agente getAgente() {
        return agente;
    }
}