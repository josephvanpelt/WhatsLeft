package com.jvanpelt.whatsleft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by blumojo on 10/2/17.
 */

public class AdapterTrans extends BaseAdapter {
    Context context;
    ArrayList<ModelTrans> transList;
    private static LayoutInflater inflater = null;

    public AdapterTrans(Context context, ArrayList<ModelTrans> transList) {
        this.context = context;
        this.transList = transList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return transList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearAdapter(){
        transList.clear();
    }

    public void addNewValues(ArrayList<ModelTrans> mObjects){
        this.transList = mObjects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_grid_item, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvVal = (TextView) convertView.findViewById(R.id.tv_value);
        CheckBox tvClear = (CheckBox) convertView.findViewById(R.id.tv_hascleared);

        ModelTrans e = new ModelTrans();
        e = transList.get(position);
        tvName.setText(String.valueOf(e.getName()));
        tvVal.setText(String.valueOf(e.getValue()));

        boolean checked = e.getCleared();
        tvClear.setChecked(checked);

        return convertView;
    }
}
