package team.unnamed.hephaestus.model.adapt.v1_14_R1;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.view.BukkitModelView;
import team.unnamed.hephaestus.model.view.ModelViewController;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

import java.util.Collection;

public class ModelViewRenderer_v1_14_R1 implements ModelViewRenderer {

    private final ModelViewController controller;

    public ModelViewRenderer_v1_14_R1() {
        this.controller = new ModelViewController_v1_14_R1();
    }

    @Override
    public BukkitModelView render(
            Model model,
            Location location,
            Collection<Player> viewers
    ) {
        BukkitModelView view = new BukkitModelView(
                controller,
                model,
                viewers,
                location
        );
        view.show();
        return view;
    }
}