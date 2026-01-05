package br.com.felixgilioli.pagamento.entity

import br.com.felixgilioli.pagamento.dto.Pagamento
import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "pagamento")
data class PagamentoORM(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false)
    val pedidoId: String,

    @Column(nullable = false)
    val valor: BigDecimal,

    @Column(nullable = false)
    val data: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val status: PagamentoStatus,

    @Column(nullable = false, length = 255)
    val link: String
) {
    fun copyWithNewStatus(status: PagamentoStatus): PagamentoORM {
        return this.copy(id = null, status = status, data = LocalDateTime.now())
    }

    fun toDomain() = Pagamento(
        id = this.id,
        pedidoId = this.pedidoId,
        valor = this.valor,
        data = this.data,
        status = this.status,
        link = this.link
    )

}

fun Pagamento.toOrm() = PagamentoORM(
    id = this.id,
    pedidoId = this.pedidoId,
    valor = this.valor,
    data = this.data,
    status = this.status,
    link = this.link
)
