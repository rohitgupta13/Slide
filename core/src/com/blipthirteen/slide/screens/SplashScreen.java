package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blipthirteen.slide.tween.SpriteAccessor;


/**
 * Created by rohit on 25-12-2016.
 */

public class SplashScreen implements Screen {

    SpriteBatch batch;
    Sprite splashsprite;
    Sprite logosprite;
    FitViewport fitViewport;
    TweenManager tm;
    Preferences prefs;

    public void show() {
        // Game settings
        // Create new if does not exist
        prefs = Gdx.app.getPreferences("com.blipthirteen.slide");
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
                    String key = "level_" + i + "_image_" + j;
                    prefs.putString(key,"NULL");
                }
            }
            prefs.flush();
        }

        Gdx.app.log("default", "" + prefs.getString("default"));

        fitViewport = new FitViewport(1080, 1920);
        batch = new SpriteBatch();
        splashsprite = new Sprite(new Texture("misc/splash.png"));
        splashsprite.setPosition(1080/2 - splashsprite.getWidth()/2, 1920/2 - splashsprite.getHeight()/2);
        logosprite = new Sprite(new Texture("misc/blipthirteen.png"));
        logosprite.setPosition(1080/2 - logosprite.getWidth()/2, 1920/2 - logosprite.getHeight()/2);

        tm = new TweenManager();

        Tween.registerAccessor(Sprite.class,new SpriteAccessor());
        Tween.set(splashsprite, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(splashsprite, SpriteAccessor.ALPHA, 1f).target(1).repeatYoyo(1, .5f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        }).start(tm);

        Tween.set(logosprite, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(logosprite, SpriteAccessor.ALPHA, 1f).target(1).repeatYoyo(1, .5f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
            }
        }).start(tm);
        tm.update(Float.MIN_VALUE); // update once avoid short flash of splash before animation
    }

    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)54/255,(float)69/255,(float)79/255,1);
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        splashsprite.draw(batch);
        logosprite.draw(batch);
        batch.end();

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
        splashsprite.getTexture().dispose();
        logosprite.getTexture().dispose();
        batch.dispose();
    }
}