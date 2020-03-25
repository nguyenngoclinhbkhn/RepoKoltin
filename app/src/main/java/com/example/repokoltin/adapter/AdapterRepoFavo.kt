package com.example.repokoltin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.repokoltin.R
import com.example.repokoltin.model.Repo
import com.example.repokoltin.model.RepoRealm
import java.util.*

class AdapterRepoFavo(listener: OnRepoListener) :
    RecyclerView.Adapter<AdapterRepoFavo.RepoHolder>() {
    private var inflater: LayoutInflater? = null
    private var list: List<RepoRealm>
    private val listener: OnRepoListener
    fun setList(list: List<RepoRealm>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoHolder {
        inflater = LayoutInflater.from(parent.context)
        return RepoHolder(inflater!!.inflate(R.layout.item_repo, parent, false))
    }

    override fun onBindViewHolder(
        holder: RepoHolder,
        position: Int
    ) {
        val repo = list[position]
        holder.txtRepo?.setText(repo?.fullname)
        holder.relativeLayout.setOnClickListener { listener.OnRepoClicked(repo) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class RepoHolder(itemView: View) : ViewHolder(itemView) {
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.relativeRepo)
        val txtRepo: TextView = itemView.findViewById(R.id.nameRepo)

    }

    interface OnRepoListener {
        fun OnRepoClicked(repo: RepoRealm?)
    }

    init {
        list = ArrayList<RepoRealm>()
        this.listener = listener
    }
}
