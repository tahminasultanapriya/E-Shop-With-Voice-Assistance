package com.example.priya.finalprojectsdp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.priya.finalprojectsdp.Common.Common;
import com.example.priya.finalprojectsdp.Common.Food;
import com.example.priya.finalprojectsdp.Interface.ItemClickListener;
import com.example.priya.finalprojectsdp.ViewHolder.FoodViewHolder;
import com.example.priya.finalprojectsdp.ViewHolder.MainViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String blogId = "";
    LinearLayoutManager manager = new LinearLayoutManager(this);

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // recyclerView = findViewById(R.layout.a)
        setContentView(R.layout.activity_food_list);
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Products");
        //textFullname =(TextView)headerView.findViewById(R.id.textFullName);
        //textFullname.setText(Common.current_user.getName());

        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
      //  recyclerView.setLayoutManager(manager);
      //  recyclerView.setHasFixedSize(true);

        if(getIntent()!=null)
        {
            blogId = getIntent().getStringExtra("BlogId");

        }
        if(blogId != null && !blogId.isEmpty())
        {
            loadListFood(blogId);
        }

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");

        loadSuggest();

        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });




    }

    private void startSearch(CharSequence text) {

         searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                 Food.class,
                 R.layout.food_list,
                 FoodViewHolder.class,
                 foodList.orderByChild("Name").equalTo(text.toString())
         ) {
             @Override
             protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                 viewHolder.food_name.setText(model.getName());

                 Picasso.with(getBaseContext()).load(model.getImage())
                         .into(viewHolder.food_image);
                 final Food local = model;
                 viewHolder.setItemClickListener(new ItemClickListener() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                         Intent productDetail = new Intent(FoodList.this,ProductDetail.class);
                         productDetail.putExtra("ProductId",adapter.getRef(position).getKey());
                         startActivity(productDetail);
                         //Toast.makeText(FoodList.this,""+local.getName(),Toast.LENGTH_SHORT).show();
                     }
                 });


             }

         };
         recyclerView.setAdapter(searchAdapter);
         }


    private void loadSuggest() {
        foodList.orderByChild("MenuId").equalTo(blogId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String blogId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_list,
                FoodViewHolder.class,
                foodList.orderByChild("MenuId").equalTo(blogId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {

                viewHolder.food_name.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent productDetail = new Intent(FoodList.this,ProductDetail.class);
                        productDetail.putExtra("ProductId",adapter.getRef(position).getKey());
                        startActivity(productDetail);
                        //Toast.makeText(FoodList.this,""+local.getName(),Toast.LENGTH_SHORT).show();
                    }
                });




            }


        };

        Log.d("TAG",""+adapter.getItemCount());

        recyclerView.setAdapter(adapter);

    }
}
