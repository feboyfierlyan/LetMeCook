package com.example.letmecook.ui

import android.content.Context
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
import com.example.letmecook.databinding.FragmentHomeBinding
import com.example.letmecook.model.Cuisine

class HomeFragment : Fragment() {

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

        setupCuisineRecyclerView()
    }

    private fun setupCuisineRecyclerView() {
        val cuisines = listOf(
            Cuisine("Italian", R.drawable.image_italian),
            Cuisine("Chinese", R.drawable.image_chinese),
            Cuisine("Mexican", R.drawable.image_mexican),
            Cuisine("Indian", R.drawable.image_indian),
            Cuisine("Japanese", R.drawable.image_japanese),
            Cuisine("French", R.drawable.image_french),
            Cuisine("Spanish", R.drawable.image_spanish),
            Cuisine("American", R.drawable.image_american)
        )

        val cuisineAdapter = CuisineAdapter(cuisines) { selectedCuisine ->
            val bundle = bundleOf("cuisine_name" to selectedCuisine.name)
            findNavController().navigate(R.id.action_homeFragment_to_recipeListFragment, bundle)
        }

        binding.recyclerViewCuisines.apply {
            // FIX: Ganti LinearLayoutManager dengan GridLayoutManager
            // Angka '4' adalah jumlah kolom. Anda bisa mengubahnya sesuai selera.
            val spanCount = 4
            layoutManager = GridLayoutManager(context, spanCount)
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