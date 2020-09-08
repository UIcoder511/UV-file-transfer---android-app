package com.example.umang.witransfer;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder>
{

    ArrayList<ViewHandler> data;



    Context mContext;
    CustomItemClickListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(mView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getAdapterPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        ViewHandler mdata = data.get(position);
        holder.itemTitle.setText(mdata.getFname());
        holder.thumbnailImage.setImageResource(mdata.getPicid());


        DecimalFormat dec = new DecimalFormat("0.00");

        if(mdata.getFsize()==-1)
        {
            holder.size.setText(" ");
        }
        else
        {
            double fsize=(mdata.getFsize())/(1024.0 * 1024.0 * 1024.0);
            if(fsize>1)
            {
                holder.size.setText(new StringBuilder().append(dec.format(fsize).concat(" GB")));
            }
            else
            {
                fsize=(mdata.getFsize())/(1024.0 * 1024.0);
                if(fsize>1)
                {
                    holder.size.setText(new StringBuilder().append(dec.format(fsize).concat(" MB")));
                }
                else
                {
                    fsize=(mdata.getFsize())/(1024.0);
                    if(fsize>1)
                    {
                        holder.size.setText(new StringBuilder().append(dec.format(fsize).concat(" KB")));
                    }
                    else
                    {
                        holder.size.setText(new StringBuilder().append(dec.format(fsize*1024.0).concat(" B")));
                    }
                }

            }

        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public ItemsListAdapter(Context mContext, ArrayList<ViewHandler> data, CustomItemClickListener listener) {
        this.data = data;
        this.mContext = mContext;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public ImageView thumbnailImage;
        public TextView size;

        ViewHolder(View v) {
            super(v);
            itemTitle = (TextView) v.findViewById(R.id.fname);
            thumbnailImage = (ImageView) v.findViewById(R.id.fpic);
            size=(TextView)v.findViewById(R.id.Fsize);
        }
    }
}

