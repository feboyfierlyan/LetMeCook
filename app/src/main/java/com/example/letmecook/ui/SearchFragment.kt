package com.example.letmecook.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letmecook.BuildConfig
import com.example.letmecook.adapter.RecipeAdapter
import com.example.letmecook.api.ApiClient
import com.example.letmecook.api.SpoonacularApi
import com.example.letmecook.databinding.FragmentSearchBinding
import com.example.letmecook.model.RecipeByIngredientResponse
import com.google.android.material.chip.Chip
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

        setupRecyclerView()
        setupTagInput()
        setupSearchButton()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(requireContext(), mutableListOf())
        binding.recyclerViewSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearchResults.adapter = recipeAdapter
    }

    private fun setupTagInput() {
        binding.buttonAddIngredient.setOnClickListener {
            val ingredientText = binding.editTextIngredient.text.toString().trim()
            if (ingredientText.isNotEmpty()) {
                addChipToGroup(ingredientText)
                binding.editTextIngredient.setText("")
            }
        }
    }

    private fun setupSearchButton() {
        binding.buttonSearch.setOnClickListener {
            val ingredients = getIngredientsFromChips()
            if (ingredients.isNotEmpty()) {
                // Panggil fungsi pencarian resep dengan API
                searchForRecipes(ingredients)
            } else {
                Toast.makeText(requireContext(), "Silakan tambahkan bahan terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchForRecipes(ingredients: String) {
        // Tampilkan animasi loading
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
                // Sembunyikan animasi loading
                binding.progressBarSearch.visibility = View.GONE

                if (response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                    // Update adapter dengan data resep
                    recipeAdapter.updateRecipes(response.body())
                } else {
                    Toast.makeText(requireContext(), "No recipes found. Try different ingredients.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<RecipeByIngredientResponse>>, t: Throwable) {
                // Sembunyikan animasi loading
                binding.progressBarSearch.visibility = View.GONE
                Toast.makeText(requireContext(), "Network Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addChipToGroup(text: String) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            isClickable = true
            isCheckable = false
            setOnCloseIconClickListener { binding.chipGroupIngredients.removeView(it) }
        }
        binding.chipGroupIngredients.addView(chip)
    }

    private fun getIngredientsFromChips(): String {
        val ingredientsList = (0 until binding.chipGroupIngredients.childCount).map {
            (binding.chipGroupIngredients.getChildAt(it) as Chip).text.toString()
        }
        return TextUtils.join(",", ingredientsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}