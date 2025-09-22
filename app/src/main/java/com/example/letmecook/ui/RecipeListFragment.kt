package com.example.letmecook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letmecook.BuildConfig
import com.example.letmecook.adapter.RecipeAdapter
import com.example.letmecook.api.ApiClient
import com.example.letmecook.api.SpoonacularApi
import com.example.letmecook.databinding.FragmentRecipeListBinding
// IMPORT PENTING: Pastikan baris-baris ini ada dan tidak error
import com.example.letmecook.model.ComplexSearchResponse
import com.example.letmecook.model.RecipeByIngredientResponse
// ---
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter
    private var cuisineName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cuisineName = it.getString("cuisine_name")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        cuisineName?.let {
            fetchRecipesByCuisine(it)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "$cuisineName Recipes"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(requireContext(), mutableListOf())
        binding.recyclerViewRecipes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeAdapter
        }
    }

    private fun fetchRecipesByCuisine(cuisine: String) {
        binding.progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.getClient().create(SpoonacularApi::class.java)
        val call = apiService.searchRecipesByCuisine(BuildConfig.SPOONACULAR_API_KEY, cuisine, 20)

        call.enqueue(object : Callback<ComplexSearchResponse> {
            override fun onResponse(call: Call<ComplexSearchResponse>, response: Response<ComplexSearchResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val searchResults = response.body()!!.results
                    val recipes = searchResults.map { result ->
                        RecipeByIngredientResponse(result.id, result.title, result.image)
                    }
                    recipeAdapter.updateRecipes(recipes)
                } else {
                    Toast.makeText(context, "Failed to fetch recipes.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ComplexSearchResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}