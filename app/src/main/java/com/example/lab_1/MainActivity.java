package com.example.lab_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lab_1.Components.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static Context context;
    private ArrayAdapter<Component> adapter;
    private EditText textInputLayout;
    private List<Component> components;
    Button buttonAdd;
    Button buttonChoose;
    Button buttonReset;
    Button buttonDisplay;
    Button buttonDeleteSelected;
    Button buttonChange;
    Button buttonSearch;
    Button buttonSettings;
    ListView componentList;
    private boolean isSelected;
    private long id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonChoose = findViewById(R.id.buttonChoose);
        buttonReset= findViewById(R.id.buttonReset);
        buttonDisplay = findViewById(R.id.buttonDisplay);
        buttonDeleteSelected = findViewById(R.id.buttonDeleteSelected);
        buttonChange = findViewById(R.id.buttonChange);
        buttonSearch = findViewById(R.id.buttonActivity);
        buttonSettings = findViewById(R.id.buttonSettings);

        textInputLayout = findViewById(R.id.componentInput);
        componentList = findViewById(R.id.componentsListView);
        components = JSONHelper.importFromJSON(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, components);
        componentList.setAdapter(adapter);

        for (int i = 0; i < components.size(); i++)
        {
            if (components.get(i).isSelected() == true)
            {
                componentList.setItemChecked(i,true);
            }
        }

        buttonAdd.setOnClickListener(view ->
        {
            String enteredData  = textInputLayout.getText().toString();
            if (enteredData.isEmpty())
            {
                Toast.makeText(this, "Введите данные" , Toast.LENGTH_SHORT).show();
            }
            else
            {
                Long index;
                ArrayList<Long> list = new ArrayList<>();
                if(components.size()!=0)
                {
                    for(int i = 0; i < components.size(); i++)
                    {
                        index = components.get(i).getId();
                        list.add(index);
                    }
                    id = Collections.max(list) + 1;
                }
                Component newComponent = new Component(id, enteredData, false);
                components.add(newComponent);
                adapter.notifyDataSetChanged();
                JSONHelper.exportToJSON(this, components);
            }
        });

        buttonChoose.setOnClickListener(view ->
        {
            if( components.size() > 0)
            {
                for(int i = 0; i < components.size(); i++)
                {
                    isSelected = true;
                    componentList.setItemChecked(i, true);
                    Component component = components.get(i);
                    component.setSelected(true);
                    adapter.notifyDataSetChanged();
                    JSONHelper.exportToJSON(this, components);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Лист пустой" , Toast.LENGTH_SHORT).show();
            }
        });

        buttonReset.setOnClickListener(view ->
        {
            for(int i = 0; i < components.size(); i++)
            {
                componentList.setItemChecked(i, false);
                Component component = components.get(i);
                component.setSelected(false);
                adapter.notifyDataSetChanged();
                JSONHelper.exportToJSON(this, components);
            }

        });

        buttonDisplay.setOnClickListener(view ->
        {
            SparseBooleanArray checkedItems = componentList.getCheckedItemPositions();
            String str = "";
            if(checkedItems != null)
            {
                for (int i = 0; i < checkedItems.size(); i++)
                {
                    if (checkedItems.valueAt(i))
                    {
                        String item = componentList.getAdapter().getItem(checkedItems.keyAt(i)).toString();
                        str += item + ", ";
                    }
                }
                str += "были выбраны";
            }
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        });

        buttonDeleteSelected.setOnClickListener(view ->
        {
            for(int i = components.size() - 1; i >= 0; i--)
            {
                if(components.get(i).isSelected() == true)
                {
                    components.remove(i);
                    componentList.setItemChecked(i,false);
                }
            }
            adapter.notifyDataSetChanged();
            JSONHelper.exportToJSON(this, components);
        });

        buttonChange.setOnClickListener(view ->
        {
            Integer count = 0;
            Integer chosenIndex = 0;
            for (int i = components.size() - 1; i >= 0; i--)
            {
                if(components.get(i).isSelected() == true)
                {
                   count++;
                   chosenIndex = i;
                }
            }
            if (count > 1)
            {
                Toast.makeText(getApplicationContext(), "Выберите только один элемент", Toast.LENGTH_SHORT).show();
            }
            else
            {
                id = components.get(chosenIndex).getId();
                String enteredData  = textInputLayout.getText().toString();
                Component newComponent = new Component(id, enteredData, false);
                components.set(chosenIndex, newComponent);
                componentList.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            JSONHelper.exportToJSON(this, components);
        });

        componentList.setOnItemLongClickListener((parent, view, position, id) ->
        {
            new AlertDialog.Builder(MainActivity.this).
                    setIcon(android.R.drawable.ic_menu_delete).
                    setTitle("Вы уверены?").
                    setMessage("Вы хотитие удалить элемент").
                    setPositiveButton("Да", (dialogInterface, which) -> {
                        components.remove(position);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
            JSONHelper.exportToJSON(this, components);
            return true;
        });

        componentList.setOnItemClickListener((parent, view, position, id) ->
        {
            Component component = components.get(position);
            if (component.isSelected() == false)
            {
                component.setSelected(true);
            }
            else
            {
                component.setSelected(false);
            }
            JSONHelper.exportToJSON(this, components);
            adapter.notifyDataSetChanged();
        });

        buttonSearch.setOnClickListener(view ->
        {
            Intent changeActivity = new Intent(MainActivity.this, ComplexSearch.class);
            startActivity(changeActivity);
        });


        buttonSettings.setOnClickListener(v ->
        {
            Intent changeActivity = new Intent(MainActivity.this, Settings.class);
            startActivity(changeActivity);
        });
    }
    public static Context getAppContext() {
        return MainActivity.context;
    }
    /*
    @Override
    protected void onStop()
    {
        super.onStop();
        JSONHelper.exportToJSON(this, components);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        JSONHelper.exportToJSON(this, components);
    }

    @Override
    protected void onPause(){
        super.onPause();
        JSONHelper.exportToJSON(this, components);
    }*/
}