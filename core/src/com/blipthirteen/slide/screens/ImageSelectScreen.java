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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blipthirteen.slide.Constants;
import com.blipthirteen.slide.tween.ActorAccessor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by rohit on 29-12-2016.
 */

public class ImageSelectScreen implements Screen {

    // Scene 2D
    private Stage stage;
    private Skin uiSkin,skin;
    private BitmapFont font;
    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private int size;
    SpriteBatch batch;
    FitViewport fitViewport;
    Preferences prefs;

    int level;
    TextButton backTextButton;
    TweenManager tm;
    float fadeInTime, fadeOutTime;

    public ImageSelectScreen(int size){
        this.size = size;
    }

    private void generateFont() {
        font= new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);

        parameter.size = 48;
        mediumFont = generator.generateFont(parameter);
        mediumFont.setColor(Color.BLACK);

        parameter.size = 36;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.BLACK);
        generator.dispose();
    }

    private void init(){
        Gdx.input.setCatchBackKey(true);
        skin = new Skin();
        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        batch = new SpriteBatch();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        Label.LabelStyle labelStyleSmall = new Label.LabelStyle(smallFont, Color.BLACK);
        Label label = new Label("Select an Image",labelStyle);

        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);
        uiSkin = new Skin();
        uiSkin.addRegions(new TextureAtlas(Gdx.files.internal("skin/plain-james-ui.atlas")));
        uiSkin.add("title", mediumFont); // smaller font
        uiSkin.add("font", font); // normal font
        uiSkin.load(Gdx.files.internal("skin/plain-james-ui.json"));

        backTextButton = new TextButton("Back", uiSkin.get("special", TextButton.TextButtonStyle.class));
        backTextButton.setPosition(915 - backTextButton.getWidth()/2,130 - backTextButton.getHeight()/2);

        Gdx.input.setInputProcessor(stage);
        tm = new TweenManager();

        final Table scrollTable = new Table();
        scrollTable.add(label).padTop(150);
        scrollTable.row();

        Texture t2 = new Texture("image_select_screen/frame5.png");

        for(int i =0 ; i< 10; i++){
            final int k = i;
            String fileName = "thumbnails/image_" + size + "_" + i + ".jpg";
            Texture t1 = new Texture(fileName);
            ImageButton imageButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(t1)));
            Drawable drawable = new TextureRegionDrawable(new TextureRegion(t2));
            ImageButton.ImageButtonStyle butttonStyle = new ImageButton.ImageButtonStyle();
            butttonStyle.up = skin.newDrawable(drawable);
            butttonStyle.down = skin.newDrawable(drawable,Color.BLACK);
            ImageButton ib = new ImageButton(butttonStyle);
            Stack stack = new Stack();
            stack.add(imageButton);
            stack.add(ib);
            scrollTable.add(stack).padTop(150);
            scrollTable.row();
            ib.addListener(new ActorGestureListener() {
                    public void touchUp (InputEvent event, float x, float y, int count, int button) {
                        fadeOut();
                           Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                                   ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size, k));
                               }
                           }, fadeOutTime);
                   }
               }
            );

            String key = "level_" + level + "_image_" + i;
            String s = prefs.getString(key);
            String timeKey = "level_" + level + "_image_time" + i;
            int time = prefs.getInteger(timeKey);
            Label l = null; Label tl = null;
            if(s.equals("NULL")){
                l = new Label("best score: none",labelStyleSmall);
            }else{
                l = new Label("best score: " + s,labelStyleSmall);
                String formattedTime = secondsToFormattedTime(time);
                tl = new Label("best time: " + formattedTime ,labelStyleSmall);
            }
            scrollTable.add(l).padTop(20).row();
            scrollTable.add(tl).padTop(20);
            scrollTable.row();
        }
        scrollTable.add().padBottom(100);

        final ScrollPane scroller = new ScrollPane(scrollTable);
        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();
        stage.addActor(table);

        backTextButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new SelectScreen());
                    }
                }, fadeOutTime);

                return true;
            }
        });
        stage.addActor(backTextButton);
    }

    private String secondsToFormattedTime(int seconds){
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


    public void show() {
        switch (size){
            case 3: level = 0;
                break;
            case 4: level = 1;
                break;
            case 5: level = 2;
                break;
            case 6: level = 3;
                break;
            case 8: level = 4;
                break;
        }
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
        Gdx.gl.glClearColor((float)230/255,(float)230/255,(float)230/255,1);
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply();
        batch.begin();
        batch.end();

        stage.act();
        stage.draw();
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            ((Game) Gdx.app.getApplicationListener()).setScreen(new SelectScreen());
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

    public void dispose() {
        stage.dispose();
        skin.dispose();
        font.dispose();
        smallFont.dispose();
        batch.dispose();
    }
}
