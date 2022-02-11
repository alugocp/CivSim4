package model;

class Mission implements Event{
	private boolean finished=false;
	private final Society s,s1;
	
	Mission(Society s,Society s1){
		this.s=s;
		this.s1=s1;
	}
	
	@Override
	public void act(Simulation sim) {
		s.missionTo(s1);
		/*Society foreign=s1.getOverallLeader().getCapital();
		if(foreign.focusState==Focus.RELIGION && !foreign.getReligion().equals(s.getReligion())) {
			sim.addEvent(new Battle(s,s1));
		}else if(foreign.focusState==Focus.WAR) {
			sim.addEvent(new War(s1.getOverallLeader(),s.getOverallLeader()));
		}*/
		Leader l=s.getOverallLeader();
		Leader l1=s1.getOverallLeader();
		int focus=l1.capital.focusState;
		String religion1=l1.capital.getReligion();
		if(focus==Focus.WAR || (focus==Focus.RELIGION && !s.getReligion().equals(religion1))) {
			sim.addEvent(new War(l1,l));
		}
		finished=true;
	}
	@Override
	public boolean finished() {
		return finished;
	}
}
