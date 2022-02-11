package model;
import java.util.Collection;
import java.util.ArrayList;

public class War implements Event{
	private final static int BATTLES_PER_TURN=100;
	private final Leader l,l1;
	private int battlesLeft;
	int i;
	
	War(Leader l,Leader l1){
		battlesLeft=Integer.max(l.warSocieties,l1.warSocieties)*BATTLES_PER_TURN;
		this.l1=l1;
		l1.wars++;
		this.l=l;
		l.wars++;
	}
	
	@Override
	public void act(Simulation sim) {
		if(sim.underTreaty(l,l1)) {
			battlesLeft=0;
		}else {
			i=0;
			lookForEnemyCities(sim,l,l1);
			if(i==0) {
				battlesLeft=0;
			}
		}
	}
	private void lookForEnemyCities(Simulation sim,Leader a,Leader d) {
		ArrayList<Society> troops=new ArrayList<>();
		troops.addAll(a.societies);
		for(Society s:troops) {
			boolean cont=true;
			for(Society s1:s.bordering) {
				if(s1.overall==d) {
					if(startBattle(sim,s,s1)) {
						return;
					}
					cont=false;
					break;
				}
			}
			if(cont && s.bordering.size()<8) {
				for(int[] o:new int[][] {{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0}}) {
					int[] c=new int[] {s.x+o[0],s.y+o[1]};
					Planet p=sim.planet;
					if(p.isValid(c[0],c[1]) && !p.isLand(c[0],c[1])) {
						final int maxDist=Expansion.getMaxSailDist(s);
						int dist=0;
						while(dist<maxDist && p.isValid(c[0],c[1]) && !p.isLand(c[0],c[1])) {
							c[0]+=o[0];
							c[1]+=o[1];
							dist++;
						}
						if(p.isValid(c[0],c[1]) && p.isLand(c[0],c[1])) {
							Society s1=p.taken[c[0]][c[1]];
							if(s1!=null && s1.getOverallLeader()==d) {
								if(startBattle(sim,s,s1)) {
									return;
								}
								break;
							}
						}
					}
				}
			}
		}
		Collection<Leader> followers=new ArrayList<>();
		followers.addAll(a.followers);
		for(Leader a1:followers) {
			if(a.followers.contains(a1)) {
				lookForEnemyCities(sim,a1,d);
			}
		}
	}
	private boolean startBattle(Simulation sim,Society s,Society s1) {
		new Battle(s,s1).act(sim);
		battlesLeft--;
		if(battlesLeft==0) {
			sim.addEvent(new Treaty(sim.r,l,l1));
			return true;
		}
		i++;
		if(i==BATTLES_PER_TURN) {
			return true;
		}
		if(l.getOverallLeader()==l1.getOverallLeader()) {
			battlesLeft=0;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean finished() {
		boolean value=battlesLeft<=0 || l.isDead() || l1.isDead();
		if(value) {
			l1.wars--;
			l.wars--;
		}
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null && obj instanceof War) {
			War w=(War)obj;
			return l==w.l && l1==w.l1;
		}
		return false;
	}
}
