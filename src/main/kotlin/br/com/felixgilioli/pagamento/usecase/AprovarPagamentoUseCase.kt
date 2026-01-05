package br.com.felixgilioli.pagamento.usecase

import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import br.com.felixgilioli.pagamento.repository.PagamentoRepository
import br.com.felixgilioli.pagamento.repository.PedidoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class AprovarPagamentoUseCase(
    private val pagamentoRepository: PagamentoRepository,
    private val pedidoRepository: PedidoRepository
) {

    fun execute(pagamentoId: UUID) {
        pagamentoRepository.findByIdOrNull(pagamentoId)
            ?.copyWithNewStatus(PagamentoStatus.PAGAMENTO_APROVADO)
            ?.let(pagamentoRepository::save)
            ?.also { pedidoRepository.updateStatus(it.pedidoId, "PAGAMENTO_APROVADO") }
            ?: throw IllegalArgumentException("Pagamento não encontrado")
    }
}