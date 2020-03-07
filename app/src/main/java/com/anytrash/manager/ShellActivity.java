package com.anytrash.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import com.anytrash.manager.model.Commands;

public class ShellActivity extends AppCompatActivity {

    EditText mCommandEditText;
    Button mExecuteButton;
    TextView mCommandLogTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCommandEditText = findViewById(R.id.command);
        mExecuteButton = findViewById(R.id.execute);
        mCommandLogTextView = findViewById(R.id.command_log);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shell, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_command:
                mCommandEditText.setText("");
                return super.onOptionsItemSelected(item);
            case R.id.action_clear_log:
                mCommandLogTextView.setText("");
                return super.onOptionsItemSelected(item);
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void executeOnClick(View view) {
        String command = mCommandEditText.getText().toString();
        String[] result = {};
        try {
            result = Commands.executeCommand(command);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (String line : result) {
            sb.append(line).append('\n');
        }
        mCommandLogTextView.setText(sb);
    }
}