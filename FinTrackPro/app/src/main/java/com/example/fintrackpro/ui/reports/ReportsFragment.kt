package com.example.fintrackpro.ui.reports

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentReportsBinding
import com.example.fintrackpro.ui.reports.PieChartManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment(R.layout.fragment_reports) {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val userId = 1 // replace with auth
    private val viewModel: ReportsViewModel by viewModels {
        ReportsViewModelFactory(requireContext(), userId)
    }

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReportsBinding.bind(view)

        setupDateButtons()
        observeUiState()
    }

    private fun setupDateButtons() {
        binding.btnStartDate.setOnClickListener {
            showDatePicker { date ->
                viewModel.setDateRange(date, viewModel.uiState.value.endDate ?: Calendar.getInstance().time)
                binding.btnStartDate.text = dateFormatter.format(date)
            }
        }

        binding.btnEndDate.setOnClickListener {
            showDatePicker { date ->
                viewModel.setDateRange(viewModel.uiState.value.startDate ?: Calendar.getInstance().time, date)
                binding.btnEndDate.text = dateFormatter.format(date)
            }
        }
    }

    private fun showDatePicker(callback: (Date) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            cal.set(year, month, day)
            callback(cal.time)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect

                    // Update pie chart
                    PieChartManager.setupPieChart(binding.pieChart, state.categoryTotals)

                    // Update breakdown list
                    binding.rvCategoryBreakdown.adapter = CategoryBreakdownAdapter(
                        state.categoryTotals,
                        state.totalSpent
                    )

                    // Update date button texts
                    state.startDate?.let { binding.btnStartDate.text = dateFormatter.format(it) }
                    state.endDate?.let { binding.btnEndDate.text = dateFormatter.format(it) }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}