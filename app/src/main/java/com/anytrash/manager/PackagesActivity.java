package com.anytrash.manager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.anytrash.manager.model.PackageItem;
import com.anytrash.manager.model.PackageItemArrayAdapter;

import static com.anytrash.manager.model.Commands.clearPackageData;
import static com.anytrash.manager.model.Commands.deletePackage;
import static com.anytrash.manager.model.Commands.executeCommand;

public class PackagesActivity extends AppCompatActivity {
    ArrayList<ApplicationInfo> applicationInfoList;
    ArrayList<PackageItem> packageList;
    ArrayList<Boolean> checkedList;
    PackageItemArrayAdapter packageItemArrayAdapter;
    RecyclerView recyclerView;
    ArrayList<Drawable> packageIconList;
    GetPackageList getPackageList;
    ClearPackagesData clearPackagesData;
    DeletePackages deletePackages;
    ClearAndDelete clearAndDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        packageList = new ArrayList<>();
        packageIconList = new ArrayList<>();
        checkedList = new ArrayList<>();
        getPackageList = new GetPackageList(this);
        getPackageList.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_packages, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            executeCommand("mount -o rw,remount /system");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            executeCommand("mount -o ro,remount /system");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_clear_data:
                clearPackagesData = new ClearPackagesData(this);
                clearPackagesData.execute();
                return true;
            case R.id.action_delete_packages:
                deletePackages = new DeletePackages(this);
                deletePackages.execute();
                return true;
            case R.id.action_clear_and_delete:
                clearAndDelete = new ClearAndDelete(this);
                clearAndDelete.execute();
                return true;
            case R.id.action_refresh_package_list:
                packageItemArrayAdapter.notifyDataSetChanged();
                for (int i = 0; i < checkedList.size(); i++) {
                    checkedList.set(i, false);
                }
                getPackageList = new GetPackageList(this);
                getPackageList.execute();
                return true;
            case R.id.select_all:
                for (int i = 0; i < checkedList.size(); i++) {
                    checkedList.set(i, true);
                }
                packageItemArrayAdapter.notifyDataSetChanged();
                return true;
            case R.id.unselect_all:
                for (int i = 0; i < checkedList.size(); i++) {
                    checkedList.set(i, false);
                }
                packageItemArrayAdapter.notifyDataSetChanged();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @SuppressLint("StaticFieldLeak")
    private class ClearPackagesData extends AsyncTask<Void, Integer, Void> {
        private int checkedItemCount;
        private ProgressDialog progressDialog;

        ClearPackagesData(AppCompatActivity appCompatActivity) {
            checkedItemCount = packageItemArrayAdapter.getCheckedItems().size();
            progressDialog = new ProgressDialog(appCompatActivity);
            progressDialog.setTitle(getString(R.string.clear_data));
            progressDialog.setMessage("Cleared " + 0 + " of " + checkedItemCount);
            progressDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (Integer checkedItemPosition : packageItemArrayAdapter.getCheckedItems()) {
                try {
                    clearPackageData(packageList.get(checkedItemPosition).getPackageName());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkedList.set(checkedItemPosition, false);
                publishProgress(checkedItemPosition);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage("Cleared " + (values[0] + 1) + " of " + checkedItemCount);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            packageItemArrayAdapter.notifyDataSetChanged();
            for (int i = 0; i < checkedList.size(); i++) {
                checkedList.set(i, false);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeletePackages extends AsyncTask<Void, Integer, Void> {
        private AppCompatActivity appCompatActivity;
        private int checkedItemCount;
        private ProgressDialog progressDialog;

        DeletePackages(AppCompatActivity appCompatActivity) {
            this.appCompatActivity = appCompatActivity;
            checkedItemCount = packageItemArrayAdapter.getCheckedItems().size();
            progressDialog = new ProgressDialog(appCompatActivity);
            progressDialog.setTitle(getString(R.string.delete_packages));
            progressDialog.setMessage("Deleted " + 0 + " of " + checkedItemCount);
            progressDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int checkedItemPosition : packageItemArrayAdapter.getCheckedItems()) {
                try {
                    deletePackage(packageList.get(checkedItemPosition).getPackageSourcePath());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(checkedItemPosition);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage("Deleted " + (values[0] + 1) + " of " + checkedItemCount);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            packageItemArrayAdapter.notifyDataSetChanged();
            for (int i = 0; i < checkedList.size(); i++) {
                checkedList.set(i, false);
            }
            getPackageList = new GetPackageList(appCompatActivity);
            getPackageList.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ClearAndDelete extends AsyncTask<Void, Integer, Void> {
        private AppCompatActivity appCompatActivity;
        private int checkedItemCount;
        private ProgressDialog progressDialog;

        ClearAndDelete(AppCompatActivity appCompatActivity) {
            this.appCompatActivity = appCompatActivity;
            checkedItemCount = packageItemArrayAdapter.getCheckedItems().size();
            progressDialog = new ProgressDialog(appCompatActivity);
            progressDialog.setTitle(getString(R.string.delete_packages));
            progressDialog.setMessage("Deleted " + 0 + " of " + checkedItemCount);
            progressDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int checkedItemPosition : packageItemArrayAdapter.getCheckedItems()) {
                try {
                    clearPackageData(packageList.get(checkedItemPosition).getPackageName());
                    deletePackage(packageList.get(checkedItemPosition).getPackageSourcePath());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(checkedItemPosition);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage("Deleted " + (values[0] + 1) + " of " + checkedItemCount);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            packageItemArrayAdapter.notifyDataSetChanged();
            for (int i = 0; i < checkedList.size(); i++) {
                checkedList.set(i, false);
            }
            getPackageList = new GetPackageList(appCompatActivity);
            getPackageList.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPackageList extends AsyncTask<Void, Void, ArrayList<String>> {
        private ProgressDialog progressDialog;
        private AppCompatActivity appCompatActivity;

        GetPackageList(AppCompatActivity appCompatActivity) {
            this.appCompatActivity = appCompatActivity;
            progressDialog = new ProgressDialog(appCompatActivity);
            progressDialog.setTitle(getString(R.string.packages));
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            PackageManager packageManager = getPackageManager();
            if (packageItemArrayAdapter == null) {
                packageItemArrayAdapter = new PackageItemArrayAdapter(R.layout.list_item, packageList, checkedList, packageIconList, appCompatActivity);

            }
            if (applicationInfoList == null) {
                applicationInfoList = (ArrayList<ApplicationInfo>) packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            }
            packageIconList.clear();
            packageList.clear();
            checkedList.clear();
            for (ApplicationInfo applicationInfo : applicationInfoList) {
                if (new File(applicationInfo.sourceDir).isFile()) {
                    packageIconList.add(packageManager.getApplicationIcon(applicationInfo));
                    packageList.add(new PackageItem(applicationInfo.packageName, packageManager.getApplicationLabel(applicationInfo).toString(), applicationInfo.sourceDir.replaceAll("^(.*)/.*?$", "$1"), applicationInfo.dataDir));
                    checkedList.add(false);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            setTitle("Packages " + "(" + packageList.size() + ")");
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            progressDialog.dismiss();
            setTitle("Packages " + "(" + packageList.size() + ")");
            packageItemArrayAdapter.notifyDataSetChanged();
            if (recyclerView == null) {
                recyclerView = findViewById(R.id.package_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(packageItemArrayAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(appCompatActivity, 1));
            }
        }
    }
}