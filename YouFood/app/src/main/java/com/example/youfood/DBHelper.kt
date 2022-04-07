package com.example.youfood


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory : SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, "User", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        var query = ("CREATE TABLE USER (" +
                "email TEXT," +
                "password TEXT )")

        if (db != null) {
            db.execSQL(query)
        }

        //1 = maybe, 2 = going

        query = ("CREATE TABLE EVENTS (" +
                "name TEXT," +
                "intrest INT )")

        if (db != null) {
            db.execSQL(query)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS USER")
            db.execSQL("DROP TABLE IF EXISTS EVENTS")
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

    fun intrestInEvent(intrest : Int, name : String) {
        val db = this.writableDatabase

        val values = ContentValues()

        values.put("name", name)
        values.put("intrest", intrest)

        db.insert("EVENTS", null, values)
    }

    fun getInterest(name: String): Cursor? {
        val db = this.readableDatabase

        return db.rawQuery("SELECT interest FROM EVENTS", null)
    }

    fun updateInterest(name : String, intrest : Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("intrest", intrest)

        val cursor = getInterest(name)

        cursor!!.moveToFirst()
        val intrest = cursor.getInt(cursor.getColumnIndex("intrest").toInt())

        if (intrest != 0 && intrest != 1 && intrest != 2) {
            intrestInEvent(intrest, name)
        } else {
            db.update("EVENTS", values, "name=?", arrayOf(name))
        }


    }

    fun removeInterest(name : String){
        val db = this.writableDatabase

        db.delete("EVENTS","name=?", arrayOf(name))
        db.close()
    }

    companion object {
        val EMAIL_COL = "email"
    }


}