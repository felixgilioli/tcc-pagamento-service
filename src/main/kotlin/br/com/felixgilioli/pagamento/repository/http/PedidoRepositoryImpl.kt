package br.com.felixgilioli.pagamento.repository.http

import br.com.felixgilioli.pagamento.repository.PedidoRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class PedidoRepositoryImpl(
    private val restTemplate: RestTemplate
) : PedidoRepository {

    override fun updateStatus(pedidoId: String, status: String) {
        val uri = UriComponentsBuilder
            .fromUriString("http://pedido-service.tcc.svc.cluster.local/v1/pedido/{pedidoId}/status")
            .queryParam("status", status)
            .buildAndExpand(pedidoId)
            .toUri()

        restTemplate.exchange(
            uri,
            HttpMethod.PUT,
            HttpEntity.EMPTY,
            Void::class.java,
        )
    }


}