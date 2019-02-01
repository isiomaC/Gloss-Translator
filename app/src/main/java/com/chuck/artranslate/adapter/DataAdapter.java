package com.chuck.artranslate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.chuck.artranslate.R;
import java.util.ArrayList;
import java.util.List;

public abstract class DataAdapter<Item, Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {

    private ArrayList<Item> mDataSet = new ArrayList<>(),
            mDataSetCopy = new ArrayList<>();

    protected int mLayoutRes = R.layout.item_list;

    public final void notifyMyDataSetChanged() {
        mDataSetCopy = new ArrayList<>(mDataSet);
        notifyDataSetChanged();
    }

    public final void filter(String filter) {

        filter = filter.toLowerCase();
        if (filter.isEmpty()) {
            mDataSet = new ArrayList<>(mDataSetCopy);
        } else {
            mDataSet = new ArrayList<>();
            onFilter(filter);
        }
        notifyDataSetChanged();

    }

    protected void onFilter(String filter) { }

    public final void add(Item item) {
        mDataSet.add(item);
        notifyMyDataSetChanged();
    }

    public final void add(Item item, int position) {
        mDataSet.add(position, item);
        notifyMyDataSetChanged();
    }

    final void addForFilter(Item item) {
        mDataSet.add(item);
    }

    public final void addAll(List<Item> itemList) {
        mDataSet.addAll(itemList);
        notifyMyDataSetChanged();
    }

    public final void replaceAll(List<Item> itemList) {
        mDataSet.clear();
        addAll(itemList);
        notifyMyDataSetChanged();
    }

    public final void replace(int position, Item item) {
        mDataSet.set(position, item);
        notifyMyDataSetChanged();
    }

    public final void remove(int item) {
        mDataSet.remove(item);
        notifyMyDataSetChanged();
    }

    public final void removeAll(int[] items){
        for (int i : items){
            mDataSet.remove(i);
        }
        notifyMyDataSetChanged();
    }

    public final Item getItem(int position) {
        return mDataSet.get(position);
    }

    final Item getItemCopy(int position) {
        return mDataSetCopy.get(position);
    }

    public abstract void onItemClick(View view, Item data, int position);

    protected abstract void displayContent(Holder holder, Item data, int position);

    protected final View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(mLayoutRes, parent,false);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        displayContent(holder, getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public int getItemCopyCount() {
        return mDataSetCopy.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
