package com.deepshooter.searchapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Avinashmishra on 02-05-2017.
 */

public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.MyViewHolder> {

     Context context;
     ArrayList<MainBean> mainBeen=new ArrayList<>() ;
     ArrayList<MainBean> tempList  ;

     TextView textView ;

    public RecyclerViewListAdapter(Context context , ArrayList<MainBean> mainBeen , TextView textView ) {
        this.context = context;
        this.tempList=mainBeen;
        this.textView =textView;

        textView.setVisibility(View.VISIBLE);
        textView.setText("No Data Found");

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,  final int position) {

         holder.mNumberText.setText(mainBeen.get(position).getId()+"");
         holder.mText.setText(mainBeen.get(position).getName());





    }

    @Override
    public int getItemCount() {

        return mainBeen.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mNumberText,mText;
        LinearLayout mLinearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            mNumberText = (TextView) itemView.findViewById(R.id.vT_number);
            mText = (TextView) itemView.findViewById(R.id.vT_Text);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);

        }
    }


    public void filter(String chars) {
        chars = chars.toLowerCase().trim();
        int id = 0;
        mainBeen.clear();
        if (chars == null || chars.length() == 0) {
            mainBeen.clear();
            textView.setVisibility(View.VISIBLE);
        } else {

            for (int i = 0; i < tempList.size(); i++) {

                String temp = tempList.get(i).getName().toLowerCase();

                if (temp.startsWith(chars, 0)) {

                     id++;
                    MainBean list = new MainBean();
                    list.setId(id);
                    list.setName(tempList.get(i).getName());

                    mainBeen.add(list);

                }

            }
            if(mainBeen.size()>0)
            {
                textView.setVisibility(View.GONE);
            }else {

                textView.setVisibility(View.VISIBLE);
            }


        }
        notifyDataSetChanged();
    }
}
