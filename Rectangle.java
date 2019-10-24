package shapes;

import java.awt.Color;
import java.util.Scanner;
import java.awt.geom.Rectangle2D.Double;;


public class Rectangle extends Double {

	
	private static final long serialVersionUID = 1L;
	private boolean fill;
	private Color c;
	
	public Rectangle(double x1, double y1, double x2, double y2, Color c, boolean fill) {
		this.setFrameFromDiagonal(x1, y1, x2, y2);
		this.c = c;
		this.fill = fill;
	}

	public Rectangle(int x, int y, int w, int h, Color c) {
		super(x,y,w,h);
		this.c = c;
	}
	
	public Rectangle(int x, int y, int w, int h, Color c, boolean fill) {
		this(x,y,w,h,c);
		this.fill = fill;
	}
	
	public Rectangle(String line) {
		Scanner sc = new Scanner(line.strip());
		sc.useDelimiter(",\\s*");
		String type = sc.next();
		if (type.charAt(0) == 'f') fill = true;
		x = sc.nextInt();
		y = sc.nextInt();
		width = sc.nextInt();
		height = sc.nextInt();
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
		return String.format("%s, %d, %d, %d, %d, %d%n", (fill ? "filledRectangle" : "rectangle"), (int) x, (int) y,
				(int) width, (int) height, c.getRGB());
	}
	
	public static void main(String[] args) {
		Rectangle e = new Rectangle("filledRectangle, 475, 325, 48, 48, -16776961\n");
	    Rectangle e1 = new Rectangle( "rectangle, 375, 425, 47, 47, -16711936\n");
	    System.out.print(e);
	    System.out.print(e1);
	}
}

