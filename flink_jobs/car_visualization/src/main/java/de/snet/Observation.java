package de.snet;

import org.omg.CORBA.DoubleHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used as a Pojo for one observation. An observation is
 * all data that is associated with one (DeviceID, Trip, Time) tuple for DAS2 data
 * or one (DataDas - Device, DataDas - Trip, DataDas - Time) tuple for DAS1 data.
 * 
 * @author JonathanH5
 *
 */
public class Observation {

	private static final Logger log = LoggerFactory.getLogger(Observation.class);
	
	public Integer deviceID;
	public Integer trip;
	public Integer time;
	public Double headingGPS = null;
//	public Double latitudeGPS = null;
//	public Double longitudeGPS = null;
	//This replaces long/lat for gson/mongo compatibility
//	public Double[] point2d;
//	public Point point2d = null;
	public Long utcTimeGPS = null;
	public Double speed = null;
	public Double acceleration = null;
	public Integer brakeStatus = null;
	public Integer absStatus = null;
	public Integer tractionControlStatus = null;
	public Integer stabilityControlStatus = null;

	public Observation() {
	}
	
	/**
	 * Splits a DAS2 line String by "," and returns a new DAS2_Observation.
	 * @param line
	 * @return DAS2_Observation with above relevant fields
	 */
	public static Observation createDAS2_Observation(String line) {
		String[] f = line.split(",");
		//remove whitspaces
		for (int i = 0; i < f.length; i++) {
			f[i] = f[i].replaceAll(" ", "");
		}
		Observation obs = new Observation();
		try {
			obs.deviceID = Integer.parseInt(f[0]);
			obs.trip = Integer.parseInt(f[1]);
			obs.time = Integer.parseInt(f[2]);
			obs.headingGPS = parseDouble(f[6]);
			Double latitudeGPS = parseDouble(f[7]);
			Double longitudeGPS = parseDouble(f[8]);
//			obs.point2d = new Point(Double.valueOf(longitudeGPS), Double.valueOf(latitudeGPS));
			obs.speed = parseDouble(f[19]);
			obs.utcTimeGPS = parseLong(f[12]);
			obs.absStatus = parseInt(f[16]);
			obs.brakeStatus = parseInt(f[17]);
			obs.acceleration = parseDouble(f[20]);
			obs.stabilityControlStatus = parseInt(f[22]);
			obs.tractionControlStatus = parseInt(f[25]);
		} catch (Exception e) {
			log.warn("Invalid line: " + line);
			log.warn(e.getMessage());
			return null;
		}
		// recalculate some fields based on specifications for later joins
		if (obs.brakeStatus != null) {
			if (obs.brakeStatus > 0) {
				obs.brakeStatus = 1;
			}
		}
		if (obs.absStatus != null) {
			switch (obs.absStatus) {
				case 0:
					obs.absStatus = null;
					break;
				case 1:
					obs.absStatus = 0;
					break;
				case 2:
					obs.absStatus = 1;
					break;
				case 3:
					obs.absStatus = 1;
					break;
				default:
					obs.absStatus = null;
			}
		}
		if (obs.tractionControlStatus != null) {
			switch (obs.tractionControlStatus) {
				case 0:
					obs.tractionControlStatus = null;
					break;
				case 1:
					obs.tractionControlStatus = 0;
					break;
				case 2:
					obs.tractionControlStatus = 1;
					break;
				default:
					obs.tractionControlStatus = null;
			}
		}
		if (obs.stabilityControlStatus != null) {
			switch (obs.stabilityControlStatus) {
				case 0:
					obs.stabilityControlStatus = null;
					break;
				case 1:
					obs.stabilityControlStatus = 0;
					break;
				case 2:
					obs.stabilityControlStatus = 1;
					break;
				default:
					obs.stabilityControlStatus = null;
			}
		}
		return obs;
	}
	
	/**
	 * Parses a String to a Double. Returns null if not possible.
	 * @param s
	 * @return the corresponding Double or null
	 */
	public static Double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Parses a String to a Long. Returns null if not possible.
	 * @param s
	 * @return the corresponding Long or null
	 */
	public static Long parseLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Parses a String to an Integer. Returns null if not possible.
	 * @param s
	 * @return the corresponding Integer or null
	 */
	public static Integer parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return deviceID + "," + trip + "," + time + "," + headingGPS + ","
//				+ latitudeGPS + "," + longitudeGPS + ","
//				+ point2d.latitude + "," + point2d.longitude +","
				+ speed + "," + utcTimeGPS + "," + absStatus + "," + brakeStatus + ","
				+ acceleration + "," + stabilityControlStatus + "," + tractionControlStatus;
	}
	
	public String toString2() {
		return "DAS2_Observation [deviceID=" + deviceID + ", trip=" + trip + ", time=" + time + ", headingGPS="
				+ headingGPS + ", latitudeGPS="
//				+ latitudeGPS
//				+ point2d.latitude
				+ ", longitudeGPS="
//				+ longitudeGPS
//				+ point2d.longitude
				+ ", utcTimeGPS=" + utcTimeGPS + ", speed=" + speed + ", acceleration=" + acceleration 
				+ ", brakeStatus=" + brakeStatus + ", absStatus=" + absStatus
				+ ", stabilityControlStatus=" + stabilityControlStatus
				+ ", tractionControlStatus=" + tractionControlStatus + "]";
	}
	/**
	 * utility method to create a point from 2 double values
	 */
//	public void setPoint(Double longitude, Double latitude){
//		point2d = new Point(longitude, latitude);
//	}
}
