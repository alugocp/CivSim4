package model;
import java.util.Collection;

class Rebellion implements Event{
	final static double SUCCESS_RATE=0.75;
	private final Collection<Society> allies;// includes rebel
	private final Society rebel;
	private int timer=1;
	
	Rebellion(Society rebel,Collection<Society> allies) {
		this.rebel=rebel;
		this.allies=allies;
	}
	void setDelayTimer(int time) {
		timer=time;
	}
	
	@Override
	public void act(Simulation sim) {
		timer--;
		if(timer==0) {
			if(sim.r.nextDouble()<SUCCESS_RATE) {
				Leader old=rebel.getOverallLeader();
				Leader l=new Leader(sim,rebel);
				for(Society s:allies) {
					s.changeLeader(sim,l);
				}
				sim.addEvent(new War(l,old));
			}
		}
	}
	@Override
	public boolean finished() {
		return timer==0;
	}
}
