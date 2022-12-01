package com.example.lab_1;

import android.content.Context;

import com.example.lab_1.Components.Component;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class JSONHelper {
    private static final String FILE_NAME = "data.json";

    static boolean exportToJSON(Context context, List<Component> dataList)
    {
        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setComponents(dataList);
        String jsonString = gson.toJson(dataItems);

        try(FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE))
        {
            fileOutputStream.write(jsonString.getBytes());
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    static List<Component> importFromJSON(Context context)
    {
        try(FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
            InputStreamReader streamReader = new InputStreamReader(fileInputStream))
        {
            Gson gson = new Gson();
            DataItems dataItems = gson.fromJson(streamReader, DataItems.class);
            return  dataItems.getComponents();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return null;
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