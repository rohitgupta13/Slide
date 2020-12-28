package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blipthirteen.slide.Constants;
import com.blipthirteen.slide.tween.ActorAccessor;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by rohit on 27-12-2016.
 */

public class SelectScreen implements Screen {

    Preferences prefs;
    Stage stage;
    Skin uiSkin;

    Table scrollTable;
    ScrollPane scroller;

    Window window;

    TextButton selectTb3;
    TextButton selectTb4;
    TextButton selectTb5;
    TextButton selectTb6;
    TextButton selectTb8;
    TextButton backTb;
    TextButton continueTb;

    Image lockIcon;
    int textButtonSize;
    int lockIconHeight;
    int lockIconWidth;

    Label label3;
    Label label4;
    Label label5;
    Label label6;
    Label label8;

    SpriteBatch batch;
    ArrayList<Label> labelList;

    BitmapFont labelFont;
    BitmapFont smallFont;
    BitmapFont mediumFont;
    BitmapFont verySmallFont;

    FitViewport fitViewport;

    TweenManager tm;

    float fadeInTime, fadeOutTime;

    private void generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        labelFont = generator.generateFont(parameter);
        labelFont.setColor(Color.BLACK);

        // Medium font for normal buttons
        parameter.size = 60;
        mediumFont = generator.generateFont(parameter);
        mediumFont.setColor(Color.BLACK);

        // Small font for some buttons
        parameter.size = 48;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.BLACK);

        // Very small font for window
        parameter.size = 38;
        verySmallFont = generator.generateFont(parameter);
        verySmallFont.setColor(Color.BLACK);
        generator.dispose();
    }

    private void init(){
        tm = new TweenManager();
        Gdx.input.setCatchBackKey(true);
        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        Label.LabelStyle labelStyle = new Label.LabelStyle(labelFont, Color.BLACK);
        Label label = new Label("Select a Level",labelStyle);

        Label.LabelStyle labelStyleSmall = new Label.LabelStyle(smallFont, Color.BLACK);

        label3 = new Label("0/10",labelStyleSmall);
        label4 = new Label("0/10",labelStyleSmall);
        label5 = new Label("0/10",labelStyleSmall);
        label6 = new Label("0/10",labelStyleSmall);
        label8 = new Label("0/10",labelStyleSmall);
        labelList = new ArrayList<Label>();
        labelList.add(label3);
        labelList.add(label4);
        labelList.add(label5);
        labelList.add(label6);
        labelList.add(label8);

        checkForCompletion();
        batch = new SpriteBatch();
        fitViewport = new FitViewport(1080,1920);
        stage = new Stage();
        stage.setViewport(fitViewport);
        Gdx.input.setInputProcessor(stage);

        uiSkin = new Skin();
        uiSkin.addRegions(new TextureAtlas(Gdx.files.internal("skin/plain-james-ui.atlas")));
        uiSkin.add("small-font", verySmallFont);
        uiSkin.add("title", smallFont); // smaller font
        uiSkin.add("font", mediumFont); // normal font

        uiSkin.load(Gdx.files.internal("skin/plain-james-ui.json"));
        Gdx.input.setInputProcessor(stage);

        backTb = new TextButton("Back", uiSkin.get("special", TextButton.TextButtonStyle.class));

        lockIconHeight = 60;
        lockIconWidth = 37;
        Texture lockIconTexture = new Texture("select_screen/lock.png");

        textButtonSize = 420;
        selectTb3 = new TextButton("3x3\nChild's Play", uiSkin);
        selectTb4 = new TextButton("4x4\nBasic", uiSkin);
        selectTb5 = new TextButton("5x5\nMediocre", uiSkin);
        selectTb6 = new TextButton("6x6\nChallenging", uiSkin);
        selectTb8 = new TextButton("8x8\nExtreme", uiSkin);

        scrollTable = new Table();
        scrollTable.add(label).padTop(150);
        scrollTable.row();
        scrollTable.add(selectTb3).padTop(130).width(textButtonSize).height(textButtonSize);
        scrollTable.row();
        scrollTable.add(label3).padTop(30);
        scrollTable.row();
        scrollTable.add(selectTb4).padTop(130).width(textButtonSize).height(textButtonSize);
        scrollTable.row();
        scrollTable.add(label4).padTop(30);
        scrollTable.row();
        if(prefs.getBoolean("level_1_locked"))
            scrollTable.add(new Image(lockIconTexture)).width(lockIconWidth).height(lockIconHeight);
        scrollTable.row();
        scrollTable.add(selectTb5).padTop(130).width(textButtonSize).height(textButtonSize);
        scrollTable.row();
        scrollTable.add(label5).padTop(30);
        scrollTable.row();
        if(prefs.getBoolean("level_2_locked"))
            scrollTable.add(new Image(lockIconTexture)).width(lockIconWidth).height(lockIconHeight);
        scrollTable.row();
        scrollTable.add(selectTb6).padTop(130).width(textButtonSize).height(textButtonSize);
        scrollTable.row();
        scrollTable.add(label6).padTop(30);
        scrollTable.row();
        if(prefs.getBoolean("level_3_locked"))
            scrollTable.add(new Image(lockIconTexture)).width(lockIconWidth).height(lockIconHeight);
        scrollTable.row();
        scrollTable.add(selectTb8).padTop(130).width(textButtonSize).height(textButtonSize);
        scrollTable.row();
        scrollTable.add(label8).pad(30);
        if(prefs.getBoolean("level_4_locked")){
            scrollTable.row();
            scrollTable.add(new Image(lockIconTexture)).width(lockIconWidth).height(lockIconHeight);
        }
        scrollTable.padBottom(150);

        scroller = new ScrollPane(scrollTable);

        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();

        window = new Window("",uiSkin);
        window.setMovable(false);
        window.row();

        window.add("Complete 5 puzzles\nfrom the preceding\ndifficulty level\nto unlock", "small-font", Color.BLACK);
        window.setWidth(700);
        window.setHeight(370);
        window.setPosition(1080/2 - window.getWidth()/2,1920/2 - window.getHeight()/2);

        continueTb = new TextButton("Continue", uiSkin.get("special", TextButton.TextButtonStyle.class));
        window.row();
        window.add(continueTb).padTop(40);
        window.setVisible(false);
        setButtonListeners();
        stage.addActor(table);
        stage.addActor(window);

        backTb.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, 0.8f);
                return true;
            }
        });
        backTb.setPosition(915 - backTb.getWidth()/2,130 - backTb.getHeight()/2);
        stage.addActor(backTb);
    }

    public void show() {
        fadeInTime = 0.5f;
        fadeOutTime = 0.5f;
        generateFont();
        init();
        fadeIn();
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

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)230 /255,(float)230/255,(float)230/255,1);

        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();

        batch.end();

        stage.act();
        stage.draw();
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
        }
        tm.update(delta);
    }

    public void resize(int width, int height) {
        fitViewport.update(width,height,true);
        stage.getViewport().update(width, height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose(){
        uiSkin.dispose();
        stage.dispose();
        batch.dispose();
        labelFont.dispose();
        mediumFont.dispose();
        smallFont.dispose();
        verySmallFont.dispose();
    }

    private void setButtonListeners() {

        selectTb3.addListener(new ActorGestureListener() {
            public void touchUp (InputEvent event, float x, float y, int count, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(3));
                    }
                }, fadeOutTime);
            }
        });

        selectTb4.addListener(new ActorGestureListener() {
            public void touchUp (InputEvent event, float x, float y, int count, int button) {
                if (prefs.getBoolean("level_1_locked")) {
                    lock();
                } else {
                    fadeOut();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(4));
                        }
                    }, fadeOutTime);
                }
            }
        });

        selectTb5.addListener(new ActorGestureListener() {
            public void touchUp (InputEvent event, float x, float y, int count, int button) {
                if (prefs.getBoolean("level_2_locked")) {
                    lock();
                } else {
                    fadeOut();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(5));
                        }
                    }, fadeOutTime);
                }
            }
        });

        selectTb6.addListener(new ActorGestureListener() {
            public void touchUp (InputEvent event, float x, float y, int count, int button) {
                if (prefs.getBoolean("level_3_locked")) {
                    lock();
                } else {
                    fadeOut();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(6));
                        }
                    }, fadeOutTime);
                }
            }
        });

        selectTb8.addListener(new ActorGestureListener() {
            public void touchUp (InputEvent event, float x, float y, int count, int button) {
                if (prefs.getBoolean("level_4_locked")) {
                    lock();
                } else {
                    fadeOut();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(8));
                        }
                    }, fadeOutTime);
                }
            }
        });

        continueTb.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        unlock();
                    }
                }, 0.25f);

                return true;
            }
        });
    }

    private void windowFadeIn(){
        Tween.registerAccessor(Actor.class,new ActorAccessor());
        Tween.set(window, ActorAccessor.ALPHA).target(0).start(tm);
        Tween.to(window, ActorAccessor.ALPHA, 1).target(1).start(tm);
        tm.update(Float.MIN_VALUE);
    }

    private void windowFadeOut(){
        Tween.registerAccessor(Actor.class,new ActorAccessor());
        Tween.set(window, ActorAccessor.ALPHA).target(1).start(tm);
        Tween.to(window, ActorAccessor.ALPHA, 1).target(0).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                window.setVisible(false);
            }
        }).start(tm);
        tm.update(Float.MIN_VALUE);
    }

    private void lock(){
        window.setVisible(true);
        backTb.setTouchable(Touchable.disabled);
        scroller.setTouchable(Touchable.disabled);
        windowFadeIn();
    }
    private void unlock(){
        backTb.setTouchable(Touchable.enabled);
        scroller.setTouchable(Touchable.enabled);
        windowFadeOut();
    }
    private void checkForCompletion(){
        int completed = 0;
        for(int i = 0; i < 5; i++){
            completed = 0;
            for (int j = 0; j < 10; j++)
            {
                String key = "level_" + i + "_image_" + j;
                String score = prefs.getString(key,"NULL");
                System.out.println("Key :" + key +" Value :" + score);
                if(!score.equals("NULL")){
                    completed++;
                }
            }
            prefs.putInteger("level_" + i + "_completed",completed);
            Label label = labelList.get(i);
            label.setText(completed + "/10");
            if(completed >= 5 && i != 4){
                System.out.println("level_" + (i+1) + "_locked : false");
                prefs.putBoolean("level_" + (i+1) + "_locked" , false);
                prefs.flush();
            }
        }
    }
}