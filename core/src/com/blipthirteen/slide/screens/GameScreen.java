package com.blipthirteen.slide.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameScreen implements Screen{

    ImageButton music, sound, backwhiteButton,retrywhiteButton;
    ImageButton.ImageButtonStyle musicButtonStyle, soundButtonStyle;
    ImageButton.ImageButtonStyle backwhiteButtonStyle,retrywhiteButtonStyle;


    // Blocks
    private ArrayList<ImageButton> blockList;
    private Vector2 emptyPosition,finalPosition;
    private HashMap<String,Vector2> map;

    // Scene 2D
    Stage stage;
    Skin skin;
    Skin uiSkin;
    TextureAtlas buttonatlas;

    private int size;
    private int level;
    private int limit;
    private int blockSize;
    private int levelInFile;

    SpriteBatch batch;
    Texture background;
    Texture largeFrame;;

    private BitmapFont font;
    int noOfMoves;
    Label scoreLabel;
    Label messageLabel;

    StretchViewport stretchViewport;
    FitViewport fitViewport;

    boolean checkScore;
    boolean checkForCompletion;


    Preferences prefs;

    Window window;
    ImageButton homeButton, retryButton, backButton;
    Label messagelabel;

    Sound sfx;
    Music bgm;


    private void generateFont() {
        font= new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.setColor(Color.WHITE);
    }


    public GameScreen(int size,int level){
        this.size = size;
        this.level = level;
        limit = size*size;
        switch(size){
            case 3: blockSize = 320;
                emptyPosition = new Vector2(700,480);
                levelInFile = 0;
                break;
            case 4: blockSize = 240;
                emptyPosition = new Vector2(780,480);
                levelInFile = 1;
                break;
            case 5: blockSize = 192;
                emptyPosition = new Vector2(828,480);
                levelInFile = 2;
                break;
            case 6: blockSize = 160;
                emptyPosition = new Vector2(860,480);
                levelInFile = 3;
                break;
            case 8: blockSize = 120;
                emptyPosition = new Vector2(900,480);
                levelInFile = 4;
                break;
        }
        finalPosition = emptyPosition;
    }

    @Override
    public void show() {
        fitViewport = new FitViewport(1080,1920);
        stretchViewport = new StretchViewport(1080,1920);

        Color fontColor = new Color( (float)208/255,(float)212/255,(float)216/255,1 );

        checkScore = false;
        checkForCompletion = true;
        prefs = Gdx.app.getPreferences("com.blipthirteen.slide");

        noOfMoves = 0;
        generateFont();
        map = new HashMap<String, Vector2>();
        populateMap();

        batch = new SpriteBatch();
        background = new Texture("game_screen/background.png");
        largeFrame = new Texture("game_screen/frame.png");

        blockList = new ArrayList<ImageButton>();
        stage = new Stage();
        stage.setViewport(fitViewport);
        skin= new Skin();
        String fileName = "packs/image_" + size + "_"+ level+".atlas";
        buttonatlas = new TextureAtlas(fileName);
        skin.addRegions(buttonatlas);
        for (int i = 0; i < limit; i++) {
            String s = "" + i;
            ImageButton button = new ImageButton(skin.getDrawable(s));
            blockList.add(button);
            button.setName("" + i);
        }

        Gdx.input.setInputProcessor(stage);


        Label.LabelStyle labelStyle = new Label.LabelStyle(font,fontColor);

        scoreLabel = new Label("Moves: " + noOfMoves,labelStyle);
        scoreLabel.setPosition(200 - scoreLabel.getWidth()/2,1500);
//        scoreLabel.setWidth(300);
//        scoreLabel.setWrap(true);
        scoreLabel.setAlignment(Align.left);
        stage.addActor(scoreLabel);



//        timeLabel = new Label("Time elapsed: ",labelStyle);
//        timeLabel.setPosition(90,1570);
//
//        stage.addActor(timeLabel);

        for (int i = 0; i < blockList.size(); i++) {
            ImageButton button = blockList.get(i);
            stage.addActor(button);
        }

        blockList.get(limit-1).setVisible(false);




        // After game completion window

        TextureAtlas textureAtlas = new TextureAtlas("game_screen/game_screen_buttons.pack");
        uiSkin = new Skin();
        uiSkin.addRegions(textureAtlas);

        ImageButton.ImageButtonStyle homebtnStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle retrybtnStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle backbtnStyle = new ImageButton.ImageButtonStyle();

        Color color = Color.LIGHT_GRAY;

        homebtnStyle.up = uiSkin.newDrawable("home5");
        homebtnStyle.down = uiSkin.newDrawable("home5",color);
        homeButton = new ImageButton(homebtnStyle);

        retrybtnStyle.up = uiSkin.getDrawable("retry5");
        retrybtnStyle.down = uiSkin.newDrawable("retry5",color);
        retryButton = new ImageButton(retrybtnStyle);

        backbtnStyle.up = uiSkin.getDrawable("back5");
        backbtnStyle.down = uiSkin.newDrawable("back5",color);
        backButton = new ImageButton(backbtnStyle);


        messageLabel = new Label("", labelStyle);
        messageLabel.setAlignment(Align.center);

        Window.WindowStyle windowStyle = new Window.WindowStyle(font,Color.BLACK,uiSkin.getDrawable("window"));
        window = new Window("",windowStyle);

        window.setWidth(900);
        window.setHeight(500);
        window.setPosition(1080/2 - window.getWidth()/2,1920/2 - window.getHeight()/2);
        window.addActor(messageLabel);
        window.addActor(homeButton);
        window.addActor(retryButton);
        window.addActor(backButton);
        messageLabel.setPosition(window.getWidth()/2 - messageLabel.getWidth()/2 , 380);
        backButton.setPosition(window.getWidth()/2 - backButton.getWidth()/2 , 85);
        homeButton.setPosition(window.getWidth()/2 - 260 - homeButton.getWidth()/2,85);
        retryButton.setPosition(window.getWidth()/2 + 260 - retryButton.getWidth()/2,85);
        window.setVisible(false);
        stage.addActor(window);

        musicButtonStyle = new ImageButton.ImageButtonStyle();
        musicButtonStyle.up = uiSkin.newDrawable("music_on",fontColor);
        musicButtonStyle.checked = uiSkin.newDrawable("music_off",fontColor);
        music = new ImageButton(musicButtonStyle);

        soundButtonStyle = new ImageButton.ImageButtonStyle();
        soundButtonStyle.up = uiSkin.newDrawable("sound_on",fontColor);
        soundButtonStyle.checked = uiSkin.newDrawable("sound_off",fontColor);
        sound = new ImageButton(soundButtonStyle);

        music.setChecked(!prefs.getBoolean("music"));
        sound.setChecked(!prefs.getBoolean("sound"));

        sound.setPosition(200 - sound.getWidth()/2, 200);
        music.setPosition(1080 - 200 - music.getWidth()/2, 200);

        backwhiteButtonStyle = new ImageButton.ImageButtonStyle();
        backwhiteButtonStyle.up = uiSkin.newDrawable("backwhite",fontColor);
        backwhiteButtonStyle.down = uiSkin.newDrawable("backwhite",color);
        backwhiteButton = new ImageButton(backwhiteButtonStyle);

        backwhiteButton.setPosition(1080/2 - backwhiteButton.getWidth()/2, 200);

        Drawable d = new TextureRegionDrawable(new TextureRegion(new Texture("game_screen/retrywhite.png")));
        retrywhiteButtonStyle = new ImageButton.ImageButtonStyle();
        retrywhiteButtonStyle.up = uiSkin.newDrawable(d,fontColor);
        retrywhiteButtonStyle.down = uiSkin.newDrawable(d,color);
        retrywhiteButton = new ImageButton(retrywhiteButtonStyle);

        retrywhiteButton.setPosition(1080 - 135 - backwhiteButton.getWidth()/2, 1500);


        stage.addActor(sound);
        stage.addActor(retrywhiteButton);
        stage.addActor(backwhiteButton);
        stage.addActor(music);

        setButtonListeners();
        randomize();
        bgm = Gdx.audio.newMusic(Gdx.files.internal("data/music.ogg"));
        sfx = Gdx.audio.newSound(Gdx.files.internal("data/sound.ogg"));
        bgm.setLooping(true);
        bgm.play();
    }

    private void randomize(){
        int index = 0;
        switch (size){
            case 3: {
                // Arrange sequentially
                for (int i = 1120; i >= 480; i -= 320) {
                    for (int j = 60; j <= 700; j += 320) {
                        ImageButton imageButton = blockList.get(index++);
                        imageButton.setPosition(j,i);
                    }
                }
            }
            break;
            case 4: {
                // Arrange sequentially
                for (int i = 1200; i >= 480; i -= 240) {
                    for (int j = 60; j <= 780; j += 240) {
                        ImageButton imageButton = blockList.get(index++);
                        imageButton.setPosition(j,i);
                    }
                }
            }
            break;
            case 5: {
                // Arrange sequentially
                for (int i = 1248; i >= 480; i -= 192) {
                    for (int j = 60; j <= 828; j += 192) {
                        ImageButton imageButton = blockList.get(index++);
                        imageButton.setPosition(j,i);
                    }
                }
            }
            break;
            case 6: {
                // Arrange sequentially
                for (int i = 1280; i >= 480; i -= 160) {
                    for (int j = 60; j <= 860; j += 160) {
                        ImageButton imageButton = blockList.get(index++);
                        imageButton.setPosition(j,i);
                    }
                }
            }
            break;
            case 8: {
                // Arrange sequentially
                for (int i = 1320; i >= 480; i -= 120) {
                    for (int j = 60; j <= 900; j += 120) {
                        ImageButton imageButton = blockList.get(index++);
                        imageButton.setPosition(j,i);
                    }
                }
            }
            break;
        }
        // Set last block to 0,0
        blockList.get(--index).setPosition(0,0);

        Set set = new HashSet();
        int swaps = 0;
        int swapLimit = (limit/2) - 2;
        while(swaps < swapLimit) {
            int rand1 = MathUtils.random(0, limit-2);
//            int rand2 = (limit-2) - rand1;
            int rand2 = MathUtils.random(0, limit-2);
            if (!set.contains(rand1) && !set.contains(rand2) && rand1 != rand2) {
                set.add(rand1);
                set.add(rand2);
                Vector2 swapPosition1 = new Vector2(blockList.get(rand1).getX(), blockList.get(rand1).getY());
                Vector2 swapPosition2 = new Vector2(blockList.get(rand2).getX(), blockList.get(rand2).getY());
                blockList.get(rand1).setPosition(swapPosition2.x, swapPosition2.y);
                blockList.get(rand2).setPosition(swapPosition1.x, swapPosition1.y);
                swaps++;
            }
        }
    }

    private void setButtonListeners() {
        for (int i = 0; i < blockList.size(); i++){
            final ImageButton imageButton = blockList.get(i);
            imageButton.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Vector2 currentPosition = new Vector2(imageButton.getX(),imageButton.getY());
                    slideToEmpty(currentPosition,imageButton);
                    return true;
                }
            });
        }
        homeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, 0.25f);

                return true;
            }
        });
        retryButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,level));
                    }
                }, 0.25f);

                return true;
            }
        });
        backButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(size));
                    }
                }, 0.25f);

                return true;
            }
        });
        sound.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean b = prefs.getBoolean("sound");
                prefs.putBoolean("sound",!b);
                prefs.flush();
                return true;
            }
        });

        music.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        boolean b = prefs.getBoolean("music");
                        prefs.putBoolean("music",!b);
                        prefs.flush();
                return true;
            }
        });
        backwhiteButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(size));
                    }
                }, 0.25f);

                return true;
            }
        });
        retrywhiteButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,level));
                    }
                }, 0.25f);

                return true;
            }
        });


    }

    private void slideToEmpty(Vector2 currentPosition, ImageButton imageButton){

        float xDif = Math.abs(currentPosition.x - emptyPosition.x);
        float yDif = Math.abs(currentPosition.y - emptyPosition.y);

        if(xDif == blockSize && yDif == 0){
            if(prefs.getBoolean("sound")){
                sfx.play(1.0f);
            }
            imageButton.setPosition(emptyPosition.x, emptyPosition.y);
            emptyPosition = currentPosition;
            noOfMoves++;
            scoreLabel.setText("Moves: " + noOfMoves);
            scoreLabel.setPosition(200 - scoreLabel.getWidth()/2 , 1500);
        }else if(xDif == 0 && yDif == blockSize){
            if(prefs.getBoolean("sound")){
                sfx.play(1.0f);
            }
            imageButton.setPosition(emptyPosition.x, emptyPosition.y);
            emptyPosition = currentPosition;
            noOfMoves++;
            scoreLabel.setText("Moves: " + noOfMoves);
            scoreLabel.setPosition(200 - scoreLabel.getWidth()/2 , 1500);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stretchViewport.getCamera().combined);
        stretchViewport.apply(true);
        batch.begin();
        batch.draw(background,0,0,1080,1920);
        batch.end();

        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        batch.draw(largeFrame,1080/2 - largeFrame.getWidth()/2,1920/2 - largeFrame.getHeight()/2);
        batch.end();

        stage.act();
        stage.draw();

        if(prefs.getBoolean("music"))
            bgm.setVolume(1);
        else bgm.setVolume(0);

        int count = 0;
        for (int i = 0; i < blockList.size()-1; i++) {
            ImageButton button = blockList.get(i);
            String buttonName = button.getName();
            Vector2 pos = map.get(buttonName);
            if(pos.x - button.getX() == 0 && pos.y - button.getY() == 0)
                count++;
        }

        if(count == limit-1 && checkForCompletion){
            ImageButton button = blockList.get(limit-1);
            button.setVisible(true);
            button.setPosition(finalPosition.x,finalPosition.y);
            checkScore = true;
            checkForCompletion = false;
            disableAllTiles();
            window.setVisible(true);
            retrywhiteButton.setTouchable(Touchable.disabled);
            backwhiteButton.setTouchable(Touchable.disabled);
        }
        if(checkScore){
            String s = prefs.getString("level_" + levelInFile + "_image_" + level);
            if (s.equals("NULL")) {
                messageLabel.setText("well done!");
                prefs.putString("level_" + levelInFile + "_image_" + level, "" + noOfMoves);
                prefs.flush();
            } else {
                int score = Integer.parseInt(s);
                if (noOfMoves < score) {
                    messageLabel.setText("new best!");
                    prefs.putString("level_" + levelInFile + "_image_" + level, "" + noOfMoves);
                    prefs.flush();
                } else {
                    messageLabel.setText("well done!");
                }
            }
            messageLabel.setPosition(window.getWidth()/2 - messageLabel.getWidth()/2 , 380);
            checkScore = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
             bgm.stop();
            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(size));
        }

    }

    @Override
    public void resize(int width, int height) {
        stretchViewport.update(width,height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        bgm.dispose();
        sfx.dispose();

        stage.dispose();
        skin.dispose();
        uiSkin.dispose();
        buttonatlas.dispose();

        batch.dispose();
        background.dispose();
        largeFrame.dispose();;
        font.dispose();
    }

    private void populateMap(){
        switch (size){

            case 3:
                map.put("0",new Vector2(60,1120));
                map.put("1",new Vector2(380,1120));
                map.put("2",new Vector2(700,1120));
                map.put("3",new Vector2(60,800));
                map.put("4",new Vector2(380,800));
                map.put("5",new Vector2(700,800));
                map.put("6",new Vector2(60,480));
                map.put("7",new Vector2(380,480));
                map.put("8",new Vector2(700,480));
                break;

            case 4:
                map.put("0",new Vector2(60,1200));
                map.put("1",new Vector2(300,1200));
                map.put("2",new Vector2(540,1200));
                map.put("3",new Vector2(780,1200));
                map.put("4",new Vector2(60,960));
                map.put("5",new Vector2(300,960));
                map.put("6",new Vector2(540,960));
                map.put("7",new Vector2(780,960));
                map.put("8",new Vector2(60,720));
                map.put("9",new Vector2(300,720));
                map.put("10",new Vector2(540,720));
                map.put("11",new Vector2(780,720));
                map.put("12",new Vector2(60,480));
                map.put("13",new Vector2(300,480));
                map.put("14",new Vector2(540,480));
                map.put("15",new Vector2(780,480));
                break;
            case 5:
                int index = 0;
                for (int i = 1248; i >= 480; i -= 192) {
                    for (int j = 60; j <= 828; j += 192) {
                        map.put(""+index++,new Vector2(j,i));
                    }
                }
                break;
            case 6:
                map.put("0",new Vector2(60,1280));
                map.put("1",new Vector2(220,1280));
                map.put("2",new Vector2(380,1280));
                map.put("3",new Vector2(540,1280));
                map.put("4",new Vector2(700,1280));
                map.put("5",new Vector2(860,1280));
                map.put("6",new Vector2(60,1120));
                map.put("7",new Vector2(220,1120));
                map.put("8",new Vector2(380,1120));
                map.put("9",new Vector2(540,1120));
                map.put("10",new Vector2(700,1120));
                map.put("11",new Vector2(860,1120));
                map.put("12",new Vector2(60,960));
                map.put("13",new Vector2(220,960));
                map.put("14",new Vector2(380,960));
                map.put("15",new Vector2(540,960));
                map.put("16",new Vector2(700,960));
                map.put("17",new Vector2(860,960));
                map.put("18",new Vector2(60,800));
                map.put("19",new Vector2(220,800));
                map.put("20",new Vector2(380,800));
                map.put("21",new Vector2(540,800));
                map.put("22",new Vector2(700,800));
                map.put("23",new Vector2(860,800));
                map.put("24",new Vector2(60,640));
                map.put("25",new Vector2(220,640));
                map.put("26",new Vector2(380,640));
                map.put("27",new Vector2(540,640));
                map.put("28",new Vector2(700,640));
                map.put("29",new Vector2(860,640));
                map.put("30",new Vector2(60,480));
                map.put("31",new Vector2(220,480));
                map.put("32",new Vector2(380,480));
                map.put("33",new Vector2(540,480));
                map.put("34",new Vector2(700,480));
                map.put("35",new Vector2(860,480));
                break;

            case 8:
                map.put("0",new Vector2(60,1320));
                map.put("1",new Vector2(180,1320));
                map.put("2",new Vector2(300,1320));
                map.put("3",new Vector2(420,1320));
                map.put("4",new Vector2(540,1320));
                map.put("5",new Vector2(660,1320));
                map.put("6",new Vector2(780,1320));
                map.put("7",new Vector2(900,1320));
                map.put("8",new Vector2(60,1200));
                map.put("9",new Vector2(180,1200));
                map.put("10",new Vector2(300,1200));
                map.put("11",new Vector2(420,1200));
                map.put("12",new Vector2(540,1200));
                map.put("13",new Vector2(660,1200));
                map.put("14",new Vector2(780,1200));
                map.put("15",new Vector2(900,1200));
                map.put("16",new Vector2(60,1080));
                map.put("17",new Vector2(180,1080));
                map.put("18",new Vector2(300,1080));
                map.put("19",new Vector2(420,1080));
                map.put("20",new Vector2(540,1080));
                map.put("21",new Vector2(660,1080));
                map.put("22",new Vector2(780,1080));
                map.put("23",new Vector2(900,1080));
                map.put("24",new Vector2(60,960));
                map.put("25",new Vector2(180,960));
                map.put("26",new Vector2(300,960));
                map.put("27",new Vector2(420,960));
                map.put("28",new Vector2(540,960));
                map.put("29",new Vector2(660,960));
                map.put("30",new Vector2(780,960));
                map.put("31",new Vector2(900,960));
                map.put("32",new Vector2(60,840));
                map.put("33",new Vector2(180,840));
                map.put("34",new Vector2(300,840));
                map.put("35",new Vector2(420,840));
                map.put("36",new Vector2(540,840));
                map.put("37",new Vector2(660,840));
                map.put("38",new Vector2(780,840));
                map.put("39",new Vector2(900,840));
                map.put("40",new Vector2(60,720));
                map.put("41",new Vector2(180,720));
                map.put("42",new Vector2(300,720));
                map.put("43",new Vector2(420,720));
                map.put("44",new Vector2(540,720));
                map.put("45",new Vector2(660,720));
                map.put("46",new Vector2(780,720));
                map.put("47",new Vector2(900,720));
                map.put("48",new Vector2(60,600));
                map.put("49",new Vector2(180,600));
                map.put("50",new Vector2(300,600));
                map.put("51",new Vector2(420,600));
                map.put("52",new Vector2(540,600));
                map.put("53",new Vector2(660,600));
                map.put("54",new Vector2(780,600));
                map.put("55",new Vector2(900,600));
                map.put("56",new Vector2(60,480));
                map.put("57",new Vector2(180,480));
                map.put("58",new Vector2(300,480));
                map.put("59",new Vector2(420,480));
                map.put("60",new Vector2(540,480));
                map.put("61",new Vector2(660,480));
                map.put("62",new Vector2(780,480));
                map.put("63",new Vector2(900,480));
                break;
        }
    }
    private void disableAllTiles(){
        for (int i = 0; i < blockList.size(); i++){
            blockList.get(i).clearListeners();
        }
    }
}