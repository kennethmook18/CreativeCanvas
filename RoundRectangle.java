package shapes;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.util.Scanner;

public class RoundRectangle extends RoundRectangle2D.Double {


	private static final long serialVersionUID = 1L;
	private Color c;
	private boolean fill;
	
	public RoundRectangle(double x1, double y1, double x2, double y2, int arcwidth, int archeight, Color c, boolean fill) {
		this.setFrameFromDiagonal(x1, y1, x2, y2);
		this.arcwidth = arcwidth;
		this.archeight = archeight;
		this.c = c;
		this.fill = fill;
	}
	
	public RoundRectangle(int x, int y, int w, int h, int arcwidth, int archeight, Color c, boolean fill) {
		super(x,y,w,h,arcwidth,archeight);
		this.c = c;
		this.fill = fill;
	}
	
	public RoundRectangle(String line) {
		Scanner sc = new Scanner(line.strip());
		sc.useDelimiter(",\\s*");
		String type = sc.next();
		if (type.charAt(0) == 'f') fill = true;
		x = sc.nextDouble();
		y = sc.nextDouble();
		width = sc.nextDouble();
		height = sc.nextDouble();
		arcwidth = sc.nextDouble();
		archeight = sc.nextDouble();
		int rgb = sc.nextInt();
		c = new Color(rgb);
		sc.close();
	}
	
	public Color getDrawColor() {
		return c;
	}
	
	public boolean isFill() {
		return fill;
	}
	
	public String toString() {
		return String.format("%s, %d, %d, %d, %d, %d, %d,%d%n", (fill ? "filledroundRectangle" : "roundRectangle"), (int) x, (int) y,
				(int) width, (int) height, (int) arcwidth, (int) archeight, c.getRGB());
	}
	
	public static void main(String[] args) {
		RoundRectangle e = new RoundRectangle("filledroundRectangle,475, 325, 48, 48, 10, 10,-16776961\n");
	    RoundRectangle e1 = new RoundRectangle("roundRectangle,375,425, 47, 47, 20, 20, -16711936\n");
	    System.out.print(e);
	    System.out.print(e1);
	}
}

	
