package thread;

import org.javatuples.Triplet;

import java.time.Instant;
import java.util.List;

public class BeginTheadByCopy extends Thread {
    private int[] array = null;
    private int numberToSearch = 0;

    private Instant start;
    private Instant end;

    private boolean found;
    private int position;

    public Triplet<Boolean, Instant, Instant> getResult() {
        return new Triplet<Boolean, Instant, Instant>(found, start, end);
    }

    public int getPosition() {
        return position;
    }

    public BeginTheadByCopy(List<Integer> list, int numberToSearch) {
        this.array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        this.numberToSearch = numberToSearch;
        found = false;
        position = -1;
    }

    @Override
    public void run() {
        start = Instant.now();
        for (int i = 0; i < array.length; i++) {
            int integer = array[i];
            if (integer == numberToSearch) {
                found = true;
                position = i;
                break;
            }
        }
        end = Instant.now();

    }
}
