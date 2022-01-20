/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.minestom;

import net.minestom.server.color.Color;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import team.unnamed.hephaestus.Bone;

public class BoneEntity extends LivingEntity {

    private static final ItemStack BASE_HELMET =
            ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
                    .meta(new LeatherArmorMeta.Builder()
                            .color(new Color(0xFFFFFF))
                            .build())
                    .build();

    private final MinestomModelView view;
    private final Bone bone;

    public BoneEntity(
            MinestomModelView view,
            Bone bone
    ) {
        super(EntityType.ARMOR_STAND);
        this.view = view;
        this.bone = bone;
        initialize();
    }

    private void initialize() {
        ArmorStandMeta meta = (ArmorStandMeta) getEntityMeta();
        meta.setSilent(true);
        meta.setHasNoGravity(true);
        meta.setSmall(bone.small());
        meta.setInvisible(true);

        // set helmet with custom model data from our bone
        setHelmet(BASE_HELMET.withMeta(itemMeta ->
                itemMeta.customModelData(bone.customModelData())));
    }

    public MinestomModelView view() {
        return view;
    }

    public Bone bone() {
        return bone;
    }

    public void colorize(Color color) {
        setHelmet(getHelmet().withMeta((LeatherArmorMeta.Builder meta) -> meta.color(color)));
    }

}
