package com.example.carteiravirtual.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carteiravirtual.R
import com.example.carteiravirtual.data.dao.CarteiraDAO
import java.util.Locale

class DepositoActivity : AppCompatActivity() {

    private lateinit var tvSaldoAtual: TextView
    private lateinit var etValorDeposito: EditText
    private lateinit var btnConfirmarDeposito: Button
    private lateinit var carteiraDAO: CarteiraDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposito)

        title = getString(R.string.title_deposito)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        carteiraDAO = CarteiraDAO(this)

        tvSaldoAtual = findViewById(R.id.tvSaldoAtualDeposito)
        etValorDeposito = findViewById(R.id.etValorDeposito)
        btnConfirmarDeposito = findViewById(R.id.btnConfirmarDeposito)

        atualizarSaldo()

        btnConfirmarDeposito.setOnClickListener {
            realizarDeposito()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun atualizarSaldo() {
        val saldo = carteiraDAO.obterSaldo("BRL")
        tvSaldoAtual.text = String.format(Locale.GERMANY, "R$ %,.2f", saldo)
    }

    private fun realizarDeposito() {
        val inputStr = etValorDeposito.text.toString().trim().replace(",", ".")
        val valor = inputStr.toDoubleOrNull()

        if (valor == null || valor <= 0.0) {
            Toast.makeText(this, getString(R.string.msg_valor_invalido), Toast.LENGTH_SHORT).show()
            return
        }

        val sucesso = carteiraDAO.depositar("BRL", valor)
        if (sucesso) {
            Toast.makeText(this, getString(R.string.msg_deposito_sucesso), Toast.LENGTH_SHORT).show()
            etValorDeposito.text.clear()
            atualizarSaldo()
            finish()
        } else {
            Toast.makeText(this, "Erro ao realizar depósito.", Toast.LENGTH_SHORT).show()
        }
    }
}

