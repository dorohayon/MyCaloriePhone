package com.example.akiscaloriephone.UI;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akiscaloriephone.Database.FoodEntry;
import com.example.akiscaloriephone.R;

import java.util.ArrayList;
import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodListViewHolder> implements Filterable {
    private List<FoodEntry> foodEntries;
    private List<FoodEntry> foodEntriesFull;
    private ItemClickListener itemClickListener;
    private Context context;


    public FoodListAdapter(Context context,ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener=itemClickListener;
        foodEntries=new ArrayList<>();
        foodEntriesFull =new ArrayList<>(foodEntries);
    }




    @NonNull
    @Override
    public FoodListAdapter.FoodListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_foodlist, parent, false);
        return new FoodListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.FoodListViewHolder holder, int position) {
        FoodEntry foodEntry=foodEntries.get(position);
        String foodName=foodEntry.getName();
        int foodCalories=foodEntry.getCalories();
        String foodSize=foodEntry.getSize();
        holder.nameTextView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        holder.nameTextView.setText(foodName);
        holder.caloriesTextView.setText(String.valueOf(foodCalories)+" "+context.getResources().getString(R.string.calories)+"/"+foodSize);
    }



    @Override
    public int getItemCount() {
        return foodEntries.size();
    }

    public void setFoods(List<FoodEntry> foodEntries) {
        this.foodEntries=foodEntries;
        foodEntriesFull =new ArrayList<>(foodEntries);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return foodsFilter;
    }

    public Filter foodsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<FoodEntry> filtered = new ArrayList<>();
            if(charSequence==null || charSequence.length()==0)
                filtered.addAll(foodEntriesFull);
            else{
                String searchString=charSequence.toString().toLowerCase().trim();
                for (FoodEntry food : foodEntriesFull)
                    if(food.getName().toLowerCase().contains(searchString) || String.valueOf(food.getCalories()).toLowerCase().contains(searchString))
                        filtered.add(food);
            }
            FilterResults results=new FilterResults();
            results.values=filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            foodEntries.clear();
            foodEntries.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public class FoodListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        TextView caloriesTextView;
        ImageView imageView;



        public FoodListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView=itemView.findViewById(R.id.list_item_name);
            caloriesTextView=itemView.findViewById(R.id.list_item_calories);
            //imageView=itemView.findViewById(R.id.list_item_image);
            //imageView=itemView.findViewById(R.id.list_item_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = foodEntries.get(getAdapterPosition()).getId();
            itemClickListener.onItemClickListener(elementId);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getCaloriesTextView() {
            return caloriesTextView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }



}
