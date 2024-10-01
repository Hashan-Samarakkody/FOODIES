package com.example.foodies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MyRecipeAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyRecipeAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recipe_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataName());
        holder.recTime.setText(dataList.get(position).getDataTime());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Name", dataList.get(holder.getAdapterPosition()).getDataName());
                intent.putExtra("Time", dataList.get(holder.getAdapterPosition()).getDataTime());
                intent.putExtra("Category", dataList.get(holder.getAdapterPosition()).getDataCategory());
                intent.putExtra("Ingredients", dataList.get(holder.getAdapterPosition()).getDataIngredients());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDescription());
                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Owner", dataList.get(holder.getAdapterPosition()).getOwner());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recTime;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.rImage);
        recTitle = itemView.findViewById(R.id.rTitle);
        recTime = itemView.findViewById(R.id.rTime);
        recCard = itemView.findViewById(R.id.rCard);
    }
}
