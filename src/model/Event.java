package model;

public interface Event {
	public void act(Simulation sim);
	public boolean finished();
}
