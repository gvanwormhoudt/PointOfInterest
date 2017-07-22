package eu.telecom.lille.pointofinterest;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import android.location.Location;

public class PointOfInterest implements Serializable {
	

	private static final long serialVersionUID = -9149864555972261917L;
	protected String label;
	protected String description;
	protected double lattitude;
	protected double longitude;
	protected Date visitedDate;
	protected int score;
	protected String imagePath;
	
	public PointOfInterest(String label, String desc, double lg, double lt, int sc, Date  visitedDate, String path) {
		this.label = label;
		this.description = desc;
		this.longitude = lg;
		this.score = sc;
		this.lattitude = lt;
		this.imagePath = path;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public double getLattitude() {
		return lattitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Date getVisitedDate() {
		return visitedDate;
	}

	public String getImagePath() { return imagePath; }

	public String toString() {
		return label;
	}
}
