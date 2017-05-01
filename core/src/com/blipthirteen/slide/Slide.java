package com.blipthirteen.slide;

import com.badlogic.gdx.Game;
import com.blipthirteen.slide.screens.LayoutDemo;
import com.blipthirteen.slide.screens.SelectScreen;
import com.blipthirteen.slide.screens.SplashScreen;

public class Slide extends Game {

	@Override
	public void create () {
		this.setScreen(new SplashScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	}
}
