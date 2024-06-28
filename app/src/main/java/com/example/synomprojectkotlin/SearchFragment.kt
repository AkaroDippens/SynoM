package com.example.synomprojectkotlin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.synomprojectkotlin.adapters.WordAdapter
import com.example.synomprojectkotlin.databinding.FragmentSearchBinding
import com.example.synomprojectkotlin.models.Word
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WordAdapter
    private lateinit var words: List<Word>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        words = parseJson()
        words = sortWordsByFirstLetter(words)

        adapter = WordAdapter(words) { word ->
            showWordDialog(word)
        }

        binding.recyclerViewWords.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewWords.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredWords = if (newText.isNullOrBlank()) {
                    words
                } else {
                    words.filter { word ->
                        word.name.contains(newText, ignoreCase = true) || word.translate.contains(newText, ignoreCase = true)
                    }
                }
                adapter.updateData(filteredWords)
                return true
            }
        })
    }

    private fun showWordDialog(word: Word) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(word.name)
        val message = StringBuilder(("Описание: " + word.definition) ?: "Не найдено описания.")
        word.synonyms?.let {
            message.append("\n\nСинонимы: ${it.values.joinToString(", ")}")
        }
        message.append("\n\nПеревод: ${word.translate}")
        builder.setMessage(message.toString())
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseJson(): List<Word> {
        val json: String?
        val words = mutableListOf<Word>()
        try {
            val inputStream: InputStream = requireContext().assets.open("scratch_1_translated.json")
            json = inputStream.bufferedReader().use { it.readText() }
            val jsonArr = JSONArray(json)
            for (i in 1 until jsonArr.length()) {
                val jsonObj = jsonArr.getJSONObject(i)
                val id = jsonObj.getInt("id")
                val word = jsonObj.getString("name")
                val definition = if (jsonObj.has("definition")) jsonObj.getString("definition") else "Не найдено"
                val synonymsJson = jsonObj.optJSONObject("synonyms")
                var translate = jsonObj.getString("translate")

                val synonyms = jsonObjectToMap(synonymsJson)

                words.add(Word(
                    id,
                    word,
                    definition,
                    synonyms,
                    translate
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return words
    }

    private fun sortWordsByFirstLetter(words: List<Word>): List<Word> {
        return words.sortedBy { it.name.firstOrNull()?.toLowerCase() }
    }

    private fun jsonObjectToMap(jsonObject: JSONObject?): Map<String, String>? {
        if (jsonObject == null) return null
        val map = mutableMapOf<String, String>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.getString(key)
            map[key] = value
        }
        return map
    }
}