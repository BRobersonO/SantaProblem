import java.util.Random;
import java.util.concurrent.*;

public class Elf implements Runnable {

	private volatile boolean day370 = false;
	public void setDay370( boolean day370 )
	{
		this.day370 = day370;
	}

	Semaphore elfSem = new Semaphore(3);

	enum ElfState {
		WORKING, TROUBLE, AT_SANTAS_DOOR
	}

	private ElfState state;
	/**
	 * The number associated with the Elf
	 */
	private int number;
	private Random rand = new Random();
	private SantaScenario scenario;

	public Elf(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ElfState.WORKING;
	}

	public ElfState getState() {
		return state;
	}

	/**
	 * Santa might call this function to fix the trouble
	 * @param state
	 */
	public void setState(ElfState state)
	{
		this.state = state;
	}

	@Override
	public void run() {
		while (!day370) {
			//I assume this sleep was meant to be inside the while loop. (was outside in skeleton code)
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			switch (state) {
				case WORKING:
					// at each day, there is a 1% chance that an elf runs into trouble.
					if ( rand.nextDouble() < 0.01) {
						state = ElfState.TROUBLE;
					}
					break;
				case TROUBLE:
					//Semaphore only lets 3 in at a time
					elfSem.acquireUninterruptibly();
					state = scenario.santa.getHelp();
					elfSem.release();
					if (day370) {
						break;
					}
					if (scenario.santa.state != Santa.SantaState.READY_FOR_CHRISTMAS) {
						scenario.santa.wakeSantaE();
						scenario.santa.resetElves(0);
					}
					break;
				case AT_SANTAS_DOOR:
					//elves go to Sants's door after grouped in 3 by the getHelp() function
					break;
			}
		}
	}
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Elf " + number + " : " + state);
	}
}