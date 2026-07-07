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
@Data
@Table(name = "Componente")
@NoArgsConstructor
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_resposta")
    @JsonIgnore
    private Resposta resposta;

    @Column(name = "assinatura", columnDefinition="TEXT")
    private String assinatura;

    public Componente(Resposta resposta, String assinatura) {
        this.resposta = resposta;
        this.assinatura = assinatura;
    }
}
