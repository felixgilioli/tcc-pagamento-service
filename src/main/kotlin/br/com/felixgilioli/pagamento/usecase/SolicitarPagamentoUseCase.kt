package br.com.felixgilioli.pagamento.usecase

import br.com.felixgilioli.pagamento.entity.PagamentoORM
import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import br.com.felixgilioli.pagamento.repository.CobrancaRepository
import br.com.felixgilioli.pagamento.repository.PagamentoRepository
import br.com.felixgilioli.pagamento.repository.PedidoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class SolicitarPagamentoUseCase(
    private val cobrancaRepository: CobrancaRepository,
    private val pagamentoRepository: PagamentoRepository,
    private val pedidoRepository: PedidoRepository
) {

    fun execute(pedidoId: String, valorTotal: BigDecimal) {
        val linkPagamento = cobrancaRepository.gerarLink(valorTotal)

        PagamentoORM(
            pedidoId = pedidoId,
            valor = valorTotal,
            status = PagamentoStatus.LINK_PAGAMENTO_GERADO,
            link = linkPagamento
        ).let(pagamentoRepository::save)

        pedidoRepository.updateStatus(pedidoId, "PAGAMENTO_SOLICITADO")
    }

}