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

public class ViewOthersRecipeAdapter extends RecyclerView.Adapter<ViewOthersViewHolder> {

    final Context context;
    private List<DataClass> dataList;

    public ViewOthersRecipeAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewOthersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_recipe_item, parent, false);
        return new ViewOthersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewOthersViewHolder holder, int position) {
        DataClass data = dataList.get(position);

        // Load image using Glide
        Glide.with(context)
                .load(data.getDataImage())
                .placeholder(R.drawable.r_2) // Optional: add a placeholder image
                .into(holder.recImage);

        // Set text views
        holder.recTitle.setText(data.getDataName());
        holder.recTime.setText(data.getDataTime());

        // Set click listener
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OthersRecipeDetailActivity.class);
                intent.putExtra("Image", data.getDataImage());
                intent.putExtra("Name", data.getDataName());
                intent.putExtra("Time", data.getDataTime());
                intent.putExtra("Category", data.getDataCategory());
                intent.putExtra("Ingredients", data.getDataIngredients());
                intent.putExtra("Description", data.getDataDescription());
                intent.putExtra("Key", data.getKey());
                intent.putExtra("Owner", data.getOwner());
                intent.putExtra("Video", data.getDataVideo());

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

class ViewOthersViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recTime;
    CardView recCard;

    public ViewOthersViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.otherRecImage);
        recTitle = itemView.findViewById(R.id.otherRecTitle);
        recTime = itemView.findViewById(R.id.otherRecTime);
        recCard = itemView.findViewById(R.id.otherRecCard);
    }
}

