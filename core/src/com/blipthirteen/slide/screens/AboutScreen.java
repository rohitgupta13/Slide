package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blipthirteen.slide.tween.ActorAccessor;
import com.blipthirteen.slide.tween.LabelAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by HP on 7/9/2017.
 */

public class AboutScreen implements Screen {

    SpriteBatch batch;
    FitViewport fitViewport;
    TweenManager tm;
    Label bt;
    BitmapFont largeFont, smallFont;
    Stage stage;
    TextButton backTextButton;
    Skin uiSkin;

    float fadeOutTime, fadeInTime;

    private void generateFont(){
        largeFont = new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 54;
        largeFont = generator.generateFont(parameter);
        parameter.size = 48;
        smallFont = generator.generateFont(parameter);
        generator.dispose();
    }

    public void show() {
        fadeInTime = 0.5f;
        fadeOutTime = 0.5f;

        generateFont();
        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);

        uiSkin = new Skin();
        uiSkin.addRegions(new TextureAtlas(Gdx.files.internal("skin/plain-james-ui.atlas")));
        uiSkin.add("title", smallFont); // smaller font
        uiSkin.add("font", largeFont); // normal font

        uiSkin.load(Gdx.files.internal("skin/plain-james-ui.json"));

        backTextButton = new TextButton("Back", uiSkin.get("special", TextButton.TextButtonStyle.class));
        backTextButton.setPosition(915 - backTextButton.getWidth()/2,130 - backTextButton.getHeight()/2);
        Gdx.input.setInputProcessor(stage);

        final Label.LabelStyle btLabelStyle = new Label.LabelStyle(largeFont, Color.BLACK);
        bt = new Label("Images from pixabay.com\n\n" +
                "Music from audionautix.com\n\n" +
                "Plain James UI skin by:\n" +
                "Raymond \"Raeleus\" Buckley\n\n" +
                "Powered by libGDX" +
                "", btLabelStyle);
        bt.setAlignment(Align.center);
        bt.setPosition(540 - bt.getWidth()/2, 1920/2 - bt.getHeight()/2);
        bt.setVisible(true);
        stage.addActor(backTextButton);
        stage.addActor(bt);


        batch = new SpriteBatch();

        tm = new TweenManager();
        fadeIn();

        backTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, fadeOutTime);

                return true;
            }
        });
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
        Gdx.gl.glClearColor((float)230/255,(float)230/255,(float)230/255,1);
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();

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