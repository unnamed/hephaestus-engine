package team.unnamed.hephaestus.model.adapt.v1_15_R1;

import team.unnamed.hephaestus.adapt.AdaptionModule;
import team.unnamed.hephaestus.view.ModelViewRenderer;

@SuppressWarnings("unused") // most times instantiated via reflection
public class AdaptionModule_v1_15_R1 implements AdaptionModule {

    @Override
    public ModelViewRenderer createRenderer() {
        return new ModelViewRenderer_v1_15_R1();
    }
}