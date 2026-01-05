package br.com.felixgilioli.pagamento.repository

import java.math.BigDecimal

interface CobrancaRepository {

    fun gerarLink(valor: BigDecimal): String
}