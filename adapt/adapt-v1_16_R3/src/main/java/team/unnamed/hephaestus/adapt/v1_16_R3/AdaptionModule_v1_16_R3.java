package team.unnamed.hephaestus.adapt.v1_16_R3;

import team.unnamed.hephaestus.adapt.AdaptionModule;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

public class AdaptionModule_v1_16_R3 implements AdaptionModule {

    @Override
    public ModelViewRenderer createRenderer() {
        return new ModelViewRenderer_v1_16_R3();
    }

}
