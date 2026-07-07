package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "Resposta")
@NoArgsConstructor
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @JsonIgnore
    private Pedido pedido;

    @Column(name = "mensagem", columnDefinition="TEXT")
    private String mensagem;

    @Column(name = "raciocinio", columnDefinition="TEXT")
    private String raciocinio;

    @Column(name="timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    public Resposta(Pedido pedido, String mensagem, String raciocinio) {
        this.pedido = pedido;
        this.mensagem = mensagem;
        this.raciocinio = raciocinio;
        timestamp = LocalDateTime.now();
    }
}
