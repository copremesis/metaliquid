package com.razormind.metaliquid;

public class Recycler extends Thread {
	public Recycler() {
		System.out.println("Starting purge thread");
	}
	int cnt = 0;
	public void run() {
		while (true) {
			try {
				if (cnt++ > 240) {
					new AppServices().restartApplication();
					System.out.println("tick tock time to refresh");
				}				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
