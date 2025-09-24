package com.example.letmecook.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letmecook.BuildConfig
import com.example.letmecook.adapter.RecipeAdapter
import com.example.letmecook.api.ApiClient
import com.example.letmecook.api.SpoonacularApi
import com.example.letmecook.databinding.FragmentSearchBinding
import com.example.letmecook.model.RecipeByIngredientResponse
import com.example.letmecook.util.SearchHistoryManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ... (Kode lain tidak berubah)
        setupRecyclerView()
        setupTagInput()
        setupSearchButton()
        loadAndDisplayRecentSearches()
        handleBackButton()
        setupResultsToolbar()
    }

    // ... (Fungsi lain seperti setupRecyclerView, searchForRecipes, dll. tidak berubah)

    /**
     * Memodifikasi fungsi ini untuk menambahkan ikon hapus pada chip riwayat
     */
    private fun addChipToGroup(text: String, chipGroup: ChipGroup, isHistoryChip: Boolean = false) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            this.isClickable = true
            this.isCheckable = false

            if (isHistoryChip) {
                // Untuk chip riwayat, buat agar bisa ditutup (ada ikon 'x')
                this.isCloseIconVisible = true

                // Aksi saat ikon 'x' di chip riwayat diklik
                this.setOnCloseIconClickListener {
                    val queryToRemove = (it as Chip).text.toString()
                    // 1. Hapus dari penyimpanan (SharedPreferences)
                    SearchHistoryManager.removeSearch(requireContext(), queryToRemove)
                    // 2. Hapus dari tampilan (UI)
                    chipGroup.removeView(it)
                    // 3. Periksa apakah riwayat jadi kosong, jika iya, tampilkan teks
                    if (chipGroup.childCount == 0) {
                        binding.textViewEmptyHistory.visibility = View.VISIBLE
                    }
                    Toast.makeText(requireContext(), "Riwayat dihapus", Toast.LENGTH_SHORT).show()
                }

                // Aksi saat badan chip (bukan ikon 'x') diklik
                setOnClickListener {
                    binding.chipGroupIngredients.removeAllViews()
                    text.split(",").forEach { ingredient ->
                        addChipToGroup(ingredient, binding.chipGroupIngredients)
                    }
                }
            } else {
                // Untuk chip bahan, hanya ada ikon hapus
                this.isCloseIconVisible = true
                setOnCloseIconClickListener { chipGroup.removeView(it) }
            }
        }
        chipGroup.addView(chip)
    }

    // --- Pastikan sisa kode Anda sama seperti respons sebelumnya ---
    // ... (sisa fungsi lainnya)
    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(requireContext(), mutableListOf())
        binding.recyclerViewSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearchResults.adapter = recipeAdapter
    }

    private fun setupSearchButton() {
        binding.buttonSearch.setOnClickListener {
            val ingredients = getIngredientsFromChips(binding.chipGroupIngredients)
            if (ingredients.isNotEmpty()) {
                SearchHistoryManager.saveSearch(requireContext(), ingredients)
                searchForRecipes(ingredients)
            } else {
                Toast.makeText(requireContext(), "Silakan tambahkan bahan terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupResultsToolbar() {
        binding.toolbarResults.setNavigationOnClickListener {
            showSearchForm()
        }
    }

    private fun searchForRecipes(ingredients: String) {
        binding.scrollView.visibility = View.GONE
        binding.buttonSearch.visibility = View.GONE
        binding.resultsContainer.visibility = View.VISIBLE
        binding.progressBarSearch.visibility = View.VISIBLE
        val apiService = ApiClient.getClient().create(SpoonacularApi::class.java)
        val call = apiService.searchRecipesByIngredients(
            BuildConfig.SPOONACULAR_API_KEY, ingredients, 20
        )
        call.enqueue(object : Callback<List<RecipeByIngredientResponse>> {
            override fun onResponse(
                call: Call<List<RecipeByIngredientResponse>>,
                response: Response<List<RecipeByIngredientResponse>>
            ) {
                binding.progressBarSearch.visibility = View.GONE
                if (response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                    recipeAdapter.updateRecipes(response.body())
                } else {
                    Toast.makeText(requireContext(), "No recipes found. Try different ingredients.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<List<RecipeByIngredientResponse>>, t: Throwable) {
                binding.progressBarSearch.visibility = View.GONE
                Toast.makeText(requireContext(), "Network Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleBackButton() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.resultsContainer.visibility == View.VISIBLE) {
                    showSearchForm()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun showSearchForm() {
        binding.resultsContainer.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
        binding.buttonSearch.visibility = View.VISIBLE
        recipeAdapter.updateRecipes(emptyList())
        loadAndDisplayRecentSearches()
    }

    private fun loadAndDisplayRecentSearches() {
        binding.chipGroupRecentSearches.removeAllViews()
        val history = SearchHistoryManager.getSearchHistory(requireContext())
        binding.textViewEmptyHistory.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        history.forEach { query ->
            addChipToGroup(query, binding.chipGroupRecentSearches, isHistoryChip = true)
        }
    }

    private fun setupTagInput() {
        binding.textInputLayout.setEndIconOnClickListener {
            val ingredientText = binding.editTextIngredient.text.toString().trim()
            if (ingredientText.isNotEmpty()) {
                addChipToGroup(ingredientText, binding.chipGroupIngredients)
                binding.editTextIngredient.setText("")
            }
        }
    }

    private fun getIngredientsFromChips(chipGroup: ChipGroup): String {
        return (0 until chipGroup.childCount).map {
            (chipGroup.getChildAt(it) as Chip).text.toString()
        }.joinToString(",")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}