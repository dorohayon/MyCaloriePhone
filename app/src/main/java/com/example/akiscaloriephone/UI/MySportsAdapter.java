package com.example.akiscaloriephone.UI;

import android.content.Context;
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

import com.example.akiscaloriephone.AppExecutors;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.SportDiaryEntry;
import com.example.akiscaloriephone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MySportsAdapter extends RecyclerView.Adapter<MySportsAdapter.MySportsViewHolder> {

    private List<SportDiaryEntry> entries;
    private Menu itemLongClickListener;
    private Context context;

    public MySportsAdapter( Context context) {
        this.context = context;
        entries=new ArrayList<>();
    }

    public void setMySport(List<SportDiaryEntry> mySportEntries) {
        int size = this.entries.size();
        this.entries.clear();
        notifyItemRangeRemoved(0, size);
        this.entries = mySportEntries;
        notifyDataSetChanged();
    }

    public void updateData(List<SportDiaryEntry> mySportEntries) {
        this.entries.clear();
        this.entries.addAll(mySportEntries);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MySportsAdapter.MySportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_my_sport, parent, false);
        return new MySportsViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MySportsAdapter.MySportsViewHolder holder, int position) {
        SportDiaryEntry entry=entries.get(position);
        holder.nameTextView.setText(entry.getName());
        holder.caloriesTextView.setText(String.valueOf(entry.getCaloriesBurned())+" "+context.getResources().getString(R.string.calories_burned));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        holder.hourTextView.setText(formatter.format(entry.getDate()));
        holder.intensityAndTimeTextView.setText(entry.getTime()+" "+context.getResources().getString(R.string.minutes)+", "+entry.getIntensity());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }



    public class MySportsViewHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener  {
        TextView nameTextView;
        TextView caloriesTextView;
        TextView intensityAndTimeTextView;
        TextView hourTextView;

        public MySportsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.mySportName);
            caloriesTextView = itemView.findViewById(R.id.mySportCalories);
            intensityAndTimeTextView = itemView.findViewById(R.id.mySportIntensityAndTime);
            hourTextView=itemView.findViewById(R.id.mySportHour);
            itemView.setOnCreateContextMenuListener(this);
        }




        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle(context.getResources().getString(R.string.selectaction));
            MenuItem delete = menu.add(Menu.NONE,2,2,context.getResources().getString(R.string.delete));
            delete.setOnMenuItemClickListener(onChange);
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case 2:
                        deleteSportItem();
                        Toast.makeText(context,context.getResources().getString(R.string.sportdeleted),Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        };



        private void deleteSportItem() {
            final AppDatabase db=AppDatabase.getInstance(context);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    db.mySportDao().delete(entries.get(getAdapterPosition()));
                }
            });
        }

    }

}
