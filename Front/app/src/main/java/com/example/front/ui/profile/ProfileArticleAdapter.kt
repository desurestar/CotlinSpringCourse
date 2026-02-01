package com.example.front.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.data.model.Article
import com.example.front.databinding.ItemArticleBinding

class ProfileArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onDeleteClick: ((Article) -> Unit)? = null
) : ListAdapter<Article, ProfileArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {
    
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
        
        fun bind(article: Article) {
            binding.apply {
                tvArticleTitle.text = article.title
                tvArticleDescription.text = article.description
                tvPublicationDate.text = article.publicationDate ?: "Дата не указана"
                tvMainAuthor.text = "Автор: ${article.getAuthorName()}"
                
                // Show coauthors count if available
                val coauthorCount = article.coauthors?.size ?: 0
                if (coauthorCount > 0) {
                    tvCoauthors.text = "Соавторы: $coauthorCount"
                    tvCoauthors.visibility = android.view.View.VISIBLE
                } else {
                    tvCoauthors.visibility = android.view.View.GONE
                }
                
                root.setOnClickListener {
                    onItemClick(article)
                }
                
                // Show delete button only if callback provided
                if (onDeleteClick != null) {
                    btnDelete.visibility = android.view.View.VISIBLE
                    btnDelete.setOnClickListener {
                        onDeleteClick.invoke(article)
                    }
                } else {
                    btnDelete.visibility = android.view.View.GONE
                }
            }
        }
    }
    
    private class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}
