package com.blipthirteen.slide.tween;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import aurelienribon.tweenengine.TweenAccessor;

public class TextButtonAccessor implements TweenAccessor<TextButton> {

	public static final int ALPHA = 0;
	public static final int MOVE_X = 1;
	public static final int MOVE_Y = 2;
	
	@Override
	public int getValues(TextButton target, int tweenType, float[] returnValues) {
		switch(tweenType) {
		case ALPHA:
			returnValues[0] = target.getColor().a;
			return 1;
		case MOVE_X:
			returnValues[0] = target.getX();
            return 1;
		case MOVE_Y:
			returnValues[0] = target.getY();
            return 1;
		
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(TextButton target, int tweenType, float[] newValues) {
		switch(tweenType) {
		case ALPHA:
			target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
			break;
		case MOVE_X:
            target.setX( + newValues[0]);
            break;
		case MOVE_Y:
            target.setY( + newValues[0]);
            break;    
		default:
			assert false;
		}
	}
}