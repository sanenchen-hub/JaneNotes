/**
 * @author sanenchen
 * JaneNotes 主界面
 */
package com.sanenchen.jane_notes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.sanenchen.jane_notes.R
import com.sanenchen.jane_notes.fragments.NotesFragment
import com.sanenchen.jane_notes.fragments.TasksFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.LitePal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LitePal.getDatabase() // 数据库初始化

        // 初始化 Toolbar
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        // 初始化 Fragment
        val notesFragment = NotesFragment() // 便签界面
        val tasksFragment = TasksFragment() // 任务界面
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.farme_fragment, notesFragment)
        transaction.commit() // 同步

        // 初始化 NavigationView
        main_navigation_view.setCheckedItem(R.id.navigation_menu_notes)
        main_navigation_view.setNavigationItemSelectedListener {
            transaction = supportFragmentManager.beginTransaction()
            when (it.itemId) {

                R.id.navigation_menu_notes  -> {
                    transaction.replace(R.id.farme_fragment, notesFragment)
                    supportActionBar?.title = "简·便签" // 设置 Title
                }
                R.id.navigation_menu_tasks -> {
                    transaction.replace(R.id.farme_fragment, tasksFragment)
                    supportActionBar?.title = "简·待办" // 设置 Title
                }
            }
            transaction.commit()
            main_drawer_layout.closeDrawer(GravityCompat.START) // 关闭
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> main_drawer_layout.openDrawer(GravityCompat.START)
        }
        return true
    }
}