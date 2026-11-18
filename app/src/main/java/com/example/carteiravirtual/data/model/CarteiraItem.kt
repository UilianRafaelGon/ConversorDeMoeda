package com.example.carteiravirtual.data.model

data class CarteiraItem(
    val codigo: String,
    val nome: String,
    val simbolo: String,
    var saldo: Double
) {
    override fun toString(): String {
        return "$nome ($codigo): $simbolo ${String.format(java.util.Locale.US, "%.4f", saldo)}"
    }
}

