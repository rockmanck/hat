package ru.game.hat.support;

public final class PlayerResult implements Comparable<PlayerResult> {
	private final int id;
	private final String name;
	private double points;
	
	public PlayerResult(int id, String name) {
		this.id = id;
		this.name = name;
		points = 0;
	}
	
	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(PlayerResult another) {
		return -1 * ((Double)points).compareTo(another.points);
	}
	
}