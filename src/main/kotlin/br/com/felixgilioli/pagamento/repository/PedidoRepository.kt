package br.com.felixgilioli.pagamento.repository

interface PedidoRepository {

    fun updateStatus(pedidoId: String, status: String)
}