import java.io.IOException;


public class Main {

	private static Process p;
	private static Process stats;
	public static void main(String[] args) throws IOException, InterruptedException {

		p = startCrawler();
        stats = startRestEngine();
		while(true) {
			if (p.isAlive()) {
				//tick
			}
			else {
				p = startCrawler();
			}
			
			if (stats.isAlive()) {
				//tock
			}
			else {
				stats = startRestEngine();
			}
		}
	}
	
	public static Process startCrawler() throws IOException {
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", "rest.jar");
		Process p = pb.start();
		new StreamGobbler(p.getErrorStream(), "ERR>>  ").start(); 
        new StreamGobbler(p.getInputStream(), "").start();
        return p;
	}
	
	public static Process startRestEngine() throws IOException {
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", "rest2.jar");
		Process p = pb.start();
		new StreamGobbler(p.getErrorStream(), "REST ERR>>  ").start(); 
        new StreamGobbler(p.getInputStream(), "REST  ").start();
        return p;
	}

}
