package model;

class Trade implements Event{
	private static final int TRADE_TIME=2;
	private int timer=0;
	private final int f,f1;
	final Society s,s1;
	boolean remove=false;
	
	public Trade(Society s,Society s1) {
		this.s=s;
		f=s.focusState;
		this.s1=s1;
		f1=s1.focusState;
		s.establishTrade(this);
		s1.establishTrade(this);
	}
	
	@Override
	public void act(Simulation sim) {
		timer++;
		if(timer==TRADE_TIME) {
			s.trade(s1);
			s1.trade(s);
			timer=0;
			if(s.tickProphecyTimer()) {
				sim.addEvent(new Prophecy(s));
			}
			if(s1.tickProphecyTimer()) {
				sim.addEvent(new Prophecy(s1));
			}
		}
	}
	
	@Override
	public boolean finished() {
		return remove || s.focusState!=f || s1.focusState!=f1;
	}
}
