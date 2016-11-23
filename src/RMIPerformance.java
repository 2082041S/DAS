import java.util.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class RMIPerformance extends Application
{


    private static Map<Float, Long> countPerMS = new HashMap<>();
    private static AuctionSystem auctionSystem = null;
    private static String method = "";
    private static long numberOfCalls = 0;
    private static long numberOfAverageResults = 100;
    private static float averageMsPerCall = 0;
    private static String reg_host = "localhost";

    private static float callClientMethod(String methodCall, long iterations) throws RemoteException
    {
        Client client = new Client(reg_host,1099);
        auctionSystem.createAuctionItem("evaluation",0 , 1000, 0);
        long startTime = System.currentTimeMillis();
        long auctionID = 0;
        
        switch (methodCall)
        {
            case "createAuction":
                
                for (int i = 0; i < iterations; i++)
                {
                    auctionID = auctionSystem.createAuctionItem("evaluation",0 , 1000, 0);
                    AuctionItemIntf auction = auctionSystem.getAuctionByID(auctionID);
                    auction.registerClient(client, 0);
                }
                break;
                
            case "bid":
                for (int i = 0; i < iterations; i++)
                {
                    auctionSystem.bid(0 , i+1, 0);
                }
                break;
            case "showAuctions":
                for (int i = 0; i < iterations; i++)
                {
                    auctionSystem.listAvailableAuctionItems();
                }
                break;
            default:
                System.out.println("Method does not exist on server");
        }
        

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.format(iterations + " " + methodCall +" calls in %d ms - %.03f ms/" + methodCall + "\n",
                    elapsedTime, (float)elapsedTime/iterations);
        System.out.println();
        return (float)elapsedTime/iterations;
    }
        
        
    public static void main(String[] args) 
    { 
        try 
        {
            method = args[0];
            numberOfCalls = Long.parseLong(args[1]);
            numberOfAverageResults = Long.parseLong(args[2]);
            AuctionSystemImpl ai = new AuctionSystemImpl();
            auctionSystem = (AuctionSystem)
                       Naming.lookup("rmi://" + reg_host + ":" + "1099" + "/AuctionService");
            for (int i = 0; i< numberOfAverageResults; i++)
            {
                float msPerCall = callClientMethod(method, numberOfCalls );
                averageMsPerCall += msPerCall;
                if (countPerMS.containsKey(msPerCall))
                {
                    long count = countPerMS.get(msPerCall);
                    countPerMS.put(msPerCall, count+1);
                }
                else
                {
                    countPerMS.put(msPerCall, 1L);
                }
            }             
            averageMsPerCall = averageMsPerCall / numberOfAverageResults;
        } 
        catch (Exception ex) 
        {
            System.out.println();
            System.out.println(ex);
            System.exit(0);
        }    
        launch(args);        
    }

    // Used for evaluation. I make sure I pass the right argument types
    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) throws Exception 
    {
        DecimalFormat df = new DecimalFormat("#.###");
        stage.setTitle("Barchart for " + method);
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<>(xAxis,yAxis);
        bc.setTitle(numberOfAverageResults + " calls of " + method + "\n" + 
                 df.format(averageMsPerCall)+ " ms/"+method +
                         " i.e " + df.format(1.0/averageMsPerCall) + " " + method + " calls per milisecond");
        xAxis.setLabel("Milisecond");       
        yAxis.setLabel(method + " calls");
        
        XYChart.Series serie = new XYChart.Series();
        SortedSet<Float> keys = new TreeSet<>(countPerMS.keySet());
        for (Float key: keys)
        {
            serie.getData().add(new XYChart.Data(key.toString()+"ms", countPerMS.get(key)));
        }
        Scene scene  = new Scene(bc,800,600);
        bc.getData().add(serie);
        stage.setScene(scene);
        stage.show();
    }

}
