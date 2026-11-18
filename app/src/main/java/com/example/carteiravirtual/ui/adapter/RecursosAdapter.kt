package com.example.carteiravirtual.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.carteiravirtual.R
import com.example.carteiravirtual.data.model.CarteiraItem
import java.util.Locale

class RecursosAdapter(
    context: Context,
    private val recursos: List<CarteiraItem>
) : ArrayAdapter<CarteiraItem>(context, 0, recursos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_recurso, parent, false)

        val item = getItem(position)

        val tvNome = view.findViewById<TextView>(R.id.tvItemNome)
        val tvCodigo = view.findViewById<TextView>(R.id.tvItemCodigo)
        val tvSaldo = view.findViewById<TextView>(R.id.tvItemSaldo)

        if (item != null) {
            tvNome.text = item.nome
            tvCodigo.text = "Código: ${item.codigo}"

            // Formatação do saldo de acordo com a moeda
            val formatado = when (item.codigo) {
                "BTC", "ETH" -> String.format(Locale.US, "%.6f %s", item.saldo, item.codigo)
                "BRL" -> String.format(Locale.GERMANY, "R$ %,.2f", item.saldo)
                "USD" -> String.format(Locale.US, "$ %,.2f", item.saldo)
                "EUR" -> String.format(Locale.GERMANY, "€ %,.2f", item.saldo)
                else -> String.format(Locale.US, "%s %,.4f", item.simbolo, item.saldo)
            }
            tvSaldo.text = formatado
        }

        return view
    }
}

