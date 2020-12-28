package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.blipthirteen.slide.Constants;
import com.blipthirteen.slide.tween.ActorAccessor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by rohit on 25-12-2016.
 */

public class MenuScreen implements Screen{

    // Scene 2D
    Stage stage;
    TextButton musicButton, soundButton, playButton, moreGamesButton, aboutButton, resumeButton;
    Label musicLabel, soundLabel, titleLabel;
    Skin uiSkin;

    // Tween
    TweenManager tm;

    SpriteBatch batch;
    FitViewport fitViewport;
    StretchViewport stretchViewport;
    Timer t;
    Preferences prefs;

    BitmapFont largeFont, mediumFont, smallFont;
    ShapeRenderer shapeRenderer;
    ProgressBar progressBar;
    float fadeInTime, fadeOutTime;
    Label progressLabel;

    private void generateFont(){
        largeFont = new BitmapFont();
        mediumFont = new BitmapFont();
        smallFont = new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;
        largeFont = generator.generateFont(parameter);
        parameter.size = 52;
        mediumFont = generator.generateFont(parameter);
        parameter.size = 36;
        smallFont = generator.generateFont(parameter);
        generator.dispose();
    }

    private void initScene2D(){
        uiSkin = new Skin();
        uiSkin.addRegions(new TextureAtlas(Gdx.files.internal("skin/plain-james-ui.atlas")));
        uiSkin.add("title", mediumFont);
        uiSkin.add("font", smallFont);

        uiSkin.load(Gdx.files.internal("skin/plain-james-ui.json"));
        Gdx.input.setInputProcessor(stage);

        playButton = new TextButton("Play", uiSkin.get("special", TextButton.TextButtonStyle.class));
        resumeButton = new TextButton("Resume", uiSkin.get("special", TextButton.TextButtonStyle.class));
        if(!Gdx.files.local("save.data").exists()){
            resumeButton.setVisible(false);
        }

        moreGamesButton = new TextButton("More Games", uiSkin);
        aboutButton = new TextButton("About", uiSkin);

        musicLabel = new Label("Music", uiSkin.get("title", Label.LabelStyle.class));
        soundLabel = new Label("Sound", uiSkin.get("title", Label.LabelStyle.class));

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(largeFont, Color.BLACK);
        titleLabel = new Label("Slide", titleLabelStyle);

        if(prefs.getBoolean("music")){
            musicButton = new TextButton("Enabled", uiSkin);
        }else{
            musicButton = new TextButton("Disabled", uiSkin);
        }

        if(prefs.getBoolean("sound")){
            soundButton = new TextButton("Enabled", uiSkin);
        }else{
            soundButton = new TextButton("Disabled", uiSkin);
        }
        progressBar = new ProgressBar(0,50,1,false,uiSkin);

        progressLabel = new Label("", uiSkin);
        progressLabel.setAlignment(Align.center);

        int progress = 0;
        for(int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                String key = "level_" + i + "_image_" + j;
                String value = prefs.getString(key);
                if(!value.equalsIgnoreCase("NULL")){
                    progress++;
                }
            }
        }

        progressBar.setValue(progress);
        progressLabel.setText("" + progressBar.getPercent() * 100 + "%");
        addToScene();
        setPositions();
    }

    private void addToScene(){
        stage.addActor(titleLabel);
        stage.addActor(playButton);
        stage.addActor(resumeButton);
        stage.addActor(moreGamesButton);
        stage.addActor(aboutButton);
        stage.addActor(musicButton);
        stage.addActor(soundButton);
        stage.addActor(musicLabel);
        stage.addActor(soundLabel);
        stage.addActor(progressBar);
        stage.addActor(progressLabel);
    }

    public void show() {
        fadeInTime = 0.5f;
        fadeOutTime = 0.5f;

        Gdx.input.setCatchBackKey(true);
        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stretchViewport = new StretchViewport(1080,1920);
        stage.setViewport(fitViewport);
        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        shapeRenderer = new ShapeRenderer();

        generateFont();
        initScene2D();

        t = new Timer();
        t.scheduleTask(new Task() {
            @Override
            public void run() {
                setButtonListeners();
            }
        }, 0.5f);

        tm = new TweenManager();
        fadeIn();
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)230/255,(float)230/255,(float)230/255,1);

        fitViewport.apply(true);
        stage.act();
        stage.draw();

        tm.update(delta); //Tween

        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            Gdx.app.exit();
        }
    }

    public void resize(int width, int height) {
        stretchViewport.update(width,height,true);
        fitViewport.update(width,height,true);
        stage.getViewport().update(width, height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        stage.dispose();
        batch.dispose();
        uiSkin.dispose();
    }

    private void setPositions() {
        titleLabel.setPosition(1080/2 - titleLabel.getWidth()/2, 1920/2 + 350 - titleLabel.getHeight()/2);

        playButton.setSize(500,125);
        playButton.setPosition(1080/2 - playButton.getWidth()/2, 1920/2 + 50 - playButton.getHeight()/2);

        resumeButton.setSize(500,125);
        resumeButton.setPosition(1080/2 - resumeButton.getWidth()/2, 1920/2 - 100 - resumeButton.getHeight()/2);

        moreGamesButton.setSize(270,70);
        moreGamesButton.setPosition(370 - moreGamesButton.getWidth()/2, 420);

        aboutButton.setSize(270,70);
        aboutButton.setPosition(1080 - 370 - aboutButton.getWidth()/2, 420);

        musicButton.setSize(270, 70);
        musicButton.setPosition(370 - musicButton.getWidth()/2,520);
        soundButton.setSize(270, 70);
        soundButton.setPosition(1080 - 370 - soundButton.getWidth()/2, 520);
        musicLabel.setPosition(400 - musicLabel.getWidth()/2,600);
        soundLabel.setPosition(1080 - 400 - soundLabel.getWidth()/2, 600);
        progressBar.setSize(700,30);
        progressBar.setPosition(540 - progressBar.getWidth()/2, 1700);
        progressLabel.setPosition(540 - progressLabel.getWidth()/2, 1760);
    }


    private void fadeIn(){
        Array<Actor> actorList = stage.getActors();
        Tween.registerAccessor(Actor.class,new ActorAccessor());
        for (Actor a:actorList){
            Tween.set(a, ActorAccessor.ALPHA).target(0).start(tm);
            Tween.to(a, ActorAccessor.ALPHA, fadeInTime).target(1).start(tm);
        }
        tm.update(Float.MIN_VALUE);
    }

    private void fadeOut(){
        Array<Actor> actorList = stage.getActors();
        Tween.registerAccessor(Actor.class,new ActorAccessor());
        for (Actor a:actorList){
            Tween.set(a, ActorAccessor.ALPHA).target(1).start(tm);
            Tween.to(a, ActorAccessor.ALPHA, fadeOutTime).target(0).start(tm);
        }
        tm.update(Float.MIN_VALUE);
    }

    private void setButtonListeners() {
        playButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							((Game)Gdx.app.getApplicationListener()).setScreen(new SelectScreen());
                    }
                }, fadeOutTime);
                return true;
            }
        });

        resumeButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                final int size = prefs.getInteger("size");
                final int level = prefs.getInteger("level");
                fadeOut();
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(size,level,true));
                    }
                }, fadeOutTime);
                return true;
            }
        });

        aboutButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new AboutScreen());
                    }
                }, fadeOutTime);
                return true;
            }
        });


        moreGamesButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        Gdx.net.openURI("https://play.google.com/store/apps/developer?id=blipthirteen");
                    }
                }, fadeOutTime);
                return true;
            }
        });

        soundButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String buttonText = soundButton.getText().toString();
                if(buttonText.equals("Enabled")){
                    soundButton.setText("Disabled");
                }else if(buttonText.equals("Disabled")){
                    soundButton.setText("Enabled");
                }
                boolean b = prefs.getBoolean("sound");
                prefs.putBoolean("sound",!b);
                prefs.flush();
                return true;
            }
        });

        musicButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String buttonText = musicButton.getText().toString();
                if(buttonText.equals("Enabled")){
                    musicButton.setText("Disabled");
                }else if(buttonText.equals("Disabled")){
                    musicButton.setText("Enabled");
                }
                boolean b = prefs.getBoolean("music");
                prefs.putBoolean("music",!b);
                prefs.flush();
                return true;

            }
        });
    }
}