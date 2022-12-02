package com.example.lab_1;

import android.content.Context;

import com.example.lab_1.Components.Component;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JSON
{
    private static final String FILE_NAME = "data.json";
    private static List<Component> components = new ArrayList<>();
    public static void exportToJSON(Context context, List<Component> dataList)
    {
        Thread thread = new Thread(() ->
        {
            Gson gson = new Gson();
            DataItems dataItems = new DataItems();
            dataItems.setComponents(dataList);
            String jsonString = gson.toJson(dataItems);

            try (FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE))
            {
                fileOutputStream.write(jsonString.getBytes());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        thread.start();
        while(thread.isAlive()){}
    }

    public static List<Component> importFromJSON(Context context)
    {
        Thread thread = new Thread(() ->
        {
            try(FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
                InputStreamReader streamReader = new InputStreamReader(fileInputStream))
            {
                Gson gson = new Gson();
                DataItems dataItems = gson.fromJson(streamReader, DataItems.class);
                components = dataItems.getComponents();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        });
        thread.start();
        while(thread.isAlive()){}
        return components;
    }


    private static class DataItems
    {
        private List<Component> components;

        List<Component> getComponents()
        {
            return components;
        }
        void setComponents(List<Component> components)
        {
            this.components = components;
        }
    }
}