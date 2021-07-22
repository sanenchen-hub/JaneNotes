/**
 * @author sanenchen
 * TasksFragment
 * 任务界面
 */
package com.sanenchen.jane_notes.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sanenchen.jane_notes.R
import com.sanenchen.jane_notes.assets.TasksTable
import kotlinx.android.synthetic.main.alert_dialog_add_task.view.*
import kotlinx.android.synthetic.main.fragment_notes.view.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.android.synthetic.main.fragment_tasks.view.*
import kotlinx.android.synthetic.main.item_task.view.*
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.findAll

class TasksFragment : Fragment() {
    lateinit var mView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_tasks, container, false)

        mView.task_add_fab.setOnClickListener {
            addTask()
        }   // 监听 FAB 添加按钮
        return mView
    }

    override fun onResume() {
        super.onResume()
        queryData()
    }

    /**
     * 查询并放入进 RecyclerView
     */
    private fun queryData() {
        val list: ArrayList<TasksData> = arrayListOf()
        val queryResult = LitePal.findAll<TasksTable>()
        // 检查是否为空
        if (queryResult.isEmpty()) {
            mView.tasks_recycler.visibility = View.GONE
            mView.tasks_none_task.visibility = View.VISIBLE
        } else {
            mView.tasks_recycler.visibility = View.VISIBLE
            mView.tasks_none_task.visibility = View.GONE
        }

        for (obj in queryResult)
            list.add(TasksData(obj.id, obj.task, obj.taskDetail))

        tasks_recycler.layoutManager =
            LinearLayoutManager(mView.context, LinearLayoutManager.VERTICAL, false)
        tasks_recycler.adapter = TasksRecyclerViewAdapter(list, mView)
    }

    /**
     * 添加动作
     */
    private fun addTask() {
        val dialogView =
            View.inflate(mView.context, R.layout.alert_dialog_add_task, null) // 绑定 View
        val dialog = AlertDialog.Builder(mView.context).create()
        dialog.setView(dialogView)
        dialog.show()
        // 监听
        dialogView.image_button_show_details.setOnClickListener { // 显示详情界面
            dialogView.alert_add_task_edit_task_detail.visibility = View.VISIBLE
        }
        dialogView.alert_add_task_save.setOnClickListener {
            if (dialogView.alert_add_task_edit_task.text.isNotBlank()
                || dialogView.alert_add_task_edit_task_detail.text.isNotEmpty()
            ) {
                val tasksTable = TasksTable(
                    dialogView.alert_add_task_edit_task.text.toString(),
                    dialogView.alert_add_task_edit_task_detail.text.toString()
                )
                tasksTable.save() // 存入数据库
                Snackbar.make(mView, "已添加任务", Snackbar.LENGTH_SHORT).show()
                queryData() // 刷新
                dialog.dismiss()
            } else
                Toast.makeText(mView.context, "填写完整后再保存叭", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * tasks_recycler 的适配器
     */
    class TasksRecyclerViewAdapter(
        private val list: ArrayList<TasksData>,
        private val mView: View
    ) :
        RecyclerView.Adapter<TasksRecyclerViewAdapter.ViewHolder>() {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val taskCheckBox: CheckBox = itemView.item_task_check_box
            val taskTask: TextView = itemView.item_task_task
            val taskTaskDetail: TextView = itemView.item_task_task_detail
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView =
                LayoutInflater.from(mView.context).inflate(R.layout.item_task, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.taskTask.text = list[position].task
            holder.taskTaskDetail.text = list[position].taskDetail
            if (list[position].taskDetail.isEmpty()) // 如果没有详细信息，就隐藏掉
                holder.taskTaskDetail.visibility = View.GONE

            // 监听 CheckBox
            holder.taskCheckBox.setOnClickListener {
                if (holder.taskCheckBox.isChecked) { // 如果将它选中，则代表已经完成任务
                    // 删除操作
                    LitePal.delete<TasksTable>(list[position].id.toLong())
                    list.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                    Snackbar.make(mView, "任务完成！", Snackbar.LENGTH_SHORT).show()
                    // 判断 List 是否为空，为空则弹出提示，隐藏此界面
                    if (list.isEmpty()) {
                        mView.tasks_none_task.visibility = View.VISIBLE
                        mView.tasks_recycler.visibility = View.GONE
                    }
                }
            }
        }

        override fun getItemCount(): Int = list.size

    }

    /**
     * Data 类
     */
    data class TasksData(val id: Int, val task: String, val taskDetail: String)
}