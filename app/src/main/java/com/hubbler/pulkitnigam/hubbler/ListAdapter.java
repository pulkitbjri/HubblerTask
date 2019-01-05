package com.hubbler.pulkitnigam.hubbler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Iterator;

import static android.content.ContentValues.TAG;
import static com.hubbler.pulkitnigam.hubbler.Activity.ListActivity.list;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Iterator<String> iter = list.get(i).keys();
        int keyValue=0;
        try {
            while (iter.hasNext()) {
                String key = iter.next();
                String value = list.get(i).getString(key);
                Log.i(TAG, "onBindViewHolder: "+keyValue+key+" "+value);

                if (keyValue==0 )
                    myViewHolder.name.setText(key+" : "+value);
                if (keyValue==1 && !value.equalsIgnoreCase(""))
                    myViewHolder.age.setText(key+": "+value);

                if (!value.equalsIgnoreCase(""))
                    keyValue=keyValue+1;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,age;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            age=itemView.findViewById(R.id.age);
        }
    }
}
