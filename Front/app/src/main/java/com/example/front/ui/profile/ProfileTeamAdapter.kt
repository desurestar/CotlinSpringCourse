package com.example.front.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.data.model.ResearchTeam
import com.example.front.databinding.ItemResearchTeamSmallBinding

class ProfileTeamAdapter(
    private val onItemClick: (ResearchTeam) -> Unit,
    private val onDeleteClick: ((ResearchTeam) -> Unit)? = null
) : ListAdapter<ResearchTeam, ProfileTeamAdapter.TeamViewHolder>(TeamDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemResearchTeamSmallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TeamViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TeamViewHolder(
        private val binding: ItemResearchTeamSmallBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(team: ResearchTeam) {
            binding.apply {
                tvTeamName.text = team.name
                tvTeamDescription.text = team.description ?: "Описание отсутствует"
                tvLeaderName.text = "Руководитель: ${team.leader?.name ?: "Не указан"}"
                
                // Show member count if available
                val memberCount = team.members?.size ?: 0
                tvMemberCount.text = "Участников: $memberCount"
                
                root.setOnClickListener {
                    onItemClick(team)
                }
                
                // Show delete button only if callback provided
                if (onDeleteClick != null) {
                    btnDelete.visibility = android.view.View.VISIBLE
                    btnDelete.setOnClickListener {
                        onDeleteClick.invoke(team)
                    }
                } else {
                    btnDelete.visibility = android.view.View.GONE
                }
            }
        }
    }
    
    private class TeamDiffCallback : DiffUtil.ItemCallback<ResearchTeam>() {
        override fun areItemsTheSame(oldItem: ResearchTeam, newItem: ResearchTeam): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ResearchTeam, newItem: ResearchTeam): Boolean {
            return oldItem == newItem
        }
    }
}
