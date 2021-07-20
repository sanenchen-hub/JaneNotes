/**
 * @author sanenchen
 * 编辑或新建 Note
 */
package com.sanenchen.jane_notes.activities

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.sanenchen.jane_notes.R
import com.sanenchen.jane_notes.utils.DataBaseHelper
import kotlinx.android.synthetic.main.activity_edit_note.*

class EditNoteActivity : AppCompatActivity() {
    private var id = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        // 配置 Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "便签详情"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 上下文链接
        id = intent.getIntExtra("id", -1)
        val action = intent.getStringExtra("action")

        if (id != -1) { // 说明不是通过点击添加便签按钮进入的，是有数据的
            val title = intent.getStringExtra("title") // 获取 Title
            val content = intent.getStringExtra("content") // 获取 Content
            edit_title.editText?.setText(title)
            edit_content_view.text = content
            edit_content.setText(content)
            when (action) {
                "View" -> { // 预览模式
                    edit_edit_fab.visibility = View.VISIBLE
                    edit_finish_fab.visibility = View.GONE
                    edit_content.visibility = View.GONE
                    edit_title.isEnabled = false
                    edit_content_view_scroll.visibility = View.VISIBLE
                }
                "Edit" -> { // 编辑模式
                    edit_edit_fab.visibility = View.GONE
                    edit_finish_fab.visibility = View.VISIBLE
                    edit_content.visibility = View.VISIBLE
                    edit_content_view_scroll.visibility = View.GONE
                }
            }
        }
        buttonListen()
    }

    /**
     * 按钮监听
     */
    private fun buttonListen() {
        edit_finish_fab.setOnClickListener {
            edit_edit_fab.visibility = View.VISIBLE
            edit_finish_fab.visibility = View.GONE
            edit_content.visibility = View.GONE
            edit_content_view_scroll.visibility = View.VISIBLE
            edit_title.isEnabled = false
            Snackbar.make(it, "保存成功，进入预览模式", Snackbar.LENGTH_SHORT).show()
            saveNote()
        } // 从编辑模式切换到预览模式

        edit_edit_fab.setOnClickListener {
            edit_edit_fab.visibility = View.GONE
            edit_finish_fab.visibility = View.VISIBLE
            edit_content_view_scroll.visibility = View.GONE
            edit_content.visibility = View.VISIBLE
            edit_title.isEnabled = true
            Snackbar.make(it, "编辑模式", Snackbar.LENGTH_SHORT).show()
        } // 从预览模式切换到编辑模式
    }

    /**
     * 保存行为
     */
    private fun saveNote() {
        val helper = DataBaseHelper(this, "JaneNotes", null, 1)
        val db = helper.readableDatabase
        if (id == -1) {
            val value = ContentValues()
            value.put("note_title", edit_title.editText?.text.toString())
            value.put("note_content", edit_content.text.toString())
            db.insert("Notes", null, value)
            this.finish() // 结束
        } else {
            edit_content_view.text = edit_content.text
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}