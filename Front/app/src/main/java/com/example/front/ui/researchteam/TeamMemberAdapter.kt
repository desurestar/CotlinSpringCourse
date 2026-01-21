package com.example.front.ui.researchteam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.data.model.TeamMember
import com.example.front.databinding.ItemTeamMemberBinding

class TeamMemberAdapter : ListAdapter<TeamMember, TeamMemberAdapter.MemberViewHolder>(MemberDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemTeamMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MemberViewHolder(
        private val binding: ItemTeamMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: TeamMember) {
            binding.tvMemberName.text = member.getMemberName()
            binding.tvMemberType.text = member.getMemberType()
            binding.tvRole.text = member.role
        }
    }

    class MemberDiffCallback : DiffUtil.ItemCallback<TeamMember>() {
        override fun areItemsTheSame(oldItem: TeamMember, newItem: TeamMember): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TeamMember, newItem: TeamMember): Boolean {
            return oldItem == newItem
        }
    }
}
