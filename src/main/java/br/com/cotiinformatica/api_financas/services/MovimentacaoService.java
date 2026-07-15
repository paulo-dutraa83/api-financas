package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoResponse;
import br.com.cotiinformatica.api_financas.entities.Movimentacao;
import br.com.cotiinformatica.api_financas.enums.TipoMovimentacao;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import br.com.cotiinformatica.api_financas.repositories.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MovimentacaoService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    //Criando uma movimentação no banco de dados
    public MovimentacaoResponse criar(MovimentacaoRequest request) {

        //Verificando se a categoria existe no banco de dados
        var categoria  = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada"));

        //Executando as validacoes
        validarMovimentacao(request);

        //Criando um objeto da classe Movimentação
        var movimentacao = new Movimentacao();

        //Preenchendo os dados da movimentação
        movimentacao.setNome(request.nome());
        movimentacao.setData(request.data());
        movimentacao.setValor(BigDecimal.valueOf(request.valor()));
        movimentacao.setTipo(TipoMovimentacao.valueOf(request.tipo()));
        movimentacao.setCategoria(categoria);

        //Salvando a movimentação no banco de dados
        movimentacaoRepository.save(movimentacao);

        //Retornando os dados da movimentação criada
        return toResponse(movimentacao);
    }

    //Metodo para alterar uma movimentação no banco de dados
    public MovimentacaoResponse alterar(UUID id, MovimentacaoRequest request) {

        //Consultando a movimentacao no banco de dados pelo id
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada"));

        //Verificando se a categoria existe no banco de dados
        var categoria  = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada"));

        //Executando as validacoes
        validarMovimentacao(request);

        //Preenchendo os dados da movimentação
        movimentacao.setNome(request.nome());
        movimentacao.setData(request.data());
        movimentacao.setValor(BigDecimal.valueOf(request.valor()));
        movimentacao.setTipo(TipoMovimentacao.valueOf(request.tipo()));
        movimentacao.setCategoria(categoria);

        //Salvando a movimentação no banco de dados
        movimentacaoRepository.save(movimentacao);

        //Retornando os dados da movimentação cadastrada usando o DTO de resposta
        return toResponse(movimentacao);
    }

    //Metodo para excluir um movimentação no banco de dados
    public MovimentacaoResponse excluir(UUID id) {

        //Consultando a movimentacao no banco de dados pelo id
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada"));

        //Excluir a movimentação no banco de dados
        movimentacaoRepository.delete(movimentacao);

        //Retornando os dados da movimentação cadastrada usando o DTO de resposta
        return toResponse(movimentacao);
    }

    //Metodo para consultar as movimentacoes por periodo de datas  e com  paginacao
    public Page<MovimentacaoResponse> consultar(LocalDate dataInicio, LocalDate dataFim, int pageIndex, int pageSize) {

        //Validando as datas
        if(dataInicio.isAfter(dataFim)) {
            throw new ValidacaoException("A data de início não pode ser maior que a data de fim");
        }

        //Configurando a paginacao
        if(pageSize > 25) pageSize = 25;
        var pageable = PageRequest.of(pageIndex, pageSize);

        //Consultando as movimentacoes no banco de dados
        var movimentacoes = movimentacaoRepository.findByData(dataInicio, dataFim, pageable);

        //Retornando os dados da movimentacao cadastrada usando o DTO
        return movimentacoes.map(this::toResponse);
    }

    //Metodo para consultar uma movimentacao pelo id
    public MovimentacaoResponse obterPorId(UUID id) {
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada"));

        //Retornando os dados da movimentacao
        return toResponse(movimentacao);
    }

    //Metodo para validar os dados da movimentacao
    public void validarMovimentacao(MovimentacaoRequest request) {
        if(request.nome() == null || request.nome().trim().isEmpty()) {
            throw new ValidacaoException("O nome da movimentação é obrigatório");
        }
        if(request.nome().length() < 6) {
            throw new ValidacaoException("O nome da movimentação deve ter pelo menos 6 caracteres");
        }
        if(request.valor().doubleValue() <= 0) {
            throw new ValidacaoException("O valor da movimentação deve ser maior que zero");
        }
        if(!request.tipo().equals("DESPESA") && !request.tipo().equals("RECEITA")) {
            throw new ValidacaoException("O tipo da movimentação deve ser DESPESA ou RECEITA");
        }

    }

    //Metodo para retornar os dados do DTO  de resposta da movimentacao
    public MovimentacaoResponse toResponse(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
            movimentacao.getId(),
            movimentacao.getNome(),
            movimentacao.getData(),
            movimentacao.getValor().doubleValue(),
            movimentacao.getTipo().toString(),
            new CategoriaResponse(
                    movimentacao.getCategoria().getId(),
                    movimentacao.getCategoria().getNome()
            )
        );
    }

}
