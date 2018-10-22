//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.PriorityQueue;
//import java.util.Queue;
//
////Second market model. This processes all arrivals and distributes jobs as it sees fit
//public class Uber {
//    private ArrayList<Integer> clustSizeList;
//    private ArrayList<Integer> speeds;
//    private ArrayList<JobMaster> generator = new ArrayList<>();
//    private PriorityQueue<Job> jobs = new PriorityQueue<>();
//    private ArrayList<DataCenter> centers = new ArrayList<>();
//
//    public Uber(ArrayList<Integer> clustSizeList, ArrayList<Integer> speeds) {
//        this.clustSizeList = clustSizeList;
//        this.speeds = speeds;
//        for (int i = 0; i < clustSizeList.size(); i++) {
//            generator.add(new JobMaster(speeds.get(i), .9, clustSizeList.get(i)));
//        }
//    }
//
//    public void genJobs() {
//        for (JobMaster j : generator) {
//            j.setLambda();
//            Queue<Job> hold = j.genJobs();
//            jobs.addAll(hold);
//        }
////        System.out.println(jobs);
//    }
//
//    public void participation(DataCenter D) {
//        if (!centers.contains(D)) {
//            centers.add(D);
//        }
//    }
//
//    public void integration() {
////        System.out.println("Num Jobs: " + jobs.size());
////        System.out.println("Num Centers: " + centers.size());
//        MergeSortCostRatio m = new MergeSortCostRatio(centers);
//        m.sortGivenArray();
//        ArrayList<DataCenter> result = m.getSortedArray();
////        System.out.println("Start");
////        for (DataCenter d : result) {
////            System.out.println(d.getCostRatio());
////        }
////        System.out.println("end");
//        for (int i = 0; i < result.size(); i++) {
//            DataCenter d =  result.get(i);
//            ArrayList<Double> availablility = d.getOnLoad();
//            Queue<Job> sendEr = new LinkedList<>();
//            double coreSend  = 0;
//            double ramSend = 0;
//            double ldSend = 0;
//            while (availablility.get(0) > coreSend && availablility.get(1) > ramSend && availablility.get(2) > ldSend && !jobs.isEmpty()) {
//                Job j = jobs.poll();
//                coreSend += j.getCoreCount();
//                ramSend += j.getRAM();
//                ldSend += j.getLocalDisk();
//                sendEr.add(j);
//            }
//            d.addJobs(sendEr);
//        }
//    }
//}