package ide.frontend.classes.views;

import java.awt.Color;

/**
 * Class used to generate a color
 * @author axelfaes
 *
 */
public class ColorFactory {
	/**
	 * Generates a color that is similar to the given color, but more /less bright
	 * @param r red component of the color
	 * @param g green component of the color
	 * @param b blue component of the color
	 * @return the new color
	 */
	public static Color calculateComplementary(int r, int g, int b){
		float[] test = Color.RGBtoHSB(r, g,b , null);
		test[2] += 0.5f;
		if(test[2] > 1.0f){
			test[2] -= 1.0f;
		}
		return new Color(Color.HSBtoRGB(test[0], test[1], test[2]));
	}
}
