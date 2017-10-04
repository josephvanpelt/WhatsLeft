package com.jvanpelt.whatsleft;

/**
 * Created by blumojo on 10/2/17.
 */

public class ModelTrans {
    private String name;
    private boolean hasCleared;
    private float value;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public boolean getCleared() { return this.hasCleared; }
    public void setCleared(boolean has_cleared) { this.hasCleared = has_cleared; }

    public float getValue() { return this.value; }
    public void setValue(float val) { this.value = val; }
}
