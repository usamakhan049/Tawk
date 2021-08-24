package com.example.tawk.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tawk.data.db.entity.User
import com.example.tawk.databinding.ItemUserBinding
import com.example.tawk.util.hide
import com.example.tawk.util.invert
import com.example.tawk.util.load
import com.example.tawk.util.show

class HomeRecyclerViewAdapter :
    PagingDataAdapter<User, HomeRecyclerViewAdapter.ViewHolder>(USER_COMPARATOR) {

    private var iUserListItemClickListner: IUserListItemClickListner? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(holder.absoluteAdapterPosition)
        if (currentItem != null) {
            holder.bind(currentItem, holder.absoluteAdapterPosition)
            holder.binding.mcvItem.setOnClickListener {
                iUserListItemClickListner?.onItemClick(currentItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val itemUserBinding = ItemUserBinding.inflate(inflator, parent, false)

        return ViewHolder(itemUserBinding)
    }

     fun setItemClickListner(listner: IUserListItemClickListner) {
        iUserListItemClickListner = listner
    }

    class ViewHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User, position: Int) {
            binding.ivUserName.text = user.login
            binding.ivUserDetail.text = user.type
            binding.ivUserProfile.load(user.avatar_url,true,true)

            if (position != 0 && (position + 1) % 4 == 0) {
                binding.ivUserProfile.invert()
            } else {
                binding.ivUserProfile.clearColorFilter()
            }

            if (user.notes.isNullOrEmpty()){
                binding.ivNoteIcon.hide()
            }else {
                binding.ivNoteIcon.show()
            }
        }

    }

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id && oldItem.login == newItem.login
            }

        }
    }
}