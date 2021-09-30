package team.unnamed.hephaestus.model.adapt;

import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

public interface AdaptionModule {

    ModelViewRenderer createRenderer(ModelViewAnimator animator);

}
