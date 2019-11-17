package com.example.akiscaloriephone.UI;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.AppExecutors;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;
import com.example.akiscaloriephone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.FoodViewHolder>  {
    private List<DiaryEntry> diaryEntries;
    private ItemClickListener itemClickListener;
    private Context context;


    public DiaryAdapter(Context context,ItemClickListener itemClickListener) {
        this.context = context;
        diaryEntries = new ArrayList<>();
        this.itemClickListener=itemClickListener;

    }

    public void setDiaryEntries(List<DiaryEntry> diaryEntries) {
        int size = this.diaryEntries.size();
        this.diaryEntries.clear();
        notifyItemRangeRemoved(0, size);
        this.diaryEntries = diaryEntries;
        notifyDataSetChanged();
    }

    public void updateData(List<DiaryEntry> diaryEntries) {
        this.diaryEntries.clear();
        this.diaryEntries.addAll(diaryEntries);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiaryAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.FoodViewHolder holder, int position) {
        DiaryEntry diaryEntry = diaryEntries.get(position);
        String foodName = diaryEntry.getName();
        int foodCalories = diaryEntry.getCalories();
        String foodSize = diaryEntry.getSize();
        double foodQuantity = diaryEntry.getQuantity();
        String foodQuantiryString = String.valueOf(foodQuantity);
        holder.nameTextView.setText(foodName);
        holder.caloriesTextView.setText(String.valueOf(foodCalories) + " " + context.getResources().getString(R.string.calories));
        holder.quantityTextView.setText(foodQuantiryString+" X "+foodSize);
        if(diaryEntry.getDate()!=null) { // i'm on favorites food
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            holder.hourTextView.setText(formatter.format(diaryEntry.getDate()));
        }
        else
            holder.hourTextView.setVisibility(View.INVISIBLE);

    }


    @Override
    public int getItemCount() {
        return diaryEntries.size();
    }


    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener, View.OnClickListener  {
        TextView nameTextView;
        TextView caloriesTextView;
        TextView quantityTextView;
        TextView hourTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.diary_name);
            caloriesTextView = itemView.findViewById(R.id.diary_calories);
            quantityTextView = itemView.findViewById(R.id.diary_quantity);
            hourTextView=itemView.findViewById(R.id.diary_hour);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }




        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle(context.getResources().getString(R.string.selectaction));
            if(diaryEntries.get(getAdapterPosition()).getDate()!=null) { //im on favorite food activity
                MenuItem edit = menu.add(Menu.NONE, 1, 1, context.getResources().getString(R.string.edit));
                edit.setOnMenuItemClickListener(onChange);
            }
            MenuItem delete = menu.add(Menu.NONE,2,2,context.getResources().getString(R.string.delete));
            delete.setOnMenuItemClickListener(onChange);
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case 1:
                        editFoodItem();
                        return true;
                    case 2:
                        deleteFoodItem();
                        Toast.makeText(context,context.getResources().getString(R.string.fooddeletedfromdiary),Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        };

        private void editFoodItem() {
            Intent editOnDiary = new Intent(context,AddToDiaryActivity.class);
            editOnDiary.putExtra(AppContract.MODE,AppContract.MODE_EDIT_FROM_DIARY);
            editOnDiary.putExtra(AppContract.FOODLIST_ID,diaryEntries.get(getAdapterPosition()).getId());
            context.startActivity(editOnDiary);
        }

        private void deleteFoodItem() {
            final AppDatabase db=AppDatabase.getInstance(context);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    db.diaryDao().delete(diaryEntries.get(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View view) {
            int elementId = diaryEntries.get(getAdapterPosition()).getId();
            itemClickListener.onItemClickListener(elementId);
        }
    }


}
