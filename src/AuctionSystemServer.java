/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package auctionsystem;

import java.rmi.Naming;	//Import naming classes to bind to rmiregistry
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionSystemServer 
{
    static int port = 1099;
    static Map<Long, AuctionItem> auctionItems = new HashMap<>();

    static void updateBid(long id, long price, long ownerid) throws RemoteException 
    {
        Bid bid = new Bid(price, ownerid);
        AuctionItem auction = auctionItems.get(id);
        auction.addBid(bid);
        auctionItems.put(id, auction);
        System.out.println("Bid received: OwnerID: "+ auction.getHighestBid().getOwnerid() + " Price: " + auction.getHighestBid().getPrice());
        System.out.println(auction.getBidders());       
    }


    
    static void addAuction(AuctionItem auction) throws RemoteException 
    {
        auctionItems. put(auction.getId(), auction);
        System.out.println("Owner id: "+auction.getOwnerid());
    }

    static Map<Long,AuctionItem> getAvailableItems() throws RemoteException
    {
        return auctionItems;
    }
    
    static AuctionItem getAuctionById(long id) throws RemoteException
    {
        return auctionItems.get(id);
    }

    static void removeAuction(long id) {
        System.out.println("Auction " + id + " expired and as a result has been removed");
        auctionItems.remove(id);
    }
    
   //calculatorserver constructor
   public AuctionSystemServer() 
   {
     
     //Construct a new CalculatorImpl object and bind it to the local rmiregistry
     //N.b. it is possible to host multiple objects on a server by repeating the
     //following method. 

     try 
     {
       	//calculator c = new calculatorimpl();
       	AuctionSystemImpl ai = new AuctionSystemImpl();
       	AuctionSystem a = (AuctionSystem) UnicastRemoteObject.exportObject(ai, 0);
       	Naming.rebind("rmi://localhost:" + port + "/AuctionService", a);
     } 
     catch (Exception e) {
       System.out.println("Server Error: " + e);
     }
   }

   
   public static void main(String args[]) {
     	//Create the new Calculator server
	if (args.length == 1)
		port = Integer.parseInt(args[0]);
	
	new AuctionSystemServer();
   }    
   
   
}
