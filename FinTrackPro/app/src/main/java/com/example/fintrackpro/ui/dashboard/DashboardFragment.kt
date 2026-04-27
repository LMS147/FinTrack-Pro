package com.example.fintrackpro.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.databinding.FragmentDashboardBinding
import com.example.fintrackpro.ui.expense.AddExpenseActivity
import com.example.fintrackpro.utils.CurrencyFormatter
import kotlinx.coroutines.launch
import com.example.fintrackpro.data.Repository.CategoryRepository

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // In a real app, obtain userId from shared preferences or auth manager
    private val userId = 1 // temporary default; replace with actual logged-in user ID

    // ViewModel factory – replace with proper DI if available
    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(requireContext(), userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeUiState()

        // Initialize repository to insert default categories
        val database = com.example.fintrackpro.data.FinTrackDatabase.getDatabase(requireContext())
        val categoryRepository = CategoryRepository(database.categoryDao())
        viewLifecycleOwner.lifecycleScope.launch {
            categoryRepository.insertDefaultCategories(userId)
        }
    }

    private fun setupRecyclerView() {
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        // Adapter will be updated when data changes
    }

    private fun setupFab() {
        binding.fabAddExpense.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: DashboardUiState) {
        if (state.isLoading) {
            // Optionally show a shimmer or progress
            return
        }

        state.errorMessage?.let {
            // Show error in a snackbar or toast
            return
        }

        val currency = state.currency

        // Update summary cards
        binding.tvBalance.text = CurrencyFormatter.format(state.totalBalance, currency)
        binding.tvIncome.text = CurrencyFormatter.format(state.totalIncome, currency)
        binding.tvExpenses.text = CurrencyFormatter.format(state.totalExpenses, currency)

        // Update recent transactions
        if (state.recentTransactions.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvRecentTransactions.adapter = null
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvRecentTransactions.adapter = RecentTransactionsAdapter(state.recentTransactions, currency)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}