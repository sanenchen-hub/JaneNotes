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
import com.sanenchen.jane_notes.assets.NotesTable
import kotlinx.android.synthetic.main.activity_edit_note.*
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.update

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
            edit_title.setText(title)
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
        edit_content_view.text = edit_content.text
        if (id == -1) { // 新建
            NotesTable(
                edit_title.text.toString(),
                edit_content.text.toString()
            ).save() // 存入数据库
            this.finish() // 结束
        } else { // 修改 (仅仅是Update，不做删除)
            val value = ContentValues()
            value.put("noteTitle", edit_title.text.toString())
            value.put("noteContent", edit_content.text.toString())
            LitePal.update<NotesTable>(value, id.toLong())
        }
    }

    /**
     * 监听 Toolbar 按钮
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { // 监听返回键
            android.R.id.home -> {
                saveNote() // 防止用户误触
                finish() // 结束
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 自动保存，通过监听返回键，来保存，防止用户忘记保存
     */
    override fun onBackPressed() {
        super.onBackPressed()
        // 检测模式，如果是预览模式则不保存
        saveNote()
    }
}