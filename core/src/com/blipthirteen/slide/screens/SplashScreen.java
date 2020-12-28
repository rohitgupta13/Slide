package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blipthirteen.slide.Constants;
import com.blipthirteen.slide.tween.LabelAccessor;


/**
 * Created by rohit on 25-12-2016.
 */

public class SplashScreen implements Screen {

    SpriteBatch batch;
    FitViewport fitViewport;
    TweenManager tm;
    Preferences prefs;
    Label bt;
    BitmapFont largeFont;
    Stage stage;

    private void initPreferences(){
        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        if(!prefs.contains("default")){
            prefs.putString("default","prefs");
            prefs.putBoolean("music",true);
            prefs.putBoolean("sound",true);
            prefs.putBoolean("level_1_locked",true);
            prefs.putBoolean("level_2_locked",true);
            prefs.putBoolean("level_3_locked",true);
            prefs.putBoolean("level_4_locked",true);
            for(int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 10; j++)
                {
                    String key1 = "level_" + i + "_image_" + j;
                    prefs.putString(key1,"NULL");
                    String key2 = "level_" + i + "_image_time" + j;
                    prefs.putInteger(key2,0);
                }
            }
            prefs.flush();
        }
    }

    private void generateFont(){
        largeFont = new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 120;
        largeFont = generator.generateFont(parameter);
        generator.dispose();
    }

    public void show() {
        // Game settings, create new if does not exist
        initPreferences();
        generateFont();
        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);

        final Label.LabelStyle btLabelStyle = new Label.LabelStyle(largeFont, Color.BLACK);
        bt = new Label("BlipThirteen", btLabelStyle);
        bt.setPosition(540 - bt.getWidth()/2, 1920/2 - bt.getHeight()/2);
        bt.setVisible(false);
        stage.addActor(bt);

        batch = new SpriteBatch();

        tm = new TweenManager();
        Tween.registerAccessor(Label.class,new LabelAccessor());
        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                bt.setVisible(true);
                tween();
            }
        }, 0.5f); // update once avoid short flash of splash before animation
    }

    private void tween(){
        Tween.set(bt, LabelAccessor.ALPHA).target(0).start(tm);
        Tween.to(bt, LabelAccessor.ALPHA, 1f).target(1).repeatYoyo(1, .5f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        }).start(tm);

        tm.update(Float.MIN_VALUE);
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)230/255,(float)230/255,(float)230/255,1);
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        // Pass
        batch.end();

        stage.act();
        stage.draw();
        tm.update(delta);
    }

    public void resize(int width, int height) {
        fitViewport.update(width,height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        largeFont.dispose();
        batch.dispose();
    }
}