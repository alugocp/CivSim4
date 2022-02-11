package model;
import java.util.Random;

class Focus {
	public static final int SCIENCE=1;
	public static final int RELIGION=2;
	public static final int CULTURE=3;
	public static final int WAR=4;
	static final int MAX=10;
	static final int INCREMENT=1;
	private int science,religion,culture,war;
	
	private Focus(int science,int religion,int culture,int war) {
		this.science=science;
		this.religion=religion;
		this.culture=culture;
		this.war=war;
	}
	Focus(Random r,Focus f) {
		this(f.science,f.religion,f.culture,f.war);
		science+=(r.nextDouble()-0.5)*MAX/2;
		religion+=(r.nextDouble()-0.5)*MAX/2;
		culture+=(r.nextDouble()-0.5)*MAX/2;
		war+=(r.nextDouble()-0.5)*MAX/2;
		bound();
	}
	Focus(Random r) {
		this(r.nextInt(MAX),r.nextInt(MAX),r.nextInt(MAX),r.nextInt(MAX));
	}
	
	// update states
	void similarize(Focus f) {
		science+=(science==f.science?0:(science<f.science?INCREMENT:-INCREMENT));
		religion+=(religion==f.religion?0:(religion<f.religion?INCREMENT:-INCREMENT));
		culture+=(culture==f.culture?0:(culture<f.culture?INCREMENT:-INCREMENT));
		war+=(war==f.war?0:(war<f.war?INCREMENT:-INCREMENT));
		bound();
	}
	int getMain() {
		int main=SCIENCE;
		int most=science;
		if(religion>most) {
			main=RELIGION;
			most=religion;
		}
		if(culture>most) {
			main=CULTURE;
			most=culture;
		}
		if(war>most) {
			main=WAR;
			most=war;
		}
		return main;
	}
	
	// forced changes
	void set(int r,int s,int c,int w) {
		religion=r;
		science=s;
		culture=c;
		war=w;
		bound();
	}
	void gainReligion() {
		religion+=INCREMENT;
		bound();
	}
	
	// support
	private void bound() {
		science=(science<0?0:(science>MAX?MAX:science));
		religion=(religion<0?0:(religion>MAX?MAX:religion));
		culture=(culture<0?0:(culture>MAX?MAX:culture));
		war=(war<0?0:(war>MAX?MAX:war));
	}
	@Override
	public boolean equals(Object obj) {
		if(obj!=null && obj instanceof Focus) {
			Focus f=(Focus)obj;
			return science==f.science &&
					religion==f.religion &&
					culture==f.culture &&
					war==f.war;
		}
		return false;
	}
}
