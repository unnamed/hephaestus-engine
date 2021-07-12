package team.unnamed.hephaestus.adapt.v1_16_R3;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewController;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

public class ModelViewRenderer_v1_16_R3
        implements ModelViewRenderer {

    private final ModelViewController controller;
    private final ModelViewAnimator animator;

    public ModelViewRenderer_v1_16_R3(ModelViewAnimator animator) {
        this.controller = new ModelViewController_v1_16_R3();
        this.animator = animator;
    }

    @Override
    public ModelView render(Player viewer, Model model, Location location) {
        ModelView view = new ModelView(controller, animator, model, viewer, location);
        view.show();
        return view;
    }

}
