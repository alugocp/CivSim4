package model;

class Alliance implements Event{
	private final Leader l1,l2;
	
	Alliance(Simulation sim,Leader l1,Leader l2){
		this.l1=l1;
		this.l2=l2;
	}
	
	@Override
	public void act(Simulation sim) {
		Leader leader=new Leader(sim,l1.capital);
		l1.leader=leader;
		l2.leader=leader;
		sim.leaders.remove(l1);
		sim.leaders.remove(l2);
		sim.leaders.add(leader);
	}
	@Override
	public boolean finished() {return true;}
}
