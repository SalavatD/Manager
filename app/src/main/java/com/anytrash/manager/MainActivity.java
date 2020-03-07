package com.anytrash.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;

import com.anytrash.manager.model.Commands;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public void startPackagesActivity(View view) {
        startActivity(new Intent(getApplicationContext(), PackagesActivity.class));
    }

    public void startNotInstalledPackagesActivity(View view) {
        startActivity(new Intent(getApplicationContext(), NotInstalledPackagesActivity.class));
    }

    public void clearExternalStorage(View view) {
        try {
            Commands.clearExternalStorage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startShellActivity(View view) {
        startActivity(new Intent(getApplicationContext(), ShellActivity.class));
    }
}