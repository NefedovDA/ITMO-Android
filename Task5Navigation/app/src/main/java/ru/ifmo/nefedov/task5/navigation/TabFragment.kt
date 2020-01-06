package ru.ifmo.nefedov.task5.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class TabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tab, container, false)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.tab_fragmentContainer, ClickerFragment())
                .commit()
        }
        return rootView
    }
}