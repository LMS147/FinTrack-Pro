package com.example.fintrackpro.ui.expense

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentExpenseBinding
import com.example.fintrackpro.ui.expense.ExpenseAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFragment : Fragment(R.layout.fragment_expense) {

    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!
    private val userId = 1 // from auth

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(requireContext(), userId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExpenseBinding.bind(view)

        setupRecyclerView()
        observeUiState()

        binding.btnFilter.setOnClickListener {
            showDateRangePicker()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        // Adapter will be set in observe
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect
                    if (state.error != null) {
                        // show error
                        return@collect
                    }
                    binding.rvExpenses.adapter = ExpenseAdapter(state.expenses) { expense ->
                        // Navigate to detail
                        val intent = Intent(requireContext(), ExpenseDetailActivity::class.java).apply {
                            putExtra("expenseId", expense.expenseId)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showDateRangePicker() {
        // For simplicity, we'll use a single DatePicker for start and end.
        // In a real app, you'd show two sequential pickers.
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            cal.set(year, month, day)
            val startDate = cal.time
            // Default end date is today
            viewModel.setDateFilter(startDate, Calendar.getInstance().time)
            binding.tvSelectedPeriod.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(startDate) + " - Today"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}