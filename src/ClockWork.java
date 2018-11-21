import java.util.ArrayList;
import java.util.Queue;
import java.util.Arrays;

/*
ClockWork is the discrete blueprint for how data centers act and interact, both in and of themselves, and with each-other.
Upon initialization this module populates its powerGrid array with Interconnections, ISOs, States, and Data Centers, creating
the environment for these objects to interact. Once motion (or one of motion's many relatives) is commenced, the simulation
steps through a minute by minute simulation of some predetermined time interval.
 */
public class ClockWork {
    /*
    time tracking
     */
    public static int t = 0; //this will be considered 12:00 am UTC
    private int month = 0; //start in january
    /*
    Hold the US Power Grid
     */
    private ArrayList<Interconnection> powerGrid = new ArrayList<>();

    public ClockWork(ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> theWorld) {
        //Populate powerGrid with Interconnection Objects
        IntCon[] C = new IntCon[]{IntCon.EASTERN, IntCon.WESTERN, IntCon.TEXAS};
        ArrayList<String> S = new ArrayList<>(Arrays.asList("Eastern Interconnection", "Western Interconnection", "ERCOT"));
        for (int i = 0; i < theWorld.size(); i++) {
            String con = theWorld.get(i).get(0).get(0).get(0).get(0).get(0).get(0).get(0).toString();
            int ind = S.indexOf(con);
            powerGrid.add(new Interconnection(C[ind], S.get(ind), t, theWorld.get(i).get(1)));
        }
    }

    /*
    This method runs the simulation with the geodiversification via sale market module open and operating
     */
    public void motion(int duration){
        //Instance of calendar to track time
        Calendar C = new Calendar();
        //Instance of the geodiversification via sale market
        Market market = new Market();
        //Tracking metrics to track job flow through the simulation
        int lTrans = 0;
        int tTransfer = 0;
        int tJobs = 0;
        int tHrs = 0;
        //Initialize month tracker to invalid month
        int lastMonth = -1;
        //Begin the simulaiton
        for (int i = 0; i < duration; i++) {
            boolean giveNewMonth = false;
            //Determine time of day based on index of loop
            int hour = C.getHour(i);
            //Give new hour printout every hour
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
                tHrs++;
            }
            //Updated information on a daily basis
            if ((i % 1440) == 0) {
                //Check to see if month has changed based on change of day
                int month = C.getMonth(i);
                if (month > lastMonth) {
                    lastMonth = month;
                    giveNewMonth = true;
                }
                //Print information about the day
                lTrans += tTransfer;
                int day = C.getDay(i);
                System.out.println("Month " + month + " Day " + day);
                System.out.println("total transfers today: " + (tTransfer));
                System.out.println("total transfers: " + lTrans);
                System.out.println("total jobs in system: " + tJobs);
                tTransfer = 0;
                tJobs = 0;
            }
            //Make sure everyone is involved
            for (Interconnection I : powerGrid) {
                for (IsoRegion P : I.getIsoRegions()) {
                    //Retrieve the authority for the current region
                    ISO T = P.getAuthority();
                    //Make sure getting correct data values
                    T.giveMonth(month);
                    for (State S : P.getStates()) {
                        for (DataCenter D : S.getClientele()) {
                            //If a new month started, reset budget and other metrics to start again
                            if (giveNewMonth) {
                                tHrs = 0;
                                D.newMonth();
                            }
                            //Update price rates on an hourly basis
                            double price =  T.getRate(tHrs);
                            if (t % 60 == 0) {
                                //Retrieve current energy price from authority
                                D.setRate(price);
                            }
                            //More useful printouts on a daily basis
                            if ((Progress.chunk == 3 && giveNewMonth) || (Progress.chunk <=2 && t % 1439 == 0)) {
                                System.out.println(D);
                                System.out.print("\n");
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            JobMaster jobMast = D.getMaster();
                            jobMast.setLambda();

                            //Generate jobs for a center to incur this minute
                            Queue<Job> hold = jobMast.genJobs();

                            //Give jobs to center
                            D.addJobs(hold);

                            //Move jobs into clusters
                            D.systemScheduler();

                            //execute tasks for this minute
                            D.setProcesses();
                            D.wash();

                            //Calculate cost incurred this minute
                            double mWh = D.powerUsage();

                            //Tag on 33% overall increase in usage due to cooling costs
                            mWh += D.coolingCost(mWh);

                            //Log energy usage totals for this state
                            S.setTotalEnergy(mWh);

                            //Log energy usage totals for this data center
                            D.logEnergyUse(mWh);
//                                D.logTEnergyUse(mWh);

                            //Cost/Revenue accrued by given center
                            D.incurredCost(price * mWh);
                            D.logPrice(price * mWh);
                            D.logProfit(D.getProfit());

                            //Offload any completed work
                            D.cleanHouse();

                            //Remove any jobs that are over their rented time limit
                            D.moveAlong();

                            //For all jobs in system, remove one minute of paid for time
                            D.tick();

                            //Mark values to track job throughput and failure rate
//                                D.tock();

                            //Particpate in the market if the time is right
                            if (D.getParticipation() == 0) {

                                //Get price projection from now till next participation time
                                D.reasonableIndulgence();

                                //If I want to buy jobs, make sure that I want to buy more than nothing
                                if (D.isBuyer()) {
                                    if (D.getOnLoad().size() > 0) {
                                        if (D.getOnLoad().get(0) > 0 && D.getOnLoad().get(1) > 0 && D.getOnLoad().get(2) > 0) {
                                            market.addBuyer(D);
                                        }
                                    }
                                } else {
                                    market.removeBuyer(D);
                                }

                                //Simulate Capitalism
                                if (D.isSeller()) {
                                    if (D.getOffLoad().size() > 0) {
                                        market.silkRoad(D);
                                    }
                                }
                            }
                        }
                        //Manage time zone for given state
                        S.moveLocalTime();
                    }
                }
            }
            //Move the simulation ahead one minute
            t++;
            //Write aggregation information to file
            if (Progress.aggregate == 1) {
                Progress.append("minByMinOutputM", "tOutputM", "JobPerformanceM");
            }
            else {
                Progress.append("avgMinByMinOutputM", "avgTOutputM", "avgJobPerformanceM");
            }
        }
    }

    /*
    This method executes the simulation with no market agent available. This is the current state of the world
    Same as motion, sans the market agent
     */
    public void lessMotion(int duration){
        Calendar C = new Calendar();
        int lTrans = 0;
        int tTransfer = 0;
        int tJobs = 0;
        int lastMonth = -1;
        int tHrs  = 0;
        for (int i = 0; i < duration; i++) {
            boolean giveNewMonth = false;
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
                tHrs++;
            }
            if ((i % 1440) == 0) {
                int month = C.getMonth(i);
                if (month > lastMonth) {
                    lastMonth = month;
                    giveNewMonth = true;
                    System.out.println("Month: " + month);
                }
                else {
                    giveNewMonth = false;
                }
                int day = C.getDayInMonth(i);
                System.out.println("Month " + month + " Day " + day);
                System.out.println("total transfers today: " + (tTransfer - lTrans));
                System.out.println("total transfers: " + tTransfer);
                System.out.println("total jobs in system: " + tJobs);
                lTrans = tTransfer;
                tTransfer = 0;
                tJobs = 0;
            }
            for (Interconnection I : powerGrid) {
                for (IsoRegion P : I.getIsoRegions()) {
                    ISO T = P.getAuthority();
                    for (State S : P.getStates()) {
                        for (DataCenter D : S.getClientele()) {
                            //Update price rates on an hourly basis
                            double price = 0;
                            //Retrieve current energy price from authority
                            int tempHrs = tHrs - S.zone();
                            price = T.getRate(tempHrs);
                            if (t % 60 == 0) {
                                D.setRate(price);
                                System.out.println(price);

                            }
                            if (giveNewMonth) {
                                D.newMonth();
                                tHrs = 0;
                            }
                            if ((Progress.chunk == 3 && giveNewMonth) || (Progress.chunk <=2 && t % 1439 == 0)) {
                                System.out.println(D);
                                System.out.print("\n");
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            JobMaster jobMast = D.getMaster();
                            jobMast.setLambda();
                            Queue<Job> hold = jobMast.genJobs();
                            D.addJobs(hold);

                            //Move jobs into clusters
                            D.systemScheduler();

                            //execute tasks for this minute
                            D.setProcesses();
                            D.wash();

                            //Calculate cost incurred this minute
                            double mWh = D.powerUsage();
                            mWh += D.coolingCost(mWh);
                            S.setTotalEnergy(mWh);

                            //Log energy usage totals for this data center
                            D.logEnergyUse(mWh);
//                                D.logTEnergyUse(mWh);

                            //Cost/Revenue accrued by given center
                            D.incurredCost(price * mWh);
                            D.logPrice(price * mWh);
//                                D.logTPrice(price * mWh);
                            D.logProfit(D.getProfit());
//                                D.logMRevenue(D.getRevenue() - D.getTotalCost());

                            //Offload any completed work
                            D.cleanHouse();

                            //Remove any jobs that are over their rented time limit
                            D.moveAlong();

                            D.tick();
                        }
                        S.moveLocalTime();
                    }
                }
            }
            t++;
            if (Progress.aggregate == 1) {
                Progress.append("minByMinOutputM", "tOutputM", "JobPerformanceM");
            }
            else {
                Progress.append("avgMinByMinOutputM", "avgTOutputM", "avgJobPerformanceM");
            }
        }
    }

    //Getter for the Power Grid
    public ArrayList<Interconnection> getPowerGrid() {
        return powerGrid;
    }

    public String toString() {
        String str = "American Energy: " + "\n";
        for (Interconnection i : powerGrid) {
            str += i + "\n";
        }
        return str;
    }
}

/*
GRAVEYARD
 */
//Run the simulation with a single data center participating in a non market environment
//public void minimalist(int time){
//    Calendar C = new Calendar();
//    Market market = new Market();
//    int lTrans = 0;
//    int tTransfer = 0;
//    int tJobs = 0;
//    int lastMonth = -1;
//    for (int i = 0; i < time; i++) {
//        boolean giveNewMonth = false;
//        int hour = C.getHour(i);
//        if ((i % 60) == 0) {
//            System.out.println("Hour " + hour);
//        }
//        if ((i % 1440) == 0) {
//            int month = C.getMonth(i);
//            if (month > lastMonth) {
//                lastMonth = month;
//                giveNewMonth = true;
//            }
//            else {
//                giveNewMonth = false;
//            }
//
//            int day = C.getDayInMonth(i);
//            System.out.println("Month " + month + " Day " + day);
//            System.out.println("total transfers today: " + (tTransfer - lTrans));
//            System.out.println("total transfers: " + tTransfer);
//            System.out.println("total jobs in system: " + tJobs);
//            lTrans = tTransfer;
//            tTransfer = 0;
//            tJobs = 0;
//        }
//        Interconnection I = powerGrid.get(0);
//        IsoRegion P = I.getIsoRegions().get(0);
//        ISO T = P.getAuthority();
//        State S = P.getStates().get(0);
//        DataCenter D = S.getClientele().get(0);
//        int t = S.getLocalTime();
//        if (giveNewMonth) {
//            D.newMonth();
//        }
//        //Update price rates on an hourly basis
//        double price = 0;
//        if (i % 60 == 0) {
//            //Retrieve current energy price from authority
//            price = T.getRate(C.getHour(t));
//            D.setRate(price);
//        }
//        if (i % 1439 == 0) {
//            System.out.println(D);
//            System.out.print("\n");
//            D.setRevAtm();
//            tTransfer += D.jobsTransfere();
//            tJobs += D.getTotalJobs();
//        }
//        if (D.getBudget() > D.incurredCost()) {
//            JobMaster jobMast = D.getMaster();
//            jobMast.setLambda();
//            Queue<Job> hold = jobMast.genJobs();
//            D.addJobs(hold);
//
//            //Move jobs into clusters
//            D.systemScheduler();
//
//            //execute tasks for this minute
//            D.setProcesses();
//            D.wash();
//
//            //Calculate cost incurred this minute
//            double mWh = D.powerUsage();
//            mWh += D.coolingCost(mWh);
//            S.setTotalEnergy(mWh);
//
//            //Log energy usage totals for this data center
//            D.logEnergyUse(mWh);
//            D.logTEnergyUse(mWh);
//
//            //Cost/Revenue accrued by given center
//            D.incurredCost(price * mWh);
//            D.logPrice(price * mWh);
//            D.logTPrice(price * mWh);
//            D.logTRevenue(D.getRevenue() - D.getTotalCost());
//            D.logMRevenue(D.getRevenue() - D.getTotalCost());
//
//            //Offload any completed work
//            D.cleanHouse();
//
//            //Remove any jobs that are over their rented time limit
//            D.moveAlong();
//
//            D.tick();
//            D.tock();
//        }
//        else {
//            D.logEnergyUse(0);
//            D.logTEnergyUse(0);
//            D.incurredCost(0);
//            D.logPrice(0);
//            D.logTPrice(0);
//            D.logTRevenue(0);
//            D.logMRevenue(0);
//            D.noFails();
//            D.noThrough();
//        }
//        S.moveLocalTime();
//        t++;
//    }
//}
//
//    public ArrayList<ArrayList<Double>> collectionSingle() {
//        ArrayList<ArrayList<Double>> holster = new ArrayList<>();
//        Interconnection i = powerGrid.get(0);
//        IsoRegion j = i.getIsoRegions().get(0);
//        State s = j.getStates().get(0);
//        DataCenter d = s.getClientele().get(0);
//        holster.add(d.getPriceLog());
//        holster.add(d.getEnergyLog());
//        holster.add(d.getRevenueLog());
//        return holster;
//    }