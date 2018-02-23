package com.tetris.saar.tetris;import android.app.Dialog;import android.content.ComponentName;import android.content.Context;import android.content.Intent;import android.content.ServiceConnection;import android.graphics.Color;import android.net.Uri;import android.os.Bundle;import android.os.IBinder;import android.support.v7.app.AppCompatActivity;import android.text.TextUtils;import android.view.KeyEvent;import android.view.Menu;import android.view.MenuItem;import android.view.MotionEvent;import android.view.View;import android.view.ViewGroup;import android.widget.Button;import android.widget.EditText;import android.widget.ImageButton;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import android.widget.Toast;import java.io.Serializable;import static com.tetris.saar.tetris.R.drawable.pausemenu;import static com.tetris.saar.tetris.R.drawable.resumbutton;import static com.tetris.saar.tetris.R.mipmap.blue;import static com.tetris.saar.tetris.R.mipmap.darkblue;import static com.tetris.saar.tetris.R.mipmap.green;import static com.tetris.saar.tetris.R.mipmap.grey;import static com.tetris.saar.tetris.R.mipmap.orange;import static com.tetris.saar.tetris.R.mipmap.purple;import static com.tetris.saar.tetris.R.mipmap.red;import static com.tetris.saar.tetris.R.mipmap.yellow;/** * The type Game activity. *///The game activitypublic class GameActivity extends AppCompatActivity implements View.OnClickListener,Serializable {    /**     * Displayed board.     */    public ImageView[][] blocks = new ImageView[10][24];    /**     * The Game manger.     */    GameManger gameManger = new GameManger(this);    /**     * Used to run the game on a different thread     */    Runnable game = new GameThread(gameManger);    /**     * The Thread.     */    Thread thread = new Thread(game);    /**     * The Gamethread.     */    GameThread gameThread = new GameThread(gameManger);    /**     * Score TextView     */    TextView tvScore;    /**     * Pause Button.     */    ImageButton ibPasue;    /**     * Next Block place.     */    ImageView[][] nextBlockView = new ImageView[4][4];    //Swipe checker points    float x1, x2, downY, upY;    /**     * The Min distance of a swipe.     */    static final int MIN_DISTANCE = 100;    /**     * The Context.     */    Context context;    /**     * The Intent.     */    Intent intent;    /**     * The Main db.     */    Databasehelper mainDB;    /**     * The action bar.     */    Menu mainMenu = null;    /**     * binding the Music service.     */    Intent musicService;    private boolean mIsBound = false;    private MusicThread mServ;    private ServiceConnection Scon  =new ServiceConnection(){        public void onServiceConnected(ComponentName name, IBinder binder) {            mServ = ((MusicThread.ServiceBinder)binder).getService();        }        public void onServiceDisconnected(ComponentName name) {            mServ = null;        }    };    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_game);        //Creating the Database        mainDB = new Databasehelper(this);        //Syncing the java and the GUI        LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);        LinearLayout nextBlockLayout = (LinearLayout) findViewById(R.id.nextBlockLayout);        tvScore = (TextView) findViewById(R.id.tvScore);        ibPasue = (ImageButton) findViewById(R.id.ibPause);        ibPasue.setOnClickListener(this);        //This screen        context = this;        //Music handle        musicService= new Intent();        mServ = new MusicThread();        doBindService();        musicService.setClass(this,MusicThread.class);        startService(musicService);        //Creating all the ImageView game board        for (int i = 0; i < blocks.length; i++) {            LinearLayout row = new LinearLayout(this);            if (i == 0) {                LinearLayout.LayoutParams leftMarhin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);                leftMarhin.setMargins(180, 0, 0, 0);                row.setLayoutParams(leftMarhin);            }            row.setOrientation(LinearLayout.VERTICAL);            layout.addView(row);            for (int j = 2; j < blocks[i].length; j++) {                ImageView image = new ImageView(this);                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(65, 65);                lp.setMargins(0, 0, 0, 0);                image.setLayoutParams(lp);                image.setPadding(10, 10, 10, 10);                blocks[i][j] = image;                row.addView(image);            }        }        //Creating all the ImageView for the next block view        for (int i = 0; i < nextBlockView.length; i++) {            LinearLayout row = new LinearLayout(this);            if (i == 0) {                LinearLayout.LayoutParams leftMarhin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);                leftMarhin.setMargins(870, 0, 0, 0);                row.setLayoutParams(leftMarhin);            }            row.setOrientation(LinearLayout.VERTICAL);            nextBlockLayout.addView(row);            for (int j = 0; j < nextBlockView[i].length; j++) {                ImageView image = new ImageView(this);                image.setBackgroundColor(Color.BLACK);                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(53, 53);                if (j == 0) {                    lp.setMargins(0, 390, 0, 0);                }                image.setLayoutParams(lp);                image.setMaxHeight(0);                image.setMaxWidth(0);                image.setPadding(10, 10, 10, 10);                nextBlockView[i][j] = image;                row.addView(image);            }        }        vSync();        //Starting the game        thread.start();    }    //Action bar handle    @Override    public boolean onCreateOptionsMenu(Menu menu) {        // Inflate the menu; this adds items to the action bar if it is present.        getMenuInflater().inflate(R.menu.bulletmenu, menu);        mainMenu = menu;        return true;    }    //Menu press should open 3 dot menu    @Override    public boolean onKeyDown(int keyCode, KeyEvent event) {        if (keyCode == KeyEvent.KEYCODE_MENU) {            mainMenu.performIdentifierAction(R.id.call, 0);            return true;        }        return super.onKeyDown(keyCode, event);    }    //Click listener open the phone    @Override    public boolean onOptionsItemSelected(MenuItem item) {        super.onOptionsItemSelected(item);        switch(item.getItemId()){            case R.id.call:                Intent call= new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + ""));                startActivity(call);                break;            case R.id.exit:                finish();//Close the app                break;            case R.id.toggleMusic:                mServ.toogleMusic();        }        return true;    }    /**     * The alert dialog at the end of the game.     */    public void gameOver() {        String tempScore = tvScore.getText().toString();        final String[] score = tempScore.split("\n"); //The score        //Creating the dialog and putting the layout        final Dialog dialog = new Dialog(this);        dialog.setCancelable(false);        dialog.setContentView(R.layout.gameoveralert);        //Syncing all the objects        TextView tvGameOverText = (TextView) dialog.findViewById(R.id.tvGameOverText);        tvGameOverText.setText("Your score is:\n" + score[1]);        //Buttons        //New Game        Button btnNewGame = (Button) dialog.findViewById(R.id.btnNewGame);        btnNewGame.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context, GameActivity.class);                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                startActivity(intent);            }        });        //Save        Button btnWantToSave = (Button) dialog.findViewById(R.id.btnSave);        btnWantToSave.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                final Dialog saveDialog = new Dialog(context);                saveDialog.setCancelable(false);                saveDialog.setContentView(R.layout.savealert);                saveDialog.show();                //Save                Button btnSave = (Button) saveDialog.findViewById(R.id.btnSave);                btnSave.setOnClickListener(new View.OnClickListener() {                    @Override                    public void onClick(View v) {                        EditText input = (EditText) saveDialog.findViewById(R.id.etInput);                        intent = new Intent(context, Scoreboard.class);                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                        if (TextUtils.isEmpty(input.getText())) {                            Toast.makeText(GameActivity.this, "Please Enter A Name", Toast.LENGTH_SHORT).show();                        } else {                            mainDB.addData(input.getText().toString(), Integer.parseInt(score[1]));                            startActivity(intent);                        }                    }                });                //Cancel                Button btnCancel = (Button) saveDialog.findViewById(R.id.btnBack);                btnCancel.setOnClickListener(new View.OnClickListener() {                    @Override                    public void onClick(View v) {                        saveDialog.dismiss();                        dialog.show();                    }                });                dialog.dismiss();            }        });        //Menu        Button btnMenu = (Button) dialog.findViewById(R.id.btnMainMenu);        btnMenu.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context, MainMenu.class);                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                startActivity(intent);            }        });        //Scoreboard        Button btnScoreboard = (Button) dialog.findViewById(R.id.btnScoreboard);        btnScoreboard.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context, Scoreboard.class);                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                startActivity(intent);            }        });        dialog.show();        dialog.getWindow().setLayout(1100, 1000);    }    /**     * V sync.     */    public void vSync(){        new Thread(new Runnable() {            @Override            public void run() {                int[][] board = gameManger.getDisplay();                for(int j= blocks[0].length -1; j>=2;j--){                    for(int i = blocks.length - 1; i >= 0; i--){                        //Empty block                        if (board[i][j] == 0) {                            blocks[i][j].setBackgroundColor(Color.rgb(0, 0, 0));                        }                        //Square                        if (board[i][j] == 1) {                            blocks[i][j].setBackgroundResource(yellow);                        }                        //Line and up right                        if (board[i][j] == 2) {                            blocks[i][j].setBackgroundResource(orange);                        }                        //Line and up left                        if (board[i][j] == 3) {                            blocks[i][j].setBackgroundResource(darkblue);                        }                        //Line                        if (board[i][j] == 4) {                            blocks[i][j].setBackgroundResource(blue);                        }                        //Z shaperd                        if (board[i][j] == 5) {                            blocks[i][j].setBackgroundResource(red);                        }                        //T shaped                        if (board[i][j] == 6) {                            blocks[i][j].setBackgroundResource(purple);                        }                        //S shaped                        if (board[i][j] == 7) {                            blocks[i][j].setBackgroundResource(green);                        }                        if (board[i][j] == 8) {                            blocks[i][j].setBackgroundResource(grey);                        }                    }                }            }        });    }    /**     * Display the board ---> convert from int to colored Image View (H-sync).     */    public void toDisplay() {        int[][] board = gameManger.getDisplay();        for (int i = blocks.length - 1; i >= 0; i--) {            for (int j = blocks[i].length - 1; j >= 2; j--) {                //Empty block                if (board[i][j] == 0) {                    blocks[i][j].setBackgroundColor(Color.rgb(0, 0, 0));                }                //Square                if (board[i][j] == 1) {                    blocks[i][j].setBackgroundResource(yellow);                }                //Line and up right                if (board[i][j] == 2) {                    blocks[i][j].setBackgroundResource(orange);                }                //Line and up left                if (board[i][j] == 3) {                    blocks[i][j].setBackgroundResource(darkblue);                }                //Line                if (board[i][j] == 4) {                    blocks[i][j].setBackgroundResource(blue);                }                //Z shaperd                if (board[i][j] == 5) {                    blocks[i][j].setBackgroundResource(red);                }                //T shaped                if (board[i][j] == 6) {                    blocks[i][j].setBackgroundResource(purple);                }                //S shaped                if (board[i][j] == 7) {                    blocks[i][j].setBackgroundResource(green);                }                if (board[i][j] == 8) {                    blocks[i][j].setBackgroundResource(grey);                }            }        }    }    //Swipe control    @Override    public boolean onTouchEvent(MotionEvent event) {        {            switch (event.getAction()) {                case MotionEvent.ACTION_DOWN:                    x1 = event.getX();                    downY = event.getY();                    break;                case MotionEvent.ACTION_UP:                    x2 = event.getX();                    upY = event.getY();                    float deltaX = x2 - x1;                    float deltaY = upY - downY;                    if (Math.abs(deltaX) > MIN_DISTANCE) {                        //Move right                        if (x2 > x1) {                            gameThread.moveRight();                        }                        // Right to left swipe action                        else {                            gameThread.moveLeft();                        }                    }//VERTICAL SCROLL                    else if (Math.abs(deltaY) > MIN_DISTANCE) {                        // top or down                        if (deltaY > 0) {                            gameThread.changeSpeed();                            return true;                        }                    } else {                        gameThread.needToChange();                    }                    break;            }            return super.onTouchEvent(event);        }    }    //Checking for button click    public void onClick(View view) {        if (view.getId() == ibPasue.getId()) {            pauseDialog();        }    }    /**     * Change score.     *     * @param text the text     */    //Changing the score    public void changeScore(int text) {        tvScore.setText("Score: \n" + text);    }    /**     * Display next block.     *     * @param nextBlock the next block     */    //Gets the next block and coverts it's id to color    public void displayNextBlock(Blocks nextBlock) {        int id = nextBlock.getId();        //Square        if (id == 1) {            colorNextBlock(yellow, nextBlock);        }        //Line And Up Right        if (id == 2) {            colorNextBlock(orange, nextBlock);        }        //Line and up left        if (id == 3) {            colorNextBlock(darkblue, nextBlock);        }        //Line        if (id == 4) {            colorNextBlock(blue, nextBlock);        }        //Z shaperd        if (id == 5) {            colorNextBlock(red, nextBlock);        }        //T shaped        if (id == 6) {            colorNextBlock(purple, nextBlock);        }        //S shaped        if (id == 7) {            colorNextBlock(green, nextBlock);        }    }    //Color the next block view    private void colorNextBlock(int color, Blocks nextBlock) {        for (int i = 0; i < nextBlockView.length; i++) {            for (int j = 0; j < nextBlockView[i].length; j++) {                nextBlockView[i][j].setBackgroundColor(Color.BLACK);            }        }        nextBlockView[1][1].setBackgroundResource(color);        if (nextBlock.isLeftUp()) {            nextBlockView[0][0].setBackgroundResource(color);        }        if (nextBlock.isUp()) {            nextBlockView[1][0].setBackgroundResource(color);        }        if (nextBlock.isRightUp()) {            nextBlockView[2][0].setBackgroundResource(color);        }        if (nextBlock.isLeft()) {            nextBlockView[0][1].setBackgroundResource(color);        }        if (nextBlock.isRight()) {            nextBlockView[2][1].setBackgroundResource(color);        }        if (nextBlock.isDownLeft()) {            nextBlockView[0][2].setBackgroundResource(color);        }        if (nextBlock.isDown()) {            nextBlockView[1][2].setBackgroundResource(color);        }        if (nextBlock.isDownRight()) {            nextBlockView[2][2].setBackgroundResource(color);        }    }    //Music bind and Unbind    private void doBindService(){        bindService(new Intent(context,MusicThread.class),                Scon,Context.BIND_AUTO_CREATE);        mIsBound = true;    }    private void doUnbindService()    {        if(mIsBound)        {            unbindService(Scon);            mIsBound = false;        }    }    @Override    public void onBackPressed() {        intent = new Intent(context, MainMenu.class);        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);        startActivity(intent);    }    @Override    public void onDestroy(){        super.onDestroy();        doUnbindService();    }    @Override    public void onStop(){        super.onStop();        pauseDialog();    }    /**     * Pause dialog.     */    public void pauseDialog(){        gameThread.pauseUnPause();        ibPasue.setImageResource(resumbutton);        final Dialog dialog = new Dialog(context);        dialog.setCancelable(false);        dialog.setContentView(R.layout.pausemenu);        //Resume        Button btnReseum = (Button) dialog.findViewById(R.id.btnResume);        btnReseum.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                ibPasue.setImageResource(pausemenu);                gameThread.pauseUnPause();                dialog.dismiss();            }        });        //New Game        Button btnNewGame = (Button) dialog.findViewById(R.id.btnNewGame);        btnNewGame.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context, GameActivity.class);                gameThread.pauseUnPause();                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                startActivity(intent);            }        });        //menu        Button btnMenu = (Button) dialog.findViewById(R.id.btnMenu);        btnMenu.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                intent = new Intent(context, MainMenu.class);                gameThread.pauseUnPause();                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                startActivity(intent);            }        });        dialog.show();        dialog.getWindow().setLayout(1000, 800);    }}