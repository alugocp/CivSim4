package model;
import java.util.ArrayList;

public class Society implements Follower{
	private static final double POPULATION_OVERLOAD_MODIFIER=2.0;
	private static final double SCIENCE_INCREMENT=0.01;
	private static final int PROPHECY_THRESHOLD=15;
	private static final int POP_UNIT=10;
	final ArrayList<Society> bordering=new ArrayList<>();
	private Trade scienceTrade,religionTrade,cultureTrade,warTrade;
	private boolean frontierCheck=true;
	private String religion,ethnicity;
	private int prophecyTimer=0;
	private Leader leader;
	String name;
	Leader overall;// used in the draw loop
	double science=0.0;
	private int pop=10;
	boolean redraw;
	final int x,y;
	Language lang;
	int focusState;
	Focus focus;
	
	Society(Simulation sim,int x,int y){
		this(new Language(sim.r),x,y);
		religion=lang.generateWord();
		ethnicity=lang.generateWord();
		focus=new Focus(sim.r);
		leader=new Leader(sim,this);
		leader.societies.add(this);
	}
	Society(Simulation sim,Society s,int x,int y){
		this(s.lang,x,y);
		religion=s.religion;
		ethnicity=s.ethnicity;
		science=s.science;
		focus=new Focus(sim.r,s.focus);
		leader=s.leader;
		leader.societies.add(this);
	}
	private Society(Language lang,int x,int y) {
		name=lang.generateWord();
		this.lang=lang;
		this.x=x;
		this.y=y;
	}
	
	// update states
	void updateStates() {
		focusState=focus.getMain();
		if(focusState!=Focus.RELIGION) {
			prophecyTimer=0;
		}
	}
	public boolean isFrontier() {
		if(frontierCheck) {
			boolean frontier=bordering.size()<8;
			if(!frontier) {
				for(Society s:bordering) {
					if(s.overall!=overall) {
						s.frontierCheck=false;
						frontier=true;
					}
				}
			}
			return frontier;
		}
		frontierCheck=true;
		return false;
	}
	public void finishDraw() {redraw=false;}
	
	// science
	private void loseScience() {
		science-=SCIENCE_INCREMENT;
		if(science<0.0) {
			science=0.0;
		}
	}
	private void gainScience() {
		science+=SCIENCE_INCREMENT;
		if(science>1.0) {
			science=1.0;
		}
	}
	
	// religion
	public String getReligion() {return religion;}
	void setReligion(String religion) {this.religion=religion;}
	void makeReligious() {
		int upper=Focus.MAX/2;
		int lower=Focus.MAX/4;
		focus.set(upper,lower,lower,lower);
	}
	boolean tickProphecyTimer() {
		prophecyTimer++;
		if(prophecyTimer==PROPHECY_THRESHOLD) {
			prophecyTimer=0;
			return true;
		}
		return false;
	}
	void missionTo(Society target) {
		target.focus.gainReligion();
		target.religion=religion;
	}
	ArrayList<Society> getMissionTargets(){
		ArrayList<Society> targets=new ArrayList<>();
		for(Society s:bordering) {
			if(!s.religion.equals(religion)) {
				targets.add(s);
			}
		}
		return targets;
	}
	
	// culture
	
	// war
	public double getBattlePower() {
		Leader l=getOverallLeader();
		return /*(1.0+science)**/l.warSocieties/(l.wars+1.0);
	}
	void capture(Simulation sim,Society enemy) {
		if(focusState!=Focus.SCIENCE) {
			enemy.loseScience();
			if(focusState==Focus.RELIGION) {
				enemy.religion=religion;
			}else if(focusState==Focus.CULTURE) {
				enemy.lang=lang;
			}
		}else {
			gainScience();
		}
		enemy.changeLeader(sim,leader);
		enemy.pop-=POP_UNIT;
		if(enemy.pop<=0) {
			enemy.pop=POP_UNIT;
			enemy.ethnicity=ethnicity;
			// convert religion and language too?
		}
	}
	void changeLeader(Simulation sim,Leader l) {
		if(leader.capital==this) {
			if(l==leader) {
				if(!sim.leaders.contains(leader)) {
					sim.leaders.add(leader);
				}
			}else {
				sim.leaders.remove(leader);
				if(!l.followers.contains(leader)) {
					l.followers.add(leader);
				}
				sim.addEvent(new Liberation(leader,l));
			}
			leader.leader.followers.remove(leader);
			leader.leader=l;
			l.forceRedraw();
		}else {
			for(Society s:bordering) {
				s.redraw=true;
			}
			leader.societies.remove(this);
			l.societies.add(this);
			leader=l;
		}
		redraw=true;
		
		// remove trade routes
		if(religionTrade!=null) {
			religionTrade.remove=true;
		}
		if(scienceTrade!=null) {
			scienceTrade.remove=true;
		}
		if(cultureTrade!=null) {
			cultureTrade.remove=true;
		}
		if(warTrade!=null) {
			warTrade.remove=true;
		}
	}
	/*private void redrawBorders(Leader l) {
		for(Society s:l.societies) {
			s.redraw=true;
			for(Society s1:s.bordering) {
				s1.redraw=true;
			}
		}
		for(Leader l1:l.followers) {
			redrawBorders(l1);
		}
	}*/
	
	// trade
	Trade getTrade(int f) {
		switch(f) {
			case(Focus.SCIENCE):
				return scienceTrade;
			case(Focus.RELIGION):
				return religionTrade;
			case(Focus.CULTURE):
				return cultureTrade;
		}
		return warTrade;
	}
	void establishTrade(Trade t) {
		Society partner=(t.s==this?t.s1:t.s);
		switch(partner.focusState) {
			case(Focus.SCIENCE):
				scienceTrade=t;
			case(Focus.RELIGION):
				religionTrade=t;
			case(Focus.CULTURE):
				cultureTrade=t;
			case(Focus.WAR):
				warTrade=t;
		}
	}
	private boolean isTradeOpen(int type) {
		return getTrade(type)==null;
	}
	ArrayList<Society> getPotentialTradingPartners() {
		ArrayList<Society> p=new ArrayList<>();
		for(Society s:bordering) {
			if(s.isTradeOpen(focusState) && isTradeOpen(s.focusState)) {
				p.add(s);
			}
		}
		return p;
	}
	void trade(Society partner) {
		switch(focusState) {
			case(Focus.RELIGION):
				partner.religion=religion;
			case(Focus.SCIENCE):
				partner.gainScience();
			case(Focus.CULTURE):
				partner.lang=lang;
		}
		if(partner.pop>=pop*POPULATION_OVERLOAD_MODIFIER) {
			ethnicity=partner.ethnicity;
		}
	}
	
	// getters
	public String getName() {return name;}
	public int getFocus() {return focusState;}
	public boolean getRedraw() {return redraw;}
	public int getX() {return x;}
	public int getY() {return y;}
	
	// follower functionality
	@Override
	public Leader getLeader() {
		return leader;
	}
	@Override
	public Leader getOverallLeader() {
		return leader.getOverallLeader();
	}
}
