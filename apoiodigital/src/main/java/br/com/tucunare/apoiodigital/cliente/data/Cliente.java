package br.com.tucunare.apoiodigital.cliente.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Data
@Table(name="Cliente")
@NoArgsConstructor
public class Cliente {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "access_key", unique = true)
    private String accessKey;

    @Column(name = "area_atuacao")
    private String areaAtuacao;

    public Cliente(String nome, String accessKey, String areaAtuacao) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.accessKey = accessKey;
        this.areaAtuacao = areaAtuacao;
    }
}
