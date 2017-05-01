package com.blipthirteen.slide.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by rohit on 31-12-2016.
 */
// This screen is for demonstrating the use of bleeding.

public class LayoutDemo implements Screen {

    FitViewport fitViewport;
    StretchViewport stretchViewport;
//    OrthographicCamera camera;
    SpriteBatch batch;
    Texture image;
    Texture background;

    @Override
    public void show() {
        //layout_demo_assets
        batch = new SpriteBatch();
        image = new Texture("layout_demo_assets/image_3_0.jpg");
        background = new Texture("layout_demo_assets/image_3_1.jpg");
//        camera = new OrthographicCamera(1080,1920);

        fitViewport = new FitViewport(1080,1920);
        stretchViewport = new StretchViewport(1080,1920);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        batch.begin();
//        stretchViewport.apply(true);
//        batch.setProjectionMatrix(stretchViewport.getCamera().combined);
//        stretchViewport.getCamera().update();
//        batch.draw(background,0,0,1080,1920);
//
//        fitViewport.apply(true);
//        batch.setProjectionMatrix(fitViewport.getCamera().combined);
//        fitViewport.getCamera().update();
//        batch.draw(image,60,0);
//        batch.end();

        batch.setProjectionMatrix(stretchViewport.getCamera().combined);

        stretchViewport.apply(true);
        batch.begin();
        batch.draw(background,0,0,1080,1920);
        batch.end();

        batch.setProjectionMatrix(fitViewport.getCamera().combined);

        fitViewport.apply(true);
        batch.begin();
        batch.draw(image,60,0);
        batch.end();


    }


    @Override
    public void resize(int width, int height) {
        fitViewport.update(width,height,true);
        stretchViewport.update(width,height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
