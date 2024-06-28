package com.example.synomprojectkotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.synomprojectkotlin.R
import com.example.synomprojectkotlin.models.Users

class RecyclerViewUserAdapter(
    private var context: Context?,
    private var usersList: ArrayList<Users>,
    private var recyclerViewInterface: RecyclerViewInterface?
) : RecyclerView.Adapter<RecyclerViewUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = usersList[position]
        holder.username.text = "Никнейм: " + user.username
        holder.email.text = "Почта: " + user.email
        holder.password.text = "Пароль: " + user.password

        holder.deleteButton.setOnClickListener { recyclerViewInterface?.onDeleteClick(user) }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    fun updateUsers(newUsersList: ArrayList<Users>) {
        usersList = newUsersList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.user_username)
        var email: TextView = itemView.findViewById(R.id.user_email)
        var password: TextView = itemView.findViewById(R.id.user_password)
        var deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)
    }
}