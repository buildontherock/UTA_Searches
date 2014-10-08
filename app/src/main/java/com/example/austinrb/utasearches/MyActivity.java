package com.example.austinrb.utasearches;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


public class MyActivity extends Activity {

    final Context context = this;
    private EditText searchText;
    private EditText tagText;
    private ImageButton saveButton;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> tagList;
    private SharedPreferences sharedPreferences;
    String originalUrl = "http://www.uta.edu/search/?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        searchText = (EditText) findViewById(R.id.searchQuery);
        tagText = (EditText) findViewById(R.id.tagQuery);
        tagList = new ArrayList<String>();
        saveButton = (ImageButton) findViewById(R.id.saveButton);
        final ListView listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tagList);
        listView.setAdapter(adapter);
        
        loadInitialPreferences();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String itemValue = (String) listView.getItemAtPosition(position);
                String query = sharedPreferences.getString(itemValue, "");
                String completeUrl = originalUrl + query;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(completeUrl));
                startActivity(i);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final String dialogOptions[] = {"Share", "Edit", "Delete"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder
                        .setTitle("Share, Edit, Delete")
                        .setPositiveButton("Share",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent shareIntent = new Intent();
                                        shareIntent.setAction(Intent.ACTION_SEND);
                                        shareIntent.setType("text/plain");
                                        startActivity(Intent.createChooser(shareIntent, "Share"));
                                    }
                                })
                        .setNegativeButton("Edit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        String selectedTag = (String) listView.getItemAtPosition(i);
                                        String query = sharedPreferences.getString(selectedTag, "");
                                        dialog.cancel();
                                        searchText.setText(query);
                                        tagText.setText(selectedTag);
                                        searchText.requestFocus();
                                    }
                                })
                        .setNeutralButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        String selectedItem = (String) listView.getItemAtPosition(i);
                                        tagList.remove(selectedItem);
                                        adapter.notifyDataSetChanged();
                                        sharedPreferences.edit().remove(selectedItem).commit();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;
            }
        });
    }


    public void onSaveButtonClick(View v){

        String tag = tagText.getText().toString();
        String query = searchText.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, query);
        editor.commit();
        searchText.setText("");
        tagText.setText("");

        tagList.add(tag);
        adapter.notifyDataSetChanged();


                ((InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tagText.getWindowToken(), 0);
    }


    private void loadInitialPreferences() {

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            tagList.add(entry.getKey());
        }
    }


 }


