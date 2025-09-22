package com.example.letmecook.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letmecook.R
import com.example.letmecook.adapter.CuisineAdapter
import com.example.letmecook.databinding.FragmentHomeBinding
import com.example.letmecook.model.Cuisine

class HomeFragment : Fragment() {

    // Interface untuk komunikasi dengan MainActivity (tidak berubah)
    interface OnSearchBarClickedListener {
        fun onSearchBarClicked()
    }

    private var listener: OnSearchBarClickedListener? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSearchBarClickedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSearchBarClickedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBarCard.setOnClickListener {
            listener?.onSearchBarClicked()
        }

        // FIX: Panggil fungsi untuk menampilkan daftar masakan
        setupCuisineRecyclerView()
    }

    /**
     * Fungsi untuk menyiapkan data masakan dan menampilkannya di RecyclerView.
     */
    private fun setupCuisineRecyclerView() {
        // Anda bisa mengganti placeholder_image dengan gambar yang sudah Anda siapkan
        val cuisines = listOf(
            Cuisine("Italian", R.drawable.placeholder_image),
            Cuisine("Chinese", R.drawable.placeholder_image),
            Cuisine("Mexican", R.drawable.placeholder_image),
            Cuisine("Indian", R.drawable.placeholder_image),
            Cuisine("Japanese", R.drawable.placeholder_image),
            Cuisine("French", R.drawable.placeholder_image),
            Cuisine("American", R.drawable.placeholder_image)
        )

        val cuisineAdapter = CuisineAdapter(cuisines) { selectedCuisine ->
            // Aksi saat item masakan di-klik: Pindah ke RecipeListFragment
            val bundle = bundleOf("cuisine_name" to selectedCuisine.name)
            findNavController().navigate(R.id.action_homeFragment_to_recipeListFragment, bundle)
        }

        binding.recyclerViewCuisines.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cuisineAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}