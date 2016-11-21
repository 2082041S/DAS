

import java.util.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.*;
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
    private static float callClientMethod(String methodCall, long iterations) throws RemoteException
    {
        long startTime = System.currentTimeMillis();
        long auctionID = 0;
        
        Client client = null;
        switch (methodCall)
        {
            case "createAuction":
                client = new Client("localhost",1099);
                auctionSystem.createAuctionItem("evaluation",0 , 1000, 0);
                for (int i = 0; i < iterations; i++)
                {
                    auctionID = auctionSystem.createAuctionItem("evaluation",0 , 1000, 0);
                    AuctionItemIntf auction = auctionSystem.getAuctionByID(auctionID);
                    auction.registerClient(client, 0);
                }
                break;
                
            case "bid":
                auctionSystem.bid(0 , 1000, 0);
                for (int i = 0; i < iterations; i++)
                {
                    auctionSystem.bid(0 , 1000, 0);
                }
                break;
            case "showAuctions":
                auctionSystem.listAvailableAuctionItems();
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
            AuctionSystemImpl ai = new AuctionSystemImpl();
            auctionSystem = (AuctionSystem) UnicastRemoteObject.exportObject(ai, 0);
            Naming.rebind("rmi://localhost:" + "1099" + "/AuctionService", auctionSystem);
            for (int i = 0; i< 100; i++)
            {
                float msPerCall = callClientMethod(method, numberOfCalls );
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
        stage.setTitle(method + " calls per milisecond bar chart");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<>(xAxis,yAxis);
        bc.setTitle(numberOfCalls + " calls of " + method);
        xAxis.setLabel("Milisecond");       
        yAxis.setLabel(method + " calls");
        
        XYChart.Series serie = new XYChart.Series();
        SortedSet<Float> keys = new TreeSet<>(countPerMS.keySet());
        for (Float key: keys)
        {
            serie.getData().add(new XYChart.Data(key.toString(), countPerMS.get(key)));
            
        }
        Scene scene  = new Scene(bc,800,600);
        bc.getData().add(serie);
        stage.setScene(scene);
        stage.show();
    }

}
