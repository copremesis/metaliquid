package com.razormind.metaliquid;

public class Recycler extends Thread {
	public Recycler() {
		System.out.println("Starting purge thread");
	}
	
	public void run() {
		while (true) {
			try {
				System.out.println("tick tock");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
