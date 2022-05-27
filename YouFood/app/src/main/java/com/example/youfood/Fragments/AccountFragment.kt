package com.example.youfood.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youfood.MainActivity
import com.example.youfood.R
import kotlinx.android.synthetic.main.account_screen.*
import java.io.File


class AccountFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton.setOnClickListener {
            val file = File(requireActivity().filesDir, "records.txt")
            if (file.delete()) {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
            }


        }
    }

}