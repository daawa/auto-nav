package com.example.submodule1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.annotation.AutoWireNav;
import com.example.annotation.IntentParam;

import test.nav.NativeNav;


@AutoWireNav(name = "SubMain")
public class MainActivity extends AppCompatActivity {

    @IntentParam(name="param",type = "parcelable")
    private static final String subParam = "sub_param";
    @IntentParam(name="param2")
    private static final String subParam2= "sub_ttt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "go to page \'second\'", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NativeNav.toSecond(MainActivity.this)
                                        .baseParamA_("base value")
                                        .baseParamBilli_(3324f)
                                        .param2_("param2_value")
                                        .go();
                            }
                        }).show();
            }
        });
    }

}
