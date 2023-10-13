package team.unnamed.hephaestus.minestomce.skin;

import team.unnamed.hephaestus.playermodel.Skin;

public interface SkinProvider {

    Skin fetchSkin(String username) throws Exception;

}