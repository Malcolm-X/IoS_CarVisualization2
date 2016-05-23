package de.snet;

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
	public Double headingGPS;
	public Double latitudeGPS;
	public Double longitudeGPS;
	public Long utcTimeGPS;
	public Double speed;
	public Double acceleration;
	public Integer brakeStatus;
	public Integer absStatus;
	public Integer tractionControlStatus;
	public Integer stabilityControlStatus;
	
	public Observation() {}
	
	/**
	 * Splits a DAS2 line String by "," and returns a new DAS2_Observation.
	 * Relevant fields: 0,1,2,6,7,8,11,12,16,17,19,22,25
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
			obs.latitudeGPS = parseDouble(f[7]);
			obs.longitudeGPS = parseDouble(f[8]);
			obs.speed = parseDouble(f[11]);
			obs.utcTimeGPS = parseLong(f[12]);
			obs.absStatus = parseInt(f[16]);
			obs.brakeStatus = parseInt(f[17]);
			obs.acceleration = parseDouble(f[22]);
			obs.stabilityControlStatus = parseInt(f[22]);
			obs.tractionControlStatus = parseInt(f[25]);
		} catch (Exception e) {
			log.warn("Invalid line: " + line);
			log.warn(e.getMessage());
			return null;
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
		return deviceID + "," + trip + "," + time + "," + headingGPS + "," + latitudeGPS + "," + longitudeGPS + ","
				+ speed + "," + utcTimeGPS + "," + absStatus + "," + brakeStatus + ","
				+ acceleration + "," + stabilityControlStatus + "," + tractionControlStatus;
	}
	
	public String toString2() {
		return "DAS2_Observation [deviceID=" + deviceID + ", trip=" + trip + ", time=" + time + ", headingGPS="
				+ headingGPS + ", latitudeGPS=" + latitudeGPS + ", longitudeGPS=" + longitudeGPS
				+ ", utcTimeGPS=" + utcTimeGPS + ", speed=" + speed + ", acceleration=" + acceleration 
				+ ", brakeStatus=" + brakeStatus + ", absStatus=" + absStatus
				+ ", stabilityControlStatus=" + stabilityControlStatus
				+ ", tractionControlStatus=" + tractionControlStatus + "]";
	}
}
