package com.example.akiscaloriephone.UI;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.Database.FoodEntry;
import com.example.akiscaloriephone.Database.SportEntry;
import com.example.akiscaloriephone.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SportListAdapter extends RecyclerView.Adapter<SportListAdapter.SportListViewHolder> implements Filterable {
    private List<SportEntry> sportEntries;
    private List<SportEntry> sportEntriesFull;

    private Context context;
    private int dateIndicator;

    public SportListAdapter(Context context) {
        this.context = context;
        sportEntries=new ArrayList<>();
        sportEntriesFull=new ArrayList<>(sportEntries);
    }

    @NonNull
    @Override
    public SportListAdapter.SportListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_sportlist, parent, false);
        return new SportListAdapter.SportListViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull SportListViewHolder holder, int position) {
        SportEntry entry=sportEntries.get(position);
        holder.nameTextView.setText(entry.getName());

    }

    @Override
    public int getItemCount() {
        return sportEntries.size();
    }

    public void setSports(List<SportEntry> sportEntries) {
        this.sportEntries=sportEntries;
        sportEntriesFull=new ArrayList<>(sportEntries);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return foodsFilter;
    }

    public Filter foodsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<SportEntry> filtered = new ArrayList<>();
            if(charSequence==null || charSequence.length()==0)
                filtered.addAll(sportEntriesFull);
            else{
                String searchString=charSequence.toString().toLowerCase().trim();
                for (SportEntry entry : sportEntriesFull)
                    if(entry.getName().toLowerCase().contains(searchString))
                        filtered.add(entry);
            }
            FilterResults results=new FilterResults();
            results.values=filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            sportEntries.clear();
            sportEntries.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public void setDateIndicator(int dateIndicator){
        this.dateIndicator=dateIndicator;
    }


    public interface ItemClickListener {
        void onItemClickListener(SportEntry sportEntry);
    }

    public class SportListViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageView;



        public SportListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView=itemView.findViewById(R.id.sportlistName);
            //imageView=itemView.findViewById(R.id.sportListImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("test","Clicked on sport "+nameTextView.getText().toString());
                    Intent addSport=new Intent(context,EditAndAddSportActivity.class);
                    addSport.putExtra(AppContract.MODE,AppContract.MODE_ADD_SPORT);
                    SportEntry entry=sportEntries.get(getAdapterPosition());
                    addSport.putExtra(AppContract.SPORT_OBJECT,(Serializable) entry);
                    addSport.putExtra(AppContract.DATE_INDICATOR,dateIndicator);
                    context.startActivity(addSport);

                }
            });
        }




        public TextView getNameTextView() {
            return nameTextView;
        }


        public ImageView getImageView() {
            return imageView;
        }
    }

}
