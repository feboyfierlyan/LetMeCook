package com.example.letmecook.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letmecook.R
import com.example.letmecook.adapter.CuisineAdapter
import com.example.letmecook.adapter.RecommendedRecipeAdapter
import com.example.letmecook.data.CuisineRepository
import com.example.letmecook.databinding.FragmentHomeBinding
import com.example.letmecook.model.Cuisine

class HomeFragment : Fragment() {

    interface OnSearchBarClickedListener {
        fun onSearchBarClicked()
    }

    private var listener: OnSearchBarClickedListener? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel. Logika API akan ada di sini.
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var recommendedRecipeAdapter: RecommendedRecipeAdapter

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
        binding.textViewSeeAllCuisines.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allCuisinesFragment)
        }

        setupCuisineRecyclerView()
        setupRecommendedRecyclerView()

        // Minta data dari ViewModel, BUKAN panggil API langsung
        homeViewModel.fetchRecommendedRecipesIfNeeded()

        // "Amati" perubahan data dari ViewModel
        observeViewModel()
    }

    private fun observeViewModel() {
        homeViewModel.recommendedRecipes.observe(viewLifecycleOwner) { recipes ->
            recommendedRecipeAdapter.updateRecipes(recipes)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.recyclerViewRecommended.visibility = View.GONE
                binding.shimmerLayoutRecommended.visibility = View.VISIBLE
                binding.shimmerLayoutRecommended.startShimmer()
            } else {
                binding.shimmerLayoutRecommended.stopShimmer()
                binding.shimmerLayoutRecommended.visibility = View.GONE
                binding.recyclerViewRecommended.visibility = View.VISIBLE
            }
        }

        homeViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            // Sembunyikan shimmer jika terjadi error
            binding.shimmerLayoutRecommended.stopShimmer()
            binding.shimmerLayoutRecommended.visibility = View.GONE
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCuisineRecyclerView() {
        // Mengambil data dari Repository, menampilkan 8 item pertama
        val cuisines = CuisineRepository.getAllCuisines().take(8)

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

    // FUNGSI fetchRecommendedRecipes() SUDAH DIHAPUS DARI SINI
    // KARENA LOGIKANYA SUDAH PINDAH KE HomeViewModel

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}