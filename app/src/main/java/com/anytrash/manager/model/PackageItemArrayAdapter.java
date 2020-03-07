package com.anytrash.manager.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import com.anytrash.manager.PackageInfoActivity;
import com.anytrash.manager.R;

public class PackageItemArrayAdapter extends RecyclerView.Adapter<PackageItemArrayAdapter.ViewHolder> {

    private int listItemLayout;
    private ArrayList<PackageItem> packageList;
    private ArrayList<Boolean> checkedList;
    private AppCompatActivity appCompatActivity;
    public static ArrayList<Drawable> packageIconList;

    public PackageItemArrayAdapter(int listItemLayout, ArrayList<PackageItem> packageList, ArrayList<Boolean> checkedList, ArrayList<Drawable> packageIconList, AppCompatActivity appCompatActivity) {
        this.listItemLayout = listItemLayout;
        this.packageList = packageList;
        this.checkedList = checkedList;
        this.appCompatActivity = appCompatActivity;
        PackageItemArrayAdapter.packageIconList = packageIconList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.packageName.setText(packageList.get(position).getPackageName());
        String packageTitle = position + 1 + ". " + packageList.get(position).getPackageTitle();
        holder.packageTitle.setText(packageTitle);
        holder.packageSourcePath.setText(packageList.get(position).getPackageSourcePath());
        holder.packageIcon.setImageDrawable(packageIconList.get(position));
        if (checkedList.get(position))
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);
    }

    public ArrayList<Integer> getCheckedItems() {
        ArrayList<Integer> checkedPositionList = new ArrayList<>();
        for (int i = 0; i < checkedList.size(); i++) {
            if (checkedList.get(i)) {
                checkedPositionList.add(i);
            }
        }
        return checkedPositionList;
    }

    private void callClearData(int position) {
        try {
            Commands.clearPackageData(packageList.get(position).getPackageName());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void callDeletePackage(int position) {
        try {
            Commands.deletePackage(packageList.get(position).getPackageSourcePath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        notifyItemRemoved(position);
        for (int i = position; i < packageList.size(); i++) {
            notifyItemChanged(i);
        }
    }

    private void callPackageInfoActivity(int position) {
        Intent intent = new Intent(appCompatActivity.getApplicationContext(), PackageInfoActivity.class);
        intent.putExtra(PackageItem.class.getSimpleName(), packageList.get(position));
        intent.putExtra("position", position);
        appCompatActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (packageList == null) {
            return 0;
        } else {
            return packageList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView packageName;
        TextView packageTitle;
        TextView packageSourcePath;
        ImageView packageIcon;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.package_name);
            packageTitle = itemView.findViewById(R.id.package_title);
            packageSourcePath = itemView.findViewById(R.id.package_source_path);
            packageIcon = itemView.findViewById(R.id.package_icon);
            checkBox = itemView.findViewById(R.id.checkbox);

            packageIcon.setOnClickListener(this);
            checkBox.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
                    final int position = getAdapterPosition();
                    menu.setHeaderTitle(position + 1 + ". " + packageList.get(position).getPackageTitle());
                    menu.add(R.string.clear_data).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            callClearData(position);
                            return true;
                        }
                    });
                    menu.add(R.string.delete_package).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            callDeletePackage(position);
                            return true;
                        }
                    });
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkbox:
                    checkedList.set(getAdapterPosition(), !checkedList.get(getAdapterPosition()));
                    break;
                case R.id.package_icon:
                    callPackageInfoActivity(getAdapterPosition());
                    break;
            }
        }
    }
}
