package com.example.lab_1.Components;

public class Component
{
    private final long id;
    private String name;
    private boolean isSelected;

    public Component(long id, String name, boolean isSelected)
    {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public  String toString()
    {
        return "Id: " + id + " Имя: " + name + "  " + isSelected;
    }

    public long getId() {
        return id;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
