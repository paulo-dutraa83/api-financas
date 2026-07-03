package br.com.cotiinformatica.api_financas.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    /*
        O mappedBy indica que a relação entre Categoria e Movimentacao é bidirecional, e que a propriedade "categoria"
        na classe Movimentacao é a responsável por mapear essa relação. Isso significa que a tabela de movimentacoes
        terá uma coluna categoria_id que referencia a tabela de categorias.
     */
    @OneToMany(mappedBy = "categoria")
    private List<Movimentacao> movimentacoes;

}
