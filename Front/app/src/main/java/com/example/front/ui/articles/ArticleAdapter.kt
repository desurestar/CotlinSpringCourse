package com.example.front.ui.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.data.model.Article
import com.example.front.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onDeleteClick: ((Article) -> Unit)? = null
) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleViewHolder(
        private val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
            
            // If delete callback is provided, show delete button and set click handler
            if (onDeleteClick != null) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onDeleteClick.invoke(getItem(position))
                    }
                }
            } else {
                binding.btnDelete.visibility = View.GONE
            }
        }

        fun bind(article: Article) {
            binding.apply {
                tvArticleTitle.text = article.title
                tvArticleDescription.text = article.description
                tvMainAuthor.text = "Автор: ${article.mainAuthor!!.name}"
                
                // Format publication date
                article.publicationDate?.let { date ->
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        val parsedDate = inputFormat.parse(date)
                        tvPublicationDate.text = parsedDate?.let { outputFormat.format(it) } ?: date
                    } catch (e: Exception) {
                        tvPublicationDate.text = date
                    }
                } ?: run {
                    tvPublicationDate.text = ""
                }
                
                // Show link indicator if external link exists
                tvLinkIndicator.visibility = if (article.externalLink != null) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                // Ensure delete button visibility aligns with adapter callback
                btnDelete.visibility = if (onDeleteClick != null) View.VISIBLE else View.GONE
            }
        }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}
