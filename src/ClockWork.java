import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Arrays;

/*
ClockWork is the discrete blueprint for how data centers act and interact, both in and of themselves, and with each-other.
Upon initialization this module populates its powerGrid array with Interconnections, ISOs, States, and Data Centers, creating
the environment for these objects to interact. Once motion (or one of motion's many relatives) is commenced, the simulation
steps through a minute by minute simulation of some predetermined time interval.
@author Jared Polonitza
 */
public class ClockWork {
    /*
    time tracking
     */
    public static int t = 0; //this will be considered 12:00 am UTC
    private int month = 1; //start in january
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
        int lastMonth = 0;
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
            if ((i % 1440) == 0 && i != 0) {
                //Check to see if month has changed based on change of day
                month = C.getMonth(i);
                if (month > lastMonth) {
                    lastMonth = month;
                    giveNewMonth = true;
                }

                //Print information about the day
                lTrans += tTransfer;
                int day = C.getDayInMonth(i);
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
                            int tempHrs = tHrs - S.zone();
//                            System.out.println("tHrs : " + tHrs);
//                            System.out.println("tempHrs : " + tempHrs);
                            double price = T.getRate(tempHrs);
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
                            //Rev for the minute set to 0
                            D.setRevAtm();

                            JobMaster jobMast = D.getMaster();
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
                            if (mWh < 0) {
                                System.out.println(mWh);
                            }
                            D.logEnergyUse(mWh);

                            //Cost/Revenue accrued by given center
                            D.incurredCost(price * mWh);
                            D.logPrice(price * mWh);
                            D.logProfit(D.getRevAtm() - (price * mWh));

                            //Offload any completed work
                            D.cleanHouse();

                            //Remove any jobs that are over their rented time limit
                            D.moveAlong();

                            //For all jobs in system, remove one minute of paid for time
                            D.tick();

                            //Mark values to track job throughput and failure rate

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
                                    if (D.getOffLoad().size() > 0 && market.anyBuyers(D)) {
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
            try {
                String month = "";
                if (this.month < 10) {
                    month = "0" + this.month;
                }
                String a = "outPutData/E/EM" + month;
                String b = "outPutData/C/CM" + month;
                String c = "outPutData/R/RM" + month;
                String d = "outPutData/P/PM" + month;
                String f = "outPutData/F/FM" + month;
                Progress.append(a,b,c,d,f);
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    /*
    This method executes the simulation with no market agent available. This is the current state of the world
    Same as motion, sans the market agent
     */
    public void lessMotion(int duration){
        Calendar C = new Calendar();
        int tJobs = 0;
        int lastMonth = 0;
        int tHrs  = 0;
        for (int i = 0; i < duration; i++) {
            boolean giveNewMonth = false;
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
                tHrs++;
            }
            if ((i % 1440) == 0 && i != 0) {
                month = C.getMonth(i);
                if (month > lastMonth) {
                    lastMonth = month;
                    giveNewMonth = true;
                }

                int day = C.getDayInMonth(i);
                System.out.println("Month " + month + " Day " + day);
                System.out.println("total jobs in system: " + tJobs);
                tJobs = 0;
            }
            for (Interconnection I : powerGrid) {
                for (IsoRegion P : I.getIsoRegions()) {
                    ISO T = P.getAuthority();
                    T.giveMonth(month);
                    for (State S : P.getStates()) {
                        for (DataCenter D : S.getClientele()) {
                            if (giveNewMonth) {
                                D.newMonth();
                                tHrs = 0;
                            }
                            //Retrieve current energy price from authority
                            int tempHrs = tHrs - S.zone();
//                            System.out.println("tHrs : " + tHrs);
//                            System.out.println("tempHrs : " + tempHrs);
                            //Update price rates on an hourly basis
                            double price = T.getRate(tempHrs);
                            if (t % 60 == 0) {
                                D.setRate(price);

                            }
                            if ((Progress.chunk == 3 && giveNewMonth) || (Progress.chunk <=2 && t % 1439 == 0)) {
                                System.out.println(D);
                                System.out.print("\n");
                                D.setRevAtm();
                                tJobs += D.getTotalJobs();
                            }

                            JobMaster jobMast = D.getMaster();
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

                            //Cost/Revenue accrued by given center
                            D.incurredCost(price * mWh);
                            D.logPrice(price * mWh);
                            D.logProfit(D.getProfit());

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
            try {
                String month = "";
                if (this.month < 10) {
                    month = "0" + this.month;
                }
                String a = "outPutData/E/ENM" + month;
                String b = "outPutData/C/CNM" + month;
                String c = "outPutData/R/RNM" + month;
                String d = "outPutData/P/PNM" + month;
                String f = "outPutData/F/FNM" + month;
                Progress.append(a,b,c,d,f);
            }
            catch (IOException e) {
                System.out.println(e);
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
