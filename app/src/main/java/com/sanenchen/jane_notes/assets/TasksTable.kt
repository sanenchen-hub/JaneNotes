package com.sanenchen.jane_notes.assets

import org.litepal.crud.LitePalSupport

class TasksTable(val task: String, val taskDetail: String): LitePalSupport() {
    var id: Int = 0
}