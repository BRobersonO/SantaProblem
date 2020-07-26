import java.util.ArrayList;
import java.util.List;

public class SantaScenario {

	public Santa santa;
	public List<Elf> elves;
	public List<Reindeer> reindeers;
	public boolean isDecember;
	public boolean isChristmas;

	public static void main(String args[]) {
		SantaScenario scenario = new SantaScenario();
		scenario.isDecember = false;
		scenario.isChristmas = false;

		// create the participants
		// Santa
		scenario.santa = new Santa(scenario);
		Thread th = new Thread(scenario.santa);
		th.start();

		// The elves: in this case: 10
		scenario.elves = new ArrayList<>();
		for(int i = 0; i != 10; i++) {
			Elf elf = new Elf(i+1, scenario);
			scenario.elves.add(elf);
			th = new Thread(elf);
			th.start();
		}

		// The reindeer: in this case: 9
		scenario.reindeers = new ArrayList<>();
		for(int i=0; i != 9; i++) {
			Reindeer reindeer = new Reindeer(i+1, scenario);
			scenario.reindeers.add(reindeer);
			th = new Thread(reindeer);
			th.start();
		}

		///////////////// All threads are running.

		// now, start the passing of time
		for(int day = 1; day < 500; day++) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// turn on December
			if (day > (365 - 31)) {
				scenario.isDecember = true;
			}
			//It's Christmas?
			if (day == 359) {
				scenario.isChristmas = true;
			}
			// print out the state:
			System.out.println("***********  Day " + day + " *************************");
			scenario.santa.report();
			for(Elf elf: scenario.elves) {
				elf.report();
			}
			for(Reindeer reindeer: scenario.reindeers) {
				reindeer.report();
			}
			//methods for terminating threads on 370th day
			if( day == 370 )
			{
				scenario.santa.resetElves(300);

				for(Reindeer reindeer: scenario.reindeers)
				{
					reindeer.setDay370(true);
				}
				for(Elf elf: scenario.elves)
				{
					elf.setDay370(true);
				}
				scenario.santa.setDay370(true);
			}
		}
	}
}