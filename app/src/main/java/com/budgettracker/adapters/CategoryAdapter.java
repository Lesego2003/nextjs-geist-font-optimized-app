package com.budgettracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.budgettracker.R;
import com.budgettracker.database.Category;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {
    
    private final Context context;
    private final CategoryClickListener listener;
    private Map<Integer, Integer> expenseCountMap = new HashMap<>();

    public interface CategoryClickListener {
        void onDeleteCategory(Category category);
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, CategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getCategoryId() == newItem.getCategoryId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getCategoryName().equals(newItem.getCategoryName());
        }
    };

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);
        holder.bind(category);
    }

    public void setExpenseCount(Map<Integer, Integer> countMap) {
        this.expenseCountMap = countMap;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryName;
        private final TextView tvExpenseCount;
        private final ImageButton btnDelete;

        CategoryViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvExpenseCount = itemView.findViewById(R.id.tvExpenseCount);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(final Category category) {
            tvCategoryName.setText(category.getCategoryName());
            
            // Set expense count
            int expenseCount = expenseCountMap.getOrDefault(category.getCategoryId(), 0);
            String expenseText = context.getResources()
                .getQuantityString(R.plurals.expense_count, expenseCount, expenseCount);
            tvExpenseCount.setText(expenseText);

            // Set click listeners
            itemView.setOnClickListener(v -> listener.onCategoryClick(category));

            btnDelete.setOnClickListener(v -> {
                if (expenseCount > 0) {
                    // Show warning dialog if category has expenses
                    showDeleteWarningDialog(category, expenseCount);
                } else {
                    // Delete directly if no expenses
                    listener.onDeleteCategory(category);
                }
            });
        }

        private void showDeleteWarningDialog(Category category, int expenseCount) {
            new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_category)
                .setMessage(context.getString(R.string.delete_category_warning, 
                    category.getCategoryName(), expenseCount))
                .setPositiveButton(R.string.delete, (dialog, which) -> 
                    listener.onDeleteCategory(category))
                .setNegativeButton(R.string.cancel, null)
                .show();
        }
    }

    // Helper method to find category position by ID
    public int findPositionById(int categoryId) {
        for (int i = 0; i < getCurrentList().size(); i++) {
            if (getItem(i).getCategoryId() == categoryId) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    // Helper method to get category at position
    public Category getCategoryAt(int position) {
        return getItem(position);
    }
}
