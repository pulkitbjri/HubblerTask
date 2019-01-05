package com.hubbler.pulkitnigam.hubbler.Activity;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hubbler.pulkitnigam.hubbler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hubbler.pulkitnigam.hubbler.Activity.ListActivity.list;

public class AddListDataActivity extends AppCompatActivity {

    ArrayList<View>  viewArrayList=new ArrayList();
    ArrayList<JSONObject>  objectArray=new ArrayList();

    LinearLayout layout;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_data);

        layout=findViewById(R.id.linear);
        add=findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeAndSaveData();
            }
        });

        getFields();


    }

    private void computeAndSaveData() {
        JSONObject userData=new JSONObject();
        try {

            for (int i = 0; i < viewArrayList.size(); i++) {
            if (viewArrayList.get(i) instanceof TextView)
            {
                TextView textView=(TextView) viewArrayList.get(i);
                if (objectArray.get(i).has("required") && objectArray.get(i).getBoolean("required")
                        && textView.getText().length()==0){
                    textView.setError("This Field Is Mandatory...");

                    return ;
                }
                else
                {
                    if (textView.getText().length()!=0 && objectArray.get(i).has("min") && !(
                            objectArray.get(i).getInt("min") < Integer.parseInt(textView.getText().toString()) &&
                                    objectArray.get(i).getInt("max") > Integer.parseInt(textView.getText().toString())) )
                    {
                        textView.setError(objectArray.get(i).getString("field-name")+ " should be between "
                                +objectArray.get(i).getInt("min")+" and "
                                +objectArray.get(i).getInt("max"));
                        return ;

                    }
                }


                userData.put(objectArray.get(i).getString("field-name"),textView.getText().toString());


            }
            else if(viewArrayList.get(i) instanceof Spinner)
            {
                Spinner spinner=(Spinner) viewArrayList.get(i);
                userData.put(objectArray.get(i).getString("field-name"),spinner.getSelectedItem().toString());

            }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("", "computeAndSaveData: "+userData.toString());

        list.add(userData);
        onBackPressed();

    }

    public View getViewByType(String s){
        if (s.equalsIgnoreCase("text") || s.equalsIgnoreCase("number") || s.equalsIgnoreCase("multiline"))
            return new EditText(this);
        else if (s.equalsIgnoreCase("dropdown"))
            return new Spinner(this);
        else
            return null;
    }


    private void generateAndAddView(String type) throws JSONException {

        JSONObject object=new JSONObject(type);

        View view=getViewByType(object.getString("type"));

        setViewConstraints(view,object);

    }

    private void setViewConstraints(View view, JSONObject object) throws JSONException {

        LinearLayout.LayoutParams layoutParams=new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30,50,30,10);
        view.setLayoutParams(layoutParams);
        view.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.rect));


        TextView textView=new TextView(this);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(layoutParams);
        textView.setText(object.getString("field-name").toUpperCase()+" :");
        layout.addView(textView);

        if (view instanceof Spinner)
        {
            JSONArray array=object.getJSONArray("options");
            List<String> spinnerList = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
               spinnerList.add(array.getString(i));
            }
            Spinner spinner = (Spinner) view;
            ArrayAdapter<String> adapter = new
                    ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);


            layout.addView(spinner);
            viewArrayList.add(spinner);
            objectArray.add(object);
        }
        else if (view instanceof EditText)
        {
            EditText editText=(EditText) view;
            if (object.getString("type").equalsIgnoreCase("text"))
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            if (object.getString("type").equalsIgnoreCase("number"))
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (object.getString("type").equalsIgnoreCase("multiline"))
            {
                editText.setLines(5);
            }
            editText.setTextColor(Color.BLACK);
            editText.setGravity(Gravity.TOP);
            layout.addView(editText);
            viewArrayList.add(editText);

            objectArray.add(object);


        }
    }

    private void getFields() {
        try {
            JSONArray arr = new JSONArray(getJson());
            for (int i = 0; i < arr.length(); i++) {
                generateAndAddView(arr.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String  getJson() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.fields);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
