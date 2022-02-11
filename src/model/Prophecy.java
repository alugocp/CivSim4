package model;
import java.util.ArrayList;

class Prophecy implements Event{
	private static final double CONVERSION_RATE=0.65;
	private static final int MAX_TIMER=20;
	private final ArrayList<Society> doConvert=new ArrayList<>();
	private final ArrayList<Society> converted=new ArrayList<>();
	private final Society prophet;// included in converted
	private final String religion;
	private int timer=MAX_TIMER;
	
	public Prophecy(Society prophet) {
		this.prophet=prophet;
		converted.add(prophet);
		doConvert.add(prophet);
		religion=prophet.lang.generateWord();
	}
	
	@Override
	public void act(Simulation sim) {
		if(timer==MAX_TIMER) {
			prophet.makeReligious();
		}
		for(Society s:converted) {
			s.setReligion(religion);
		}
		ArrayList<Society> converts=new ArrayList<>();
		for(Society missionary:doConvert) {
			for(Society s:missionary.bordering) {
				if(!s.getReligion().equals(religion)) {
					s.setReligion(religion);
					converted.add(s);
					if(sim.r.nextDouble()<CONVERSION_RATE) {
						converts.add(s);
					}
				}
			}
		}
		doConvert.addAll(converts);
		timer--;
		if(timer==0) {
			Society capital=prophet.getOverallLeader().getCapital();
			if(capital.focusState==Focus.RELIGION && !capital.getReligion().equals(religion)) {
				sim.addEvent(new Rebellion(prophet,converted));
			}
		}
	}
	@Override
	public boolean finished() {
		return timer==0;
	}
}
