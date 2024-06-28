package com.example.synomprojectkotlin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.synomprojectkotlin.adapters.RecyclerViewInterface
import com.example.synomprojectkotlin.adapters.RecyclerViewUserAdapter
import com.example.synomprojectkotlin.databinding.FragmentAdminBinding
import com.example.synomprojectkotlin.models.Users

class AdminFragment : Fragment(), RecyclerViewInterface {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecyclerViewUserAdapter
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBaseHelper = DataBaseHelper(requireContext())

        binding.listUser.layoutManager = LinearLayoutManager(requireContext())
        binding.listUser.setHasFixedSize(true)

        adapter = RecyclerViewUserAdapter(requireContext(), dataBaseHelper.getUsers(), this)
        binding.listUser.adapter = adapter
    }


    override fun onDeleteClick(user: Users) {
        dataBaseHelper.deleteUser(user.id)
        val updatedUsersList = dataBaseHelper.getUsers()
        adapter.updateUsers(updatedUsersList)
    }
}