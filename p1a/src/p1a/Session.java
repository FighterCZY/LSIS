package p1a;

import java.util.Date;
import java.sql.Timestamp;


public class Session {
	private String sessionID;
	private Integer versionNumber;
	private Timestamp expires = new Timestamp(0);
	private static int expTime = 60;  // session expiration time in seconds, i.e. 2 min
	private String[] str;
	private int LoggedOut = 0;
	
	public void setLogOut(){
		LoggedOut = 1;
	}
	
	public Session() {
		setExpires();
		versionNumber = 0;
	}
	
	protected void parseCookieData(String data) {
		String[] cookiePieces = data.split("#");
		this.setSessionID(cookiePieces[0]);
			}

	protected String createCookieData(String[] locations) {
		String cookieData = this.getSessionID() + "#" + this.getVersionNumber()
		+ "#" + locations[0] + "#" + LoggedOut;
		return cookieData;
	}

	
	public Session(int versionNumber, String sessionID) {
		setExpires();
		versionNumber = (Integer)this.versionNumber;
		sessionID = this.sessionID;
	}
	

	public void getSession(String data, String clientIP) {
		String sID = (clientIP + this.expires.toString()+Math.random()*1000);
		setSessionID(sID);
		versionNumber = 0;	
	}
	
	
	public void fetchSession(String sID) {
		setExpires();
		setSessionID(sID);
	}
	
	protected String getSessionID() {
		return sessionID;
	}
	
	
	protected void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	protected int getVersionNumber() {
		return versionNumber;
	}

	protected void setExpires() {
		Date date = new Date();
		Timestamp stamp = new Timestamp(date.getTime());
		expires.setTime(stamp.getTime() + (expTime * 1000));
	}

	protected Timestamp getExpires() {
		return expires;
	}

	protected int getExpTime() {
		return expTime;
	}
	
	protected void increaseVersion(){
		versionNumber++;
	}
	
	protected String[] getString(){
		return str;
	}
	
	protected void writeData(String data){
		versionNumber++;
		if(data.length() > 256){
			data = data.substring(0,256);
		}
		String[] temp = {data, versionNumber.toString(), expires.toString(), String.valueOf(expires.getTime())};
		str = temp;
		Handler.sessionTable.put(sessionID, this);
	}

	protected String readData(){
		return str[0];
	}
	
}
