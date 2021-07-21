/**
 * @author sanenchen
 * 便签界面
 */
package com.sanenchen.jane_notes.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sanenchen.jane_notes.R
import com.sanenchen.jane_notes.activities.EditNoteActivity
import com.sanenchen.jane_notes.assets.NotesTable
import kotlinx.android.synthetic.main.alert_dialog_adapter.view.*
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.fragment_notes.view.*
import kotlinx.android.synthetic.main.item_note.view.*
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.find
import org.litepal.extension.findAll

class NotesFragment: Fragment() {
    lateinit var mView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_notes, container, false)

        // 添加便签的 FAB 监听
        mView.main_add_fab.setOnClickListener {
            startActivity(Intent(mView.context, EditNoteActivity::class.java))
        }

        return mView
    }

    override fun onResume() {
        super.onResume()
        setData() // 重新进入界面即刷新
    }

    /**
     * 查询数据，放入数据至 RecyclerView
     */
    private fun setData() {
        LitePal.getDatabase() // 数据库初始化

        /**
         * 数据查询
         */
        val list: ArrayList<NotesData> = ArrayList()
        val notesQueryResult = LitePal.order("id desc").find<NotesTable>()
        // 判断结果是否为 0
        if (notesQueryResult.isEmpty()) {
            mView.main_none_note.visibility = View.VISIBLE
            mView.main_nested_scroll.visibility = View.GONE
        } else {
            mView.main_none_note.visibility = View.GONE
            mView.main_nested_scroll.visibility = View.VISIBLE
        }

        for (result in notesQueryResult) { // 结果放入 list 中
            // 便签清除机制（当标题和内容都为空时候，删除此便签）
            if (result.noteTitle.isEmpty() && result.noteContent.isEmpty()) {
                LitePal.delete<NotesTable>(result.id.toLong())
            } else
                list.add(NotesData(result.id, result.noteTitle, result.noteContent))
        }

        // 配置 Adapter
        mView.main_notes_recycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mView.main_notes_recycler.adapter = MainNotesAdapter(list, mView.context)
    }

    /**
     * RecyclerView 适配器 MainNotesAdapter
     */
    class MainNotesAdapter(
        private val list: ArrayList<NotesData>,
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

            // 判断标题是否为空，如果为空，则将标题隐藏
            if (list[position].title.isEmpty())
                holder.itemNoteTitle.visibility = View.GONE

            holder.itemNoteCardView.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)
                val dialog = builder.create()
                val view = View.inflate(context, R.layout.alert_dialog_adapter, null)
                dialog.setView(view)
                dialog.show()
                view.alert_dialog_adapter_delete_button.setOnClickListener {
                    LitePal.delete<NotesTable>(list[position].id.toLong())
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
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
    data class NotesData(val id: Int, val title: String, val note: String)
}