package team.unnamed.hephaestus.util;

import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;

public final class KeyFrames {

    private KeyFrames() {
    }

    public static KeyFrame getNext(float tick, List<KeyFrame> frames) {
        KeyFrame selectedFrame = null;
        for (KeyFrame frame : frames) {
            if (frame.getPosition() > tick){
                if (selectedFrame == null) {
                    selectedFrame = frame;
                } else if (frame.getPosition() < selectedFrame.getPosition()) {
                    selectedFrame = frame;
                }
            }
        }

        return selectedFrame;
    }

    public static KeyFrame getPrevious(float tick, List<KeyFrame> frames) {
        return getPrevious(tick, frames, Vector3Float.ZERO);
    }

    public static KeyFrame getPrevious(float tick, List<KeyFrame> frames, Vector3Float def) {
        KeyFrame selectedFrame = null;
        for (KeyFrame frame : frames) {
            if (frame.getPosition() <= tick) {
                if (selectedFrame == null) {
                    selectedFrame = frame;
                } else if (frame.getPosition() > selectedFrame.getPosition()) {
                    selectedFrame = frame;
                }
            }
        }

        if (selectedFrame == null) {
            return new KeyFrame(0, def);
        }

        return selectedFrame;
    }

}
