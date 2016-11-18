

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Steghi
 */
public class RMIPerformance 
{

    private static AuctionSystem auctionSystem = null;
    private static void RMIPerformance(String methodCall, long iterations) throws RemoteException
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
        System.out.format(iterations + " " + methodCall +" calls in %d ms - %.02f ms/" + methodCall + "\n",
                    elapsedTime, (float)elapsedTime/iterations);
        System.out.println();
    }
        
        
    public static void main(String[] args) 
    { 
        try 
        {
            AuctionSystemImpl ai = new AuctionSystemImpl();
            auctionSystem = (AuctionSystem) UnicastRemoteObject.exportObject(ai, 0);
            Naming.rebind("rmi://localhost:" + "1099" + "/AuctionService", auctionSystem);
            RMIPerformance(args[0], Long.parseLong(args[1]));
        } 
        catch (Exception ex) 
        {
            System.out.println("Failed to connect to Server");
        }
        System.exit(0);
        
    }
}
