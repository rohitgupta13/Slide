package com.blipthirteen.slide.tween;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by HP on 6/30/2017.
 */

public class LabelAccessor implements TweenAccessor<Label> {

    public static final int ALPHA = 0;

    @Override
    public int getValues(Label target, int tweenType, float[] returnValues) {
        switch(tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Label target, int tweenType, float[] newValues) {
        switch(tweenType) {
            case ALPHA:
                target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
                break;
            default:
                assert false;
        }
    }
}