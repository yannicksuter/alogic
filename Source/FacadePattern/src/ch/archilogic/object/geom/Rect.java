package ch.archilogic.object.geom;

import ch.archilogic.object.Object;
import javax.vecmath.Point2f;

public class Rect extends Object {
	public Rect(Point2f topLeft, Point2f bottomDown) {
		setIsVerticeStripClosed(true);
		addVertice(topLeft);
		addVertice(new Point2f(bottomDown.x, topLeft.y));
		addVertice(bottomDown);
		addVertice(new Point2f(topLeft.x, bottomDown.y));
	}
}
