package team.unnamed.hephaestus.adapt.v1_16_R3;

import team.unnamed.hephaestus.adapt.AdaptionModule;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

@SuppressWarnings("unused") // most times instantiated via reflection
public class AdaptionModule_v1_16_R3 implements AdaptionModule {

    @Override
    public ModelViewRenderer createRenderer(ModelViewAnimator animator) {
        return new ModelViewRenderer_v1_16_R3(animator);
    }
}