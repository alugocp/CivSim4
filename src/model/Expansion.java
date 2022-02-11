package model;
import java.util.ArrayList;

class Expansion implements Event{
	private static final int MAX_SAIL_DIST=25;
	private final Society s;
	
	public Expansion(Society s) {
		this.s=s;
	}
	
	@Override
	public void act(Simulation sim) {
		ArrayList<int[]> sites=getPossibleSites(sim);
		if(sites.size()==0) {
			if(s.bordering.size()==8) {
				Society s1=s.bordering.get(sim.r.nextInt(s.bordering.size()));
				if(s.getLeader()!=s1.getLeader()) {
					//sim.addEvent(new Battle(s,s1));
					sim.addEvent(new War(s.getOverallLeader(),s1.getOverallLeader()));
				}
			}
			return;
		}
		int[] c=sites.get(sim.r.nextInt(sites.size()));
		sim.planet.addSociety(new Society(sim,s,c[0],c[1]));
		s.redraw=true;
	}
	@Override
	public boolean finished() {return true;}
	
	// support
	private ArrayList<int[]> getPossibleSites(Simulation sim){
		int[][] possible=new int[][] {{-1,0},{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1}};
		ArrayList<int[]> sites=new ArrayList<>();
		for(int[] a:possible) {
			int[] c={s.getX()+a[0],s.getY()+a[1]};
			if(sim.planet.isValid(c[0],c[1])) {
				if(sim.planet.canBuildCity(c[0],c[1])) {
					sites.add(c);
				}else if(sim.planet.grid[c[0]][c[1]]==Planet.WATER) {
					final int maxDist=getMaxSailDist(s);
					int distance=0;
					while(distance<maxDist && sim.planet.isValid(c[0],c[1]) && !sim.planet.isLand(c[0],c[1])) {
						c[0]+=a[0];
						c[1]+=a[1];
						distance++;
					}
					if(sim.planet.canBuildCity(c[0],c[1])) {
						sites.add(c);
					}
				}
			}
		}
		return sites;
	}
	static int getMaxSailDist(Society s) {;
		return MAX_SAIL_DIST+(int)Math.ceil(s.science*500.0);
	}
}
