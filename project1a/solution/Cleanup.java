package p1a;

import java.util.Date;
import java.util.concurrent.*;
import java.util.TimerTask;
import java.util.Enumeration;

public class Cleanup extends TimerTask {
	public void run() {
		System.out.println("Daemon Cleanup Thread Has Started");
		try {
			ConcurrentHashMap<String, Session> hMap = Handler.sessionTable;
			for (Enumeration<String> i = hMap.keys(); i.hasMoreElements();) {
				String key = i.nextElement();
				String[] s = hMap.get(key).getString();
				System.out.println(s[0]+" "+s[1]+" "+s[2]+" "+s[3]);
				if (new Date().getTime() > Long.parseLong(s[3])){
					hMap.remove(key);
					System.out.println(key
							+ " has been removed from the hash table.");
				}
			}
			System.out.print("Cleanup Daemon Complete. ");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}
}