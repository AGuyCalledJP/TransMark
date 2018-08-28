import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

public class ClockWork {

    public static int t = 0; //this will be considered 12:00 am UTC
    private int month = 0; //start in january
    private ArrayList<Interconnection> powerGrid = new ArrayList<>();
    private Random rand = new Random();
    public static Calendar calendar = new Calendar();

    public ClockWork() {
        IntCon[] C = new IntCon[]{IntCon.EASTERN, IntCon.WESTERN, IntCon.TEXAS};
        String[] S = new String[]{"Eastern Interconnection", "Western Interconnection", "ERCOT"};
        for (int i = 0; i < C.length; i++) {
            powerGrid.add(new Interconnection(C[i], S[i], t));
        }
    }

    //Run the simulation
    public void motion(){
        double tPrice = 0;
        Calendar C = new Calendar();
        Market market = new Market();
        int lTrans = 0;
        int tTransfer = 0;
        int tJobs = 0;
        for (int i = 0; i < 44640; i++) { //44640 one month
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
            }
            if ((i % 1440) == 0) {
                //W.setExtremes();
                //temp = W.temperature();
                //System.out.println("Day extremes: " + W.getDayHigh() + " high, " + W.getDayLow());
                int month = C.getMonth(i);
                int day = C.getDay(i);
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
//                            System.out.println(lambda);
                            double[] temp = new double[]{0};
                            //Set short job percentage threshold
                            if ((i % 1439) == 0) {
                                System.out.println(D);
                                System.out.println("Usage: " + D.completeUsage());
                                System.out.println("Rev last day: " + D.getRevAtm());
                                System.out.println("Total revenue: " + D.getRevenue());
                                System.out.println("Current Price: " + T.getRate());
                                System.out.print("\n");
                                tPrice = 0;
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            if (D.getBudget() > D.incurredCost()) {
                                JobMaster jobMast = D.getMaster();
                                jobMast.setLambda();

//                                jobMast.simArrival(S.getLocalTime());
                                Queue<Job> hold = jobMast.genJobs();
                                D.addJobs(hold);

                                //Move jobs into clusters
                                D.reganomics();

                                //execute tasks for this minute
                                D.setProcesses();

                                D.wash();

                                //Calculate cost incurred this minute
                                double mWh = D.powerUsage();
                                double currentT = temp[0];
                                S.setTotalEnergy(mWh);
                                D.logEnergyUse(mWh);
//                                D.logEnergyUse(mWh + (mWh * .33)); //include cost of cooling
                                //System.out.println(currentT);
                                mWh += D.coolingCost(mWh);
                                double price = T.getRate();
                                D.setRate(T.getRate());
                                D.incurredCost((price * (mWh)));
                                D.logPrice(price * mWh);
                                tPrice += price * mWh;
                                D.logRevenue(D.getRevenue() - D.getTotalCost());

                                //Offload any completed work
                                D.cleanHouse();

                                //Remove any jobs that are over their rented time limit
                                D.moveAlong();

                                D.tick();

                                if (D.getParticipation() == 0) {
                                    D.reasonableIndulgence();
                                    if (D.isSeller()) {
                                        if (D.getOnLoad().size() > 0) {
                                            if (D.getOnLoad().get(0) > 0 && D.getOnLoad().get(1) > 0 && D.getOnLoad().get(2) > 0) {
                                                market.addSeller(D);
                                            }
                                        }
                                    } else {
                                        market.removeSeller(D);
                                    }
                                    if (D.isBuyer()) {
                                        if (D.getOffLoad().size() > 0) {
                                            market.silkRoad(D);
                                        }
                                    }
                                }
                            }
                            else {
                                D.incurredCost(0);
                                D.logPrice(0);
                                D.logRevenue(0);
                            }
                        }
                        S.moveLocalTime();
                    }
                }
            }
            t++;
        }
    }

    //Run the simulation
    public void lessMotion(){
        double tPrice = 0;
        Calendar C = new Calendar();
        Market market = new Market();
        int lTrans = 0;
        int tTransfer = 0;
        int tJobs = 0;
        for (int i = 0; i < 44640; i++) { //44640 one month
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
            }
            if ((i % 1440) == 0) {
                //W.setExtremes();
                //temp = W.temperature();
                //System.out.println("Day extremes: " + W.getDayHigh() + " high, " + W.getDayLow());
                int month = C.getMonth(i);
                int day = C.getDay(i);
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
//                            System.out.println(lambda);
                            double[] temp = new double[]{0};
                            //Set short job percentage threshold
                            if ((i % 1439) == 0) {
//                                System.out.println(D);
//                                System.out.println("Usage: " + D.completeUsage());
//                                System.out.println("Rev last day: " + D.getRevAtm());
//                                System.out.println("Total revenue: " + D.getRevenue());
//                                System.out.println("Current Price: " + T.getRate());
//                                System.out.print("\n");
                                tPrice = 0;
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            if (D.getBudget() > D.incurredCost()) {
                                JobMaster jobMast = D.getMaster();
                                jobMast.setLambda();

//                                jobMast.simArrival(S.getLocalTime());
                                Queue<Job> hold = jobMast.genJobs();
                                D.addJobs(hold);

                                //Move jobs into clusters
                                D.reganomics();

                                //execute tasks for this minute
                                D.setProcesses();

                                D.wash();

                                //Calculate cost incurred this minute
                                double mWh = D.powerUsage();
                                double currentT = temp[0];
                                S.setTotalEnergy(mWh);
                                D.logEnergyUse(mWh);
//                                D.logEnergyUse(mWh + (mWh * .33)); //include cost of cooling
                                //System.out.println(currentT);
                                mWh += D.coolingCost(mWh);
                                double price = T.getRate();
                                D.setRate(T.getRate());
                                D.incurredCost((price * (mWh)));
                                double rev = D.getProfit() - D.getTotalCost();
                                D.logPrice(price * mWh);
                                D.logRevenue(rev);
                                tPrice += price * mWh;

                                //Offload any completed work
                                D.cleanHouse();

                                //Remove any jobs that are over their rented time limit
                                D.moveAlong();

                                D.tick();
                            }
                            else {
                                D.logRevenue(0);
                                D.incurredCost(0);
                                D.logPrice(0);
                            }
                        }
                        S.moveLocalTime();
                    }
                }
            }
            t++;
        }
    }
    //Run the simulation
    public void VIP(){
        double tPrice = 0;
        Calendar C = new Calendar();
        ArrayList<Integer> clustSizeList = new ArrayList<>();
        ArrayList<Integer> speedList = new ArrayList<>();
        for (Interconnection I : powerGrid) {
            for (IsoRegion P : I.getIsoRegions()) {
                ISO T = P.getAuthority();
                for (State S : P.getStates()) {
                    for (DataCenter D : S.getClientele()) {
                        clustSizeList.add(D.numCluster());
                        speedList.add(D.getClustSpeed());
                    }
                }
            }
        }
        Uber uber = new Uber(clustSizeList, speedList);
        int lTrans = 0;
        int tTransfer = 0;
        int tJobs = 0;
        for (int i = 0; i < 44640; i++) { //44640 one month
            //do work
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
            }
            if ((i % 1440) == 0) {
                //W.setExtremes();
                //temp = W.temperature();
                //System.out.println("Day extremes: " + W.getDayHigh() + " high, " + W.getDayLow());
                int month = C.getMonth(i);
                int day = C.getDay(i);
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
//                            System.out.println(lambda);
                            double[] temp = new double[]{0};
                            //Set short job percentage threshold
                            if ((i % 1439) == 0) {
//                                System.out.println(D);
//                                System.out.println("Usage: " + D.completeUsage());
//                                System.out.println("Rev last day: " + D.getRevAtm());
//                                System.out.println("Total revenue: " + D.getRevenue());
//                                System.out.println("Current Price: " + T.getRate());
//                                System.out.print("\n");
                                tPrice = 0;
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            if (D.getBudget() > D.incurredCost()) {
                                if (t > 0) {
                                    //Move jobs into clusters
                                    D.reganomics();

                                    //execute tasks for this minute
                                    D.setProcesses();

                                    D.wash();

                                    //Calculate cost incurred this minute
                                    double mWh = D.powerUsage();
                                    double currentT = temp[0];
                                    S.setTotalEnergy(mWh);
                                    D.logEnergyUse(mWh);
//                                D.logEnergyUse(mWh + (mWh * .33)); //include cost of cooling
                                    //System.out.println(currentT);
                                    mWh += D.coolingCost(mWh);
                                    double price = T.getRate();
                                    D.setRate(T.getRate());
                                    D.incurredCost((price * (mWh)));
                                    double rev = D.getProfit() - D.getTotalCost();
                                    D.logPrice(price * mWh);
                                    D.logRevenue(rev);
                                    tPrice += price * mWh;

                                    //Offload any completed work
                                    D.cleanHouse();

                                    //Remove any jobs that are over their rented time limit
                                    D.moveAlong();
                                }
                                //calculate cost etc for market participation
                                D.uberEverywhere();

                                if (D.isAvailable()) {
                                    uber.participation(D);
                                }

                                D.tick();
                            }
                            else {
                                D.incurredCost(0);
                                D.logPrice(0);
                                D.logRevenue(0);
                            }
                        }
                        S.moveLocalTime();
                    }
                }
            }
            //add jobs to system
            uber.genJobs();

            //send out jobs
            uber.integration();

            t++;
        }
    }

    public void ultimatum(){
        double tPrice = 0;
        Market market = new Market();
        Calendar C = new Calendar();
        ArrayList<Integer> clustSizeList = new ArrayList<>();
        ArrayList<Integer> speedList = new ArrayList<>();
        for (Interconnection I : powerGrid) {
            for (IsoRegion P : I.getIsoRegions()) {
                ISO T = P.getAuthority();
                for (State S : P.getStates()) {
                    for (DataCenter D : S.getClientele()) {
                        clustSizeList.add(D.numCluster());
                        speedList.add(D.getClustSpeed());
                    }
                }
            }
        }
        Uber uber = new Uber(clustSizeList, speedList);
        int lTrans = 0;
        int tTransfer = 0;
        int tJobs = 0;
        for (int i = 0; i < 44640; i++) { //44640 one month
            //do work
            int hour = C.getHour(i);
            if ((i % 60) == 0) {
                System.out.println("Hour " + hour);
            }
            if ((i % 1440) == 0) {
                //W.setExtremes();
                //temp = W.temperature();
                //System.out.println("Day extremes: " + W.getDayHigh() + " high, " + W.getDayLow());
                int month = C.getMonth(i);
                int day = C.getDay(i);
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
//                            System.out.println(lambda);
                            double[] temp = new double[]{0};
                            //Set short job percentage threshold
                            if ((i % 1439) == 0) {
//                                System.out.println(D);
//                                System.out.println("Usage: " + D.completeUsage());
//                                System.out.println("Rev last day: " + D.getRevAtm());
//                                System.out.println("Total revenue: " + D.getRevenue());
//                                System.out.println("Current Price: " + T.getRate());
//                                System.out.print("\n");
                                tPrice = 0;
                                D.setRevAtm();
                                tTransfer += D.jobsTransfere();
                                tJobs += D.getTotalJobs();
                            }
                            if (D.getBudget() > D.incurredCost()) {
                                if (t > 0) {
                                    //Move jobs into clusters
                                    D.reganomics();

                                    //execute tasks for this minute
                                    D.setProcesses();

                                    D.wash();

                                    //Calculate cost incurred this minute
                                    double mWh = D.powerUsage();
                                    double currentT = temp[0];
                                    S.setTotalEnergy(mWh);
                                    D.logEnergyUse(mWh);
//                                D.logEnergyUse(mWh + (mWh * .33)); //include cost of cooling
                                    //System.out.println(currentT);
                                    mWh += D.coolingCost(mWh);
                                    double price = T.getRate();
                                    D.setRate(T.getRate());
                                    D.incurredCost((price * (mWh)));
                                    double rev = D.getProfit() - D.getTotalCost();
                                    D.logPrice(price * mWh);
                                    D.logRevenue(rev);
                                    tPrice += price * mWh;

                                    //Offload any completed work
                                    D.cleanHouse();

                                    //Remove any jobs that are over their rented time limit
                                    D.moveAlong();

                                    if (D.getParticipation() == 0) {
                                        D.reasonableIndulgence();
                                        if (D.isSeller()) {
                                            if (D.getOnLoad().size() > 0) {
                                                if (D.getOnLoad().get(0) > 0 && D.getOnLoad().get(1) > 0 && D.getOnLoad().get(2) > 0) {
                                                    market.addSeller(D);
                                                }
                                            }
                                        } else {
                                            market.removeSeller(D);
                                        }
                                        if (D.isBuyer()) {
                                            if (D.getOffLoad().size() > 0) {
                                                market.silkRoad(D);
                                            }
                                        }
                                    }
                                }
                                //calculate cost etc for market participation
                                D.uberEverywhere();

                                if (D.isAvailable()) {
                                    uber.participation(D);
                                }

                                D.tick();
                            }
                            else {
                                D.incurredCost(0);
                                D.logPrice(0);
                                D.logRevenue(0);
                            }
                        }
                        S.moveLocalTime();
                    }
                }
            }
            //add jobs to system
            uber.genJobs();

            //send out jobs
            uber.integration();

            t++;
        }
    }

    public ArrayList<Interconnection> getPowerGrid() {
        return powerGrid;
    }

    public String dataDump() {
        String str = "";
        for (Interconnection i : powerGrid) {
            for (IsoRegion j : i.getIsoRegions()) {
                for (State s : j.getStates()) {
                    for (DataCenter d : s.getClientele()) {
                        str += d.getPriceLog() + "\n";
                        str += d.getEnergyLog() + "\n";
                        str += d.getRevenueLog() + "\n";
                    }
                }
            }
        }
        return str;
    }

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

    public String centerDump() {
        String str = "";
        for (Interconnection i : powerGrid) {
            for (IsoRegion j : i.getIsoRegions()) {
                for (State s : j.getStates()) {
                    for (DataCenter d : s.getClientele()) {
                        str +=  d + "\n";
                    }
                }
            }
        }
        return str;
    }

    public String toString() {
        String str = "American Energy: " + "\n";
        for (Interconnection i : powerGrid) {
            str += i + "\n";
        }
        return str;
    }
}
