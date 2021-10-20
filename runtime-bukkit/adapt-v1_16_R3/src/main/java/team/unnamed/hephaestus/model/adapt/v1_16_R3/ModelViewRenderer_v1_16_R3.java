package team.unnamed.hephaestus.model.adapt.v1_16_R3;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.view.BukkitModelView;
import team.unnamed.hephaestus.view.ModelViewController;
import team.unnamed.hephaestus.view.ModelViewRenderer;

import java.util.Collection;

public class ModelViewRenderer_v1_16_R3 implements ModelViewRenderer {

    private final ModelViewController controller;

    public ModelViewRenderer_v1_16_R3() {
        this.controller = new ModelViewController_v1_16_R3();
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