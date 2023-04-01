package com.example.androidchess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import com.example.androidchess.Chess;

public class Recorded_Games extends AppCompatActivity {

    private static final String FILE_NAME = "save.txt";

    ListView listView;

    ArrayList<String> arrayList = new ArrayList<String>();

    public static HashMap<String, ArrayList<String>> Games = new HashMap<String, ArrayList<String>>();

    public static String selectedGame = "";
    public static ArrayList<String> selectedGameMoves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorded__games);

        listView = (ListView) findViewById(R.id.listview);


        //System.out.println(Games + "yo mama");


        for (String key : Games.keySet()) {
            arrayList.add(key);
        }

        Load();

//        FileOutputStream fos = null;
//        try {
//            fos = openFileOutput(FILE_NAME,MODE_PRIVATE);
//            fos.write(Games.keySet().toString().getBytes());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if(fos != null){
//                System.out.println("Saved!!!!!!");
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Recorded_Games.this, arrayList.get(position).toString(), Toast.LENGTH_SHORT).show();

                selectedGame = arrayList.get(position);
                selectedGameMoves = Games.get(selectedGame);
            }
        });
    }

    public void Load() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String Text;

            while ((Text = br.readLine()) != null) {
                sb.append(Text).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sortName(View view) {

        arrayList.sort(String::compareToIgnoreCase);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


    }

    public void sortDate(View view) {

        ArrayList<String> returnList = new ArrayList<String>();

        System.out.println(arrayList.size());
        for (String s : arrayList) {

            try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(s.split(":")[1]);

                if (returnList.size() == 0) {
                    returnList.add(s);
                } else {
                    boolean added = false;
                    for (int i = 0; i < returnList.size(); i++) {

                        String stringInList = returnList.get(i);
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(stringInList.split(":")[1]);

                        if (date1.compareTo(date2) == -1) {
                            added = true;
                            returnList.add(i, s);
                            break;
                        }
                    }

                    if (!added) {
                        returnList.add(s);
                    }
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        arrayList = returnList;
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

    public void open(View view) {

        Intent intent = new Intent(this, Replay.class);
        startActivity(intent);
        getApplicationContext();
    }

}
