package shapes;

import java.awt.Color;
import java.awt.Polygon;
import java.lang.String;


public class Polygon2 extends Polygon {


	private static final long serialVersionUID = 1L;
	private boolean fill;
	private Color c;
	
	public Polygon2(int[] x, int[] y, int n, Color c) {
		super(x,y,n);
		this.c = c;
	}
	
	public Polygon2(int[] x, int[] y, int n, Color c, boolean fill) {
		super(x,y,n);
		this.c = c;
		this.fill = fill;
	}

	public Polygon2(String line) {
		line = line.strip();
		String data[] = line.split(",");
		for (String d: data) {
			d.replaceAll(",", "");
			d.replaceAll(" ", "");
		}
		npoints = Integer.parseInt(data[1]);
		int rgb = Integer.parseInt(data[2]);
		c = new Color(rgb);
		xpoints = new int[npoints];
		ypoints = new int[npoints];
		if (data[0].charAt(0) == 'f') fill = true;
		int j = 0;
		for (int i = 0; i < 2*npoints; i += 2) {
			xpoints[j] = Integer.parseInt(data[i+3]);
			ypoints[j] = Integer.parseInt(data[i+4]);
			j += 1;
		}
	}	
	

	public Color getDrawColor() {
		return c;
	}
	
	public boolean isFill() {
		return fill;
	}
	
	public String toString() {
		String typeColor = (fill ? "filledPolygon," : "polygon,") + npoints + "," + c.getRGB();
		String xy = "";
		for(int i = 0; i < npoints; i++) {
			xy += "," + xpoints[i] + "," + ypoints[i];
		}
		return String.format("%s%s%n", typeColor, xy);
	  }

	
	public static void main(String[] args) {
		Polygon2 e = new Polygon2("filledPolygon,3,-256,250,100,100,700,700,600\n");
		Polygon2 e1 = new Polygon2("polygon,3,-256,250,100,100,700,700,600\n");
	    System.out.print(e);
	    System.out.print(e1);
	}
}
