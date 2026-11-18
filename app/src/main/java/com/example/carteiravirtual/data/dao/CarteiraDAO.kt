package com.example.carteiravirtual.data.dao

import android.content.ContentValues
import android.content.Context
import com.example.carteiravirtual.data.db.DBHelper
import com.example.carteiravirtual.data.model.CarteiraItem

class CarteiraDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    fun listarTodos(): List<CarteiraItem> {
        val lista = mutableListOf<CarteiraItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COL_CODIGO, DBHelper.COL_NOME, DBHelper.COL_SIMBOLO, DBHelper.COL_SALDO),
            null,
            null,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val codigo = it.getString(it.getColumnIndexOrThrow(DBHelper.COL_CODIGO))
                    val nome = it.getString(it.getColumnIndexOrThrow(DBHelper.COL_NOME))
                    val simbolo = it.getString(it.getColumnIndexOrThrow(DBHelper.COL_SIMBOLO))
                    val saldo = it.getDouble(it.getColumnIndexOrThrow(DBHelper.COL_SALDO))
                    lista.add(CarteiraItem(codigo, nome, simbolo, saldo))
                } while (it.moveToNext())
            }
        }
        return lista
    }

    fun buscarPorCodigo(codigo: String): CarteiraItem? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COL_CODIGO, DBHelper.COL_NOME, DBHelper.COL_SIMBOLO, DBHelper.COL_SALDO),
            "${DBHelper.COL_CODIGO} = ?",
            arrayOf(codigo),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val cod = it.getString(it.getColumnIndexOrThrow(DBHelper.COL_CODIGO))
                val nome = it.getString(it.getColumnIndexOrThrow(DBHelper.COL_NOME))
                val simbolo = it.getString(it.getColumnIndexOrThrow(DBHelper.COL_SIMBOLO))
                val saldo = it.getDouble(it.getColumnIndexOrThrow(DBHelper.COL_SALDO))
                return CarteiraItem(cod, nome, simbolo, saldo)
            }
        }
        return null
    }

    fun obterSaldo(codigo: String): Double {
        return buscarPorCodigo(codigo)?.saldo ?: 0.0
    }

    fun atualizarSaldo(codigo: String, novoSaldo: Double): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COL_SALDO, novoSaldo)
        }
        val rowsAffected = db.update(
            DBHelper.TABLE_NAME,
            values,
            "${DBHelper.COL_CODIGO} = ?",
            arrayOf(codigo)
        )
        return rowsAffected > 0
    }

    fun depositar(codigo: String, valor: Double): Boolean {
        val saldoAtual = obterSaldo(codigo)
        return atualizarSaldo(codigo, saldoAtual + valor)
    }

    fun debitar(codigo: String, valor: Double): Boolean {
        val saldoAtual = obterSaldo(codigo)
        if (saldoAtual >= valor) {
            return atualizarSaldo(codigo, saldoAtual - valor)
        }
        return false
    }
}

