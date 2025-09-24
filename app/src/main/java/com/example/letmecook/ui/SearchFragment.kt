package com.example.letmecook.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letmecook.BuildConfig
import com.example.letmecook.adapter.RecipeAdapter
import com.example.letmecook.api.ApiClient
import com.example.letmecook.api.SpoonacularApi
import com.example.letmecook.databinding.FragmentSearchBinding
import com.example.letmecook.model.AutocompleteResult
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
    private lateinit var autocompleteAdapter: ArrayAdapter<String>
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTagInput()
        setupSearchButton()
        loadAndDisplayRecentSearches()
        handleBackButton()
        setupResultsToolbar()
        setupAutocomplete()
    }

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

    private fun searchForRecipes(ingredients: String) {
        // SEMBUNYIKAN Wajah #1 (Form Pencarian)
        binding.scrollView.visibility = View.GONE
        binding.buttonSearch.visibility = View.GONE

        // TAMPILKAN Wajah #2 (Hasil Pencarian)
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
                if (response.isSuccessful && response.body() != null && !response.body()!!.isEmpty()) {
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

    // Fungsi untuk kembali ke form pencarian
    private fun showSearchForm() {
        // TAMPILKAN KEMBALI Wajah #1
        binding.scrollView.visibility = View.VISIBLE
        binding.buttonSearch.visibility = View.VISIBLE
        // SEMBUNYIKAN Wajah #2
        binding.resultsContainer.visibility = View.GONE
        // Kosongkan hasil resep sebelumnya
        recipeAdapter.updateRecipes(emptyList())
        loadAndDisplayRecentSearches()
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

    private fun setupResultsToolbar() {
        binding.toolbarResults.setNavigationOnClickListener {
            showSearchForm()
        }
    }

    // --- Sisa kode untuk Autocomplete dan Tag (tidak berubah) ---
    private fun setupAutocomplete() {
        autocompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.autoCompleteIngredient.setAdapter(autocompleteAdapter)
        binding.autoCompleteIngredient.setOnItemClickListener { _, _, position, _ ->
            val selected = autocompleteAdapter.getItem(position)
            if (selected != null) {
                addChipToGroup(selected, binding.chipGroupIngredients)
                binding.autoCompleteIngredient.setText("")
            }
        }
        binding.autoCompleteIngredient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
            }
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length > 1) {
                    searchRunnable = Runnable { fetchAutocompleteSuggestions(query) }
                    handler.postDelayed(searchRunnable!!, 500)
                }
            }
        })
    }

    private fun fetchAutocompleteSuggestions(query: String) {
        val apiService = ApiClient.getClient().create(SpoonacularApi::class.java)
        val call = apiService.autocompleteIngredients(BuildConfig.SPOONACULAR_API_KEY, query, 5)
        call.enqueue(object : Callback<List<AutocompleteResult>> {
            override fun onResponse(
                call: Call<List<AutocompleteResult>>,
                response: Response<List<AutocompleteResult>>
            ) {
                if (isAdded && response.isSuccessful && response.body() != null) {
                    val suggestions = response.body()!!.map { it.name }
                    autocompleteAdapter.clear()
                    autocompleteAdapter.addAll(suggestions)
                    autocompleteAdapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<List<AutocompleteResult>>, t: Throwable) {}
        })
    }

    private fun setupTagInput() {
        binding.textInputLayout.setEndIconOnClickListener {
            val ingredientText = binding.autoCompleteIngredient.text.toString().trim()
            if (ingredientText.isNotEmpty()) {
                addChipToGroup(ingredientText, binding.chipGroupIngredients)
                binding.autoCompleteIngredient.setText("")
            }
        }
    }

    private fun loadAndDisplayRecentSearches() {
        binding.chipGroupRecentSearches.removeAllViews()
        val history = SearchHistoryManager.getSearchHistory(requireContext())
        binding.textViewEmptyHistory.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        history.forEach { query ->
            addChipToGroup(query, binding.chipGroupRecentSearches, isHistoryChip = true)
        }
    }

    private fun addChipToGroup(text: String, chipGroup: ChipGroup, isHistoryChip: Boolean = false) {
        val chip = Chip(requireContext()).apply {
            this.text = text; isClickable = true; isCheckable = false
            if (isHistoryChip) {
                this.isCloseIconVisible = true
                setOnCloseIconClickListener {
                    val queryToRemove = (it as Chip).text.toString()
                    SearchHistoryManager.removeSearch(requireContext(), queryToRemove)
                    chipGroup.removeView(it)
                    if (chipGroup.childCount == 0) {
                        binding.textViewEmptyHistory.visibility = View.VISIBLE
                    }
                    Toast.makeText(requireContext(), "Riwayat dihapus", Toast.LENGTH_SHORT).show()
                }
                setOnClickListener {
                    binding.chipGroupIngredients.removeAllViews()
                    text.split(",").forEach { ingredient ->
                        addChipToGroup(ingredient, binding.chipGroupIngredients)
                    }
                }
            } else {
                isCloseIconVisible = true
                setOnCloseIconClickListener { chipGroup.removeView(it) }
            }
        }
        chipGroup.addView(chip)
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