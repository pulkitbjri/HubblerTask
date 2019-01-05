package com.hubbler.pulkitnigam.hubbler.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.hubbler.pulkitnigam.hubbler.ListAdapter;
import com.hubbler.pulkitnigam.hubbler.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {


    public static ArrayList<JSONObject > list=new ArrayList<>();

    ListAdapter adapter;

    RecyclerView recyclerView;
    TextView total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView=findViewById(R.id.recycler);
        total=findViewById(R.id.total);

        adapter=new ListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        total.setText("Total Reports : "+list.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:

                startActivity(new Intent(this,AddListDataActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
