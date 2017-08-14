package com.tetris.saar.tetris;import android.graphics.Color;import android.support.v7.app.ActionBar;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.Toast;import java.util.Random;public class GameActivity extends AppCompatActivity {   ImageView[][] blocks = new ImageView[10][22];    GameManger gameManger = new GameManger();    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_game);        Random rnd = new Random();        int controlHeight = 0;        int controlLeft = 0;        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);        for(int i=0; i< blocks.length; i++) {            LinearLayout row = new LinearLayout(this);            row.setOrientation(LinearLayout.VERTICAL);            layout.addView(row);            for (int j = 0; j < blocks[i].length; j++) {                ImageView image = new ImageView(this);                image.setBackgroundColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(67, 67);                lp.setMargins(0, 0,0 , 0);                image.setLayoutParams(lp);                image.setMaxHeight(0);                image.setMaxWidth(0);                String str = "iv" + i + j;                int resID = getResources().getIdentifier(str, "id", getPackageName());                image.setId(resID);                blocks[i][j] = image;                row.addView(image);            }        }    }    public void toDisplay(){         gameManger.getDisplay();    }}