package com.portfolio.financas.exception;

/**
 * Lançada quando tentamos buscar/editar/excluir algo que não existe no banco.
 * RuntimeException porque não queremos obrigar todo mundo a declarar "throws".
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
