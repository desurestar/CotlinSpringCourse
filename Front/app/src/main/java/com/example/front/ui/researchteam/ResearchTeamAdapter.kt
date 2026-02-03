package com.example.front.ui.researchteam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.data.model.ResearchTeam
import com.example.front.databinding.ItemResearchTeamBinding

class ResearchTeamAdapter(
    private val onItemClick: (ResearchTeam) -> Unit,
    private val onDeleteClick: ((ResearchTeam) -> Unit)? = null,
    private val onItemLongClick: ((ResearchTeam) -> Unit)? = null
) : ListAdapter<ResearchTeam, ResearchTeamAdapter.TeamViewHolder>(TeamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemResearchTeamBinding.inflate(
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
        private val binding: ItemResearchTeamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(team: ResearchTeam) {
            binding.tvTeamName.text = team.name
            binding.tvLeader.text = "👤 Лидер: ${team.leader?.name ?: "Не указан"}"
            
            val membersCount = team.members?.size ?: 0
            binding.tvMembersCount.text = "👥 $membersCount участников"
            
            val worksCount = team.researchWorks?.size ?: 0
            binding.tvWorksCount.text = "📝 $worksCount работы"
            
            binding.root.setOnClickListener {
                onItemClick(team)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick?.invoke(team)
                true
            }

            // Delete button
            binding.btnDeleteTeam?.let { btn ->
                if (onDeleteClick != null) {
                    btn.visibility = android.view.View.VISIBLE
                    btn.setOnClickListener { onDeleteClick.invoke(team) }
                } else {
                    btn.visibility = android.view.View.GONE
                }
            }
        }
    }

    class TeamDiffCallback : DiffUtil.ItemCallback<ResearchTeam>() {
        override fun areItemsTheSame(oldItem: ResearchTeam, newItem: ResearchTeam): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResearchTeam, newItem: ResearchTeam): Boolean {
            return oldItem == newItem
        }
    }
}
