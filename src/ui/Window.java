package ui;
import model.Simulation;
import model.Society;
import model.Leader;
import model.Planet;
import model.HSV;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Graphics;
import java.awt.Color;

public class Window extends JFrame{
	static final long serialVersionUID=0;
	private final Simulation sim;
	private final int width=1000;
	private final int height=700;
	private final int[][] grid;
	private boolean run=true;
	private Thread loop;
	private boolean first=true;
	
	public static void main(String[] args) {
		long seed=-2017;// 5000000
		if(args.length>0) {
			seed=Long.parseLong(args[0]);
		}
		new Window(seed);
	}
	
	private Window(long seed) {
		// Initialize new simulation
		sim=new Simulation(seed,width,height);
		grid=sim.getPlanetGrid();
		
		// Build window
		setTitle("Alex Lugo's World Simulator");
		setResizable(false);
		setSize(width,height);
		setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println(sim.getLeaders().size()+" civs at end");
				run=false;
				System.exit(0);
			}
		});
		
		// Start simulation/output data
		System.out.println("Seed: "+seed);
		System.out.println(sim.getLeaders().size()+" initial civs");
		startLoop();
	}
	
	// Simulation loop
	private void startLoop() {
		loop=new Thread(()->{
			while(run) {
				try {
					Thread.sleep(500);
				}catch(InterruptedException i) {
					System.out.println("Interrupt!");
				}
				sim.doFrame();
				repaint();
			}
		});
		loop.start();
	}
	
	// Graphics
	@Override
	public void paint(Graphics g) {
		if(first) {
			paintPlanet(g);
			first=false;
		}
		for(Leader l:sim.getLeaders()) {
			HSV hsv=l.getPoliticalColor();
			paintLeader(g,l,Color.getHSBColor(hsv.getH(),hsv.getS(),hsv.getV()));
		}
	}
	private void paintPlanet(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.GREEN);
		for(int a=0;a<grid.length;a++) {
			for(int b=0;b<grid[a].length;b++) {
				if(grid[a][b]==Planet.LAND) {
					g.fillRect(a,b,1,1);
				}
			}
		}
	}
	private void paintLeader(Graphics g,Leader l,Color color) {
		for(Society s:l.getSocieties()) {
			if(s.getRedraw()) {
				if(s.isFrontier()) {
					g.setColor(Color.BLACK);
				}else {
					g.setColor(color);
				}
				g.fillRect(s.getX(),s.getY(),1,1);
				s.finishDraw();
			}
		}
		for(Leader l1:l.getFollowers()) {
			paintLeader(g,l1,color);
		}
	}
}
