package com.example.carteiravirtual

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.carteiravirtual.data.dao.CarteiraDAO
import com.example.carteiravirtual.ui.ConverterRecursosActivity
import com.example.carteiravirtual.ui.DepositoActivity
import com.example.carteiravirtual.ui.ListarRecursosActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tvSaldoPrincipal: TextView
    private lateinit var btnDepositar: Button
    private lateinit var btnListarRecursos: Button
    private lateinit var btnConverterRecursos: Button

    private lateinit var carteiraDAO: CarteiraDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        carteiraDAO = CarteiraDAO(this)

        tvSaldoPrincipal = findViewById(R.id.tvSaldoPrincipal)
        btnDepositar = findViewById(R.id.btnDepositar)
        btnListarRecursos = findViewById(R.id.btnListarRecursos)
        btnConverterRecursos = findViewById(R.id.btnConverterRecursos)

        btnDepositar.setOnClickListener {
            val intent = Intent(this, DepositoActivity::class.java)
            startActivity(intent)
        }

        btnListarRecursos.setOnClickListener {
            val intent = Intent(this, ListarRecursosActivity::class.java)
            startActivity(intent)
        }

        btnConverterRecursos.setOnClickListener {
            val intent = Intent(this, ConverterRecursosActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarSaldoReal()
    }

    private fun atualizarSaldoReal() {
        val saldoBRL = carteiraDAO.obterSaldo("BRL")
        tvSaldoPrincipal.text = String.format(Locale.GERMANY, "R$ %,.2f", saldoBRL)
    }
}
