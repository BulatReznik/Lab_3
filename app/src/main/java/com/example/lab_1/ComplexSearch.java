package com.example.lab_1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.lab_1.Components.Component;

import java.util.List;

public class ComplexSearch extends AppCompatActivity {
    private SearchView searchView;
    private List<Component> components;
    private ArrayAdapter<Component> adapter;
    private ListView componentList;
    private Button buttonBack;
    private DB db;
    private boolean typeStorage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex_search);
        buttonBack = findViewById(R.id.buttonBack);
        componentList = findViewById(R.id.secondList);
        searchView = findViewById(R.id.searchComponent);
        Bundle arguments = getIntent().getExtras();
        typeStorage = (Boolean) arguments.get("typeStorage");
        if(typeStorage==true)
        {
            db = new DB(this);
            db.open();
            components = db.getComponents();
        }
        else
        {
            components = JSONHelper.importFromJSON(this);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, components);
        componentList.setAdapter(adapter);

        for (int i = 0; i < components.size(); i++)
        {
            if (components.get(i).isSelected() == true)
            {
                componentList.setItemChecked(i,true);
            }
        }
        componentList.setOnItemClickListener((parent, view, position, id) ->
        {
            Component component = components.get(position);
            if(component.isSelected() == false)
            {
                component.setSelected(true);
            }
            else
            {
                component.setSelected(false);
            }
            if(typeStorage)
                db.updateComponent(component);
            if(typeStorage==false)
            {
                JSONHelper.exportToJSON (this, components);
            }
            adapter.notifyDataSetChanged();
        });


        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit (String query)
            {
                ComplexSearch.this.adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange (String newText)
            {
                ComplexSearch.this.adapter.getFilter().filter(newText);
                return false;
            }
        });
        buttonBack.setOnClickListener(view ->
        {
            Intent changeActivity = new Intent(ComplexSearch.this, MainActivity.class);
            startActivity(changeActivity);
        });

    }
    @Override
    protected void onPause(){
        super.onPause();
        if (typeStorage==true)
        {
            for (Component component: components)
            {
                db.updateComponent(component);
            }
            db.close();
        }
        else
        {
            JSONHelper.exportToJSON(this, components);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (typeStorage==true)
        {
            for (Component component: components)
            {
                db.updateComponent(component);
            }
            db.close();
        }
        else
        {
            JSONHelper.exportToJSON(this, components);
        }
    }

    @Override
    public void onStop() {

        super.onStop();

        if (typeStorage==true)
        {
            for (Component component: components)
            {
                db.updateComponent(component);
            }
            db.close();
        }
        else
        {
            JSONHelper.exportToJSON(this, components);
        }
    }

}