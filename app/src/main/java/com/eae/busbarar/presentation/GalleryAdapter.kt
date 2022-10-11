package com.eae.busbarar.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eae.busbarar.databinding.ListItemGalleryBinding
import com.eae.busbarar.data.model.Image

class GalleryAdapter(private val callback: IGallery) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    var list: List<Image> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: ListItemGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item: Image = list[position]
            Glide.with(binding.root).load(item.path).into(binding.imageView)
            binding.materialCardView.setOnClickListener {
                callback.onItemClick(item)
            }
        }
    }

}