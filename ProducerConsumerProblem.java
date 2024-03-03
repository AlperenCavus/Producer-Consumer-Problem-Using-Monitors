package PCP;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

//MAHMUT ALPEREN ÇAVUŞ 210408044
class Monitor {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity = 100;

    // producer adds a number to the buffer
    public synchronized void produce(int number) throws InterruptedException {
        while (buffer.size() == capacity) {
            wait(); // wait if the buffer is full
        }
        buffer.add(number);
        System.out.println("Produced: " + number);
        notifyAll(); // notify consumers that a number is available
    }

    // Consumer fetches a number from the buffer
    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait(); // wait if the buffer is empty
        }
        int number = buffer.poll();
        System.out.println("Consumed: " + number);
        notifyAll(); // notify producers that space is available in the buffer area
        return number;
    }
}

class Producer extends Thread {
    private final Monitor monitor;

    public Producer(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            int number = rand.nextInt(100) + 1;
          
                try {
					monitor.produce(number);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // simulate some processing time
            
        }
    }
}

class Consumer extends Thread {
    private final Monitor monitor;
    private final FileWriter fileWriter;

    public Consumer(Monitor monitor, FileWriter fileWriter) {
        this.monitor = monitor;
        this.fileWriter = fileWriter;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 50; i++) {
                int number = monitor.consume();
                synchronized (fileWriter) {
                    fileWriter.write(number + "\n");
                    fileWriter.flush();
                }
                sleep(50); // simulate some processing time
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}

public class ProducerConsumerProblem {
    public static void main(String[] args) {
        try {
            Monitor monitor = new Monitor();
            FileWriter fileWriter = new FileWriter("C:\\Users\\ASUS\\Downloads\\Numbers.txt"); //change your file path

            // create producer threads
            for (int i = 0; i < 5; i++) {
                new Producer(monitor).start();
            }

            // create consumer threads
            for (int i = 0; i < 5; i++) {
                new Consumer(monitor, fileWriter).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

