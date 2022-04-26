
package jobManager;

import client.Worker;
import com.healthmarketscience.rmiio.RemoteIterator;
import task.Task;
import util.CombineRemoteIterator;
import util.Logger;
import util.RemoteFileIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JobManagerImpl implements JobManager {
    Task map;
    Task reduce;
    List<WorkerContainer> workers;
    int reducers;
    List<Assignment> mapAssignments = new ArrayList<>();
    List<Assignment> reduceAssignments = new ArrayList<>();
    List<Path> chunks;
    List<Path> outputFiles;
    InputStream[] inputStreams;
    UUID uid;
    String clientName;
    Logger logger;

    public JobManagerImpl(String associatedClient, Task map, Task reduce, InputStream[] inputStreams, Map<String, Worker> workers, int reducers) {
        this.clientName = associatedClient;
        this.logger = new Logger(String.format("JobManager_%s", associatedClient));
        this.uid = UUID.randomUUID();
        this.map = map;
        this.reduce = reduce;
        this.workers = new ArrayList<>();
        for (Map.Entry<String, Worker> e : workers.entrySet()) this.workers.add(new WorkerContainer(e.getKey(), e.getValue()));
        this.reducers = reducers;
        this.inputStreams = inputStreams;
        try {
            this.generateChunks(inputStreams);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.outputFiles = new ArrayList<>();
        File parent = new File("./output");
        if (!parent.exists()) parent.mkdirs();
        for (int i = 0; i < reducers; i++) {
            Path p = Path.of(String.format("./output/%s-%d.csv", uid.toString(), i));
            try {
                Files.createFile(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.outputFiles.add(p);
        }
    }

    @Override
    public void run() {

        // SPLIT DATA
        // create chunks

        // generate map assignments
        for (int i = 0; i < this.workers.size(); i++) {
            Assignment a = null;
            WorkerContainer wc = this.workers.get(i);
            try {
                int finalI = i;
                DataGetter dg = new DataGetter() {
                    @Override
                    public RemoteIterator<String> get() {
                        try{
                            return generateRemoteIterator(finalI);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                a = new Assignment(wc.worker, wc.workerName, dg, this.map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mapAssignments.add(a);
        }


        logger.log("Map Assignments generated: %s", mapAssignments.toString());

        Map<Assignment, Thread> mapThreads = new HashMap<>();

        boolean mapComplete = false;
        // generate thread for each assignment
        mapAssignments.forEach(a -> {
            Thread t = new Thread(new workerThread(a));
            t.start();
//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            mapThreads.put(a, t);
        });

        Set<WorkerContainer> availableWorkers = new HashSet<>();
        List<Assignment> incomplete =  new ArrayList<>(mapAssignments);
        List<Assignment> reassigned = new ArrayList<>();
        while (!mapComplete) {
            int finished = 0;
            int total = mapAssignments.size();
            for (Assignment m : mapAssignments) {
                if (m.isComplete) {
                    availableWorkers.add(new WorkerContainer(m.workerName, m.worker));
                    incomplete.remove(m);
                    logger.log("");
                    finished++;
                }
            }
            if (finished == total) mapComplete = true;
            else if (finished > total * 2/3) {
                for (Assignment stalled : incomplete) {
                    if (reassigned.contains(stalled)) continue;
                    logger.log("Reassigning stalled work");
                    mapThreads.get(stalled).interrupt();
                    mapThreads.remove(stalled);
                    // todo what if no available workers?

                    WorkerContainer available = availableWorkers.iterator().next();
                    availableWorkers.remove(available);
                    logger.log("Reassigning from %s to %s", stalled.workerName, available.workerName);
                    stalled.worker = available.worker;
                    stalled.workerName = available.workerName;
                    Thread t = new Thread(new workerThread(stalled));
                    t.start();
                    mapThreads.put(stalled, t);
                    reassigned.add(stalled);
                }
            }
        }

        // todo: reassignment of work

        // map tasks have all finished at this point
        logger.log("Map completed");

        // REDUCE
        List<WorkerContainer> reduceWorkers = new ArrayList<>();
        int j = 0;
        for (WorkerContainer wc : this.workers) {
            if (j++ == reducers) break;
            reduceWorkers.add(new WorkerContainer(wc.workerName, wc.worker));
        }

        // make assignments
        int k = 0;
        for (Assignment mapAssignment : mapAssignments) {
            int workerIndex = k % reducers;
            WorkerContainer wc = reduceWorkers.get(workerIndex);
            String workerName = wc.workerName;
            Worker worker = wc.worker;
            if (k >= reducers) {
                Assignment a = reduceAssignments.get(workerIndex); // workerIndex should correspond to assignment index
                RemoteIterator<String> prev = a.dataGetter.get();
                a.dataGetter = () -> {
                    try {
                        return new CombineRemoteIterator<>(mapAssignment.worker.getComputedData(), prev);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                };
            } else {
                DataGetter dg = () -> {
                    try {
                        return mapAssignment.worker.getComputedData();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                };
                Assignment reduceAssignment = new Assignment(worker, workerName, dg, this.reduce);
                reduceAssignments.add(reduceAssignment);
            }
            k++;
        }

        List<Thread> reduceThreads = new ArrayList<>();

        // do the reduce work
        reduceAssignments.forEach(a -> {
            Thread t = new Thread(new workerThread(a));
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            reduceThreads.add(t);
        });

        // todo reassign work after super majority are finished?

        // for each reduce assignment, get the outputData and stream to a file.
        int l = 0;
        for (Assignment a : reduceAssignments) {
            RemoteIterator<String> data;
            try {
                data = a.worker.getComputedData();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            Path writeTo = outputFiles.get(l);
            try (BufferedWriter writer = Files.newBufferedWriter(writeTo)) {
                while (data.hasNext()) {
                    writer.write(data.next());
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            l++;
        }

        // delete all intermediate data
        for (Assignment ma : mapAssignments) {
            try {
                ma.worker.taskCompleted(this.map);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        for (Assignment ra : reduceAssignments) {
            try {
                ra.worker.taskCompleted(this.reduce);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        this.chunks.forEach(p -> {
            try {
                Files.deleteIfExists(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // print out location of output data
        System.out.println("MapReduce completed!!!");
        System.out.println("find output in:");
        this.outputFiles.forEach(f -> System.out.printf("%s%n", f));
    }

    private static class workerThread implements Runnable {

        Assignment a;

        workerThread(Assignment assignment) {
            this.a = assignment;
        }

        @Override
        public void run() {
            try {
                a.worker.runTask(a.task, a.dataGetter.get());
                a.isComplete = true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private RemoteIterator<String> generateRemoteIterator(int nth) throws IOException {
        Path chunk = this.chunks.get(nth);
        logger.log("Generating iterator for chunk %d from file %s", nth, chunk);
        return RemoteFileIterator.iterator(chunk);
    }

    /**
     * Generate M chunks of data for each map worker
     *
     * @param fileStreams data streams of input
     * @throws IOException if can't write
     */
    private void generateChunks(InputStream[] fileStreams) throws IOException {
        int totalWorkers = workers.size();
        this.chunks = new ArrayList<>();
        this.logger.log("Generating %d chunks", totalWorkers);
        Iterator<String> iter = new Iterator<>() {
            int streamIndex = 0;
            Scanner scanner = new Scanner(fileStreams[streamIndex]);

            @Override
            public boolean hasNext() {
                if (scanner.hasNextLine()) return true;
                scanner.close();
                if (streamIndex == fileStreams.length - 1) return false;
                scanner = new Scanner(fileStreams[++streamIndex]);

                return this.hasNext();
            }

            @Override
            public String next() {
                return scanner.nextLine();
            }
        };

        for (int i = 0; i < totalWorkers; i++) {
            Path p = Path.of(String.format("./chunk-%s.csv", i));
            try {
                Files.createFile(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chunks.add(p);
        }

        List<BufferedWriter> openChunks = chunks.stream().map(f -> {
            try {
                return Files.newBufferedWriter(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        int n = 0;
        while (iter.hasNext()) {
            String data = iter.next();
            BufferedWriter chunk = openChunks.get(n % totalWorkers);
            chunk.write(data);
            chunk.newLine();
            n++;
        }

        openChunks.forEach(fw -> {
            try {
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        logger.log("Chunks generated: ");
        for (Path p : chunks) {
            logger.log("%-20s", p);
        }
    }

    private class WorkerContainer {
        Worker worker;
        String workerName;

        WorkerContainer(String s, Worker w) {
            workerName = s;
            worker = w;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof WorkerContainer)) return false;
            WorkerContainer t = (WorkerContainer) obj;
            return Objects.equals(t.workerName, this.workerName);
        }

        @Override
        public int hashCode() {
            return workerName.hashCode();
        }
    }
}
