package com.example.synomprojectkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.synomprojectkotlin.R
import com.example.synomprojectkotlin.databinding.WordItemBinding
import com.example.synomprojectkotlin.models.Word

class WordAdapter(
    private var wordList: List<Word>,
    private val onItemClick: (Word) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.tvWord)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val word = wordList[position]
                    onItemClick(word)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.wordTextView.text = wordList[position].name
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    fun updateData(newWordList: List<Word>) {
        wordList = newWordList
        notifyDataSetChanged()
    }
}