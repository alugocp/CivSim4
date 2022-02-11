package model;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Random;

public class Simulation {
	private final ArrayList<Event> queue=new ArrayList<>();
	private final ArrayList<Event> events=new ArrayList<>();
	final ArrayList<Leader> leaders=new ArrayList<>();
	final Planet planet;
	final Color color;
	final Random r;
	
	public Simulation(long seed,int width,int height) {
		r=new Random(seed);
		color=new Color(r);
		planet=new Planet(this,width,height);
	}
	
	void addEvent(Event e) {
		if(!queue.contains(e)) {
			queue.add(e);
		}
	}
	boolean ongoingWar(War w) {
		return queue.contains(w);
	}
	boolean underTreaty(Leader l,Leader l1) {
		return queue.contains(new Treaty(r,l,l1));
	}
	
	public void doFrame() {
		// update states
		for(Leader l:leaders) {
			l.shiftFocus(this);
			l.updateStates();
		}
		
		// handle old events on the queue
		events.addAll(queue);
		for(Event e:events) {
			e.act(this);
			if(e.finished()) {
				queue.remove(e);
			}
		}
		events.clear();
		
		// add new events to the queue
		for(Leader l:leaders) {
			if(checkLeader(l)) {
				leaders.remove(l);
			}
		}
	}
	private boolean checkLeader(Leader l) {
		for(Society s:l.societies) {
			s.overall=s.getOverallLeader();
			
			if(s.bordering.size()<8) {
				queue.add(new Expansion(s));
			}
			
			ArrayList<Society> list=s.getPotentialTradingPartners();
			if(list.size()>0) {
				queue.add(new Trade(s,list.get(r.nextInt(list.size()))));
			}
			
			if(s.focusState==Focus.RELIGION) {
				list=s.getMissionTargets();
				if(list.size()>0) {
					queue.add(new Mission(s,list.get(r.nextInt(list.size()))));
				}
			}
		}
		for(Leader l1:l.followers) {
			if(checkLeader(l1)) {
				l.followers.remove(l1);
			}
		}
		return l.isDead();
	}
	
	// getters
	public int[][] getPlanetGrid(){
		return planet.grid;
	}
	public Collection<Leader> getLeaders(){return leaders;}
	
	// NOTES
	// Be careful if you ever tweak an individual society's language. It's currently set by reference
}
