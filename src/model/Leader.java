package model;
import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeSet;

public class Leader implements Follower,Comparable<Leader>{
	ArrayList<Society> societies=new ArrayList<>();
	Collection<Leader> followers=new TreeSet<>();
	private War aggressionWar;
	Society capital;// included in societies
	Leader leader;
	int warSocieties;
	int wars;
	final private HSV color;
	private Focus interest;
	//private boolean forceRedraw;
	
	Leader(Simulation sim,Society capital){
		color=sim.color.newColor();
		interest=new Focus(sim.r);
		sim.leaders.add(this);
		this.capital=capital;
		leader=this;
	}
	
	// states
	void updateStates() {
		warSocieties=0;
		for(Society s:societies) {
			s.updateStates();
			warSocieties+=(s.focusState==Focus.WAR?1:0);
		}
		for(Leader l:followers) {
			l.updateStates();
			warSocieties+=l.warSocieties;
		}
	}
	void shiftFocus(Simulation sim) {
		similarize(capital.focus);
		capital.focus.similarize(interest);
		if(capital.focus.equals(interest)) {
			interest=new Focus(sim.r);
		}
		if(capital.focusState==Focus.WAR && sim.leaders.size()>1) {
			if(aggressionWar==null || !sim.ongoingWar(aggressionWar)) {
				Leader l=this;
				while(l==this) {
					l=sim.leaders.get(sim.r.nextInt(sim.leaders.size()));
				}
				aggressionWar=new War(this,l);
				sim.addEvent(aggressionWar);
			}
		}
		
	}
	private void similarize(Focus f) {
		for(Society s:societies) {
			s.focus.similarize(f);
		}
		for(Leader l:followers) {
			l.similarize(f);
		}
	}
	boolean isDead() {
		return societies.size()==0 && followers.size()==0;
	}
	void forceRedraw() {
		//forceRedraw=true;
		for(Society s:societies) {
			s.redraw=true;
			for(Society s1:s.bordering) {
				s1.redraw=true;
			}
		}
		for(Leader l:followers) {
			l.forceRedraw();
		}
	}
	/*public boolean handleRedraw() {
		boolean f=forceRedraw;
		forceRedraw=false;
		return f;
	}*/
	
	// getters
	public HSV getPoliticalColor() {
		return getOverallLeader().color;
	}
	public Society getCapital() {return capital;}
	public String getName() {
		String base=capital.getName();
		if(followers.size()>0) {
			return Language.asEmpire(base);
		}
		/*if(capital.science<Society.MAX_SCIENCE/2.0) {
			return Language.asKingdom(base);
		}*/
		return Language.asCountry(base);
	}
	public Collection<Society> getSocieties(){return societies;}
	public Collection<Leader> getFollowers(){return followers;}
	
	// follower functionality
	@Override
	public Leader getLeader() {
		return leader;
	}
	@Override
	public Leader getOverallLeader() {
		Leader l=leader;
		while(l.leader!=l) {
			l=l.leader;
		}
		return l;
	}
	
	@Override
	public int compareTo(Leader l) {
		return color.compareTo(l.color)+capital.getName().compareTo(l.capital.name);
	}
}
