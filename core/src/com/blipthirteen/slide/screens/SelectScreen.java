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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

/**
 * Created by rohit on 27-12-2016.
 */

public class SelectScreen implements Screen {

    Preferences prefs;
    Stage stage;
    Skin skin;

    Table scrollTable;
    ScrollPane scroller;
    TextureAtlas buttonatlas;

    Window window;
    ImageButton continueButton;

    ImageButton select3;
    ImageButton select4;
    ImageButton select5;
    ImageButton select6;
    ImageButton select8;
    ImageButton backButton;


    Label label3;
    Label label4;
    Label label5;
    Label label6;
    Label label8;


    SpriteBatch batch;
    Sprite splashsprite;
    ArrayList<Label> labelList;

    BitmapFont font;
    BitmapFont smallFont;
    FitViewport fitViewport;


    private void generateFont() {
        font= new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        font = generator.generateFont(parameter);
        font.setColor(Color.WHITE);

        parameter.size = 55;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.WHITE);
        generator.dispose();
    }

    private void init(){
        Gdx.input.setCatchBackKey(true);
        prefs = Gdx.app.getPreferences("com.blipthirteen.slide");
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label("Select a Level",labelStyle);

        Label.LabelStyle labelStyleSmall = new Label.LabelStyle(smallFont, Color.WHITE);


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
        splashsprite = new Sprite(new Texture("misc/splash.png"));
        splashsprite.setPosition(1080/2 - splashsprite.getWidth()/2, 1920/2 - splashsprite.getHeight()/2);

        fitViewport = new FitViewport(1080,1920);
        stage = new Stage();
        stage.setViewport(fitViewport);
        Gdx.input.setInputProcessor(stage);

        skin= new Skin();
        buttonatlas = new TextureAtlas("select_screen/final_select.pack");
        skin.addRegions(buttonatlas);

        Color tintColor = new Color((float)112/255,(float)128/255,(float)144/255,1);

        ImageButton.ImageButtonStyle btnStyle3 = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle btnStyle4 = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle btnStyle5 = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle btnStyle6 = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle btnStyle8 = new ImageButton.ImageButtonStyle();


        btnStyle3.up = skin.getDrawable("3x3");
        btnStyle3.down = skin.newDrawable("3x3", tintColor);
        select3 = new ImageButton(btnStyle3);

        btnStyle4.up = skin.getDrawable("4x4");
        btnStyle4.down = skin.newDrawable("4x4", tintColor);
        select4 = new ImageButton(btnStyle4);

        btnStyle5.up = skin.getDrawable("5x5");
        btnStyle5.down = skin.newDrawable("5x5", tintColor);
        select5 = new ImageButton(btnStyle5);

        btnStyle6.up = skin.getDrawable("6x6");
        btnStyle6.down = skin.newDrawable("6x6", tintColor);
        select6 = new ImageButton(btnStyle6);

        btnStyle8.up = skin.getDrawable("8x8");
        btnStyle8.down = skin.newDrawable("8x8", tintColor);
        select8 = new ImageButton(btnStyle8);



        scrollTable = new Table();
        scrollTable.add(label).padTop(250);
        scrollTable.row();
        scrollTable.add(select3).padTop(130);
        scrollTable.row();
        scrollTable.add(label3).padTop(30);
        scrollTable.row();
        scrollTable.add(select4).padTop(130);
        scrollTable.row();
        scrollTable.add(label4).padTop(30);
        scrollTable.row();
        if(prefs.getBoolean("level_1_locked"))
            scrollTable.add(new Image(skin.getDrawable("lock")));
        scrollTable.row();
        scrollTable.add(select5).padTop(130);
        scrollTable.row();
        scrollTable.add(label5).padTop(30);
        scrollTable.row();
        if(prefs.getBoolean("level_2_locked"))
            scrollTable.add(new Image(skin.getDrawable("lock")));
        scrollTable.row();
        scrollTable.add(select6).padTop(130);
        scrollTable.row();
        scrollTable.add(label6).padTop(30);
        scrollTable.row();
        if(prefs.getBoolean("level_3_locked"))
            scrollTable.add(new Image(skin.getDrawable("lock")));
        scrollTable.row();
        scrollTable.add(select8).padTop(130);
        scrollTable.row();
        scrollTable.add(label8).pad(30);
        scrollTable.row();
        if(prefs.getBoolean("level_4_locked"))
            scrollTable.add(new Image(skin.getDrawable("lock"))).padBottom(100);
        scrollTable.row();

        //final ScrollPane
        scroller = new ScrollPane(scrollTable);

        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();

        // Locked window
        Window.WindowStyle windowStyle = new Window.WindowStyle(font,Color.BLACK,skin.getDrawable("window3"));
        ImageButton.ImageButtonStyle continuebtnStyle = new ImageButton.ImageButtonStyle();

        continuebtnStyle.up = skin.getDrawable("continue4");
        continuebtnStyle.down = skin.newDrawable("continue4",tintColor);
        continueButton = new ImageButton(continuebtnStyle);
        window = new Window("",windowStyle);

        window.setWidth(770);
        window.setHeight(330);
        window.setPosition(1080/2 - window.getWidth()/2,1920/2 - window.getHeight()/2);
        window.addActor(continueButton);
        continueButton.setPosition(window.getWidth()/2 - continueButton.getWidth()/2,0);
        window.setVisible(false);

        setButtonListeners();
        stage.addActor(table);
        stage.addActor(window);

        Drawable d = new TextureRegionDrawable(new TextureRegion(new Texture("select_screen/back.png")));
        ImageButton.ImageButtonStyle backButttonStyle = new ImageButton.ImageButtonStyle();
        backButttonStyle.up = skin.newDrawable(d);
        backButttonStyle.down = skin.newDrawable(d,tintColor);
        backButton = new ImageButton(backButttonStyle);
        backButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, 0.25f);

                return true;
            }
        });
        backButton.setSize(110,110);
        backButton.setPosition(900,180);
        stage.addActor(backButton);



    }

    public void show() {
        generateFont();
        init();
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
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
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

    public void dispose(){
        skin.dispose();
        stage.dispose();
        buttonatlas.dispose();
        batch.dispose();
        splashsprite.getTexture().dispose();
        font.dispose();
        smallFont.dispose();
    }

    private void setButtonListeners() {
        select3.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(3));
                    }
                }, 0.25f);

                return true;
            }
        });
        select4.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if(prefs.getBoolean("level_1_locked")){
                            lock();
                        }
                        else
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(4));
                    }
                }, 0.25f);

                return true;
            }
        });
        select5.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if(prefs.getBoolean("level_2_locked")){
                            lock();
                        }
                        else
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(5));
                    }
                }, 0.25f);

                return true;
            }
        });
        select6.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if(prefs.getBoolean("level_3_locked")){
                            lock();
                        }
                        else
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(6));
                    }
                }, 0.25f);

                return true;
            }
        });
        select8.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if(prefs.getBoolean("level_4_locked")){
                            lock();
                        }
                        else
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new ImageSelectScreen(8));
                    }
                }, 0.25f);

                return true;
            }
        });
        continueButton.addListener(new InputListener() {
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

    private void lock(){
        window.setVisible(true);
        backButton.setTouchable(Touchable.disabled);
        scroller.setTouchable(Touchable.disabled);
    }
    private void unlock(){
        window.setVisible(false);
        backButton.setTouchable(Touchable.enabled);
        scroller.setTouchable(Touchable.enabled);
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
