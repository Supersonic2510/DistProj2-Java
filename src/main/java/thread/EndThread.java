package thread;

import org.javatuples.Triplet;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class EndThread extends Thread{

    List<Integer> list = null;

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

    public EndThread(List<Integer> list, int numberToSearch) {
        this.list = list;
        this.numberToSearch = numberToSearch;
        found = false;
        position = -1;
    }

    @Override
    public void run() {
        start = Instant.now();
        for (int i = list.size() - 1; i >= 0; i--) {
            Integer integer = list.get(i);
            if (integer == numberToSearch) {
                found = true;
                position = i;
                break;
            }
        }
        end = Instant.now();

    }
}
