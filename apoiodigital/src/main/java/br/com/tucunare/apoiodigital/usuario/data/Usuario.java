package br.com.tucunare.apoiodigital.usuario.data;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Data
@Table(name = "usuario")
@NoArgsConstructor
public class Usuario {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "nome")
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    public Usuario(String externalId, String nome, Cliente cliente) {
        this.externalId = externalId;
        this.nome = nome;
        this.cliente = cliente;
    }
}
