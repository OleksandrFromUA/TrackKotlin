package com.example.map.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.map.databinding.FragmentMapBinding


class MapFragment : Fragment() {
private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
binding = FragmentMapBinding.inflate(inflater, container, false)




       return binding.root
    }


}