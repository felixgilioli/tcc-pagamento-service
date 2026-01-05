package br.com.felixgilioli.pagamento.usecase

import br.com.felixgilioli.pagamento.dto.Pagamento
import br.com.felixgilioli.pagamento.repository.PagamentoRepository
import org.springframework.stereotype.Service

@Service
class BuscarPagamentoByPedidoUseCase(private val pagamentoRepository: PagamentoRepository) {

    fun execute(pedidoId: String): Pagamento? = pagamentoRepository.findFirstByPedidoIdOrderByDataDesc(pedidoId)?.toDomain()
}