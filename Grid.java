package painting;

import java.awt.Color; 
import java.awt.Graphics2D;


public class Grid {

	private int w;
	private int h;
	private int x;
	private int y;
	private boolean showGrid = true;

	public Grid(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

	}

	public void display(Graphics2D g2) {
		if (showGrid) {
			for (int x1 = x; x1 <= w; x1 += 10) {
				g2.setColor(new Color(0xf0f8ff)); 
				if (x1 % 50 == 0 && x1 > 0) {
					g2.setColor(Color.lightGray);
					g2.drawString("" + x1, x1, 15);
				}
				g2.drawLine(x1, 0, x1, h);
			}
			for (int y1 = y; y1 <= h; y1 += 10) {
				g2.setColor(new Color(0xf0f8ff));
				if (y1 % 50 == 0) {
					g2.setColor(Color.lightGray);
					g2.drawString("" + y1, 5, y1);
				}
				g2.drawLine(0, y1, w, y1);
			}
		}
	}

	public void toggleGrid() {
		showGrid = !showGrid;
	}

	public boolean isVisible() {
		return showGrid;
	}

}
