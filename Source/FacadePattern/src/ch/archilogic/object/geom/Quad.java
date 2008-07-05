package ch.archilogic.object.geom;

import ch.archilogic.object.Object;
import javax.vecmath.Point2f;

public class Quad extends Object {
	public void Rect(Point2f p1, Point2f p2, Point2f p3, Point2f p4) {
		setIsVerticeStripClosed(true);
		addVertice(p1);
		addVertice(p2);
		addVertice(p3);
		addVertice(p4);
	}
}
