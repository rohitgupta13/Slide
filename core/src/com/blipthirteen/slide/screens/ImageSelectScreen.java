package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by rohit on 29-12-2016.
 */

public class ImageSelectScreen implements Screen {

    // Scene 2D
    private Stage stage;
    private Skin skin;
    private TextureAtlas buttonatlas;
    private BitmapFont font;
    private BitmapFont smallFont;
    private int size;
    SpriteBatch batch;
    Sprite splashsprite;
    FitViewport fitViewport;
    Preferences prefs;

    int level;




    public ImageSelectScreen(int size){
        this.size = size;
    }

    private void generateFont() {
        font= new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Baron_Neue.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        font = generator.generateFont(parameter);
        font.setColor(Color.WHITE);

        parameter.size = 50;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.WHITE);
        generator.dispose();
    }

    private void init(){
        Gdx.input.setCatchBackKey(true);
        skin = new Skin();
        prefs = Gdx.app.getPreferences("com.blipthirteen.slide");
        batch = new SpriteBatch();
        splashsprite = new Sprite(new Texture("misc/splash.png"));
        splashsprite.setPosition(1080/2 - splashsprite.getWidth()/2, 1920/2 - splashsprite.getHeight()/2);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle labelStyleSmall = new Label.LabelStyle(smallFont, Color.WHITE);
        Label label = new Label("Select an Image",labelStyle);

        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);
        Gdx.input.setInputProcessor(stage);

        final Table scrollTable = new Table();
        scrollTable.add(label).padTop(250);
        scrollTable.row();

        Texture t2 = new Texture("image_select_screen/frame.png");

        for(int i =0 ; i< 10; i++){
            final int k = i;
            String fileName = "thumbnails/image_" + size + "_" + i + ".jpg";
            Texture t1 = new Texture(fileName);
            ImageButton imageButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(t1)));
            Drawable drawable = new TextureRegionDrawable(new TextureRegion(t2));
            ImageButton.ImageButtonStyle butttonStyle = new ImageButton.ImageButtonStyle();
            butttonStyle.up = skin.newDrawable(drawable);
            butttonStyle.down = skin.newDrawable(drawable,Color.LIGHT_GRAY);
            ImageButton ib = new ImageButton(butttonStyle);
            Stack stack = new Stack();
            stack.add(imageButton);
            stack.add(ib);
            scrollTable.add(stack).padTop(150);
            scrollTable.row();
            ib.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(size, k));
                        }
                    }, 0.25f);

                    return true;
                }
            });
            String key = "level_" + level + "_image_" + i;
            String s = prefs.getString(key);
            Label l = null;
            if(s.equals("NULL")){
                l = new Label("high score: none",labelStyleSmall);
            }else{
                l = new Label("high score: " + s,labelStyleSmall);
            }
            scrollTable.add(l).padTop(20);
            scrollTable.row();
        }
        scrollTable.add().padBottom(100);

        final ScrollPane scroller = new ScrollPane(scrollTable);
        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();
        stage.addActor(table);

        Drawable d = new TextureRegionDrawable(new TextureRegion(new Texture("image_select_screen/back.png")));
        ImageButton.ImageButtonStyle backButttonStyle = new ImageButton.ImageButtonStyle();
        backButttonStyle.up = skin.newDrawable(d);
        backButttonStyle.down = skin.newDrawable(d,Color.LIGHT_GRAY);
        ImageButton backButton = new ImageButton(backButttonStyle);
        backButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new SelectScreen());
                    }
                }, 0.25f);

                return true;
            }
        });
        backButton.setPosition(900,180);
        stage.addActor(backButton);
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
        generateFont();
        init();
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)54/255,(float)69/255,(float)79/255,1);
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply();
        batch.begin();
        splashsprite.draw(batch);
        batch.end();

        stage.act();
        stage.draw();
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            ((Game) Gdx.app.getApplicationListener()).setScreen(new SelectScreen());
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
        skin.dispose();
        buttonatlas.dispose();
        font.dispose();
        smallFont.dispose();
        batch.dispose();
        splashsprite.getTexture().dispose();
    }
}
