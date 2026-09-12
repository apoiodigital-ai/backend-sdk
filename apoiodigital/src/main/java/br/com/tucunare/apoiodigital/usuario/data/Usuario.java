package br.com.tucunare.apoiodigital.usuario.data;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * An end user of a partner ({@link Cliente}) app. The partner app supplies an
 * already-anonymized identifier (an opaque hash minted on THEIR side — doc V2 §2.4) which the
 * SDK forwards verbatim as {@code userId}; it is stored here as {@link #externalId}, unique
 * per tenant, and the row is auto-provisioned on first use (see
 * {@code UsuarioService#resolverOuCriar}). The internal UUID {@link #id} stays as the FK
 * target for Pedido. This backend never stores a phone number or a password for end users.
 * (The old standalone-app fields {@code telefone}/{@code senha} were removed along with the
 * phone+password login flow they supported; see the refactor report for why.)
 */
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

    /** The partner-minted anonymized identifier ({@code userId} on the wire), unique per tenant. */
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
