/*
Statistical aggregation module
@author Jared Polonitza
 */
public class Stats {
    ClockWork c;

    public Stats(ClockWork c) {
        this.c = c;
    }

    public String results() {
        String str = "";
        double total1 = 0;
        double total2 = 0;
        double total3 = 0;
        double total4 = 0;
        double total5 = 0;
        double jobsRej1 = 0;
        double jobsRej2 = 0;
        double jobsRej3 = 0;
        double jobsRej4 = 0;
        double jobsRej5 = 0;
        double jobsSent1 = 0;
        double jobsSent2 = 0;
        double jobsSent3 = 0;
        double jobsSent4 = 0;
        double jobsSent5 = 0;
        double jobsReceived1 = 0;
        double jobsReceived2 = 0;
        double jobsReceived3 = 0;
        double jobsReceived4 = 0;
        double jobsReceived5 = 0;
        double jobsIncurred1 = 0;
        double jobsIncurred2 = 0;
        double jobsIncurred3 = 0;
        double jobsIncurred4 = 0;
        double jobsIncurred5 = 0;
        double jobsDone1 = 0;
        double jobsDone2 = 0;
        double jobsDone3 = 0;
        double jobsDone4 = 0;
        double jobsDone5 = 0;
        double budget1 = 0;
        double budget2 = 0;
        double budget3 = 0;
        double budget4 = 0;
        double budget5 = 0;
        double totalCost1 = 0;
        double totalCost2 = 0;
        double totalCost3 = 0;
        double totalCost4 = 0;
        double totalCost5 = 0;
        double totalRev1 = 0;
        double totalRev2 = 0;
        double totalRev3 = 0;
        double totalRev4 = 0;
        double totalRev5 = 0;
        double totalProfit1 = 0;
        double totalProfit2 = 0;
        double totalProfit3 = 0;
        double totalProfit4 = 0;
        double totalProfit5 = 0;
        double energyUsed = 0;
        for (Interconnection I : c.getPowerGrid()) {
            for (IsoRegion R : I.getIsoRegions()) {
                for (State S : R.getStates()) {
                    for (DataCenter D : S.getClientele()) {
                        if (D.numCluster() == 1) {
                            total1++;
                            jobsRej1 += D.getJobsRejected();
                            jobsSent1 += D.getJobsSent();
                            jobsReceived1 += D.getJobsRecieved();
                            jobsIncurred1 += D.getTotalJobs();
                            jobsDone1 += D.getJobsProcessed();
                            budget1 += D.getBudget();
                            totalCost1 += D.getTotalCost();
                            totalRev1 += D.getRevenue();
                            totalProfit1 += D.getProfit();
                        }
                        else if (D.numCluster() == 2) {
                            total2++;
                            jobsRej2 += D.getJobsRejected();
                            jobsSent2 += D.getJobsSent();
                            jobsReceived2 += D.getJobsRecieved();
                            jobsIncurred2 += D.getTotalJobs();
                            jobsDone2 += D.getJobsProcessed();
                            budget2 += D.getBudget();
                            totalCost2 += D.getTotalCost();
                            totalRev2 += D.getRevenue();
                            totalProfit2 += D.getProfit();
                        }
                        else if (D.numCluster() == 3) {
                            total3++;
                            jobsRej3 += D.getJobsRejected();
                            jobsSent3 += D.getJobsSent();
                            jobsReceived3 += D.getJobsRecieved();
                            jobsIncurred3 += D.getTotalJobs();
                            jobsDone3 += D.getJobsProcessed();
                            budget3 += D.getBudget();
                            totalCost3 += D.getTotalCost();
                            totalRev3 += D.getRevenue();
                            totalProfit3 += D.getProfit();
                        }
                        else if (D.numCluster() == 4) {
                            total4++;
                            jobsRej4 += D.getJobsRejected();
                            jobsSent4 += D.getJobsSent();
                            jobsReceived4 += D.getJobsRecieved();
                            jobsIncurred4 += D.getTotalJobs();
                            jobsDone4 += D.getJobsProcessed();
                            budget4 += D.getBudget();
                            totalCost4 += D.getTotalCost();
                            totalRev4 += D.getRevenue();
                            totalProfit4 += D.getProfit();
                        }
                        else {
                            total5++;
                            jobsRej5 += D.getJobsRejected();
                            jobsSent5 += D.getJobsSent();
                            jobsReceived5 += D.getJobsRecieved();
                            jobsIncurred5 += D.getTotalJobs();
                            jobsDone5 += D.getJobsProcessed();
                            budget5 += D.getBudget();
                            totalCost5 += D.getTotalCost();
                            totalRev5 += D.getRevenue();
                            totalProfit5 += D.getProfit();
                        }
                    }
                    energyUsed += S.getTotalEnergy();
                }
            }
        }
        jobsRej1 = jobsRej1 / total1;
        jobsSent1 = jobsSent1 / total1;
        jobsReceived1 = jobsReceived1 / total1;
        jobsIncurred1 = jobsIncurred1 / total1;
        jobsDone1 = jobsDone1 / total1;
        budget1 = budget1 / total1;
        totalCost1 = totalCost1 / total1;
        totalRev1 = totalRev1 / total1;
        totalProfit1 = totalProfit1 / total1;
        jobsRej2 = jobsRej2 / total2;
        jobsSent2 = jobsSent2 / total2;
        jobsReceived2 = jobsReceived2 / total2;
        jobsIncurred2 = jobsIncurred2 / total2;
        jobsDone2 = jobsDone2 / total2;
        budget2 = budget2 / total2;
        totalCost2 = totalCost2 / total2;
        totalRev2 = totalRev2 / total2;
        totalProfit2 = totalProfit2 / total2;
        jobsRej3 = jobsRej3 / total3;
        jobsSent3 = jobsSent3 / total3;
        jobsReceived3 = jobsReceived3 / total3;
        jobsIncurred3 = jobsIncurred3 / total3;
        jobsDone3 = jobsDone3 / total3;
        budget3 = budget3 / total3;
        totalCost3 = totalCost3 / total3;
        totalRev3 = totalRev3 / total3;
        totalProfit3 = totalProfit3 / total3;
        jobsRej4 = jobsRej4 / total4;
        jobsSent4 = jobsSent4 / total4;
        jobsReceived4 = jobsReceived4 / total4;
        jobsIncurred4 = jobsIncurred4 / total4;
        jobsDone4 = jobsDone4 / total4;
        budget4 = budget4 / total4;
        totalCost4 = totalCost4 / total4;
        totalRev4 = totalRev4 / total4;
        totalProfit4 = totalProfit4 / total4;
        jobsRej5 = jobsRej5 / total5;
        jobsSent5 = jobsSent5 / total5;
        jobsReceived5 = jobsReceived5 / total5;
        jobsIncurred5 = jobsIncurred5 / total5;
        jobsDone5 = jobsDone5 / total5;
        budget5 = budget5 / total5;
        totalCost5 = totalCost5 / total5;
        totalRev5 = totalRev5 / total5;
        totalProfit5 = totalProfit5 / total5;

        str += "Average Jobs Failed Single Cluster: " + jobsRej1 + "\n";
        str += "Average Jobs Sent Single Cluster: " + jobsSent1 + "\n";
        str += "Average Jobs Recieved Single Cluster: " + jobsReceived1 + "\n";
        str += "Average Jobs Incurred Single Cluster: " + jobsIncurred1 + "\n";
        str += "Average Jobs Done Single Cluster: " + jobsDone1 + "\n";
        str += "Average Budget Single Cluster: " + budget1 + "\n";
        str += "Average Cost Single Cluster: " + totalCost1 + "\n";
        str += "Average Revenue Single Cluster: " + totalRev1 + "\n";
        str += "Average Profit Single Cluster: " + totalProfit1 + "\n \n";

        str += "Average Jobs Failed Two Clusters: " + jobsRej2 + "\n";
        str += "Average Jobs Sent Two Clusters: " + jobsSent2 + "\n";
        str += "Average Jobs Recieved Two Clusters: " + jobsReceived2 + "\n";
        str += "Average Jobs Incurred Two Clusters: " + jobsIncurred2 + "\n";
        str += "Average Jobs Done Two Clusters: " + jobsDone2 + "\n";
        str += "Average Budget Two Clusters: " + budget2 + "\n";
        str += "Average Cost Two Clusters: " + totalCost2 + "\n";
        str += "Average Revenue Two Clusters: " + totalRev2 + "\n";
        str += "Average Profit Two Clusters: " + totalProfit2 + "\n \n";

        str += "Average Jobs Failed Three Clusters: " + jobsRej3 + "\n";
        str += "Average Jobs Sent Three Clusters: " + jobsSent3 + "\n";
        str += "Average Jobs Recieved Three Clusters: " + jobsReceived3 + "\n";
        str += "Average Jobs Incurred Three Clusters: " + jobsIncurred3 + "\n";
        str += "Average Jobs Done Three Clusters: " + jobsDone3 + "\n";
        str += "Average Budget Three Clusters: " + budget3 + "\n";
        str += "Average Cost Three Clusters: " + totalCost3 + "\n";
        str += "Average Revenue Three Clusters: " + totalRev3 + "\n";
        str += "Average Profit Three Clusters: " + totalProfit3 + "\n \n";

        str += "Average Jobs Failed Four Clusters: " + jobsRej4 + "\n";
        str += "Average Jobs Sent Four Clusters: " + jobsSent4 + "\n";
        str += "Average Jobs Recieved Four Clusters: " + jobsReceived4 + "\n";
        str += "Average Jobs Incurred Four Clusters: " + jobsIncurred4 + "\n";
        str += "Average Jobs Done Four Clusters: " + jobsDone4 + "\n";
        str += "Average Budget Four Clusters: " + budget4 + "\n";
        str += "Average Cost Four Clusters: " + totalCost4 + "\n";
        str += "Average Revenue Four Clusters: " + totalRev4 + "\n";
        str += "Average Profit Four Clusters: " + totalProfit4 + "\n \n";

        str += "Average Jobs Failed Five Clusters: " + jobsRej5 + "\n";
        str += "Average Jobs Sent Five Clusters: " + jobsSent5 + "\n";
        str += "Average Jobs Recieved Five Clusters: " + jobsReceived5 + "\n";
        str += "Average Jobs Incurred Five Clusters: " + jobsIncurred5 + "\n";
        str += "Average Jobs Done Five Clusters: " + jobsDone5 + "\n";
        str += "Average Budget Five Clusters: " + budget5 + "\n";
        str += "Average Cost Five Clusters: " + totalCost5 + "\n";
        str += "Average Revenue Five Clusters: " + totalRev5 + "\n";
        str += "Average Profit Five Clusters: " + totalProfit5 + "\n \n";

        str += "Total Energy Used: " + energyUsed + "\n";

        return str;
    }
}
