package com.example.youfood


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory : SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, "User", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE USER (" +
                "email TEXT," +
                "password TEXT )")

        if (db != null) {
            db.execSQL(query)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS USER")
        }
    }

    fun addUser(email : String, password : String){
        val values = ContentValues()

        values.put("email", email)
        values.put("password", password)

        val db = this.writableDatabase

        db.insert("USER", null, values)

        db.close()
    }

    fun getUser(): Cursor? {
        val db = this.readableDatabase

        return db.rawQuery("SELECT email FROM USER", null)
    }

    fun deleteUser(email: String) {
        val db = this.writableDatabase

        db.delete("USER", "name=?", arrayOf(email))
        db.close()
    }


}