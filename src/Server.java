/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package auctionsystem;

import java.io.Serializable;
import java.rmi.Naming;	//Import naming classes to bind to rmiregistry
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server
{
    static int port = 1099;
    static AuctionSystem auctionSystem = null; 
               
   public Server() 
   {
     try 
     {
       	AuctionSystemImpl ai = new AuctionSystemImpl();
       	auctionSystem = (AuctionSystem) UnicastRemoteObject.exportObject(ai, 0);
       	Naming.rebind("rmi://localhost:" + port + "/AuctionService", auctionSystem);
        System.out.println(auctionSystem.restoreAuctions());
        
     } 
     catch (Exception e) 
     {
       System.out.println("Server Error: " + e);
     }

   }

   // wrapper for auctionItem to use in order to remove itself from auction list once it expires.
   static void removeAuction(long auctionID) throws RemoteException
   {
       auctionSystem.removeAuction(auctionID);
   }
   
   public static void main(String args[]) 
   {
	if (args.length == 1)
		port = Integer.parseInt(args[0]);
	
        new Server();
   }    






   
}
