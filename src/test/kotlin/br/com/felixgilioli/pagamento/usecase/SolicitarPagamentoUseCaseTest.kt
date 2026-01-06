package br.com.felixgilioli.pagamento.usecase

import br.com.felixgilioli.pagamento.entity.PagamentoORM
import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import br.com.felixgilioli.pagamento.repository.CobrancaRepository
import br.com.felixgilioli.pagamento.repository.PagamentoRepository
import br.com.felixgilioli.pagamento.repository.PedidoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class SolicitarPagamentoUseCaseTest {

    private lateinit var cobrancaRepository: CobrancaRepository
    private lateinit var pagamentoRepository: PagamentoRepository
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: SolicitarPagamentoUseCase

    private val pedidoId = UUID.randomUUID().toString()
    private val valorTotal = BigDecimal.TEN
    private val linkPagamento = "http://link-pagamento.com"

    @BeforeEach
    fun setUp() {
        cobrancaRepository = mockk()
        pagamentoRepository = mockk()
        pedidoRepository = mockk()
        useCase = SolicitarPagamentoUseCase(cobrancaRepository, pagamentoRepository, pedidoRepository)
    }

    @Test
    fun `deve solicitar pagamento, salvar pagamento e atualizar status do pedido`() {
        every { cobrancaRepository.gerarLink(valorTotal) } returns linkPagamento
        every { pagamentoRepository.save(any()) } answers { firstArg() }
        every { pedidoRepository.updateStatus(pedidoId, "PAGAMENTO_SOLICITADO") } answers { }

        useCase.execute(pedidoId, valorTotal)

        verify(exactly = 1) { cobrancaRepository.gerarLink(valorTotal) }
        verify(exactly = 1) {
            pagamentoRepository.save(
                match {
                    it.pedidoId == pedidoId &&
                            it.valor == valorTotal &&
                            it.status == PagamentoStatus.LINK_PAGAMENTO_GERADO &&
                            it.link == linkPagamento
                }
            )
        }
        verify(exactly = 1) { pedidoRepository.updateStatus(pedidoId, "PAGAMENTO_SOLICITADO") }
    }

    @Test
    fun `deve propagar excecao quando nao for possivel gerar link e nao persistir nem atualizar pedido`() {
        every { cobrancaRepository.gerarLink(valorTotal) } throws RuntimeException("Falha ao gerar link")

        assertThrows(RuntimeException::class.java) {
            useCase.execute(pedidoId, valorTotal)
        }

        verify(exactly = 1) { cobrancaRepository.gerarLink(valorTotal) }
        verify(exactly = 0) { pagamentoRepository.save(any<PagamentoORM>()) }
        verify(exactly = 0) { pedidoRepository.updateStatus(any(), any()) }
    }
}