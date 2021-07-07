package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.model.*;
import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.resourcepack.java.*;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static team.unnamed.hephaestus.util.ModelMath.shrink;

public class ModelGeometryTransformer {

    public static final float DISPLAY_SCALE = 3.7333333F;
    public static final float DISPLAY_TRANSLATION_Y = -6.4f;

    public List<ModelBone> getAllBones(ModelGeometry model) {
        List<ModelBone> bones = new ArrayList<>(model.getBones());
        model.getBones().forEach(bone -> bones.addAll(this.getBoneBones(bone)));
        return bones;
    }

    private List<ModelBone> getBoneBones(ModelBone bone) {
        List<ModelBone> bones = new ArrayList<>();
        if (bone.getComponents().isEmpty()) {
            return bones;
        }

        bone.getComponents().forEach(component -> {
            if (component instanceof ModelBone) {
                bones.add((ModelBone) component);
                bones.addAll(this.getBoneBones((ModelBone) component));
            }
        });

        return bones;
    }

    public JavaModel generateJavaModel(Model model, ModelDescription description, ModelBone bone) {
        Map<String, String> textures = new HashMap<>();
        model.getGeometry().getTextureMap().forEach((id, name) ->
                textures.put("" + id, "hephaestus:" + model.getName() + "/" + name)
        );

        Map<String, JavaDisplay> display = new HashMap<>();

        display.put("head", new JavaDisplay(
                new Vector3Float(0, DISPLAY_TRANSLATION_Y, 0),
                new Vector3Float(0, 0, 0),
                new Vector3Float(DISPLAY_SCALE, DISPLAY_SCALE, DISPLAY_SCALE)
        ));

        List<JavaCube> elements = new ArrayList<>();

        int index = 0;

        Vector3Float bonePivot = bone.getPivot();

        float deltaX = bonePivot.getX() - 8.0F;
        float deltaY = bonePivot.getY() - 8.0F;
        float deltaZ = bonePivot.getZ() - 8.0F;

        for (ModelComponent component : bone.getComponents()) {
            if (component instanceof ModelCube) {
                ModelCube cube = (ModelCube) component;

                Vector3Float origin = cube.getOrigin();
                Vector3Float cubePivot = cube.getPivot();
                Vector3Float size = cube.getSize();

                Vector3Float from = new Vector3Float(
                        16F - origin.getX() + deltaX - size.getX(),
                        origin.getY() - deltaY,
                        origin.getZ() - deltaZ
                );
                Vector3Float to = new Vector3Float(
                        from.getX() + size.getX(),
                        from.getY() + size.getY(),
                        from.getZ() + size.getZ()
                );

                String axis = cube.getRotationAxis();

                Vector3Float rotationOrigin;
                float angle = 0;
                switch (axis) {
                    case "z":
                        angle = cube.getRotation().getZ();
                        break;
                    case "x":
                        angle = -cube.getRotation().getX();
                        break;
                    case "y":
                        angle = -cube.getRotation().getY();
                        break;
                }

                if (angle % 22.5D != 0.0D || angle > 45.0F || angle < -45.0F) {
                    throw new IllegalArgumentException("Angle has to be 45 through -45 degrees in 22.5 degree increments");
                }

                if (cubePivot.getX() == 0 && cubePivot.getY() == 0 && cubePivot.getZ() == 0) {
                    rotationOrigin = new Vector3Float(8, 8, 8);
                } else {
                    rotationOrigin = new Vector3Float(
                            shrink(-cubePivot.getX() + bonePivot.getX() + 8),
                            shrink(cubePivot.getY() - bonePivot.getY() + 8),
                            shrink(cubePivot.getZ() - bonePivot.getZ() + 8)
                    );
                }

                JavaRotation rotation = new JavaRotation(
                        axis,
                        angle,
                        rotationOrigin
                );

                Map<String, JavaFace> faces = new HashMap<>();
                FacedTextureBound[] bounds = cube.getTextureBounds();

                float ratio = 16.0F / description.getTextureWidth();
                for (TextureFace face : TextureFace.values()) {
                    FacedTextureBound bound = bounds[face.ordinal()];
                    float[] uv;

                    if (bound == null) {
                        continue;
                    } else {

                        Vector2Int boundFrom = bound.getBounds();
                        Vector2Int boundSize = bound.getSize();

                        float sX = boundFrom.getX() * ratio;
                        float sY = boundFrom.getY() * ratio;

                        float eX = (boundFrom.getX() + boundSize.getX()) * ratio;
                        float eY = (boundFrom.getY() + boundSize.getY()) * ratio;

                        if (face != TextureFace.UP) {
                            if (face != TextureFace.DOWN) {
                                uv = new float[] {sX, sY, eX, eY};
                            } else {
                                uv = new float[] {sX, eY, eX, sY};
                            }
                        } else {
                            uv = new float[] {eX, eY, sX, sY};
                        }
                    }

                    faces.put(face.name().toLowerCase(), new JavaFace(
                            uv,
                            "#" + bound.getTextureId()
                    ));
                }

                JavaCube javaCube = new JavaCube(
                        bone.getName() + "-cube-" + (index++),
                        from,
                        to,
                        rotation,
                        faces
                );

                javaCube.shrink();
                elements.add(javaCube);
            }
        }

        for (ModelComponent component : bone.getComponents()) {
            if (component instanceof ModelBone) {
                ModelBone child = (ModelBone) component;
                child.setRelativeOffset(bonePivot.divide(16));
                child.updateChildRelativeOffset();
            }
        }

        return new JavaModel(
                bone.getName(),
                new Vector2Int(
                        description.getTextureWidth(),
                        description.getTextureHeight()
                ),
                textures,
                display,
                elements
        ).normalize();
    }
}
