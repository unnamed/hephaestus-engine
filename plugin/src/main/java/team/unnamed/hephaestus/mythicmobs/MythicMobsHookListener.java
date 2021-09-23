package team.unnamed.hephaestus.mythicmobs;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.hephaestus.ModelRegistry;
import team.unnamed.hephaestus.ModelViewRegistry;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

public class MythicMobsHookListener implements Listener {

    private final ModelRegistry registry;
    private final ModelViewRegistry viewRegistry;
    private final ModelViewRenderer renderer;

    public MythicMobsHookListener(
            ModelRegistry registry,
            ModelViewRegistry viewRegistry,
            ModelViewRenderer renderer
    ) {
        this.registry = registry;
        this.viewRegistry = viewRegistry;
        this.renderer = renderer;
    }

    @EventHandler
    public void onLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("MODEL")) {
            event.register(new ModelMechanic(
                    registry,
                    viewRegistry,
                    event.getConfig(),
                    renderer
            ));
        }
    }

}
