package com.example.lab_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.lab_1.Components.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private ArrayAdapter<Component> adapter;
    private EditText textInputLayout;
    private List<Component> components;
    private ListView componentList;
    private long id = 0;
    private DB db;
    private boolean typeStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle arguments = getIntent().getExtras();
        typeStorage = (Boolean) arguments.get("typeStorage");

        Button buttonAdd = findViewById(R.id.buttonAdd);
        Button buttonChoose = findViewById(R.id.buttonChoose);
        Button buttonReset = findViewById(R.id.buttonReset);
        Button buttonDisplay = findViewById(R.id.buttonDisplay);
        Button buttonDeleteSelected = findViewById(R.id.buttonDeleteSelected);
        Button buttonChange = findViewById(R.id.buttonChange);
        Button buttonSearch = findViewById(R.id.buttonActivity);
        Button buttonSettings = findViewById(R.id.buttonSettings);
        textInputLayout = findViewById(R.id.componentInput);
        componentList = findViewById(R.id.componentsListView);

        if (typeStorage)
        {
            db = new DB(this);
            db.open();
            components = db.getComponents();
        }
        else
        {
            components = JSON.importFromJSON(this);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, components);
        componentList.setAdapter(adapter);

        for (int i = 0; i < components.size(); i++)
        {
            if (components.get(i).isSelected())
            {
                componentList.setItemChecked (i,true);
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
                long index;
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

                if(typeStorage)
                {
                    db.addComponent (newComponent);
                    updateWidget();
                }
                else
                {
                    JSON.exportToJSON (this, components);
                }
            }

        });

        buttonChoose.setOnClickListener(view ->
        {
            if( components.size() > 0)
            {
                for(int i = 0; i < components.size(); i++)
                {
                    componentList.setItemChecked(i, true);
                    Component component = components.get(i);
                    component.setSelected(true);
                    adapter.notifyDataSetChanged();
                    if (typeStorage)
                    {
                        db.updateComponent(component);
                    }
                    else
                    {
                        JSON.exportToJSON(this, components);
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Лист пустой" , Toast.LENGTH_SHORT).show();
            }
        });

        buttonReset.setOnClickListener(view ->
        {
            for (int i = 0; i < components.size(); i++)
            {
                componentList.setItemChecked(i, false);
                Component component = components.get(i);
                component.setSelected(false);
                adapter.notifyDataSetChanged();
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
            for (int i = componentList.getCount() -1; i >= 0; i--)
            {
                if (components.get(i).isSelected())
                {
                    if (typeStorage)
                    {
                        db.deleteComponent(components.get(i).getId());
                        updateWidget();
                    }
                    components.remove(i);
                    componentList.setItemChecked(i,false);

                }
            }
            adapter.notifyDataSetChanged();
            if(!typeStorage)
            {
                JSON.exportToJSON(this, components);
            }
        });

        buttonChange.setOnClickListener(view ->
        {
            int count = 0;
            int chosenIndex = 0;
            for (int i = components.size() - 1; i >= 0; i--)
            {
                if(components.get(i).isSelected())
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
            if(!typeStorage)
            {
                JSON.exportToJSON(this, components);
            }
        });

        componentList.setOnItemLongClickListener((parent, view, position, id) ->
        {
            new AlertDialog.Builder(MainActivity.this).
                    setIcon(android.R.drawable.ic_menu_delete).
                    setTitle("Вы уверены?").
                    setMessage("Вы хотитие удалить элемент").
                    setPositiveButton("Да", (dialogInterface, which) ->
                    {
                        components.remove(position);
                        if(typeStorage)
                        {
                            db.deleteComponent(components.get(position).getId());

                        }
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Нет", null)
                    .show();

            if(!typeStorage)
            {
                JSON.exportToJSON(this, components);
            }
            return true;
        });

        componentList.setOnItemClickListener((parent, view, position, id) ->
        {
            Component component = components.get(position);
            component.setSelected(!component.isSelected());
            if(typeStorage)
            {
                db.updateComponent(component);
            }
            if(!typeStorage)
            {
                JSON.exportToJSON(this, components);
            }
            adapter.notifyDataSetChanged();
        });

        buttonSearch.setOnClickListener(view ->
        {
            Intent changeActivity = new Intent(MainActivity.this, ComplexSearch.class);
            changeActivity.putExtra("typeStorage", typeStorage);
            startActivity(changeActivity);
        });

        buttonSettings.setOnClickListener(view ->
        {
            Intent changeActivity = new Intent(MainActivity.this, Settings.class);
            startActivity(changeActivity);
        });

    }


    @Override
    protected void onPause(){
        super.onPause();
        if (typeStorage)
        {
            for (Component component: components)
            {
                db.updateComponent(component);
            }

        }
        else
        {
            JSON.exportToJSON(this, components);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (typeStorage)
        {
            for (Component component: components)
            {
                db.updateComponent(component);
            }
            db.close();
        }
        else
        {
            JSON.exportToJSON(this, components);
        }
    }

    @Override
    public void onStop() {

        super.onStop();

        if (typeStorage)
        {
            for (Component component: components)
            {
                db.updateComponent(component);
            }
        }
        else
        {
            JSON.exportToJSON(this, components);
        }
    }
    private void updateWidget()
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.new_app_widget);
        ComponentName thisWidget = new ComponentName(this, CountWidget.class);
        remoteViews.setTextViewText(R.id.appwidget_text, "Записей в БД:" + db.getCount());
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
}