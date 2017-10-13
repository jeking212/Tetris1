package com.tetris.saar.tetris;import android.content.Context;import android.content.Intent;import android.database.sqlite.SQLiteDatabase;import android.graphics.Color;import android.support.v7.app.AlertDialog;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.text.TextUtils;import android.view.MotionEvent;import android.view.View;import android.view.ViewGroup;import android.widget.Button;import android.widget.EditText;import android.widget.ImageButton;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import android.widget.Toast;import static com.tetris.saar.tetris.R.drawable.pausemenu;import static com.tetris.saar.tetris.R.drawable.resumbutton;import static com.tetris.saar.tetris.R.drawable.roundedbutton;import static com.tetris.saar.tetris.R.mipmap.black;import static com.tetris.saar.tetris.R.mipmap.blue;import static com.tetris.saar.tetris.R.mipmap.darkblue;import static com.tetris.saar.tetris.R.mipmap.green;import static com.tetris.saar.tetris.R.mipmap.grey;import static com.tetris.saar.tetris.R.mipmap.lego_blocks_detail;import static com.tetris.saar.tetris.R.mipmap.orange;import static com.tetris.saar.tetris.R.mipmap.purple;import static com.tetris.saar.tetris.R.mipmap.red;import static com.tetris.saar.tetris.R.mipmap.yellow;public class GameActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {    public ImageView[][] blocks = new ImageView[10][24]; //Displayed board    //Game Manager    GameManger gameManger = new GameManger(this);    //Used to run the game on a different thread    Runnable game= new GameThread(gameManger);    Thread thread =new Thread(game);    GameThread gameThread = new GameThread(gameManger);    //Score TextView    TextView tvScore;    //Pause Button    ImageButton ibPasue;    //Next Block place    ImageView[][] nextBlockView = new ImageView[4][4];    //Swipe checker     float x1,x2,downY,upY;    static final int MIN_DISTANCE = 100;    //This screen    Context context;    Intent intent;    //For the database    SQLiteDatabase mainDB = null;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_game);        //Creating the Database        createDB();        //Syncing the java and the GUI        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);        LinearLayout nextBlockLayout = (LinearLayout) findViewById(R.id.nextBlockLayout);        tvScore = (TextView) findViewById(R.id.tvScore);        ibPasue = (ImageButton) findViewById(R.id.ibPause);        ibPasue.setOnClickListener(this);        context = this;        //Creating all the ImageView game board        for(int i=0; i< blocks.length; i++) {            LinearLayout row = new LinearLayout(this);            if(i==0){                LinearLayout.LayoutParams leftMarhin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);                leftMarhin.setMargins(180,0,0,0);                row.setLayoutParams(leftMarhin);            }            row.setOrientation(LinearLayout.VERTICAL);            layout.addView(row);            for (int j = 2; j < blocks[i].length; j++) {                    ImageView image = new ImageView(this);                    //image.setBackgroundColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(65, 65);                    lp.setMargins(0, 0, 0, 0);                    image.setLayoutParams(lp);                    image.setMaxHeight(0);                    image.setMaxWidth(0);                    image.setPadding(10, 10, 10, 10);                    blocks[i][j] = image;                    row.addView(image);            }        }        //Creating all the ImageView for the next block view        for(int i=0; i< nextBlockView.length;i++){            LinearLayout row = new LinearLayout(this);            if(i==0){                LinearLayout.LayoutParams leftMarhin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);                leftMarhin.setMargins(870,0,0,0);                row.setLayoutParams(leftMarhin);            }            row.setOrientation(LinearLayout.VERTICAL);            nextBlockLayout.addView(row);            for (int j = 0; j < nextBlockView[i].length; j++) {                ImageView image = new ImageView(this);                image.setBackgroundColor(Color.BLACK);                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(53, 53);                if(j==0){                    lp.setMargins(0,390,0,0);                }               // lp.setMargins(0, 0, 0, 0);                image.setLayoutParams(lp);                image.setMaxHeight(0);                image.setMaxWidth(0);                image.setPadding(10, 10, 10, 10);                nextBlockView[i][j] = image;                row.addView(image);            }        }        toDisplay();        //Starting the game        thread.start();    }    //The alart dialog at the end of the game    public void gameOver(){        String tempScore = tvScore.getText().toString();      final String[] score = tempScore.split("\n");        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);        builder1.setTitle("GAME OVER");        builder1.setCancelable(false);        final LinearLayout mainAlertLayout = new LinearLayout(context);        final LinearLayout.LayoutParams mainLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);        mainAlertLayout.setOrientation(LinearLayout.VERTICAL);        mainAlertLayout.setLayoutParams(mainLp);        LinearLayout.LayoutParams textParms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);        LinearLayout textLayout = new LinearLayout(context);        textLayout.setOrientation(LinearLayout.HORIZONTAL);        textParms.setMargins(190,200,0,0);        textLayout.setLayoutParams(textParms);        TextView massageData = new TextView(context);        massageData.setText("You Lost. \nYour score is: " + score[1]);        massageData.setTextColor(Color.BLACK);        massageData.setTextSize(30);        textLayout.addView(massageData);        mainAlertLayout.addView(textLayout);        final LinearLayout buttonLayout = new LinearLayout(context);        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);        buttonLayout.setLayoutParams(mainLp);        Button newGame = new Button(context);        Button wantToSave = new Button(context);        Button mainMenu = new Button(context);        Button scoreboard = new Button(context);        newGame.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context,GameActivity.class);                startActivity(intent);            }        });        //The Alert dialog with the save option        wantToSave.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {              AlertDialog.Builder saveBuilder = new AlertDialog.Builder(context);                saveBuilder.setTitle("Save Score:");                saveBuilder.setCancelable(false);              LinearLayout mainLayout = new LinearLayout(context);                mainLayout.setOrientation(LinearLayout.VERTICAL);                LinearLayout.LayoutParams mainParms= new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);                mainLayout.setLayoutParams(mainParms);                mainParms.setMargins(0,250,0,0);                LinearLayout dataLayout = new LinearLayout(context);                dataLayout.setOrientation(LinearLayout.HORIZONTAL);                dataLayout.setLayoutParams(mainParms);                TextView saveText =new TextView(context);                saveText.setText("Please Enter Name:");                saveText.setTextSize(20);                saveText.setTextColor(Color.BLACK);                final EditText input = new EditText(context);                input.setWidth(550);                dataLayout.addView(saveText);                dataLayout.addView(input);                mainLayout.addView(dataLayout);                LinearLayout buttonSaveLayout = new LinearLayout(context);                buttonSaveLayout.setOrientation(LinearLayout.HORIZONTAL);                LinearLayout.LayoutParams saveButtonsParam = new LinearLayout.LayoutParams(350, ViewGroup.LayoutParams.WRAP_CONTENT);                saveButtonsParam.setMargins(150,200,0,0);                Button save = new Button(context);                Button back = new Button(context);                save.setOnClickListener(new View.OnClickListener() {                    @Override                    public void onClick(View v) {                         intent = new Intent(context,Scoreboard.class);                        if(TextUtils.isEmpty(input.getText())){                            Toast.makeText(GameActivity.this, "Please Enter A Name", Toast.LENGTH_SHORT).show();                        }else                        {                            mainDB.execSQL("INSERT INTO scoreboard1 (name, score) VALUES('" + input.getText() + "','" + Integer.parseInt(score[1]) + "');");                            startActivity(intent);                        }                    }                });                back.setOnClickListener(new View.OnClickListener() {                    @Override                    public void onClick(View v) {                        gameOver();                    }                });                save.setText("Save");                save.setTextSize(20);                save.setAllCaps(false);                save.setBackgroundResource(roundedbutton);                save.setTextColor(Color.WHITE);                back.setText("Go Back");                back.setTextSize(20);                back.setAllCaps(false);                back.setBackgroundResource(roundedbutton);                back.setTextColor(Color.WHITE);                save.setLayoutParams(saveButtonsParam);                back.setLayoutParams(saveButtonsParam);                buttonSaveLayout.addView(back);                buttonSaveLayout.addView(save);                mainLayout.addView(buttonSaveLayout);                saveBuilder.setView(mainLayout);                AlertDialog alert1 = saveBuilder.create();                alert1.show();                alert1.getWindow().setBackgroundDrawableResource(lego_blocks_detail);                alert1.getWindow().setLayout(1100,1000);            }        });        mainMenu.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context,MainMenu.class);                startActivity(intent);            }        });        scoreboard.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context,Scoreboard.class);                startActivity(intent);            }        });        newGame.setBackgroundResource(roundedbutton);        wantToSave.setBackgroundResource(roundedbutton);        mainMenu.setBackgroundResource(roundedbutton);        scoreboard.setBackgroundResource(roundedbutton);        newGame.setTextSize(15);        newGame.setText("New Game");        newGame.setAllCaps(false);        newGame.setTextColor(Color.WHITE);        wantToSave.setTextSize(15);        wantToSave.setText("Save");        wantToSave.setAllCaps(false);        wantToSave.setTextColor(Color.WHITE);        mainMenu.setTextSize(15);        mainMenu.setText("Menu");        mainMenu.setAllCaps(false);        mainMenu.setTextColor(Color.WHITE);        scoreboard.setTextSize(15);        scoreboard.setText("Score");        scoreboard.setAllCaps(false);        scoreboard.setTextColor(Color.WHITE);        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);        LinearLayout.LayoutParams scoreboardParms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);        buttonParams.setMargins(10,200,0,0);        buttonLayout.addView(newGame,buttonParams);        buttonLayout.addView(wantToSave,buttonParams);        buttonLayout.addView(mainMenu,buttonParams);        scoreboardParms.setMargins(10,200,30,0);        buttonLayout.addView(scoreboard,scoreboardParms);        mainAlertLayout.addView(buttonLayout);        builder1.setView(mainAlertLayout);        AlertDialog alert11 = builder1.create();        alert11.show();        alert11.getWindow().setBackgroundDrawableResource(lego_blocks_detail);        alert11.getWindow().setLayout(1100,1000);    }    //Display the board ---> convert from int to colored Image View    public void toDisplay(){    int[][] board=  gameManger.getDisplay();        for(int i=blocks.length-1; i>=0; i--){            for(int j=blocks[i].length-1; j>=2; j--){                //Empty block                if(board[i][j]==0){                 blocks[i][j].setBackgroundColor(Color.argb(1000,0,0,0));                    //blocks[i][j].setBackgroundResource(black);                }                //Square                if(board[i][j] == 1){                    //blocks[i][j].setBackgroundColor(Color.YELLOW);                    blocks[i][j].setBackgroundResource(yellow);                }                //Line and up right                if(board[i][j] == 2){                    //blocks[i][j].setBackgroundColor(Color.rgb(255,140,0));                    blocks[i][j].setBackgroundResource(orange);                }                //Line and up left                if(board[i][j]==3){                    //blocks[i][j].setBackgroundColor(Color.rgb(0,0,205));                    blocks[i][j].setBackgroundResource(darkblue);                }                //Line                if(board[i][j]==4){                    //blocks[i][j].setBackgroundColor(Color.rgb(135,206,250));                    blocks[i][j].setBackgroundResource(blue);                }                //Z shaperd                if(board[i][j]==5){                    //blocks[i][j].setBackgroundColor(Color.RED);                    blocks[i][j].setBackgroundResource(red);                }                //T shaped                if(board[i][j]==6){                    //blocks[i][j].setBackgroundColor(Color.rgb(138,43,226));                    blocks[i][j].setBackgroundResource(purple);                }                //S shaped                if(board[i][j]==7){                    //blocks[i][j].setBackgroundColor(Color.GREEN);                    blocks[i][j].setBackgroundResource(green);                }                if(board[i][j] ==8){                    //blocks[i][j].setBackgroundColor(Color.GRAY);                    blocks[i][j].setBackgroundResource(grey);                }            }        }    }    //Swipe control    @Override    public boolean onTouchEvent(MotionEvent event){        {            switch(event.getAction())            {                case MotionEvent.ACTION_DOWN:                    x1 = event.getX();                    downY = event.getY();                    break;                case MotionEvent.ACTION_UP:                    x2 = event.getX();                    upY = event.getY();                    float deltaX = x2 - x1;                    float deltaY = upY- downY;                    if (Math.abs(deltaX) > MIN_DISTANCE)                    {                        //Move right                        if (x2 > x1)                        {                            gameThread.moveRight();                        }                        // Right to left swipe action                        else                        {                            gameThread.moveLeft();                        }                    }//VERTICAL SCROLL                    else                        if(Math.abs(deltaY) > MIN_DISTANCE){                            // top or down                            if(deltaY > 0)                            {                                gameThread.changeSpeed();                                return true;                            }                        }                    else                    {                            gameThread.needToChange();                    }                    break;            }            return super.onTouchEvent(event);        }    }    //Checking for button click    public void onClick(View view){        if(view.getId() == ibPasue.getId()){            gameThread.pauseUnPause();            ibPasue.setImageResource(resumbutton);            AlertDialog.Builder builder = new AlertDialog.Builder(context);            builder.setTitle("Game Paused");            builder.setCancelable(false);            LinearLayout mainLayout = new LinearLayout(context);            mainLayout.setOrientation(LinearLayout.VERTICAL);            final TextView gamePausedText= new TextView(context);            LinearLayout.LayoutParams textParms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);            gamePausedText.setText("Game Paused");            gamePausedText.setTextColor(Color.BLACK);            gamePausedText.setLayoutParams(textParms);            mainLayout.addView(gamePausedText);            Button dismiss = new Button(context);            Button newGame = new Button(context);            Button mainMenu = new Button(context);            LinearLayout buttonLayout = new LinearLayout(context);            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);            dismiss.setText("Resume");            dismiss.setBackgroundResource(roundedbutton);            dismiss.setTextColor(Color.WHITE);            dismiss.setAllCaps(false);            newGame.setText("Restart");            newGame.setBackgroundResource(roundedbutton);            newGame.setTextColor(Color.WHITE);            newGame.setAllCaps(false);            mainMenu.setText("Menu");            mainMenu.setBackgroundResource(roundedbutton);            mainMenu.setTextColor(Color.WHITE);            mainMenu.setAllCaps(false);            newGame.setOnClickListener(new View.OnClickListener() {                @Override                public void onClick(View v) {                    intent = new Intent(context,GameActivity.class);                    gameThread.pauseUnPause();                    startActivity(intent);                }            });            mainMenu.setOnClickListener(new View.OnClickListener() {                @Override                public void onClick(View v) {                    intent = new Intent(context,MainMenu.class);                    gameThread.pauseUnPause();                    startActivity(intent);                }            });            dismiss.setLayoutParams(buttonLayoutParams);            newGame.setLayoutParams(buttonLayoutParams);            mainMenu.setLayoutParams(buttonLayoutParams);            buttonLayout.addView(mainMenu);            buttonLayout.addView(newGame);            buttonLayout.addView(dismiss);            mainLayout.addView(buttonLayout);            builder.setView(mainLayout);            final AlertDialog alert = builder.create();            dismiss.setOnClickListener(new View.OnClickListener() {                @Override                public void onClick(View v) {                    gameThread.pauseUnPause();                    ibPasue.setImageResource(pausemenu);                    alert.dismiss();                }            });            alert.show();        }    }    public void changeScore(int text){        tvScore.setText("Score: \n" + text);    }    @Override    public boolean onTouch(View v, MotionEvent event) {        {        }        return true;    }    //Gets the next block and coverts it's id to color    public void displayNextBlock(Blocks nextBlock){        int id = nextBlock.getId();        //Square        if(id==1){            //colorNextBlock(Color.YELLOW,nextBlock);            colorNextBlock(yellow,nextBlock);        }        //Line And Up Right        if(id == 2){            //colorNextBlock(Color.rgb(255,140,0),nextBlock);            colorNextBlock(orange,nextBlock);        }        //Line and up left        if(id==3){            //colorNextBlock(Color.rgb(0,0,205),nextBlock);            colorNextBlock(darkblue,nextBlock);        }        //Line        if(id==4){           //colorNextBlock(Color.rgb(135,206,250),nextBlock);            colorNextBlock(blue,nextBlock);        }        //Z shaperd        if(id==5){            //colorNextBlock(Color.RED,nextBlock);            colorNextBlock(red,nextBlock);        }        //T shaped        if(id==6){            //colorNextBlock(Color.rgb(138,43,226),nextBlock);            colorNextBlock(purple,nextBlock);        }        //S shaped        if(id==7){            //colorNextBlock(Color.GREEN,nextBlock);            colorNextBlock(green,nextBlock);        }    }    //Color the next block view    private void colorNextBlock(int color,Blocks nextBlock){        for(int i=0; i<nextBlockView.length;i++){            for(int j=0; j< nextBlockView[i].length; j++){                nextBlockView[i][j].setBackgroundColor(Color.BLACK);            }        }        //nextBlockView[1][1].setBackgroundColor(color);        nextBlockView[1][1].setBackgroundResource(color);        if(nextBlock.isLeftUp()){            //nextBlockView[0][0].setBackgroundColor(color);            nextBlockView[0][0].setBackgroundResource(color);        }        if(nextBlock.isUp()){            //nextBlockView[1][0].setBackgroundColor(color);            nextBlockView[1][0].setBackgroundResource(color);        }        if(nextBlock.isRightUp()){            //nextBlockView[2][0].setBackgroundColor(color);            nextBlockView[2][0].setBackgroundResource(color);        }        if(nextBlock.isLeft()){            //nextBlockView[0][1].setBackgroundColor(color);            nextBlockView[0][1].setBackgroundResource(color);        }        if(nextBlock.isRight()){            //nextBlockView[2][1].setBackgroundColor(color);            nextBlockView[2][1].setBackgroundResource(color);        }        if(nextBlock.isDownLeft()){            //nextBlockView[0][2].setBackgroundColor(color);            nextBlockView[0][2].setBackgroundResource(color);        }        if(nextBlock.isDown()){            //nextBlockView[1][2].setBackgroundColor(color);            nextBlockView[1][2].setBackgroundResource(color);        }        if(nextBlock.isDownRight()){            //nextBlockView[2][2].setBackgroundColor(color);            nextBlockView[2][2].setBackgroundResource(color);        }    }    public void createDB(){        try {            mainDB = this.openOrCreateDatabase("Scoreboard",MODE_PRIVATE,null);            mainDB.execSQL("CREATE TABLE IF NOT EXISTS scoreboard1" + "(id integer primary key , name VARCHAR,score integer);");        }catch (Exception e){        }    }}//Full visible == 1000 alpha