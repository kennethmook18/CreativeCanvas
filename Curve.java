package shapes;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D.Double;

import painting.Painting;

import java.awt.geom.PathIterator;


public class Curve extends Double {

	private static final long serialVersionUID = -448960191756718259L;
	private Color c;
	
	public Curve(int x1, int y1, Color c) {
		moveTo(x1, y1);
		this.c = c;
	}
	
	public Curve(String line) {
		String data[] = line.split(",");
		int rgb = Integer.parseInt(data[1]);
		c = new Color(rgb);
		if (data.length == 6) {
			int x1 = Integer.parseInt(data[2]);
			int y1 = Integer.parseInt(data[3]);
			int x2 = Integer.parseInt(data[4]);
			int y2 = Integer.parseInt(data[5]);
			moveTo(x1, y1);
			lineTo(x2, y2);
		}
		if (data.length == 8) {
			double x1 = Integer.parseInt(data[2]);
			double y1 = Integer.parseInt(data[3]);
			double x2 = Integer.parseInt(data[4]);
			double y2 = Integer.parseInt(data[5]);
			double x3 = Integer.parseInt(data[6]);
			double y3 = Integer.parseInt(data[7]);
			moveTo(x1, y1);
			quadTo(x2, y2, x3, y3);
		}
		else if (data.length == 10) {
			double x1 = Integer.parseInt(data[2]);
			double y1 = Integer.parseInt(data[3]);
			double x2 = Integer.parseInt(data[4]);
			double y2 = Integer.parseInt(data[5]);
			double x3 = Integer.parseInt(data[6]);
			double y3 = Integer.parseInt(data[7]);
			double x4 = Integer.parseInt(data[8]);
			double y4 = Integer.parseInt(data[9]);
			moveTo(x1, y1);
			curveTo(x2, y2, x3, y3, x4, y4);
		}
		
		else {
			double[] xints = new double[(data.length-2)/2];
			double[] yints = new double[(data.length-2)/2];
			int j = 0;
			for (int i = 2; i < data.length; i=i+2) {
				xints[j] = Integer.parseInt(data[i]);
				yints[j] = Integer.parseInt(data[i+1]);
				j += 1;
			}
			moveTo(xints[0], yints[0]);
			for (int k = 1; k < xints.length; k++) {
				lineTo(xints[k], yints[k]);
			}
		}
	}
	
	public Color getDrawColor() {
		return c;
	}
	
	public String toString() {
		String typecolor = "curve," + c.getRGB();
		String xy = "";
		PathIterator pathit = getPathIterator(null);
		double[] coordinates = new double[6];
	    while (!pathit.isDone()) {
	    	int type = pathit.currentSegment(coordinates);
	    	if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {
		        int x = (int) coordinates[0];
		        int y = (int) coordinates[1];
		        xy += "," + x + "," + y;
	    	}
	        pathit.next();
	    }
	    return String.format("%s%s%n", typecolor, xy);
	}
	
	public static void main(String[] args) {
		Curve e = new Curve("curve,-16777216,100,800,400,500,700,300");
		Curve e1 = new Curve("curve,-16777216,100,800,400,500,700,300,800,800");
		Curve e2 = new Curve("curve,-16777216,100,800,400,500,700,300,800,800,100,100");
	    System.out.print(e);
	    System.out.print(e1);
	    System.out.print(e2);
	}
}
