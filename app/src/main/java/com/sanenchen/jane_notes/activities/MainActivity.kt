/**
 * @author sanenchen
 * JaneNotes 主界面
 */
package com.sanenchen.jane_notes.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sanenchen.jane_notes.R
import com.sanenchen.jane_notes.utils.DataBaseHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_dialog_adapter.view.*
import kotlinx.android.synthetic.main.item_note.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 添加便签的 FAB 监听
        main_add_fab.setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        setData()
    }

    /**
     * 查询数据，放入数据至 RecyclerView
     */
    private fun setData() {
        // 数据库初始化
        val dataBaseHelper = DataBaseHelper(this, "JaneNotes", null, 1)
        val db = dataBaseHelper.readableDatabase

        /**
         * 数据查询
         */
        val list: ArrayList<MainNotesData> = ArrayList()
        val cursor = db.rawQuery("SELECT * from Notes order by id desc", null)
        if (cursor.count == 0) {
            main_none_note.visibility = View.VISIBLE
            main_nested_scroll.visibility = View.GONE
        } else {
            main_none_note.visibility = View.GONE
            main_nested_scroll.visibility = View.VISIBLE
        }
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    MainNotesData(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("note_title")),
                        cursor.getString(cursor.getColumnIndex("note_content"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        // 配置 Adapter
        main_notes_recycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        main_notes_recycler.adapter = MainNotesAdapter(list, this)
    }

    /**
     * RecyclerView 适配器 MainNotesAdapter
     */
    class MainNotesAdapter(
        private val list: ArrayList<MainNotesData>,
        private val context: Context
    ) :
        RecyclerView.Adapter<MainNotesAdapter.ViewHolder>() {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val itemNoteCardView: CardView = itemView.item_note_cardView
            val itemNoteTitle: TextView = itemView.item_note_title
            val itemNoteNote: TextView = itemView.item_note_note
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemNoteTitle.text = list[position].title
            holder.itemNoteNote.text = list[position].note

            holder.itemNoteCardView.setOnClickListener {
                val intent = Intent(context, EditNoteActivity::class.java)
                intent.putExtra("id", list[position].id)
                intent.putExtra("title", list[position].title)
                intent.putExtra("content", list[position].note)
                intent.putExtra("action", "View")
                context.startActivity(intent)
            }

            holder.itemNoteCardView.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)
                val dialog = builder.create()
                val view = View.inflate(context, R.layout.alert_dialog_adapter, null)
                dialog.setView(view)
                dialog.show()
                view.alert_dialog_adapter_delete_button.setOnClickListener {
                    val dataBaseHelper = DataBaseHelper(context, "JaneNotes", null, 1)
                    val db = dataBaseHelper.readableDatabase
                    db.delete("Notes", "id = ?", arrayOf("${list[position].id}"))
                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                    notifyItemRemoved(position)
                    list.removeAt(position) // 删除 ArrayList 中的数据
                    notifyItemRangeChanged(position, itemCount)
                    dialog.dismiss()
                }
                view.alert_dialog_adapter_view_button.setOnClickListener {
                    val intent = Intent(context, EditNoteActivity::class.java)
                    intent.putExtra("id", list[position].id)
                    intent.putExtra("title", list[position].title)
                    intent.putExtra("content", list[position].note)
                    intent.putExtra("action", "View")
                    context.startActivity(intent)
                    dialog.dismiss()
                }
                view.alert_dialog_adapter_edit_button.setOnClickListener {
                    val intent = Intent(context, EditNoteActivity::class.java)
                    intent.putExtra("id", list[position].id)
                    intent.putExtra("title", list[position].title)
                    intent.putExtra("content", list[position].note)
                    intent.putExtra("action", "Edit")
                    context.startActivity(intent)
                    dialog.dismiss()
                }
                return@setOnLongClickListener true
            }
        }

        override fun getItemCount(): Int = list.size
    }

    /**
     * 数据类 MainNotesData
     */
    data class MainNotesData(val id: Int, val title: String, val note: String)
}