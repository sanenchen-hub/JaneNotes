package com.sanenchen.jane_notes.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

open class DataBaseHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    private val mContext = context

    private val createNotes =
        "create table Notes(id integer primary key autoincrement, note_title text, note_content text)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createNotes)
        Toast.makeText(mContext, "数据库创建成功", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}