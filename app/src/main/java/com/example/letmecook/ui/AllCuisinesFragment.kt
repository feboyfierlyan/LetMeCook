package com.example.letmecook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.letmecook.R
import com.example.letmecook.adapter.CuisineAdapter
import com.example.letmecook.data.CuisineRepository
import com.example.letmecook.databinding.FragmentAllCuisinesBinding

class AllCuisinesFragment : Fragment() {

    private var _binding: FragmentAllCuisinesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllCuisinesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup tombol kembali di toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Ambil semua data masakan dari repository
        val allCuisines = CuisineRepository.getAllCuisines()

        // Gunakan adapter yang sudah ada (CuisineAdapter)
        val cuisineAdapter = CuisineAdapter(allCuisines) { selectedCuisine ->
            // Aksi saat item diklik: navigasi ke halaman hasil resep
            val bundle = bundleOf("cuisine_name" to selectedCuisine.name)
            findNavController().navigate(R.id.action_allCuisinesFragment_to_recipeListFragment, bundle)
        }

        binding.recyclerViewAllCuisines.apply {
            // Gunakan GridLayoutManager agar menjadi petak
            layoutManager = GridLayoutManager(context, 4) // 4 kolom
            adapter = cuisineAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}