package com.example.synomprojectkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.synomprojectkotlin.adapters.RecyclerViewInterface
import com.example.synomprojectkotlin.adapters.RecyclerViewUserAdapter
import com.example.synomprojectkotlin.databinding.ActivityAdminBinding
import com.example.synomprojectkotlin.models.Users
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var buttonDefinition: Button
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            bottomNavigation = bottomNavAdmin
        }

        buttonDefinition = findViewById(R.id.btnToDefinition)

        bottomNavigation.selectedItemId = R.id.admin
        setFragment(AdminFragment(), "ADMIN_FRAGMENT")

        buttonDefinition.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout_admin)

            if (currentFragment is DefinitionFragment) {
                bottomNavigation.selectedItemId = R.id.play
                setFragment(SynomFragment(), "SYNOM_FRAGMENT")
                buttonDefinition.setBackgroundResource(R.drawable.button_definition)
            } else {
                bottomNavigation.selectedItemId = R.id.play
                setFragment(DefinitionFragment(), "DEFINITION_FRAGMENT")
                buttonDefinition.setBackgroundResource(R.drawable.button_to_synom)
            }
        }

        bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (item.itemId == bottomNavigation.selectedItemId) {
                return@OnNavigationItemSelectedListener true
            }
            when (item.itemId) {
                R.id.admin -> {
                    setFragment(AdminFragment(), "ADMIN_FRAGMENT")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.search -> {
                    setFragment(SearchFragment(), "SEARCH_FRAGMENT")
                    return@OnNavigationItemSelectedListener true
                }

                R.id.play -> {
                    setFragment(SynomFragment(), "SYNOM_FRAGMENT")
                    return@OnNavigationItemSelectedListener true
                }

                R.id.profile -> {
                    setFragment(ProfileFragment(), "PROFILE_FRAGMENT")
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun setFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout_admin, fragment, tag)
            .commit()
    }
}