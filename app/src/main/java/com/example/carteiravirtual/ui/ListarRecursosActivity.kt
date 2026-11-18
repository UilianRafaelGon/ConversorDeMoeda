package com.example.carteiravirtual.ui

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.carteiravirtual.R
import com.example.carteiravirtual.data.dao.CarteiraDAO
import com.example.carteiravirtual.ui.adapter.RecursosAdapter

class ListarRecursosActivity : AppCompatActivity() {

    private lateinit var lvRecursos: ListView
    private lateinit var tvEmpty: TextView
    private lateinit var carteiraDAO: CarteiraDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_recursos)

        title = getString(R.string.title_listar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        carteiraDAO = CarteiraDAO(this)

        lvRecursos = findViewById(R.id.lvRecursos)
        tvEmpty = findViewById(R.id.tvEmpty)

        carregarRecursos()
    }

    override fun onResume() {
        super.onResume()
        carregarRecursos()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun carregarRecursos() {
        val recursos = carteiraDAO.listarTodos()
        if (recursos.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            lvRecursos.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            lvRecursos.visibility = View.VISIBLE
            val adapter = RecursosAdapter(this, recursos)
            lvRecursos.adapter = adapter
        }
    }
}

