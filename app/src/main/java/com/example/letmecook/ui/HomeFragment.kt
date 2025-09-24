package com.example.letmecook.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import com.example.letmecook.BuildConfig
import com.example.letmecook.R
import com.example.letmecook.adapter.CuisineAdapter
import com.example.letmecook.adapter.RecommendedRecipeAdapter
import com.example.letmecook.api.ApiClient
import com.example.letmecook.api.SpoonacularApi
import com.example.letmecook.databinding.FragmentHomeBinding
import com.example.letmecook.model.Cuisine
import com.example.letmecook.model.RandomRecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    interface OnSearchBarClickedListener {
        fun onSearchBarClicked()
    }

    private var listener: OnSearchBarClickedListener? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! // Perhatikan '!!', ini yang menyebabkan error jika _binding null

    private lateinit var recommendedRecipeAdapter: RecommendedRecipeAdapter

    private val homeViewModel: HomeViewModel by viewModels()

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
        setupRecommendedRecyclerView()

        homeViewModel.fetchRecommendedRecipesIfNeeded()
        observeViewModel()
    }

    private fun observeViewModel() {
        // Amati perubahan pada daftar resep
        homeViewModel.recommendedRecipes.observe(viewLifecycleOwner) { recipes ->
            // Saat data berubah, update adapter RecyclerView
            recommendedRecipeAdapter.updateRecipes(recipes)
        }

        // Amati perubahan pada status loading
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarRecommended.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Amati jika ada pesan error
        homeViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
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
            val spanCount = 4
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = cuisineAdapter
        }
    }

    private fun setupRecommendedRecyclerView() {
        recommendedRecipeAdapter = RecommendedRecipeAdapter(emptyList())
        binding.recyclerViewRecommended.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedRecipeAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // _binding di-set null di sini, yang menyebabkan crash jika API merespons setelah ini.
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}