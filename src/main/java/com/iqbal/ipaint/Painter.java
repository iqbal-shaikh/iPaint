package com.iqbal.ipaint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Painter extends JFrame
{
	JMenuBar mb;
	JMenu file;
	JMenu help;
	JMenuItem ap;
	JMenuItem close;
	JButton brushBut, lineBut, ellipseBut, rectBut, strokeBut, fillBut, calBut, magicBut, eraseBut;
	JSlider transSlider;
	JLabel transLabel;
	DecimalFormat dec = new DecimalFormat("#.##");
	Graphics2D graphSettings;
	int currentAction = 1;
	float transparentVal = 1.0f;
	Color strokeColor = Color.BLACK, fillColor = Color.BLACK;

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e)
		{
			System.out.println("error" + e.getStackTrace());
		}

		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				new Painter();

			}
		});
	}

	public Painter()
	{

		this.setSize(1150, 700);
		this.setTitle("ipaint");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mb = new JMenuBar();

		file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		ap = new JMenuItem("about iPaint");

		close = new JMenuItem("close       Alt+C");

		close.setMnemonic(KeyEvent.VK_C);
		close.setToolTipText("Exit Application");
		close.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		help = new JMenu("Help");

		file.addSeparator();
		file.add(ap);
		file.addSeparator();
		file.add(close);

		mb.add(file);

		mb.add(help);
		setJMenuBar(mb);

		// Swing box that will hold all the buttons

		Box theBox = Box.createHorizontalBox();

		// Make all the buttons in makeMeButtons by passing the
		// button icon.
		
		brushBut = makeMeButtons(getClass().getResource("/p1.jpg"), 1);
		brushBut.setToolTipText("Pencil");
		lineBut = makeMeButtons(getClass().getResource("/line.jpg"), 2);
		lineBut.setToolTipText("Line");
		ellipseBut = makeMeButtons(getClass().getResource("/circle.png"), 3);
		ellipseBut.setToolTipText("circle/Ellipse");
		rectBut = makeMeButtons(getClass().getResource("/rect.jpg"), 4);
		rectBut.setToolTipText("Rectangle");
		calBut = makeMeButtons(getClass().getResource("/cal1.jpg"), 8);
		magicBut = makeMeButtons(getClass().getResource("/magic.png"), 9);
		eraseBut = makeMeButtons(getClass().getResource("/eraser.png"), 5);

		strokeBut = makeMeColorButton(getClass().getResource("/sc.jpg"), 6, true);
		strokeBut.setToolTipText("Stroke Color");
		fillBut = makeMeColorButton(getClass().getResource("/pb.jpg"), 7, false);
		fillBut.setToolTipText("Fill color");

		theBox.add(eraseBut);
		theBox.add(brushBut);
		theBox.add(lineBut);
		theBox.add(ellipseBut);
		theBox.add(rectBut);
		theBox.add(calBut);
		theBox.add(magicBut);
		theBox.add(strokeBut);
		theBox.add(fillBut);

		// Add transparent label and slider

		transLabel = new JLabel("Transparent: 1");

		// Min value, Max value and starting value for slider

		transSlider = new JSlider(1, 99, 99);

		// Create an instance of ListenForEvents to handle events

		ListenForSlider lForSlider = new ListenForSlider();

		// Tell Java that you want to be alerted when an event
		// occurs on the slider

		transSlider.addChangeListener(lForSlider);

		theBox.add(transLabel);
		theBox.add(transSlider);

		// Add box of buttons to the panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.LIGHT_GRAY);
		buttonPanel.add(theBox);

		this.add(buttonPanel, BorderLayout.SOUTH);

		this.add(new DrawingBoard(), BorderLayout.CENTER);

		// Show the frame
		setLocationRelativeTo(null);
//        this.pack();
		this.setVisible(true);
	}
	// Spits out buttons based on the image supplied
	// actionNum represents each shape to be drawn

	public JButton makeMeButtons(URL iconFile, final int actionNum)
	{
		JButton theBut = new JButton();
		Icon butIcon = new ImageIcon(iconFile);
		theBut.setIcon(butIcon);

		theBut.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				currentAction = actionNum;

			}
		});

		return theBut;
	}

	// Spits out buttons based on the image supplied and
	// whether a stroke or fill is to be defined
	public JButton makeMeColorButton(URL iconFile, final int actionNum, final boolean stroke)
	{
		JButton theBut = new JButton();
		Icon butIcon = new ImageIcon(iconFile);
		theBut.setIcon(butIcon);

		theBut.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{

				if (stroke)
				{

					strokeColor = JColorChooser.showDialog(null, "Pick a Stroke", Color.BLACK);

				} else
				{
					fillColor = JColorChooser.showDialog(null, "Pick a Fill", Color.BLACK);

				}
			}
		});

		return theBut;
	}

	class DrawingBoard extends JComponent
	{

		@Override
		public void setBackground(Color bg)
		{
			super.setBackground(bg);

		}

		// ArrayLists that contain each shape drawn along with
		// that shapes stroke and fill
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		ArrayList<Color> shapeFill = new ArrayList<Color>();
		ArrayList<Color> shapeStroke = new ArrayList<Color>();
		ArrayList<Float> transPercent = new ArrayList<Float>();
		Point drawStart, drawEnd;

		public DrawingBoard()
		{

			this.addMouseListener(new MouseAdapter()
			{

				public void mousePressed(MouseEvent e)
				{

					if (currentAction != 1 && currentAction != 5 && currentAction != 8 && currentAction != 9)
					{

						// When the mouse is pressed get x & y position

						drawStart = new Point(e.getX(), e.getY());
						drawEnd = drawStart;
						repaint();

					}

				}

				public void mouseReleased(MouseEvent e)
				{

					if (currentAction != 1 && currentAction != 5 && currentAction != 8 && currentAction != 9)
					{

						// Create a shape using the starting x & y
						// and finishing x & y positions

						Shape aShape = null;

						if (currentAction == 2)
						{
							aShape = drawLine(drawStart.x, drawStart.y, e.getX(), e.getY());
						} else if (currentAction == 3)
						{
							aShape = drawEllipse(drawStart.x, drawStart.y, e.getX(), e.getY());
						} else if (currentAction == 4)
						{

							// Create a new rectangle using x & y coordinates

							aShape = drawRectangle(drawStart.x, drawStart.y, e.getX(), e.getY());
						}

						// Add shapes, fills and colors to there ArrayLists

						shapes.add(aShape);
						shapeFill.add(fillColor);
						shapeStroke.add(strokeColor);

						// Add transparency value to ArrayList

						transPercent.add(transparentVal);

						drawStart = null;
						drawEnd = null;

						// repaint the drawing area

						repaint();

					}

				}
			});

			this.addMouseMotionListener(new MouseMotionAdapter()
			{

				public void mouseDragged(MouseEvent e)
				{

					if (currentAction == 9)
					{
						Shape aShape = null;
						graphSettings = (Graphics2D) getGraphics();

						aShape = drawMagicBrush(400, 400, e.getX(), e.getY());

						shapes.add(aShape);
						shapeFill.add(fillColor);
						shapeStroke.add(strokeColor);

						// Add the transparency value

						transPercent.add(transparentVal);

					}
//                    
					if (currentAction == 8)
					{

						Shape aShape = null;

						graphSettings = (Graphics2D) getGraphics();

						aShape = drawGraph(e.getX() - 10, e.getY(), e.getX() + 10, e.getY());

						shapes.add(aShape);
						shapeFill.add(fillColor);
						shapeStroke.add(strokeColor);

						// Add the transparency value

						transPercent.add(transparentVal);

					}

					if (currentAction == 1)
					{

						int x = e.getX();
						int y = e.getY();

						Shape aShape = null;

						// Make stroke and fill equal to eliminate the fact that this is an ellipse

						fillColor = strokeColor;

						aShape = drawBrush(x, y, 5, 5);

						shapes.add(aShape);
						shapeFill.add(fillColor);
						shapeStroke.add(strokeColor);

						// Add the transparency value

						transPercent.add(transparentVal);

					}

					if (currentAction == 5)
					{

						int x = e.getX();
						int y = e.getY();

						Shape aShape = null;

						aShape = drawBrush(x, y, 20, 20);

						shapes.add(aShape);
						shapeFill.add(Color.white);
						shapeStroke.add(Color.white);

						// Add the transparency value

						transPercent.add(transparentVal);

					}

					// Get the final x & y position after the mouse is dragged

					drawEnd = new Point(e.getX(), e.getY());
					repaint();
				}
			});

		}

		public void paint(Graphics g)
		{
			// Class used to define the shapes to be drawn

			graphSettings = (Graphics2D) g;

			// Antialiasing cleans up the jagged lines and defines rendering rules

			graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Defines the line width of the stroke
			if (currentAction != 8 && currentAction != 9)
			{
				graphSettings.setStroke(new BasicStroke(4));
			}
			// Iterators created to cycle through strokes and fills
			Iterator<Color> strokeCounter = shapeStroke.iterator();
			Iterator<Color> fillCounter = shapeFill.iterator();

			Iterator<Float> transCounter = transPercent.iterator();

			for (Shape s : shapes)
			{

				graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transCounter.next()));

//                       if(currentAction != 9)
//                       {
				graphSettings.setPaint(strokeCounter.next());

				graphSettings.draw(s);

				graphSettings.setPaint(fillCounter.next());

				graphSettings.fill(s);
//                       }
			}

			if (drawStart != null && drawEnd != null)
			{

				graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f));

				graphSettings.setPaint(Color.LIGHT_GRAY);

				Shape aShape = null;

				if (currentAction == 2)
				{
					aShape = drawLine(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
				} else if (currentAction == 3)
				{
					aShape = drawEllipse(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
				} else if (currentAction == 4)
				{

					aShape = drawRectangle(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
				}

				graphSettings.draw(aShape);
			}
		}

		private Rectangle2D.Float drawRectangle(int x1, int y1, int x2, int y2)
		{

			int x = Math.min(x1, x2);
			int y = Math.min(y1, y2);

			int width = Math.abs(x1 - x2);
			int height = Math.abs(y1 - y2);

			return new Rectangle2D.Float(x, y, width, height);
		}

		private Ellipse2D.Float drawEllipse(int x1, int y1, int x2, int y2)
		{
			int x = Math.min(x1, x2);
			int y = Math.min(y1, y2);
			int width = Math.abs(x1 - x2);
			int height = Math.abs(y1 - y2);

			return new Ellipse2D.Float(x, y, width, height);
		}

		private Line2D.Float drawLine(int x1, int y1, int x2, int y2)
		{

			return new Line2D.Float(x1, y1, x2, y2);
		}

		private Ellipse2D.Float drawBrush(int x1, int y1, int brushStrokeWidth, int brushStrokeHeight)
		{

			return new Ellipse2D.Float(x1, y1, brushStrokeWidth, brushStrokeHeight);

		}

		private Line2D.Float drawGraph(int x1, int y1, int x2, int y2)
		{

			return new Line2D.Float(x1, y1, x2, y2);
		}

		private Line2D.Float drawMagicBrush(int x1, int y1, int x2, int y2)
		{

			int rd = (int) (Math.random() * 200);
			int grn = (int) (Math.random() * 200);
			int blu = (int) (Math.random() * 200);
			graphSettings.setColor(new Color(rd, grn, blu));

			return new Line2D.Float(x1, y1, x2, y2);
		}
	}

	class ListenForSlider implements ChangeListener
	{

		// Called when the spinner is changed
		public void stateChanged(ChangeEvent e)
		{

			if (e.getSource() == transSlider)
			{

				transLabel.setText("Transparent: " + dec.format(transSlider.getValue() * .01));

				transparentVal = (float) (transSlider.getValue() * .01);

			}

		}
	}
}
