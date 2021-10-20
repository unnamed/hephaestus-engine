package team.unnamed.hephaestus.model.adapt.v1_16_R3;

import team.unnamed.hephaestus.model.adapt.AdaptionModule;
import team.unnamed.hephaestus.view.ModelViewRenderer;

@SuppressWarnings("unused") // most times instantiated via reflection
public class AdaptionModule_v1_16_R3 implements AdaptionModule {

    @Override
    public ModelViewRenderer createRenderer() {
        return new ModelViewRenderer_v1_16_R3();
    }
}