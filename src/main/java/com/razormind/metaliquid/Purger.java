package com.razormind.metaliquid;

/**
 * Purge every time a certain threshold is met
 *
 */
public class Purger extends Thread {

	private J2Sql sql = new J2Sql();
	private int maxDocs = 2000;
	private int purge = 100;

	public Purger() {
		System.out.println("Starting purge thread");
	}

	public void run() {
		while (true) {
			sql.purge(maxDocs, purge);

			try {
				// don't wanna kill the system, sleep for a minute (or 2)
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
