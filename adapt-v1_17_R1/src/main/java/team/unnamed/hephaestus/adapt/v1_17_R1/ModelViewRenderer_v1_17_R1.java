package team.unnamed.hephaestus.adapt.v1_17_R1;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.adapt.v1_17_R1.ModelViewController_v1_17_R1;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimationQueue;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewController;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

public class ModelViewRenderer_v1_17_R1 implements ModelViewRenderer {

    private final ModelViewController controller;
    private final ModelViewAnimator animator;

    public ModelViewRenderer_v1_17_R1(ModelViewAnimator animator) {
        this.animator = animator;
        this.controller = new ModelViewController_v1_17_R1();
    }

    @Override
    public ModelView render(Player viewer, Model model, Location location, ModelAnimationQueue animationQueue) {
        ModelView view = new ModelView(controller, animator, animationQueue, model, viewer, location);
        view.show();
        return view;
    }
}