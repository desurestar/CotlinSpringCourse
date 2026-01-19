package com.example.front.ui.departments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.R
import com.example.front.data.model.Department
import com.example.front.databinding.ItemDepartmentBinding

class DepartmentAdapter(
    private val onItemClick: (Department) -> Unit
) : ListAdapter<Department, DepartmentAdapter.DepartmentViewHolder>(DepartmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val binding = ItemDepartmentBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return DepartmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DepartmentViewHolder(
        private val binding: ItemDepartmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(department: Department) {
            binding.tvDepartmentName.text = department.departmentName
            binding.tvEmployeeCount.text = itemView.context.getString(
                R.string.employee_count, 
                department.employeeCount ?: 0
            )
            
            binding.root.setOnClickListener {
                onItemClick(department)
            }
        }
    }

    class DepartmentDiffCallback : DiffUtil.ItemCallback<Department>() {
        override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean {
            return oldItem == newItem
        }
    }
}
