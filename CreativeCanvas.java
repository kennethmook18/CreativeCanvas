package painting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import shapes.Curve;
import shapes.Ellipse;
import shapes.Line;
import shapes.Polygon2;
import shapes.Rectangle;
import shapes.RoundRectangle;

public class CreativeCanvas extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<Shape> shapes;
	private ArrayList<Shape> selectedShapes = new ArrayList<Shape>();
	private String shapeType = "Line";
	private boolean fill = false;
	private Color color = new Color(-16777216);
	private String[] text = { "X:", "Y:" };
	private JPanel pSouth;
	private Painting canvas;
	private JTextField[] mouseStates;
	private double x = 0.0;
	private double y = 0.0;
	private Boolean clipboard = false;
	private Stack<Shape> redoStack = new Stack<Shape>();
	private String FileName = null;
	private Curve tempCurve;
	private Shape tempShape;
	private int[] xints = new int[100];
	private int[] yints = new int[100];
	private int polyCount = 0;
	private RoundRectangle2D.Double selectRectangle;
	private Popup infoP;
	private JButton closeB;

	public CreativeCanvas() {
		super("Drawing");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		getContentPane().setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setJMenuBar(createMenuBar());
		shapes = new ArrayList<Shape>();
		canvas = new Painting(1200, 800, shapes);
		SwingUtilities.updateComponentTreeUI(canvas);
		canvas.addMouseListener(new MyMouseListener());
		canvas.addMouseMotionListener(new MyMouseMotionListener());
		canvas.addKeyListener(new MyKeyListener());
		canvas.setFocusable(true);
		add("Center", canvas);
		// Create the array of text fields.
		pSouth = new JPanel();
		mouseStates = new JTextField[2];
		for (int i = 0; i < mouseStates.length; i++) {
			mouseStates[i] = new JTextField(text[i], 10);
			mouseStates[i].setEditable(false);
			pSouth.add(mouseStates[i]);
		}
		add("South", pSouth);
		pack();
		setVisible(true);
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenuItem menuItem;
		JCheckBoxMenuItem cbMenuItem;
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F); // mnemonics use ALT_MASK

		String[] commands = { "Open", "Save", "Save As..", "Clear Screen" };
		int[] keyEvents = { KeyEvent.VK_O, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_C };

		for (int i = 0; i < commands.length; i++) {
			menuItem = new JMenuItem(commands[i]);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvents[i], ActionEvent.ALT_MASK));
			menuItem.addActionListener(this);
			fileMenu.add(menuItem);
		}
		menuBar.add(fileMenu);

		JMenu drawMenu = new JMenu("Draw");
		drawMenu.setMnemonic(KeyEvent.VK_D); // mnemonics use ALT_MASK
		cbMenuItem = new JCheckBoxMenuItem("Grid", true);
		cbMenuItem.addItemListener(this);
		cbMenuItem.setAccelerator(KeyStroke.getKeyStroke('g'));
		drawMenu.add(cbMenuItem);
		cbMenuItem = new JCheckBoxMenuItem("Fill", false);
		cbMenuItem.setAccelerator(KeyStroke.getKeyStroke('f'));
		cbMenuItem.addItemListener(this);
		drawMenu.add(cbMenuItem);
		menuItem = new JMenuItem("Edit Color");
		menuItem.setAccelerator(KeyStroke.getKeyStroke('c'));
		menuItem.addActionListener(this);
		drawMenu.add(menuItem);

		String[] cmds = { "Line", "Curve", "Ellipse", "Rectangle", "Round Rectangle", "Polygon" };
		int[] drawKeyEvents = { KeyEvent.VK_L, KeyEvent.VK_Q, KeyEvent.VK_E, KeyEvent.VK_T, KeyEvent.VK_A,
				KeyEvent.VK_P };
		for (int i = 0; i < cmds.length; i++) {
			menuItem = new JMenuItem(cmds[i]);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(drawKeyEvents[i], ActionEvent.CTRL_MASK));
			menuItem.addActionListener(this);
			drawMenu.add(menuItem);
		}
		menuBar.add(drawMenu);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E); // mnemonics use ALT_MASK

		String[] commands2 = { "Select", "Cut", "Copy", "Paste", "Undo", "Redo" };
		int[] editKeyEvents = { KeyEvent.VK_S, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V, KeyEvent.VK_Z,
				KeyEvent.VK_R };
		for (int i = 0; i < commands2.length; i++) {
			menuItem = new JMenuItem(commands2[i]);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(editKeyEvents[i], ActionEvent.CTRL_MASK));
			menuItem.addActionListener(this);
			editMenu.add(menuItem);
		}
		menuBar.add(editMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H); // mnemonics use ALT_MASK

		menuItem = new JMenuItem("Program Info");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		helpMenu.add(menuItem);
		menuBar.add(helpMenu);

		return menuBar;
	}

	public void clearTextFields() {
		for (int i = 0; i < 2; i++)
			mouseStates[i].setBackground(Color.lightGray);
	}

	class MyMouseListener implements MouseListener {
		Point pointStart = null;
		Point pointEnd = null;

		public void mousePressed(MouseEvent e) {
			clearTextFields();
			x = e.getX();
			y = e.getY();
			switch (shapeType) {
			case "Curve":
				Curve curve = new Curve(e.getX(), e.getY(), color);
				tempCurve = curve;
				shapes.add(curve);
				break;
			case "Rectangle":
				Rectangle rectangle = new Rectangle(x, y, x, y, color, fill);
				tempShape = rectangle;
				shapes.add(tempShape);
				break;
			case "Round Rectangle":
				RoundRectangle rrectangle = new RoundRectangle(x, y, x, y, 50, 50, color, fill);
				tempShape = rrectangle;
				shapes.add(tempShape);
				break;
			case "Line":
				Line line = new Line(x, y, x, y, color);
				tempShape = line;
				shapes.add(tempShape);
				break;
			case "Ellipse":
				Ellipse ellipse = new Ellipse(x, y, x, y, color, fill);
				tempShape = ellipse;
				shapes.add(tempShape);
				break;
			case "Polygon":
				xints[polyCount] = e.getX();
				yints[polyCount] = e.getY();
				polyCount += 1;
				break;
			}
			canvas.removeSelectRectangle();
			repaint();
		}

		public void mouseClicked(MouseEvent e) {
			clearTextFields();
		}

		public void mouseEntered(MouseEvent e) {
			clearTextFields();
		}

		public void mouseExited(MouseEvent e) {
			clearTextFields();
		}

		public void mouseReleased(MouseEvent e) {
			clearTextFields();
			pointEnd = e.getPoint();
			switch (shapeType) {
			case "Line":
				shapes.add(tempShape);
				break;
			case "Rectangle":
				shapes.add(tempShape);
				break;
			case "Round Rectangle":
				shapes.add(tempShape);
				break;
			case "Ellipse":
				shapes.add(tempShape);
				break;
			case "Select Rectangle":
				selectedShapes.clear();
				RoundRectangle selectRectangle = new RoundRectangle(Math.min(x, e.getX()), Math.min(y, e.getY()),
						Math.max(x, e.getX()), Math.max(y, e.getY()), 50, 50, color, fill);
				canvas.setSelectRectangle(selectRectangle);
				Rectangle selectRectangle2 = new Rectangle(Math.min(x, e.getX()), Math.min(y, e.getY()),
						Math.max(x, e.getX()), Math.max(y, e.getY()), color, fill);
				for (Shape s : shapes) {
					if (s.intersects(selectRectangle2)) {
						selectedShapes.add(s);
					}
				}
				break;
			}
			repaint();
		}
	}

	class MyMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			clearTextFields();
			mouseStates[0].setText("X: " + e.getX());
			mouseStates[1].setText("Y: " + e.getY());
			switch (shapeType) {
			case "Curve":
				tempCurve.lineTo(e.getX(), e.getY());
				repaint();
				break;

			case "Rectangle":
				Rectangle rectangle = new Rectangle(Math.min(x, e.getX()), Math.min(y, e.getY()), Math.max(x, e.getX()),
						Math.max(y, e.getY()), color, fill);
				tempShape = rectangle;
				shapes.remove(shapes.size() - 1);
				shapes.add(tempShape);
				repaint();
				break;

			case "Round Rectangle":
				RoundRectangle rrectangle = new RoundRectangle(Math.min(x, e.getX()), Math.min(y, e.getY()),
						Math.max(x, e.getX()), Math.max(y, e.getY()), 50, 50, color, fill);
				tempShape = rrectangle;
				shapes.remove(shapes.size() - 1);
				shapes.add(tempShape);
				repaint();
				break;

			case "Ellipse":
				Ellipse ellipse = new Ellipse(Math.min(x, e.getX()), Math.min(y, e.getY()), Math.max(x, e.getX()),
						Math.max(y, e.getY()), color, fill);
				tempShape = ellipse;
				shapes.remove(shapes.size() - 1);
				shapes.add(tempShape);
				repaint();
				break;

			case "Line":
				Line line = new Line(x, y, e.getX(), e.getY(), color);
				tempShape = line;
				shapes.remove(shapes.size() - 1);
				shapes.add(tempShape);
				repaint();
				break;

			}
		}

		public void mouseMoved(MouseEvent e) {
			mouseStates[0].setText("X: " + e.getX());
			mouseStates[1].setText("Y: " + e.getY());
		}
	}

	class MyKeyListener implements KeyListener {
		@SuppressWarnings("null")
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER) {
				Polygon2 poly = new Polygon2(xints, yints, polyCount, color, fill);
				shapes.add(poly);
				repaint();
				try {
					Arrays.fill(xints, (Integer) null);
					Arrays.fill(yints, (Integer) null);
				} catch (Exception unused) {
					; // Ignore exception because we can't do anything. Will use default.
				}
				polyCount = 0;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBoxMenuItem source = (JCheckBoxMenuItem) (e.getSource());
		switch (source.getText()) {
		case "Fill":
			fill = source.getState();
			break;
		case "Grid":
			if (source.getState())
				canvas.showGrid();
			else
				canvas.hideGrid();
			repaint();
			break;
		}
		// temporary feedback
		String eventData = "Item event detected.\n" + "    Event source: " + source.getText() + " (an instance of "
				+ source.getClass() + ")\n" + "    New state: "
				+ ((e.getStateChange() == ItemEvent.SELECTED) ? "selected" : "unselected");
		System.out.println(eventData);
		System.out.printf("shapeType : %s, fill : %b, color : %d%n%n", shapeType, fill, color.getRGB());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.contentEquals("Edit Color")) {
			color = JColorChooser.showDialog(this, "Select Color", color);
			pSouth.setBackground(color);
		}

		switch (command) {
		case "Line":
			shapeType = "Line";
			break;
		case "Curve":
			shapeType = "Curve";
			break;
		case "Ellipse":
			shapeType = "Ellipse";
			break;
		case "Rectangle":
			shapeType = "Rectangle";
			break;
		case "Round Rectangle":
			shapeType = "Round Rectangle";
			break;
		case "Polygon":
			shapeType = "Polygon";
			break;
		case "Select":
			shapeType = "Select Rectangle";
			break;
		case "Cut":
			if (selectedShapes.size() > 0) {
				for (Shape s : selectedShapes) {
					shapes.remove(s);
				}
			}
			clipboard = true;
			canvas.removeSelectRectangle();
			repaint();
			break;
		case "Copy":
			clipboard = true;
			canvas.removeSelectRectangle();
			repaint();
			break;
		case "Paste":
			if (selectedShapes.size() > 0) {
				for (Shape s : selectedShapes) {
					shapes.add(s);
				}
			}
			clipboard = false;
			canvas.removeSelectRectangle();
			repaint();
			break;
		case "Undo":
			redoStack.push(shapes.get(shapes.size() - 1));
			shapes.remove(shapes.size() - 1);
			repaint();
			break;
		case "Redo":
			if (!redoStack.empty()) {
				shapes.add(redoStack.pop());
				repaint();
			}
			break;
		case "Open":
			shapes.clear();
			redoStack.clear();
			FileName = canvas.loadPainting();
			repaint();
			break;
		case "Save":
			if (FileName != null) {
				try {
					File file = new File(FileName);
					FileWriter fw = new FileWriter(file);
					for (Shape s : shapes) {
						String w = s.toString();
						fw.write(w + "\n");
					}
					fw.close();
				} catch (Exception ioe) {
					ioe.printStackTrace();
				}
			} else {
				try {
					String name = JOptionPane.showInputDialog("Enter File Name");
					File file = new File(name);
					FileName = name;
					FileWriter fw = new FileWriter(file);
					for (Shape s : shapes) {
						String w = s.toString();
						fw.write(w + "\n");
					}
					fw.close();
				} catch (Exception ioe) {
					ioe.printStackTrace();
				}
			}
			break;

		case "Save As..":
			try {
				String name = JOptionPane.showInputDialog("Enter File Name");
				File file = new File(name);
				FileName = name;
				FileWriter fw = new FileWriter(file);
				for (Shape s : shapes) {
					String w = s.toString();
					fw.write(w + "\n");
				}
				fw.close();
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
			break;
		case "Clear Screen":
			shapes.clear();
			canvas.removeSelectRectangle();
			repaint();
			break;
		case "Program Info":
			popup();
			break;
		case "Close":
			hideP();
			closeB.setVisible(false);
			break;
		}

		// temporary feedback
		Object o = e.getSource();
		String className = o.getClass().getName().substring(o.getClass().getName().lastIndexOf('.') + 1);
		System.out.printf("Event: %s%n    ActionCommand: %s%n   Source class: %s%n", e.getClass(), command, className);
		System.out.printf("shapeType : %s, fill : %b, color : %d%n%n", shapeType, fill, color.getRGB());
	}

	public void popup() {
		JLabel infoL = new JLabel(
				"<html>Program Functionality:<br>-Save and Save As store the information from the shapes "
						+ "on the screen in a text file in the current directory that can be opened later.<br>-Open allows "
						+ "you to select a file and then draws the shapes whose information are stored in the file on "
						+ "the canvas.<br>-Clear Screen removes all shapes from the canvas.<br>-Fill toggles whether "
						+ "or not the shapes being drawn are filled in.<br>-Grid toggles the gridlines and pixel labels.<br>"
						+ "-Edit Color allows you to select the color the shapes will be drawn in.<br>-Selecting a different "
						+ "shape in the draw menu changes the type of shape you are drawing.<br>-Draw lines, ellipses, and "
						+ "rectangles by dragging and releasing the mouse.<br>-Draw curves by freeform dragging across "
						+ "the canvas.<br>-Draw polygons by clicking the points that you want to connect and then pressing "
						+ "enter when you're done.<br>-To select shapes to cut or copy, choose select in the edit menu "
						+ "and drag to select shapes.<br>-Once you have selected shapes you can cut or copy them using the "
						+ "commands in the edit menu.<br>-Paste repaints the last shape(s) that was cut or copied in the same "
						+ "location that it was in.<br>-Undo removes the last shape drawn, redo repaints the last shape "
						+ "that was undone.<br>-The hot keys to open up the menu are Alt-F for File, Alt-D for Draw, "
						+ "Alt-E for Edit, and Alt-H for Help.<br>-Some hotkeys for specific actions only work if the "
						+ "menu that the functions are in is open (includes f for Fill, g for Grid, c for Edit Color).</html>");

		PopupFactory pf = new PopupFactory();
		JPanel p2 = new JPanel();
		p2.add(infoL);
		infoP = pf.getPopup(canvas, p2, 225, 150);
		closeB = new JButton("Close");
		// closeB.setBounds(200,300,50,50);
		closeB.addActionListener(this);
		infoP.show();
		canvas.add(closeB);
		closeB.setBounds(560, 400, 80, 30);
	}

	public void hideP() {
		infoP.hide();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception unused) {
			; // Ignore exception because we can't do anything. Will use default.
		}
		new CreativeCanvas();
	}

}