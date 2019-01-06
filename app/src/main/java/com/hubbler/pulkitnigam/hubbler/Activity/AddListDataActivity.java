package com.hubbler.pulkitnigam.hubbler.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
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
import android.widget.FrameLayout;
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
import java.util.Stack;

import static com.hubbler.pulkitnigam.hubbler.Activity.ListActivity.list;

public class AddListDataActivity extends AppCompatActivity {

    ArrayList<View>  viewArrayList=new ArrayList();
    ArrayList<JSONObject>  objectArray=new ArrayList();

    LinearLayout layout;
    FloatingActionButton add;

    ArrayList<TextView> compositeArrayList=new ArrayList<>();
    HashMap<Integer,JSONObject> compositeJSONmap= new HashMap<Integer, JSONObject>();

    int pos;


    boolean parent=true;
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

        if (getIntent().hasExtra("data"))
        {
            parent=false;
            pos=getIntent().getIntExtra("pos",0);

            Log.i("sssssss", "onCreate: "+pos);
            try {

                JSONObject object=new JSONObject(getIntent().getStringExtra("data")) ;
                setTitle(object.getString("field-name"));
                JSONArray array=object.getJSONArray("fields");
                for (int i = 0; i < array.length(); i++) {
                    generateAndAddView(array.get(i).toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            setTitle("Add Report");
            getFields();

        }

    }

    private void computeAndSaveData() {
        StringBuilder builder=new StringBuilder("");
        JSONObject userData=new JSONObject();
        try {

            for (int i = 0; i < viewArrayList.size(); i++) {
                if (viewArrayList.get(i) instanceof EditText)
                {
                    EditText textView=(EditText) viewArrayList.get(i);
                    if (objectArray.get(i).has("required") && objectArray.get(i).getBoolean("required")
                            && textView.getText().length()==0){
                        textView.setError("This Field Is Mandatory...");

                        return ;
                    }

                    if (textView.getText().length()!=0 && objectArray.get(i).has("min") && !(
                                objectArray.get(i).getInt("min") < Integer.parseInt(textView.getText().toString()) &&
                                        objectArray.get(i).getInt("max") > Integer.parseInt(textView.getText().toString())) )
                    {
                        textView.setError(objectArray.get(i).getString("field-name")+ " should be between "
                                +objectArray.get(i).getInt("min")+" and "
                                +objectArray.get(i).getInt("max"));
                        return ;
                    }


                    if (textView.getText().length()!=0)
                        builder.append(textView.getText().toString()+",");

                    userData.put(objectArray.get(i).getString("field-name"),textView.getText().toString());
                }
                else if(viewArrayList.get(i) instanceof Spinner)
                {
                    Spinner spinner=(Spinner) viewArrayList.get(i);
                    userData.put(objectArray.get(i).getString("field-name"),spinner.getSelectedItem().toString());
                    builder.append(spinner.getSelectedItem().toString()+",");

                }
                else if (viewArrayList.get(i) instanceof TextView)
                {
                    TextView textView=(TextView) viewArrayList.get(i);
                    if (objectArray.get(i).has("required") && objectArray.get(i).getBoolean("required")
                            && textView.getText().length()==0){
                        textView.setError("This Field Is Mandatory...");

                        return ;
                    }

                    if (compositeJSONmap.containsKey(viewArrayList.get(i).getTag()))
                      userData.put(objectArray.get(i).getString("field-name"),compositeJSONmap.get(viewArrayList.get(i).getTag()));
                    else
                        userData.put(objectArray.get(i).getString("field-name"),"");

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (parent)
        {
            Log.i("", "computeAndSaveData: "+userData.toString());

            list.add(userData);
            onBackPressed();
        }
        else {
            try {
                userData.put("complete",builder.toString().substring(0,builder.toString().length()-1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",userData.toString());
            returnIntent.putExtra("pos",pos);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }

    }


    public View getViewByType(String s){
        if (s.equalsIgnoreCase("text") || s.equalsIgnoreCase("number") || s.equalsIgnoreCase("multiline"))
            return new EditText(this);
        else if (s.equalsIgnoreCase("dropdown"))
            return new Spinner(this);
        else if (s.equalsIgnoreCase("composite"))
            return new TextView(this);
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

        if (view instanceof Spinner) //dropdown
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
        else if (view instanceof EditText) //field
        {
            EditText editText=(EditText) view;
            editText.setHint(object.getString("field-name"));
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
        else if (view instanceof TextView)  //composite
        {

            TextView editText=(TextView) view;
            editText.setHint(object.getString("field-name"));
            editText.setTextSize(18f);
            editText.setTextColor(Color.LTGRAY);

            editText.setTag(compositeArrayList.size());
            compositeArrayList.add(editText);
            layout.addView(editText);
            viewArrayList.add(editText);
            setclickListener(editText,object,compositeArrayList.size()-1);
            objectArray.add(object);

        }
    }

    private void setclickListener(TextView textView, final JSONObject object, final int size) {

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddListDataActivity.this,AddListDataActivity.class)
                                .putExtra("data",object.toString())
                                .putExtra("pos",size)
                        ,compositeArrayList.size());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == Activity.RESULT_OK)
            {
                try {

                    String result=data.getStringExtra("result");
                    int pos=data.getIntExtra("pos",0);
                    Log.i("", "onActivityResult: "+result+"     "+pos);
                    JSONObject object=new JSONObject(result);
                    compositeJSONmap.put(pos,object);
                    compositeArrayList.get(pos).setError(null);
                    compositeArrayList.get(pos).setText(object.getString("complete"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
