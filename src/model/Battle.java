package model;

class Battle implements Event{
	private boolean finished=false;
	private final Society s,s1;
	
	Battle(Society s,Society s1){
		this.s=s;
		this.s1=s1;
	}
	
	@Override
	public void act(Simulation sim) {
		if(s.getOverallLeader()!=s1.getOverallLeader()) {
			double bp=s.getBattlePower();
			double bp1=s1.getBattlePower();
			if(bp>bp1) {
				s.capture(sim,s1);
			}
			if(bp1>bp) {
				s1.capture(sim,s);
			}
		}
		finished=true;
	}
	
	@Override
	public boolean finished() {
		return finished;
	}
}
