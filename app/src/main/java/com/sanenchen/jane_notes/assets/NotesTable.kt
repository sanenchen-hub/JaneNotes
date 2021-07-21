package com.sanenchen.jane_notes.assets

import org.litepal.crud.LitePalSupport

class NotesTable(val noteTitle: String, val noteContent: String): LitePalSupport() {
    val id: Int = 0
}
