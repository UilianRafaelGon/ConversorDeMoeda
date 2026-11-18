package com.example.carteiravirtual.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.carteiravirtual.data.model.CarteiraItem

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "carteira_virtual.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "carteira"
        const val COL_CODIGO = "codigo"
        const val COL_NOME = "nome"
        const val COL_SIMBOLO = "simbolo"
        const val COL_SALDO = "saldo"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_CODIGO TEXT PRIMARY KEY,
                $COL_NOME TEXT NOT NULL,
                $COL_SIMBOLO TEXT NOT NULL,
                $COL_SALDO REAL NOT NULL DEFAULT 0.0
            )
        """.trimIndent()
        db.execSQL(createTable)

        // Inicializa as moedas suportadas pelo app
        val moedasIniciais = listOf(
            CarteiraItem("BRL", "Real", "R$", 0.0),
            CarteiraItem("USD", "Dólar", "$", 0.0),
            CarteiraItem("EUR", "Euro", "€", 0.0),
            CarteiraItem("BTC", "Bitcoin", "BTC", 0.0),
            CarteiraItem("ETH", "Ethereum", "ETH", 0.0)
        )

        for (item in moedasIniciais) {
            val values = ContentValues().apply {
                put(COL_CODIGO, item.codigo)
                put(COL_NOME, item.nome)
                put(COL_SIMBOLO, item.simbolo)
                put(COL_SALDO, item.saldo)
            }
            db.insert(TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}

