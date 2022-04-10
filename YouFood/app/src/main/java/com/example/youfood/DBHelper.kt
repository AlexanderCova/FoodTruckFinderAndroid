package com.example.youfood


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull

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

    fun setIntrest(name : String): String {
        val db = this.writableDatabase

        val values = ContentValues()

        values.put("name", name)
        values.put("intrest", 1)

        val cursor = getInterest(name)
        val interest = cursor!!.getIntOrNull(0)


        if (interest == null) {
            db.insert("EVENTS", null, values)
            return "Uninterested"
        } else if(interest == 1) {
            db.delete("EVENTS", "name=?", arrayOf(name))
            return "Interested"
        }
        return ""
    }

    fun getInterest(name: String): Cursor? {
        val db = this.readableDatabase

        return db.rawQuery("SELECT interest FROM EVENTS WHERE name=$name", null)
    }

    companion object {
        val EMAIL_COL = "email"
    }


}