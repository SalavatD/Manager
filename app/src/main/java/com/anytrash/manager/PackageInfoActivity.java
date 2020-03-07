package com.anytrash.manager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.anytrash.manager.model.PackageItem;
import com.anytrash.manager.model.PackageItemArrayAdapter;

public class PackageInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_info);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        PackageItem packageItem = (PackageItem) getIntent().getExtras().getSerializable(PackageItem.class.getSimpleName());
        Integer position = (Integer) getIntent().getExtras().get("position");

        setTitle(packageItem.getPackageTitle());
        ((TextView) findViewById(R.id.package_title)).setText(packageItem.getPackageTitle());
        ((TextView) findViewById(R.id.package_name)).setText(packageItem.getPackageName());
        ((TextView) findViewById(R.id.package_source_path)).setText(packageItem.getPackageSourcePath());
        ((TextView) findViewById(R.id.package_data_dir)).setText(packageItem.getPackageDataDir());
        ((ImageView) findViewById(R.id.package_icon)).setImageDrawable(PackageItemArrayAdapter.packageIconList.get(position));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}