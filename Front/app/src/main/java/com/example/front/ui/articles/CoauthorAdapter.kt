package com.example.front.ui.articles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.data.model.Employee
import com.example.front.databinding.ItemCoauthorBinding

class CoauthorAdapter : ListAdapter<Employee, CoauthorAdapter.CoauthorViewHolder>(CoauthorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoauthorViewHolder {
        val binding = ItemCoauthorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoauthorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoauthorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CoauthorViewHolder(
        private val binding: ItemCoauthorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            binding.apply {
                tvCoauthorName.text = employee.name
                tvCoauthorDepartment.text = employee.department?.departmentName ?: "Кафедра не указана"
            }
        }
    }

    class CoauthorDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}
