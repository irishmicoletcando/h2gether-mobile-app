package com.h2gether.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.h2gether.R

class ViewPageAdapter(
    private var title: List<String>,
    private var description: List<String>,
    private var images: List<Int>
) : RecyclerView.Adapter<ViewPageAdapter.Pager2ViewHolder>() {

    //TODO: create inner class Pager2ViewHolder
}