package amuse.visualization.amuseAdaptor;

import java.util.ArrayList;
import java.util.HashMap;

import openglCommon.datastructures.Material;
import openglCommon.exceptions.FileOpeningException;
import openglCommon.math.VecF3;
import openglCommon.models.Model;
import openglCommon.models.base.Sphere;
import amuse.visualization.AmuseSettings;

public class Hdf5Snapshotter {
    private final AmuseSettings       settings        = AmuseSettings.getInstance();

    private static AmuseGasOctreeNode cubeRoot;
    private ArrayList<Star>           stars;

    private final static String       evoNamePostfix  = ".evo";
    private final static String       gravNamePostfix = ".grav";
    private final static String       gasNamePostfix  = ".gas";

    private static String intToString(int input) {
        String result = "";
        if (input < 10) {
            result += "00000" + input;
        } else if ((input >= 10) && (input < 100)) {
            result += "0000" + input;
        } else if ((input >= 100) && (input < 1000)) {
            result += "000" + input;
        } else if ((input >= 1000) && (input < 10000)) {
            result += "00" + input;
        } else if ((input >= 10000) && (input < 100000)) {
            result += "0" + input;
        } else {
            result += input;
        }

        return result;
    }

    private int lastDisplayedFrame = -1;

    public Hdf5Snapshotter() {
        stars = new ArrayList<Star>();
    }

    public AmuseGasOctreeNode getOctreeRoot() {
        return Hdf5Snapshotter.cubeRoot;
    }

    public ArrayList<Star> getStars() {
        return stars;
    }

    public void open(String namePrefix, int currentFrame, int levelOfDetail, HashMap<Integer, Model> cloudModels,
            boolean overrideUpdate) throws FileOpeningException {

        if ((currentFrame != lastDisplayedFrame) || overrideUpdate) {
            final int gasSubdivision = settings.getGasSubdivision();
            final int starSubdivision = settings.getStarSubdivision();
            final int gasParticlesPerOctreeNode = settings.getGasParticlesPerOctreeNode();

            String evoName, gravName, gasName;

            gasName = namePrefix + Hdf5Snapshotter.intToString(currentFrame) + Hdf5Snapshotter.gasNamePostfix;
            evoName = namePrefix + Hdf5Snapshotter.intToString(currentFrame) + Hdf5Snapshotter.evoNamePostfix;
            gravName = namePrefix + Hdf5Snapshotter.intToString(currentFrame) + Hdf5Snapshotter.gravNamePostfix;

            Hdf5Snapshotter.cubeRoot = new AmuseGasOctreeNode(new Sphere(Material.random(), gasSubdivision, 1f,
                    new VecF3()), gasParticlesPerOctreeNode, 0, gasSubdivision, new VecF3(-settings.getOctreeEdges(),
                    -settings.getOctreeEdges(), -settings.getOctreeEdges()), settings.getOctreeEdges());
            Hdf5GasCloudReader.read(Hdf5Snapshotter.cubeRoot, gasName);

            stars = Hdf5StarReader.read(starSubdivision, evoName, gravName);

            if (settings.getGasStarInfluencedColor()) {
                Hdf5Snapshotter.cubeRoot.finalizeAdding(stars);
            }

            lastDisplayedFrame = currentFrame;
        }
    }
}
