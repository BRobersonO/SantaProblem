public class Santa implements Runnable {

	enum SantaState {SLEEPING, READY_FOR_CHRISTMAS, WOKEN_UP_BY_ELVES, WOKEN_UP_BY_REINDEER};
	public SantaState state;

	private volatile boolean day370 = false;
	public void setDay370( boolean day370 )
	{
		this.day370 = day370;
	}

	private int waitingElves = 0;
	public void resetElves(int i){ waitingElves = i;}

	public int atHome = 0;

	SantaScenario scenario;
	public Santa(SantaScenario scenario)
	{
		this.scenario = scenario;
		this.state = SantaState.SLEEPING;
	}

	@Override
	public void run() {
		while(!day370) {
			// wait a day...
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			switch(state) {
				case SLEEPING: // if sleeping, continue to sleep
					break;
				case WOKEN_UP_BY_ELVES:
					//only 3 elves at a time will be at door
					for (Elf elf : scenario.elves) {
						if (elf.getState() == Elf.ElfState.AT_SANTAS_DOOR) {
							elf.setState(Elf.ElfState.WORKING);
						}
					}
					state = SantaState.SLEEPING;
					break;
				case WOKEN_UP_BY_REINDEER:
					for (Reindeer reindeer:scenario.reindeers) {
						reindeer.setState(Reindeer.ReindeerState.AT_THE_SLEIGH);
					}
					state = SantaState.READY_FOR_CHRISTMAS;
					break;
				case READY_FOR_CHRISTMAS: // nothing more to be done
					break;
			}
		}
	}
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Santa : " + state);
	}
	public void wakeSantaE() {
		this.state = SantaState.WOKEN_UP_BY_ELVES;
	}
	//3 elves at a time leave with a new state
	public synchronized Elf.ElfState getHelp() {
		waitingElves ++;
		while (!day370 && scenario.santa.state != SantaState.READY_FOR_CHRISTMAS) {
			if (waitingElves < 3) {
				try {
					//thread will eventually die if in trouble when 370 passes
					this.wait(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (day370) {
				this.notifyAll();
				return (Elf.ElfState.TROUBLE);
			}
			this.notifyAll();
			return (Elf.ElfState.AT_SANTAS_DOOR);
		}
		this.notifyAll();
		return (Elf.ElfState.TROUBLE);
	}
	public void wakeSantaR() {
		state = SantaState.WOKEN_UP_BY_REINDEER;}
	public synchronized void checkIn(){
		atHome ++;
	}
}