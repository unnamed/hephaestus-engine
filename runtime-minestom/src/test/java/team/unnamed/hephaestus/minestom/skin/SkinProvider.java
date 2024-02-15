package team.unnamed.hephaestus.minestom.skin;

import team.unnamed.hephaestus.player.Skin;

public interface SkinProvider {

    Skin fetchSkin(String username) throws Exception;

}