package com.blipthirteen.slide.screens;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.blipthirteen.slide.Constants;
import com.blipthirteen.slide.tween.ActorAccessor;
import com.blipthirteen.slide.tween.SpriteAccessor;


import aurelienribon.tweenengine.*;




public class GameScreen implements Screen{

    Label musicLabel, soundLabel;
    TextButton backTextButton, soundTextButton, musicTextButton, resetTextButton, pauseTextButton;

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
    Texture largeFrame;;

    private BitmapFont font, smallFont, largeFont;
    int noOfMoves;

    Label messageLabel;

    StretchViewport stretchViewport;
    FitViewport fitViewport;

    boolean checkScore;
    boolean checkForCompletion;

    Preferences prefs;


    Sound sfx;
    Music bgm;

    Window gameCompletionWindow, pauseWindow;
    TextButton nextImageTextButton;
    TextButton restartTextButton;
    TextButton homeTextButton;
    TextButton backToImageSelectScreenTextButton;
    TextButton nextImageTextButtonPauseWindow;
    TextButton restartTextButtonPauseWindow;
    TextButton homeTextButtonPauseWindow;
    TextButton backToImageSelectScreenTextButtonPauseWindow;
    TextButton resumeTextButton;
    TextButton saveTextButton;

    Label scoreTextLabel, timeTextLabel;
    Label scoreLabel, timeLabel;
    Label saveMessageLabel;
    Label lockMessageLabel;


    boolean uiVisibility;
    TweenManager tm;
    boolean saveGame;
    int saveGameTime;

    boolean paused;
    boolean gameFinished;


    long startTime;
    long elapsedTime;
    long pauseStamp;
    long pauseOffset;
    String timeString;

    float fadeInTime, fadeOutTime;

    Sprite boxSprite;

    private void generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;
        largeFont = generator.generateFont(parameter);
        largeFont.setColor(Color.BLACK);
        parameter.size = 48;
        font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);
        parameter.size = 36;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.BLACK);
        generator.dispose();
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
        uiVisibility = true;
        this.saveGame = false;
    }

    public GameScreen(int size,int level, boolean saveGame){
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
        uiVisibility = true;
        this.saveGame = saveGame;
    }

    private HashMap loadGame(){
        HashMap tempMap = null;
        if(Gdx.files.local("save.data").exists()){
            FileHandle file = Gdx.files.local("save.data");
            try {
                tempMap = deserialize(file.readBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tempMap;
    }

    private void setupGameCompletionWindow(){
        gameCompletionWindow = new Window("", uiSkin);
        gameCompletionWindow.setMovable(false);
        gameCompletionWindow.setVisible(false);
        gameCompletionWindow.setWidth(700);
        gameCompletionWindow.setHeight(370);
        gameCompletionWindow.setPosition(1080/2 - gameCompletionWindow.getWidth()/2, 240 - gameCompletionWindow.getHeight()/2);
        gameCompletionWindow.add(nextImageTextButton).width(590);
        gameCompletionWindow.row().padTop(10);
        gameCompletionWindow.add(restartTextButton).width(590);
        gameCompletionWindow.row().padTop(10);
        gameCompletionWindow.add(homeTextButton).width(590);
        gameCompletionWindow.row().padTop(10);
        gameCompletionWindow.add(backToImageSelectScreenTextButton).width(590);
        gameCompletionWindow.row();
    }
    private void setupGamePauseWindow(){
        pauseWindow = new Window("", uiSkin);
        pauseWindow.setMovable(false);
        pauseWindow.setVisible(false);
        pauseWindow.setWidth(700);
        pauseWindow.setHeight(450);
        pauseWindow.setPosition(1080/2 - pauseWindow.getWidth()/2, 1920/2 - pauseWindow.getHeight()/2);
        pauseWindow.add(resumeTextButton).width(590);
        pauseWindow.row().padTop(10);
        pauseWindow.add(nextImageTextButtonPauseWindow).width(590);
        pauseWindow.row().padTop(10);
        pauseWindow.add(restartTextButtonPauseWindow).width(590);
        pauseWindow.row().padTop(10);
        pauseWindow.add(homeTextButtonPauseWindow).width(590);
        pauseWindow.row().padTop(10);
        pauseWindow.add(backToImageSelectScreenTextButtonPauseWindow).width(590);
        pauseWindow.row();
    }

    private void setUpWindows(){
        // Buttons for game completion window
        nextImageTextButton = new TextButton("Next Image", uiSkin);
        restartTextButton = new TextButton("Restart", uiSkin);
        homeTextButton = new TextButton("Back to Main Menu", uiSkin);
        backToImageSelectScreenTextButton = new TextButton("Select another image", uiSkin);
        // Buttons for pause window
        nextImageTextButtonPauseWindow = new TextButton("Next Image", uiSkin);
        restartTextButtonPauseWindow = new TextButton("Restart", uiSkin);
        homeTextButtonPauseWindow = new TextButton("Back to Main Menu", uiSkin);
        backToImageSelectScreenTextButtonPauseWindow = new TextButton("Select another image", uiSkin);
        resumeTextButton = new TextButton("Resume", uiSkin);
        setupGameCompletionWindow();
        setupGamePauseWindow();
    }

    private void initScene2D(){
        uiSkin = new Skin();
        uiSkin.addRegions(new TextureAtlas(Gdx.files.internal("skin/plain-james-ui.atlas")));
        uiSkin.add("title", smallFont);
        uiSkin.add("font", font);
        uiSkin.load(Gdx.files.internal("skin/plain-james-ui.json"));
        if(prefs.getBoolean("music")){
            musicTextButton = new TextButton("Enabled", uiSkin.get("special", TextButton.TextButtonStyle.class));
        }else{
            musicTextButton = new TextButton("Disabled", uiSkin.get("special", TextButton.TextButtonStyle.class));
        }

        if(prefs.getBoolean("sound")){
            soundTextButton = new TextButton("Enabled", uiSkin.get("special", TextButton.TextButtonStyle.class));
        }else{
            soundTextButton = new TextButton("Disabled", uiSkin.get("special", TextButton.TextButtonStyle.class));
        }
        backTextButton = new TextButton("Back", uiSkin.get("special", TextButton.TextButtonStyle.class));
        resetTextButton = new TextButton("Reset", uiSkin.get("special", TextButton.TextButtonStyle.class));
        pauseTextButton = new TextButton("Pause", uiSkin.get("special", TextButton.TextButtonStyle.class));
        saveTextButton = new TextButton("Save", uiSkin.get("special", TextButton.TextButtonStyle.class));
        musicLabel = new Label("Music", uiSkin);
        soundLabel = new Label("Sound", uiSkin);
        saveMessageLabel = new Label("Saved", uiSkin);
        saveMessageLabel.setVisible(false);
        lockMessageLabel = new Label("Locked!", uiSkin);
        lockMessageLabel.setVisible(false);
        setUpWindows();
        setPositions();
        addToStage();
    }

    private void setPositions(){
        musicTextButton.setSize(238,musicTextButton.getHeight());
        soundTextButton.setSize(238,soundTextButton.getHeight());
        musicLabel.setPosition(160 - musicLabel.getWidth()/2,375 - musicLabel.getHeight()/2);
        soundLabel.setPosition(168 - soundLabel.getWidth()/2,220 - soundLabel.getHeight()/2);
        musicTextButton.setPosition(210 - musicTextButton.getWidth()/2, 312 - musicTextButton.getHeight()/2);
        soundTextButton.setPosition(210 - soundTextButton.getWidth()/2, 157 - soundTextButton.getHeight()/2);
        pauseTextButton.setWidth(238);
        backTextButton.setWidth(238);
        resetTextButton.setWidth(238);
        saveTextButton.setWidth(238);
        resetTextButton.setPosition(1080 - 210 - resetTextButton.getWidth()/2, 367 - resetTextButton.getHeight()/2);
        pauseTextButton.setPosition(1080 - 210 - pauseTextButton.getWidth()/2, 297 - pauseTextButton.getHeight()/2);
        backTextButton.setPosition(1080 - 210 - backTextButton.getWidth()/2,157 - backTextButton.getHeight()/2);
        saveTextButton.setPosition(1080 - 210 - saveTextButton.getWidth()/2, 227 - saveTextButton.getHeight()/2);
        saveMessageLabel.setPosition(1080/2 - saveMessageLabel.getWidth()/2, 227 - saveMessageLabel.getHeight()/2);
    }
    private void addToStage(){
        stage.addActor(pauseWindow);
        stage.addActor(gameCompletionWindow);
        stage.addActor(backTextButton);
        stage.addActor(soundTextButton);
        stage.addActor(musicLabel);
        stage.addActor(soundLabel);
        stage.addActor(musicTextButton);
        stage.addActor(resetTextButton);
        stage.addActor(pauseTextButton);
        stage.addActor(saveTextButton);
        stage.addActor(saveMessageLabel);
        stage.addActor(lockMessageLabel);
    }

    @Override
    public void show() {
        fadeInTime = 0.5f;
        fadeOutTime = 0.5f;
        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        fitViewport = new FitViewport(1080,1920);
        stretchViewport = new StretchViewport(1080,1920);
        tm = new TweenManager();
        checkScore = false;
        checkForCompletion = true;

        generateFont();
        map = new HashMap<String, Vector2>();
        populateMap();

        batch = new SpriteBatch();
        largeFrame = new Texture("game_screen/frame2.png");
        boxSprite = new Sprite(largeFrame);

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

        Color c = new Color(0.59f,0.59f, 0.59f, 1f);
        Label.LabelStyle largeLabel = new Label.LabelStyle(largeFont, c);
        scoreLabel = new Label("0",largeLabel);
        scoreLabel.setPosition(1080 - 70 - scoreLabel.getWidth()/2, 1465);
        scoreLabel.setAlignment(Align.right);
        timeLabel = new Label("0",largeLabel);
        timeLabel.setPosition(65 - scoreLabel.getWidth()/2, 1465);
        timeLabel.setAlignment(Align.left);



        Label.LabelStyle labelStyle = new Label.LabelStyle(font,Color.BLACK);
        scoreTextLabel = new Label("Moves",labelStyle);
        scoreTextLabel.setPosition(1080 - 126 - scoreTextLabel.getWidth()/2,1580);
        scoreTextLabel.setAlignment(Align.left);

        timeTextLabel = new Label("Time elapsed",labelStyle);
        timeTextLabel.setPosition(204 - timeTextLabel.getWidth()/2,1580);

        stage.addActor(scoreTextLabel);
        stage.addActor(timeLabel);
        stage.addActor(timeTextLabel);
        stage.addActor(scoreLabel);

        for (int i = 0; i < blockList.size(); i++) {
            ImageButton button = blockList.get(i);
            stage.addActor(button);
        }

        blockList.get(limit-1).setVisible(false);
        initScene2D();

        // After game completion window
        Label.LabelStyle messageLabelStyle = new Label.LabelStyle(largeFont, Color.BLACK);
        messageLabel = new Label("", messageLabelStyle);
        messageLabel.setAlignment(Align.center);
        messageLabel.setVisible(false);
        stage.addActor(messageLabel);
        enableTilesListeners();
        setButtonListeners();


        if(saveGame){
            noOfMoves = prefs.getInteger("saveGameScore");
            saveGameTime = prefs.getInteger("saveGameTime");
            HashMap hm = loadGame();
            reArrange(hm);
            scoreLabel.setText("" + noOfMoves);
        }else{
            noOfMoves = 0;
            randomize();
        }
        startTime = System.nanoTime();
        pauseStamp = 0;
        pauseOffset = 0;
        bgm = Gdx.audio.newMusic(Gdx.files.internal("data/music.ogg"));
        sfx = Gdx.audio.newSound(Gdx.files.internal("data/sound.ogg"));
        bgm.setLooping(true);
        bgm.play();
        fadeIn();
    }

    private void fadeIn(){
        Array<Actor> actorList = stage.getActors();
        Tween.registerAccessor(Actor.class,new ActorAccessor());
        for (Actor a:actorList){
            Tween.set(a, ActorAccessor.ALPHA).target(0).start(tm);
            Tween.to(a, ActorAccessor.ALPHA, fadeInTime).target(1).start(tm);
        }
        Tween.registerAccessor(Sprite.class,new SpriteAccessor());
        Tween.set(boxSprite, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(boxSprite, SpriteAccessor.ALPHA, fadeInTime).target(1).start(tm);
        tm.update(Float.MIN_VALUE);
    }

    private void fadeOut(){
        Array<Actor> actorList = stage.getActors();
        Tween.registerAccessor(Actor.class,new ActorAccessor());
        for (Actor a:actorList){
            Tween.set(a, ActorAccessor.ALPHA).target(1).start(tm);
            Tween.to(a, ActorAccessor.ALPHA, fadeOutTime).target(0).start(tm);
        }
        Tween.registerAccessor(Sprite.class,new SpriteAccessor());
        Tween.set(boxSprite, SpriteAccessor.ALPHA).target(1).start(tm);
        Tween.to(boxSprite, SpriteAccessor.ALPHA, fadeOutTime).target(0).start(tm);
        tm.update(Float.MIN_VALUE);
    }

    private void enableTilesListeners(){
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
    }

    private void reArrange(HashMap<String, Vector2> m){
        for (Map.Entry<String, Vector2> entry : m.entrySet())
        {
            Vector2 pos = entry.getValue();
            String indexString = entry.getKey();
            int index = Integer.parseInt(indexString);
            ImageButton imageButton = blockList.get(index);
            imageButton.setPosition(pos.x,pos.y);
        }
        for (Map.Entry<String, Vector2> entry : map.entrySet())
        {
           if(!m.containsValue(entry.getValue()))
           {
                emptyPosition = entry.getValue();
           }
        }

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
        saveTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        saveGame();
                        System.out.println("Saved");
                    }
                }, 0.15f);

                return true;
            }
        });

        resumeTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        resume();
                    }
                }, 0.25f);

                return true;
            }
        });

        //next
        restartTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,level));
                    }
                }, fadeOutTime);

                return true;
            }
        });
        restartTextButtonPauseWindow.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,level));
                    }
                }, fadeOutTime);

                return true;
            }
        });
        homeTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, fadeOutTime);

                return true;
            }
        });
        homeTextButtonPauseWindow.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, fadeOutTime);

                return true;
            }
        });


        backToImageSelectScreenTextButtonPauseWindow.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(size));
                    }
                }, fadeOutTime);

                return true;
            }
        });
        backToImageSelectScreenTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(size));
                    }
                }, fadeOutTime);

                return true;
            }
        });

        soundTextButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String buttonText = soundTextButton.getText().toString();
                if(buttonText.equals("Enabled")){
                    soundTextButton.setText("Disabled");
                }else if(buttonText.equals("Disabled")){
                    soundTextButton.setText("Enabled");
                }
                boolean b = prefs.getBoolean("sound");
                prefs.putBoolean("sound",!b);
                prefs.flush();
                return true;
            }
        });

        musicTextButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String buttonText = musicTextButton.getText().toString();
                if(buttonText.equals("Enabled")){
                    musicTextButton.setText("Disabled");
                }else if(buttonText.equals("Disabled")){
                    musicTextButton.setText("Enabled");
                }
                boolean b = prefs.getBoolean("music");
                prefs.putBoolean("music",!b);
                prefs.flush();
                return true;
            }
        });
        backTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(size));
                    }
                }, fadeOutTime);

                return true;
            }
        });
        resetTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bgm.stop();
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,level));
                    }
                }, fadeOutTime);

                return true;
            }
        });
        pauseTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        pause();
                    }
                }, 0.25f);

                return true;
            }
        });
        nextImageTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if(level != 9){
                            bgm.stop();
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,++level));
                        }else{
                            // Check for next diffi..
                            if(levelInFile!=4){
                                String str = "level_"+ (levelInFile + 1) + "_locked";
                                if(!prefs.getBoolean(str)){
                                    // 3 4 5 6 8
                                    if(size < 6){
                                        bgm.stop();
                                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(++size,0));
                                    }else{
                                        bgm.stop();
                                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size + 2,0));
                                    }
                                }else{
                                    lockMessageLabel.setText("Next Level\nis Locked!");
                                    lockMessageLabel.setAlignment(Align.center);
                                    lockMessageLabel.setPosition(1080/2 - lockMessageLabel.getWidth()/2, 1525 - lockMessageLabel.getHeight()/2);
                                    lockMessageLabel.setVisible(true);
                                    Tween.set(lockMessageLabel, ActorAccessor.ALPHA).target(0).start(tm);
                                    Tween.to(lockMessageLabel, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1,0.5f).setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int arg0, BaseTween<?> arg1) {
                                            lockMessageLabel.setVisible(false);
                                        }
                                    }).start(tm);
                                    tm.update(Float.MIN_VALUE);
                                }
                            }else{
                                lockMessageLabel.setText("oops!\nout of puzzles!");
                                lockMessageLabel.setAlignment(Align.center);
                                lockMessageLabel.setPosition(1080/2 - lockMessageLabel.getWidth()/2, 1525 - lockMessageLabel.getHeight()/2);
                                lockMessageLabel.setVisible(true);
                                Tween.set(lockMessageLabel, ActorAccessor.ALPHA).target(0).start(tm);
                                Tween.to(lockMessageLabel, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1,0.5f).setCallback(new TweenCallback() {
                                    @Override
                                    public void onEvent(int arg0, BaseTween<?> arg1) {
                                        lockMessageLabel.setVisible(false);
                                    }
                                }).start(tm);
                                tm.update(Float.MIN_VALUE);
                            }
                        }

                    }
                }, 0.25f);

                return true;
            }
        });

        nextImageTextButtonPauseWindow.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if(level != 9){
                            bgm.stop();
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,++level));
                        }else{
                            // Check for next diffi..
                            if(levelInFile!=4){
                                String str = "level_"+ (levelInFile + 1) + "_locked";
                                if(!prefs.getBoolean(str)){
                                    // 3 4 5 6 8
                                    if(size < 6){
                                        bgm.stop();
                                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(++size,0));
                                    }else{
                                        bgm.stop();
                                        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size + 2,0));
                                    }
                                }else{
                                    lockMessageLabel.setText("Next Level\nis Locked!");
                                    lockMessageLabel.setAlignment(Align.center);
                                    lockMessageLabel.setPosition(1080/2 - lockMessageLabel.getWidth()/2, 1525 - lockMessageLabel.getHeight()/2);
                                    lockMessageLabel.setVisible(true);
                                    Tween.set(lockMessageLabel, ActorAccessor.ALPHA).target(0).start(tm);
                                    Tween.to(lockMessageLabel, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1,0.5f).setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int arg0, BaseTween<?> arg1) {
                                            lockMessageLabel.setVisible(false);
                                        }
                                    }).start(tm);
                                    tm.update(Float.MIN_VALUE);
                                }
                            }else{
                                lockMessageLabel.setText("oops!\nout of puzzles!");
                                lockMessageLabel.setAlignment(Align.center);
                                lockMessageLabel.setPosition(1080/2 - lockMessageLabel.getWidth()/2, 1525 - lockMessageLabel.getHeight()/2);
                                lockMessageLabel.setVisible(true);
                                Tween.set(lockMessageLabel, ActorAccessor.ALPHA).target(0).start(tm);
                                Tween.to(lockMessageLabel, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1,0.5f).setCallback(new TweenCallback() {
                                    @Override
                                    public void onEvent(int arg0, BaseTween<?> arg1) {
                                        lockMessageLabel.setVisible(false);
                                    }
                                }).start(tm);
                                tm.update(Float.MIN_VALUE);
                            }
                        }

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
            scoreLabel.setText("" + noOfMoves);
        }else if(xDif == 0 && yDif == blockSize){
            if(prefs.getBoolean("sound")){
                sfx.play(1.0f);
            }
            imageButton.setPosition(emptyPosition.x, emptyPosition.y);
            emptyPosition = currentPosition;
            noOfMoves++;
            scoreLabel.setText("" + noOfMoves);
        }
    }

    private String secondsToFormattedTime(long nanos){
        int seconds = (int) TimeUtils.nanosToMillis(nanos) / 1000;
        if(saveGame){
            seconds += saveGameTime;
        }
        if(seconds < 60){
            if(seconds < 10){
                return new String("00:0" + seconds);
            }else return "00:" + seconds;
        }else if(seconds >= 60 && seconds <3600){
            int minutes = seconds / 60;
            int sec = (seconds % 60) % 60;
            StringBuilder timeString = new StringBuilder();
            if(minutes < 10){
                timeString.append("0" + minutes + ":");
            }else timeString.append(minutes + ":");

            if(sec < 10){
                timeString.append("0" + sec);
            }else timeString.append(sec);
            return new String(timeString.toString());
        }else if(seconds >= 3600){
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int sec = ((seconds % 3600) % 60) % 60;
            StringBuilder timeString = new StringBuilder();
            if(hours < 10){
                timeString.append("0" + hours + ":");
            }else timeString.append(hours + ":");

            if(minutes < 10){
                timeString.append("0" + minutes + ":");
            }else timeString.append(minutes + ":");

            if(sec < 10){
                timeString.append("0" + sec);
            }else timeString.append(sec);
            return new String(timeString.toString());
        }
        else return null;
    }

    private void setUpTween(){
        Tween.registerAccessor(Actor.class,new ActorAccessor());
        Tween.set(messageLabel, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(messageLabel, ActorAccessor.ALPHA, 2).target(1).start(tm);
        Tween.set(gameCompletionWindow, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(gameCompletionWindow, ActorAccessor.ALPHA, 2).target(1).start(tm);
        tm.update(Float.MIN_VALUE); // update once avoid short flash of splash before animation
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)230/255,(float)230/255,(float)230/255,1);
        if(gameFinished){
            timeLabel.setText(timeString);
        }
        else if(!paused) {
            elapsedTime = TimeUtils.timeSinceNanos(startTime + pauseOffset);
            timeString = secondsToFormattedTime(elapsedTime);
            timeLabel.setText(timeString);
        }

        batch.setProjectionMatrix(stretchViewport.getCamera().combined);
        stretchViewport.apply(true);
        batch.begin();
        batch.end();

        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        boxSprite.setPosition(1080/2 - largeFrame.getWidth()/2,1920/2 - largeFrame.getHeight()/2);
        boxSprite.draw(batch);
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
            gameFinished = true;
            disableAllTiles();
            gameCompletionWindow.setVisible(true);
            toggleOtherUIComponents();
            setUpTween();
        }

        if(checkScore){
            String s = prefs.getString("level_" + levelInFile + "_image_" + level);
            int timeInSeconds = (int) TimeUtils.nanosToMillis(elapsedTime) / 1000;
            if (s.equals("NULL")) {
                messageLabel.setText("well done!");
                prefs.putString("level_" + levelInFile + "_image_" + level, "" + noOfMoves);
                prefs.putInteger("level_" + levelInFile + "_image_time" + level, timeInSeconds);
                prefs.flush();
            } else {
                int score = Integer.parseInt(s);
                int time =  prefs.getInteger("level_" + levelInFile + "_image_time" + level);
                if (noOfMoves < score) {
                    messageLabel.setText("new best score!");
                    prefs.putString("level_" + levelInFile + "_image_" + level, "" + noOfMoves);
                    prefs.flush();
                    if(time > timeInSeconds && time != 0){
                        messageLabel.setText("new best score\n& time!");
                        prefs.putInteger("level_" + levelInFile + "_image_time" + level, timeInSeconds);
                        prefs.flush();
                    }
                }else if(time > timeInSeconds && time != 0){
                    messageLabel.setText("new best time!");
                    prefs.putInteger("level_" + levelInFile + "_image_time" + level, timeInSeconds);
                    prefs.flush();
                }
                else {
                    messageLabel.setText("well done!");
                }
            }
            messageLabel.setAlignment(Align.center);
            messageLabel.setPosition(1080/2 - messageLabel.getWidth()/2, 1760);
            messageLabel.setVisible(true);
            checkScore = false;
        }

        tm.update(delta);

        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
             bgm.stop();
            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(size));
        }
    }

    private byte[] serialize(HashMap map) throws  Exception{
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(map);
        return b.toByteArray();
    }

    private HashMap deserialize(byte[] bytes) throws  Exception{
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = null;
        o = new ObjectInputStream(b);
        return (HashMap) o.readObject();
    }

    private void saveGame(){
        saveMessageLabel.setVisible(true);
        Tween.set(saveMessageLabel, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(saveMessageLabel, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1,0.5f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                saveMessageLabel.setVisible(false);
            }
        }).start(tm);
        tm.update(Float.MIN_VALUE);

        HashMap<String,Vector2> positions = new HashMap<String, Vector2>();
        for (int i = 0; i < blockList.size()-1; i++) {
            ImageButton button = blockList.get(i);
            String buttonName = button.getName();
            Vector2 pos = new Vector2(blockList.get(i).getX(), blockList.get(i).getY());
            positions.put(buttonName,pos);
        }
        FileHandle file = Gdx.files.local("save.data");
        try{
            file.writeBytes(serialize(positions),false);
            prefs.putInteger("saveGameScore", noOfMoves);
            int seconds = (int) TimeUtils.nanosToMillis(elapsedTime) / 1000;
            prefs.putInteger("size", size);
            prefs.putInteger("level", level);
            prefs.putInteger("saveGameTime", seconds);
            prefs.flush();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    private void toggleOtherUIComponents(){
        uiVisibility = !uiVisibility;
        musicLabel.setVisible(uiVisibility);
        soundLabel.setVisible(uiVisibility);
        musicTextButton.setVisible(uiVisibility);
        soundTextButton.setVisible(uiVisibility);
        backTextButton.setVisible(uiVisibility);
        resetTextButton.setVisible(uiVisibility);
        pauseTextButton.setVisible(uiVisibility);
        saveTextButton.setVisible(uiVisibility);
    }

    @Override
    public void resize(int width, int height) {
        stretchViewport.update(width,height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        paused = true;
        pauseStamp = System.nanoTime();
        pauseWindow.setVisible(true);
        Tween.set(pauseWindow, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(pauseWindow, ActorAccessor.ALPHA, 1).target(1)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int arg0, BaseTween<?> arg1) {
                        toggleOtherUIComponents();
                    }
                }).start(tm);

        tm.update(Float.MIN_VALUE);
        fadeOutComponents();
        disableAllTiles();
    }

    @Override
    public void resume() {
        toggleOtherUIComponents();
        fadeInComponents();
        Tween.set(pauseWindow, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(pauseWindow, ActorAccessor.ALPHA, 1).target(0).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                pauseWindow.setVisible(false);
            }
        }).start(tm);
        tm.update(Float.MIN_VALUE);
        paused = false;
        pauseOffset += TimeUtils.timeSinceNanos(pauseStamp);
        enableTilesListeners();
    }

    private void fadeInComponents(){
        Tween.set(musicLabel, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(musicLabel, ActorAccessor.ALPHA, 1).target(1).start(tm);

        Tween.set(soundLabel, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(soundLabel, ActorAccessor.ALPHA, 1).target(1).start(tm);

        Tween.set(musicTextButton, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(musicTextButton, ActorAccessor.ALPHA, 1).target(1).start(tm);

        Tween.set(soundTextButton, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(soundTextButton, ActorAccessor.ALPHA, 1).target(1).start(tm);

        Tween.set(resetTextButton, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(resetTextButton, ActorAccessor.ALPHA, 1).target(1).start(tm);

        Tween.set(pauseTextButton, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(pauseTextButton, ActorAccessor.ALPHA, 1).target(1).start(tm);

        Tween.set(saveTextButton, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(saveTextButton, ActorAccessor.ALPHA, 1).target(1).start(tm);

        Tween.set(backTextButton, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(backTextButton, ActorAccessor.ALPHA, 1).target(1).start(tm);

        tm.update(Float.MIN_VALUE);
    }


    private void fadeOutComponents(){
        Tween.set(musicLabel, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(musicLabel, ActorAccessor.ALPHA, 1).target(0).start(tm);

        Tween.set(soundLabel, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(soundLabel, ActorAccessor.ALPHA, 1).target(0).start(tm);

        Tween.set(musicTextButton, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(musicTextButton, ActorAccessor.ALPHA, 1).target(0).start(tm);

        Tween.set(soundTextButton, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(soundTextButton, ActorAccessor.ALPHA, 1).target(0).start(tm);

        Tween.set(resetTextButton, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(resetTextButton, ActorAccessor.ALPHA, 1).target(0).start(tm);

        Tween.set(pauseTextButton, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(pauseTextButton, ActorAccessor.ALPHA, 1).target(0).start(tm);

        Tween.set(saveTextButton, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(saveTextButton, ActorAccessor.ALPHA, 1).target(0).start(tm);

        Tween.set(backTextButton, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(backTextButton, ActorAccessor.ALPHA, 1).target(0).start(tm);

        tm.update(Float.MIN_VALUE); // update once avoid short flash of splash before animation
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
        largeFrame.dispose();;
        font.dispose();
    }

    private void populateMap(){
        int index;
        switch (size){
            case 3:
                index = 0;
                for (int i = 1120; i >= 480; i -= 320) {
                    for (int j = 60; j <= 700; j += 320) {
                        map.put(""+ index++, new Vector2( j, i));
                    }
                }
                break;

            case 4:
                index = 0;
                for (int i = 1200; i >= 480; i -= 240) {
                    for (int j = 60; j <= 780; j += 240) {
                        map.put(""+ index++, new Vector2( j, i));
                    }
                }
                break;
            case 5:
                index = 0;
                for (int i = 1248; i >= 480; i -= 192) {
                    for (int j = 60; j <= 828; j += 192) {
                        map.put(""+ index++, new Vector2( j, i));
                    }
                }
                break;
            case 6:
                index = 0;
                for (int i = 1280; i >= 480; i -= 160) {
                    for (int j = 60; j <= 860; j += 160) {
                        map.put(""+ index++, new Vector2( j, i));
                    }
                }
                break;

            case 8:
                index = 0;
                for (int i = 1320; i >= 480; i -= 120) {
                    for (int j = 60; j <= 900; j += 120) {
                        map.put(""+ index++, new Vector2( j, i));
                    }
                }
                break;
        }
    }
    private void disableAllTiles(){
        for (int i = 0; i < blockList.size(); i++){
            blockList.get(i).clearListeners();
        }
    }
}