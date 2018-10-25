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
                int day = C.getDay(i);
                System.out.println("Month " + month + " Day " + day);
                System.out.println("total transfers today: " + (tTransfer - lTrans));
                System.out.println("total transfers: " + tTransfer);
                System.out.println("total jobs in system: " + tJobs);
                lTrans = tTransfer;
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
                            int time = S.getLocalTime();
                            //If a new month started, reset budget and other metrics to start again
                            if (giveNewMonth) {
                                D.newMonth();
                            }
                            //Update price rates on an hourly basis
                            double price = 0;
                            if (t % 60 == 0) {
                                //Retrieve current energy price from authority
                                price = T.getRate(C.getHour(time));
                                D.setRate(price);
                            }
                            //More useful printouts on a daily basis
                            if (giveNewMonth) {
                                System.out.println(D);
                                System.out.println("Usage: " + D.completeUsage());
                                System.out.println("Rev last day: " + D.getRevAtm());
                                System.out.println("Total revenue: " + D.getRevenue());
                                System.out.println("Current Price: " + price);
                                System.out.print("\n");
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            //If a center has already crossed its budget threshold for the month,stop all processes
                            if (D.getBudget() > D.incurredCost()) {
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

                                //Cost/Revenue accrued by given center
                                D.incurredCost((price * (mWh)));
                                D.logPrice(price * mWh);
                                D.logRevenue(D.getRevenue() - D.getTotalCost());

                                //Offload any completed work
                                D.cleanHouse();

                                //Remove any jobs that are over their rented time limit
                                D.moveAlong();

                                //For all jobs in system, remove one minute of paid for time
                                D.tick();

                                //Mark values to track job throughput and failure rate
                                D.tock();

                                //Particpate in the market if the time is right
                                if (D.getParticipation() == 0) {

                                    //Get price projection from now till next participation time
                                    D.reasonableIndulgence();

                                    //If I want to sell jobs, make sure I have a meaningful contribution to the market
                                    if (D.isSeller()) {
                                        if (D.getOnLoad().size() > 0) {
                                            if (D.getOnLoad().get(0) > 0 && D.getOnLoad().get(1) > 0 && D.getOnLoad().get(2) > 0) {
                                                market.addSeller(D);
                                            }
                                        }
                                    } else {
                                        market.removeSeller(D);
                                    }

                                    //If im not selling then I must be buying
                                    if (D.isBuyer()) {
                                        if (D.getOffLoad().size() > 0) {
                                            //Simulate Capitalism
                                            market.silkRoad(D);
                                        }
                                    }
                                }
                            }
                            //If youre overbudget, you no longer get to play
                            else {
                                D.incurredCost(0);
                                D.logPrice(0);
                                D.logRevenue(0);
                                D.noFails();
                                D.noThrough();
                            }
                        }
                        //Manage time zone for given state
                        S.moveLocalTime();
                    }
                }
            }
            //Move the simulation ahead one minute
            t++;
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
        for (int i = 0; i < duration; i++) {
            boolean giveNewMonth = false;
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
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
                            int time = S.getLocalTime();
                            //Update price rates on an hourly basis
                            double price = 0;
                            if (t % 60 == 0) {
                                //Retrieve current energy price from authority
                                price = T.getRate(C.getHour(time));
                                D.setRate(price);
                            }
                            if (giveNewMonth) {
                                D.newMonth();
                            }
                            if (giveNewMonth) {
                                System.out.println(D);
                                System.out.println("Usage: " + D.completeUsage());
                                System.out.println("Rev last day: " + D.getRevAtm());
                                System.out.println("Total revenue: " + D.getRevenue());
                                System.out.println("Current Price: " + T.getRate(time));
                                System.out.print("\n");
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            if (D.getBudget() > D.incurredCost()) {
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
                                D.logEnergyUse(mWh);
                                D.setRate(price);
                                D.incurredCost((price * (mWh)));
                                double rev = D.getProfit() - D.getTotalCost();
                                D.logPrice(price * mWh);
                                D.logRevenue(rev);

                                //Offload any completed work
                                D.cleanHouse();

                                //Remove any jobs that are over their rented time limit
                                D.moveAlong();

                                D.tick();
                                D.tock();
                            }
                            else {
                                D.logRevenue(0);
                                D.incurredCost(0);
                                D.logPrice(0);
                                D.noFails();
                                D.noThrough();
                            }
                        }
                        S.moveLocalTime();
                    }
                }
            }
            t++;
        }
    }

    //Run the simulation with a single data center participating in a non market environment
    public void minimalist(int time){
        Calendar C = new Calendar();
        Market market = new Market();
        int lTrans = 0;
        int tTransfer = 0;
        int tJobs = 0;
        int lastMonth = -1;
        for (int i = 0; i < time; i++) {
            boolean giveNewMonth = false;
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
            }
            if ((i % 1440) == 0) {
                int month = C.getMonth(i);
                if (month > lastMonth) {
                    lastMonth = month;
                    giveNewMonth = true;
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
            Interconnection I = powerGrid.get(0);
            IsoRegion P = I.getIsoRegions().get(0);
            ISO T = P.getAuthority();
            State S = P.getStates().get(0);
            DataCenter D = S.getClientele().get(0);
            int t = S.getLocalTime();
            if (giveNewMonth) {
                D.newMonth();
            }
            //Update price rates on an hourly basis
            double price = 0;
            if (i % 60 == 0) {
                //Retrieve current energy price from authority
                price = T.getRate(C.getHour(t));
                D.setRate(price);
            }
            if (giveNewMonth) {
                System.out.println(D);
                System.out.println("Usage: " + D.completeUsage());
                System.out.println("Rev last day: " + D.getRevAtm());
                System.out.println("Total revenue: " + D.getRevenue());
                System.out.println("Current Price: " + price);
                System.out.println("Budget For Coming Month: " + D.getBudget());
                System.out.print("\n");
                D.setRevAtm();
                tTransfer += D.jobsTransfere();
                tJobs += D.getTotalJobs();
            }
            if (D.getBudget() > D.incurredCost()) {
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
                D.logEnergyUse(mWh);
                D.setRate(price);
                D.incurredCost((price * (mWh)));
                double rev = D.getProfit() - D.getTotalCost();
                D.logPrice(price * mWh);
                D.logRevenue(rev);

                //Offload any completed work
                D.cleanHouse();

                //Remove any jobs that are over their rented time limit
                D.moveAlong();

                D.tick();
                D.tock();
            }
            else {
                D.logRevenue(0);
                D.incurredCost(0);
                D.logPrice(0);
                D.noFails();
                D.noThrough();
            }
            S.moveLocalTime();
            t++;
        }
    }

    //Getter for the Power Grid
    public ArrayList<Interconnection> getPowerGrid() {
        return powerGrid;
    }

    //Aggregate and return all information about Energy, Cost, and Profit available in the simulation
    public ArrayList<ArrayList<Double>> collection() {
        ArrayList<ArrayList<Double>> holster = new ArrayList<>();
        for (Interconnection i : powerGrid) {
            for (IsoRegion j : i.getIsoRegions()) {
                for (State s : j.getStates()) {
                    for (DataCenter d : s.getClientele()) {
                        holster.add(d.getPriceLog());
                        holster.add(d.getEnergyLog());
                        holster.add(d.getRevenueLog());
                    }
                }
            }
        }
        return holster;
    }

    public ArrayList<ArrayList<Double>> collectionSingle() {
        ArrayList<ArrayList<Double>> holster = new ArrayList<>();
        Interconnection i = powerGrid.get(0);
        IsoRegion j = i.getIsoRegions().get(0);
        State s = j.getStates().get(0);
        DataCenter d = s.getClientele().get(0);
        holster.add(d.getPriceLog());
        holster.add(d.getEnergyLog());
        holster.add(d.getRevenueLog());
        return holster;
    }

    public String toString() {
        String str = "American Energy: " + "\n";
        for (Interconnection i : powerGrid) {
            str += i + "\n";
        }
        return str;
    }
}
