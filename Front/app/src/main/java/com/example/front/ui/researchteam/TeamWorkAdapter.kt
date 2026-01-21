package com.example.front.ui.researchteam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.data.model.TeamResearchWork
import com.example.front.databinding.ItemTeamWorkBinding

class TeamWorkAdapter : ListAdapter<TeamResearchWork, TeamWorkAdapter.WorkViewHolder>(WorkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val binding = ItemTeamWorkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WorkViewHolder(
        private val binding: ItemTeamWorkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(work: TeamResearchWork) {
            binding.tvWorkTitle.text = work.title
            binding.tvWorkDescription.text = work.description
            binding.tvStatus.text = work.getStatusText()
            
            val statusColor = ContextCompat.getColor(binding.root.context, work.getStatusColor())
            binding.tvStatus.setTextColor(statusColor)
        }
    }

    class WorkDiffCallback : DiffUtil.ItemCallback<TeamResearchWork>() {
        override fun areItemsTheSame(oldItem: TeamResearchWork, newItem: TeamResearchWork): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TeamResearchWork, newItem: TeamResearchWork): Boolean {
            return oldItem == newItem
        }
    }
}
