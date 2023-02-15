package ch.epfl.cs107.play.game.actor;

import ch.epfl.cs107.play.math.RegionOfInterest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */
public class advancementBar{
    private final ImageGraphics[] bar = new ImageGraphics[27]; //All possible states for the advancement bar

    /**
     * Constructor for the advancement bar
     */
    public advancementBar() {
        List<RegionOfInterest> roi = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j=0; j<9;j++){
                roi.add(new RegionOfInterest(192*j, 21*i, 192, 21));
            }
        }
        for (int i = 0; i < 27; i++) {
            bar[i] = new ImageGraphics("menuItem/advancementBar.png", 4, 0.3f, roi.get(i));
        }
    }

    /**
     * @param advancement: how far in the level the player is
     * @return ImageGraphics: the advancement bar image at the right state
     */
    public ImageGraphics getCurrentBar(float advancement) {
        return bar[(int) (advancement * 26)];
    }
}