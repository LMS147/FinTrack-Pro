package com.example.fintrackpro.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.Expense
import com.example.fintrackpro.databinding.ItemExpenseBinding
import com.example.fintrackpro.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvDescription.text = expense.description
            binding.tvAmount.text = CurrencyFormatter.format(expense.amount, "ZAR")
            binding.tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(expense.date)
            binding.tvCategory.text = "Category " + expense.categoryId // replace with name if needed
            binding.ivPhoto.visibility = android.view.View.GONE // set later if photo exists

            val color = if (expense.isIncome)
                binding.root.context.getColor(com.example.fintrackpro.R.color.primary_green)
            else
                binding.root.context.getColor(com.example.fintrackpro.R.color.error_red)
            binding.tvAmount.setTextColor(color)

            binding.root.setOnClickListener { onItemClick(expense) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount() = expenses.size
}