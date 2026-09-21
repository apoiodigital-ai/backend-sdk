package br.com.tucunare.apoiodigital.cliente.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "access_key", nullable = false, unique = true)
    private String accessKey;

    @Column(name = "area_atuacao")
    private String areaAtuacao;

    public Cliente(String nome, String accessKey, String areaAtuacao) {
        this.nome = nome;
        this.accessKey = accessKey;
        this.areaAtuacao = areaAtuacao;
    }
}
