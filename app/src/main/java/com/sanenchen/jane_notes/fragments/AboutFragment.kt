/**
 * @author sanenchen
 * 关于界面
 */
package com.sanenchen.jane_notes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sanenchen.jane_notes.R

class AboutFragment: Fragment() {
    private lateinit var mView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_about, container, false)
        return mView
    }
}