package br.com.tucunare.apoiodigital.usuario.data;

import br.com.tucunare.apoiodigital.usuario.exception.InvalidPasswordLengthException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;
import br.com.tucunare.apoiodigital.cliente.data.Cliente;

@Entity
@Data
@Table(name="Usuario")
@NoArgsConstructor
public class Usuario {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    public Usuario(String nome, Cliente cliente) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.cliente = cliente;
    }
}
