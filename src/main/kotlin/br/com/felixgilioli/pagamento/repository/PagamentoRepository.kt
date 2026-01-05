package br.com.felixgilioli.pagamento.repository

import br.com.felixgilioli.pagamento.entity.PagamentoORM
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PagamentoRepository : JpaRepository<PagamentoORM, UUID> {

    fun findFirstByPedidoIdOrderByDataDesc(pedidoId: String): PagamentoORM?
}