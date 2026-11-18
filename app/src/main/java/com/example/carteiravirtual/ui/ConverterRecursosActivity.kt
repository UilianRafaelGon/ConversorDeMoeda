package com.example.carteiravirtual.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carteiravirtual.R
import com.example.carteiravirtual.data.dao.CarteiraDAO
import com.example.carteiravirtual.data.model.CarteiraItem
import com.example.carteiravirtual.data.network.AwesomeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ConverterRecursosActivity : AppCompatActivity() {

    private lateinit var spMoedaOrigem: Spinner
    private lateinit var spMoedaDestino: Spinner
    private lateinit var tvSaldoOrigemDisponivel: TextView
    private lateinit var tvSaldoDestinoDisponivel: TextView
    private lateinit var etValorConversao: EditText
    private lateinit var btnExecutarConversao: Button
    private lateinit var progressBarConversao: ProgressBar
    private lateinit var layoutResultado: LinearLayout
    private lateinit var tvResultadoConversao: TextView
    private lateinit var tvCotacaoUtilizada: TextView

    private lateinit var carteiraDAO: CarteiraDAO
    private lateinit var apiService: AwesomeApiService

    // Lista de moedas suportadas com rótulos amigáveis
    private val moedas = listOf(
        Pair("BRL", "Real (BRL)"),
        Pair("USD", "Dólar (USD)"),
        Pair("EUR", "Euro (EUR)"),
        Pair("BTC", "Bitcoin (BTC)"),
        Pair("ETH", "Ethereum (ETH)")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter_recursos)

        title = getString(R.string.title_converter)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        carteiraDAO = CarteiraDAO(this)
        apiService = AwesomeApiService.create()

        spMoedaOrigem = findViewById(R.id.spMoedaOrigem)
        spMoedaDestino = findViewById(R.id.spMoedaDestino)
        tvSaldoOrigemDisponivel = findViewById(R.id.tvSaldoOrigemDisponivel)
        tvSaldoDestinoDisponivel = findViewById(R.id.tvSaldoDestinoDisponivel)
        etValorConversao = findViewById(R.id.etValorConversao)
        btnExecutarConversao = findViewById(R.id.btnExecutarConversao)
        progressBarConversao = findViewById(R.id.progressBarConversao)
        layoutResultado = findViewById(R.id.layoutResultado)
        tvResultadoConversao = findViewById(R.id.tvResultadoConversao)
        tvCotacaoUtilizada = findViewById(R.id.tvCotacaoUtilizada)

        configurarSpinners()

        btnExecutarConversao.setOnClickListener {
            iniciarConversao()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun configurarSpinners() {
        val nomes = moedas.map { it.second }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nomes)

        spMoedaOrigem.adapter = adapter
        spMoedaDestino.adapter = adapter

        // Padrão: De BRL (índice 0) para USD (índice 1)
        spMoedaOrigem.setSelection(0)
        spMoedaDestino.setSelection(1)

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                atualizarSaldosExibidos()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spMoedaOrigem.onItemSelectedListener = listener
        spMoedaDestino.onItemSelectedListener = listener

        atualizarSaldosExibidos()
    }

    private fun getMoedaOrigemCodigo(): String = moedas[spMoedaOrigem.selectedItemPosition].first
    private fun getMoedaDestinoCodigo(): String = moedas[spMoedaDestino.selectedItemPosition].first

    private fun formatarSaldo(codigo: String, saldo: Double): String {
        return when (codigo) {
            "BTC", "ETH" -> String.format(Locale.US, "%.6f %s", saldo, codigo)
            "BRL" -> String.format(Locale.GERMANY, "R$ %,.2f", saldo)
            "USD" -> String.format(Locale.US, "$ %,.2f", saldo)
            "EUR" -> String.format(Locale.GERMANY, "€ %,.2f", saldo)
            else -> String.format(Locale.US, "%.4f %s", saldo, codigo)
        }
    }

    private fun atualizarSaldosExibidos() {
        val codOrigem = getMoedaOrigemCodigo()
        val codDestino = getMoedaDestinoCodigo()

        val saldoOrigem = carteiraDAO.obterSaldo(codOrigem)
        val saldoDestino = carteiraDAO.obterSaldo(codDestino)

        tvSaldoOrigemDisponivel.text = "Saldo disponível: ${formatarSaldo(codOrigem, saldoOrigem)}"
        tvSaldoDestinoDisponivel.text = "Saldo disponível: ${formatarSaldo(codDestino, saldoDestino)}"
    }

    private fun showLoading() {
        progressBarConversao.visibility = View.VISIBLE
        btnExecutarConversao.isEnabled = false
        layoutResultado.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBarConversao.visibility = View.GONE
        btnExecutarConversao.isEnabled = true
    }

    private fun iniciarConversao() {
        val codOrigem = getMoedaOrigemCodigo()
        val codDestino = getMoedaDestinoCodigo()

        if (codOrigem == codDestino) {
            Toast.makeText(this, getString(R.string.msg_moedas_iguais), Toast.LENGTH_LONG).show()
            return
        }

        val inputStr = etValorConversao.text.toString().trim().replace(",", ".")
        val valor = inputStr.toDoubleOrNull()

        if (valor == null || valor <= 0.0) {
            Toast.makeText(this, getString(R.string.msg_valor_invalido), Toast.LENGTH_SHORT).show()
            return
        }

        val saldoOrigem = carteiraDAO.obterSaldo(codOrigem)
        if (saldoOrigem < valor) {
            Toast.makeText(this, getString(R.string.msg_saldo_insuficiente), Toast.LENGTH_LONG).show()
            return
        }

        executarConversaoApi(codOrigem, codDestino, valor)
    }

    /**
     * Obtém a taxa de câmbio entre a moeda de origem e destino usando a Awesome API.
     * Awesome API fornece cotações no padrão {MOEDA_BASE}-{MOEDA_COTADA}, ex: USD-BRL, EUR-BRL, BTC-BRL, ETH-BRL.
     * Caso o par direto não exista (ex: BRL-USD ou EUR-USD), calculamos usando a cotação em relação ao BRL.
     */
    private suspend fun obterTaxaDeCambio(de: String, para: String): Double {
        return withContext(Dispatchers.IO) {
            // Tenta consultar par direto primeiro
            val parDireto = "$de-$para"
            val chaveDireta = "$de$para"

            try {
                val resp = apiService.getCotacao(parDireto)
                val cotacao = resp[chaveDireta] ?: resp.values.firstOrNull()
                val bid = cotacao?.bid?.toDoubleOrNull()
                if (bid != null && bid > 0.0) {
                    return@withContext bid
                }
            } catch (e: Exception) {
                Log.d("ConverterRecursos", "Par direto $parDireto não encontrado, calculando via BRL: ${e.message}")
            }

            // Se for de outra moeda para BRL (ex: USD para BRL): já é o par USD-BRL
            if (para == "BRL") {
                val resp = apiService.getCotacao("$de-BRL")
                val cotacao = resp["$de" + "BRL"] ?: resp.values.firstOrNull()
                val bid = cotacao?.bid?.toDoubleOrNull()
                    ?: throw IllegalStateException("Cotação não encontrada para $de-BRL")
                return@withContext bid
            }

            // Se for de BRL para outra moeda (ex: BRL para USD): usamos 1 / (USD-BRL)
            if (de == "BRL") {
                val resp = apiService.getCotacao("$para-BRL")
                val cotacao = resp["$para" + "BRL"] ?: resp.values.firstOrNull()
                val bid = cotacao?.bid?.toDoubleOrNull()
                    ?: throw IllegalStateException("Cotação não encontrada para $para-BRL")
                return@withContext 1.0 / bid
            }

            // Conversão cruzada (ex: USD para EUR, BTC para ETH): converte 'de' para BRL e depois BRL para 'para'
            val respOrigem = apiService.getCotacao("$de-BRL")
            val cotacaoOrigem = respOrigem["$de" + "BRL"] ?: respOrigem.values.firstOrNull()
            val taxaDeParaBrl = cotacaoOrigem?.bid?.toDoubleOrNull()
                ?: throw IllegalStateException("Cotação não encontrada para $de-BRL")

            val respDestino = apiService.getCotacao("$para-BRL")
            val cotacaoDestino = respDestino["$para" + "BRL"] ?: respDestino.values.firstOrNull()
            val taxaParaParaBrl = cotacaoDestino?.bid?.toDoubleOrNull()
                ?: throw IllegalStateException("Cotação não encontrada para $para-BRL")

            // 1 unidade de 'de' vale taxaDeParaBrl reais.
            // 1 unidade de 'para' vale taxaParaParaBrl reais.
            // Logo, taxa (de -> para) = taxaDeParaBrl / taxaParaParaBrl
            return@withContext taxaDeParaBrl / taxaParaParaBrl
        }
    }

    private fun executarConversaoApi(codOrigem: String, codDestino: String, valor: Double) {
        lifecycleScope.launch {
            try {
                showLoading()

                val taxa = obterTaxaDeCambio(codOrigem, codDestino)
                val valorConvertido = valor * taxa

                // Atualiza e persiste os valores no banco de dados
                val debitado = carteiraDAO.debitar(codOrigem, valor)
                if (!debitado) {
                    Toast.makeText(
                        this@ConverterRecursosActivity,
                        getString(R.string.msg_saldo_insuficiente),
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                carteiraDAO.depositar(codDestino, valorConvertido)

                // Atualiza interface
                atualizarSaldosExibidos()
                etValorConversao.text.clear()

                val strConvertido = formatarSaldo(codDestino, valorConvertido)
                tvResultadoConversao.text = "Convertido: $strConvertido"
                tvCotacaoUtilizada.text = String.format(
                    Locale.US,
                    "Taxa de câmbio: 1 %s = %.6f %s",
                    codOrigem,
                    taxa,
                    codDestino
                )
                layoutResultado.visibility = View.VISIBLE

                Toast.makeText(
                    this@ConverterRecursosActivity,
                    "Conversão efetuada com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.e("ConverterRecursosActivity", "Erro na conversão", e)
                Toast.makeText(
                    this@ConverterRecursosActivity,
                    "Erro ao consultar cotação da API: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                hideLoading()
            }
        }
    }
}

