package model;

public class Liberation implements Event{
	private static final double SUCCESS_RATE=Rebellion.SUCCESS_RATE*0.1;
	private final Leader l,l1;
	
	Liberation(Leader l,Leader l1){
		this.l1=l1;
		this.l=l;
	}
	
	@Override
	public void act(Simulation sim) {
		if(sim.r.nextDouble()<SUCCESS_RATE) {
			l.capital.changeLeader(sim, l);
			l.capital=l.societies.get(sim.r.nextInt(l.societies.size()));
			sim.addEvent(new Treaty(sim.r,l,l1));
		}
	}
	
	@Override
	public boolean finished() {
		return true;
	}
}
