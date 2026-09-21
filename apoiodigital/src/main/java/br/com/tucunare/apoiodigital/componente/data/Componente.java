package br.com.tucunare.apoiodigital.componente.data;

import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "componente")
@Data
@NoArgsConstructor
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "assinatura", columnDefinition = "TEXT", nullable = false)
    private String assinatura;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_resposta", nullable = false)
    @JsonIgnore
    private Resposta resposta;

    public Componente(String assinatura, Resposta resposta) {
        this.assinatura = assinatura;
        this.resposta = resposta;
    }
}
