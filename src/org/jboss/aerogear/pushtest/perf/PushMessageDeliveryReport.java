package org.jboss.aerogear.pushtest.perf;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class PushMessageDeliveryReport {

    private static final int BASE_OFFSET = 100;

    private static final int AGENTS_OFFSET = BASE_OFFSET * BASE_OFFSET * BASE_OFFSET;
    private static final int PROCESS_OFFSET = BASE_OFFSET * BASE_OFFSET;
    private static final int THREAD_OFFSET = BASE_OFFSET;
    private static final int RUN_OFFSET = 1;

    private BitSet bitmap;
    private int totalDeliveredMessages;
    private int maxAgent;
    private int maxProcess;
    private int maxThread;
    private int maxRun;

    public PushMessageDeliveryReport() {
        reset();
    }

    public void reset() {
        this.bitmap = new BitSet(BASE_OFFSET * AGENTS_OFFSET);
        this.totalDeliveredMessages = 0;
        this.maxAgent = 0;
        this.maxProcess = 0;
        this.maxThread = 0;
        this.maxRun = 0;
    }

    public void delivered(int agent, int process, int thread, int run) {

        totalDeliveredMessages++;
        maxAgent = agent > maxAgent ? agent : maxAgent;
        maxProcess = process > maxProcess ? process : maxProcess;
        maxThread = thread > maxThread ? thread : maxThread;
        maxRun = run > maxRun ? run : maxRun;

        bitmap.set(index(agent, process, thread, run));
    }

    public Integer totalDeliveredMessages() {
        return totalDeliveredMessages;
    }

    public List<Integer> missingMessages() {

        List<Integer> missingMessages = new ArrayList<Integer>();

        // we expect at least some message was delivered
        // otherwise we are not able to report stats anyway
        if (totalDeliveredMessages > 0) {
            for (int agent = 0; agent <= maxAgent; agent++) {
                for (int process = 0; process <= maxProcess; process++) {
                    for (int thread = 0; thread <= maxThread; thread++) {
                        for (int run = 0; run <= maxRun; run++) {
                            int messageNo = index(agent, process, thread, run);
                            if (!bitmap.get(messageNo)) {
                                missingMessages.add(messageNo);
                            }
                        }
                    }
                }
            }
        }

        return missingMessages;
    }

    private int index(int agent, int process, int thread, int run) {

        return AGENTS_OFFSET * agent + PROCESS_OFFSET * process + THREAD_OFFSET * thread + RUN_OFFSET * run;
    }

}