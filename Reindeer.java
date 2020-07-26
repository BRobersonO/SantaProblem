import java.util.Random;
import java.util.concurrent.*;

public class Reindeer implements Runnable {

	public enum ReindeerState {AT_BEACH, AT_WARMING_SHED, AT_THE_SLEIGH};
	private ReindeerState state;
	private SantaScenario scenario;
	private Random rand = new Random();

	boolean day370 = false;
	public void setDay370( boolean day370 )
	{
		this.day370 = day370;
	}

	Semaphore reinSem = new Semaphore(1);

	public void setState(ReindeerState state)
	{
		this.state = state;
	}

	/**
	 * The number associated with the reindeer
	 */
	private int number;

	public Reindeer(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ReindeerState.AT_BEACH;
	}

	@Override
	public synchronized void run() {
		while(!day370) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// see what we need to do:
			switch(state) {
				case AT_BEACH: { // if it is December, the reindeer might think about returning from the beach
					if (scenario.isDecember) {
						if (rand.nextDouble() < 0.1) {
							state = ReindeerState.AT_WARMING_SHED;
						}
					}
					//force back to Shed when it's Christmas
					if (scenario.isChristmas) {
						for (Reindeer reindeer: scenario.reindeers) {
							reindeer.setState(ReindeerState.AT_WARMING_SHED);
						}
					}
					break;
				}
				case AT_WARMING_SHED:
					// if all the reindeer are home, wake up santa
					reinSem.acquireUninterruptibly();
					scenario.santa.checkIn();
					reinSem.release();
						try {
							while (scenario.santa.atHome < 9) {
								this.wait(4000);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					if (scenario.santa.atHome == 9 && scenario.santa.state != Santa.SantaState.READY_FOR_CHRISTMAS) {
						this.notifyAll();
						scenario.santa.wakeSantaR();
					}
					break;
				case AT_THE_SLEIGH:
					// keep pulling
					break;
			}
		}
	}
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Reindeer " + number + " : " + state);
	}
}