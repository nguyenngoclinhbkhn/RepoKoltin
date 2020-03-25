package com.example.repokoltin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repokoltin.R
import com.example.repokoltin.model.Repo
import java.util.ArrayList

class AdapterRepoSearch (listener: OnRepoSearchListener) :
    RecyclerView.Adapter<AdapterRepoSearch.RepoHolder>() {
    private var inflater: LayoutInflater? = null
    private var list: List<Repo>
    private val listener: OnRepoSearchListener
    fun setList(list: List<Repo>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoHolder {
        inflater = LayoutInflater.from(parent.context)
        return RepoHolder(inflater!!.inflate(R.layout.item_repo_search, parent, false))
    }

    override fun onBindViewHolder(
        holder: RepoHolder,
        position: Int
    ) {
        val repo: Repo = list[position]
        holder.txtRepo?.setText(repo.fullName)
        holder.checkBox.setOnClickListener { listener.onRepoSearchClicked(repo)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class RepoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.relativeRepoSearch)
        val txtRepo: TextView = itemView.findViewById(R.id.nameRepoSearch)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkboxRepoSearch)

    }

    interface OnRepoSearchListener {
        fun onRepoSearchClicked(repo: Repo)
    }

    init {
        list = ArrayList<Repo>()
        this.listener = listener
    }
}
