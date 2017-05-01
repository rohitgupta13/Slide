package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Timer;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blipthirteen.slide.tween.ImageAccessor;
import com.blipthirteen.slide.tween.ImageButtonAccessor;

/**
 * Created by rohit on 25-12-2016.
 */

public class MenuScreen implements Screen{

    // Buttons
    ImageButton play;
    ImageButton music, sound;
    ImageButton.ImageButtonStyle musicButtonStyle, soundButtonStyle;

    // Scene 2D
    Stage stage;
    Skin skin;
    TextureAtlas buttonatlas;
    Image image;

    // Tween
    TweenManager tm;

    SpriteBatch batch;
    Sprite splashsprite;
    FitViewport fitViewport;
    Timer t;
    Preferences prefs;

    public void show() {
        Gdx.input.setCatchBackKey(true);

        batch = new SpriteBatch();
        // Is named splash beacuse its copied from splash screen
        // Should be renamed to something else
        splashsprite = new Sprite(new Texture("misc/splash.png"));
        splashsprite.setPosition(1080/2 - splashsprite.getWidth()/2, 1920/2 - splashsprite.getHeight()/2);

        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);

        prefs = Gdx.app.getPreferences("com.blipthirteen.slide");

        skin= new Skin();
        buttonatlas = new TextureAtlas("menu_screen/slide_menu.pack");
        skin.addRegions(buttonatlas);

        play = new ImageButton(skin.getDrawable("play_up"),skin.getDrawable("play_down"));

        musicButtonStyle = new ImageButton.ImageButtonStyle();
        musicButtonStyle.up = skin.getDrawable("music_on");
        musicButtonStyle.checked = skin.getDrawable("music_off");
        music = new ImageButton(musicButtonStyle);

        soundButtonStyle = new ImageButton.ImageButtonStyle();
        soundButtonStyle.up = skin.getDrawable("sound_on");
        soundButtonStyle.checked = skin.getDrawable("sound_off");
        sound = new ImageButton(soundButtonStyle);

        music.setChecked(!prefs.getBoolean("music"));
        sound.setChecked(!prefs.getBoolean("sound"));
        image = new Image(skin.getDrawable("slide"));

        setButtonPostion();
        Gdx.input.setInputProcessor(stage);

        stage.addActor(image);
        stage.addActor(play);
        stage.addActor(music);
        stage.addActor(sound);


        t = new Timer();
        t.scheduleTask(new Task() {
            @Override
            public void run() {
                setButtonListeners();
            }
        }, 0.25f);

        tm = new TweenManager();
        Tween.registerAccessor(ImageButton.class,new ImageButtonAccessor());

        Tween.set(play, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(play, ImageButtonAccessor.ALPHA, 0.85f).target(1).start(tm);

        Tween.set(music, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(music, ImageButtonAccessor.ALPHA, 0.85f).target(1).start(tm);

        Tween.set(sound, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(sound, ImageButtonAccessor.ALPHA, 0.85f).target(1).start(tm);

        Tween.registerAccessor(Image.class,new ImageAccessor());
        Tween.set(image, ImageAccessor.ALPHA).target(0).start(tm);
        Tween.to(image, ImageAccessor.ALPHA, 0.85f).target(1).start(tm);

        tm.update(Float.MIN_VALUE); // update once avoid short flash of splash before animation
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)54/255,(float)69/255,(float)79/255,1);

        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        splashsprite.draw(batch);
        batch.end();

        stage.act();
        stage.draw();

        tm.update(delta); //Tween

        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            Gdx.app.exit();
        }
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

    public void dispose() {
        stage.dispose();
        splashsprite.getTexture().dispose();
        batch.dispose();
        skin.dispose();
        buttonatlas.dispose();
    }

    private void setButtonPostion() {
        image.setPosition(1080/2 - image.getWidth()/2, 1920/2 + 550 - image.getHeight()/2);
        play.setPosition(1080/2 - play.getWidth()/2,1920/2 - play.getHeight()/2);
        sound.setPosition(150 - sound.getWidth()/2, 150);
        music.setPosition(1080 - 150 - music.getWidth()/2, 150);
    }

    private void setButtonListeners() {
        play.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							((Game)Gdx.app.getApplicationListener()).setScreen(new SelectScreen());
                    }
                }, 0.10f);

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
    }
}