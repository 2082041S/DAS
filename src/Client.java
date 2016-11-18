/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package auctionsystem;

/*
	Code: Calculator client		calculatorClient.java
	

	Simple client program that remotely calls a set of arithmetic
	methods available on the remote calculatorimpl object

*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;			//Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException;	//Import the RemoteException class so you can catch it
import java.net.MalformedURLException;	//Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException;	//Import the NotBoundException class so you can catch it
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Client extends UnicastRemoteObject implements ClientIntf
{
    private static long ownerid;
    private static AuctionSystem auctionSystem = null;
            
    public Client(String reg_host, int reg_port) throws RemoteException 
    {
        super();
        // Create the reference to the remote object through the rmiregistry	
        try
        {
        auctionSystem = (AuctionSystem)
                       Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/AuctionService");
        }
        catch (MalformedURLException murle) 
        {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
            System.exit(0);
        }
        catch (RemoteException re) 
        {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
            System.exit(0);
        }
        catch (NotBoundException nbe) 
        {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
            System.exit(0);
        }
        Timer failureDetectorTimer = new Timer();
        failureDetectorTimer.schedule(new failureDetectorTask(), 1000 , 1000);
        
    }

    
    public static void main(String[] args) 
    { 
    Client client = null;   
    String reg_host = "localhost";
    int reg_port = 1099;

    if (args.length == 1) 
    {
     reg_port = Integer.parseInt(args[0]);
    } 
    else if (args.length == 2) 
    {
     reg_host = args[0];
     reg_port = Integer.parseInt(args[1]);
    }
    
    try 
    {    
        client = new Client(reg_host, reg_port);        
        ownerid = auctionSystem.getNextOwnerID();
        System.out.println("Please choose one of the following actions: createAuction, bid, showAuctions, storeAllAuctions");
        System.out.println("In order to create an auction type in: createAuction <name> <minValue> <closeTime>");
        System.out.println("In order to place a bid type in: bid <auctionID> <value>");
        System.out.println("In order to check all available auctions type in: showAuctions");
        System.out.println("In order to store all created auctions permanently on the server type in: storeAllAuctions");
        System.out.println("If you want to quit type in: quit " );
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        while (!(line = br.readLine()).startsWith("quit"))
        {
            String[] words = line.split(" ");
            String command = words[0];
            switch (command)
            {
                case "createAuction":
                    String name = words[1];
                    try
                    {
                        long minValue = Long.parseLong(words[2]);
                        long closeTime = Long.parseLong(words[3]);
                        if (minValue >=0 && closeTime >= 0)
                        {
                            long auctionID = auctionSystem.createAuctionItem(name, minValue, closeTime,ownerid);
                            AuctionItemIntf auction = auctionSystem.getAuctionByID(auctionID);
                            System.out.println("You have succesfully created an auction. The ID of the auction is "+ auctionID);
                            auction.registerClient(client, ownerid);

                        }
                        else
                        {
                            System.out.println("The minimum bid price and the close time need to be positive. Please try again.");
                        }                       
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("Wrong input. minimum bid price and close time must be integers.");
                        System.out.println("Please try again: createAuction <name> <minValue> <closeTime>");
                    }
                    break;

                case "bid":          
                    try
                    {
                        long auctionID = Long.parseLong(words[1]);
                        long value = Long.parseLong(words[2]);
                        String returnMessage = auctionSystem.bid(auctionID, value, ownerid);                   
                        if (returnMessage.equals("You have succesfully made a bid"))
                        {
                            AuctionItemIntf auctionIntf =  auctionSystem.getAuctionByID(auctionID);
                            auctionIntf.registerClient(client, ownerid);
                        }
                        System.out.println(returnMessage);                        
                    }
                    catch (Exception e)
                    {
                        System.out.println("Wrong input. Auction id and bid price must be positive integers.");
                        System.out.println("Please try again: createAuction <name> <minValue> <closeTime>");
                    }
                    break;

                case "showAuctions":
                    List<String> auctions = auctionSystem.listAvailableAuctionItems();
                    if (!auctions.isEmpty())
                    {
                        System.out.println("Here are the available auctions:");

                        for (String auction : auctions)
                            System.out.println(auction);                        
                    }
                    else
                    {
                        System.out.println("There are no available auctions");
                    }
                    break;
                  
                case "storeAllAuctions":
                    String returnedMessage = auctionSystem.storeAllAuctions();
                    System.out.println(returnedMessage);
                    break;
                                       
                default:
                    System.out.println("Please choose one of the following actions: createAuction, bid, showAuctions, storeAllAuctions");
            }
        }  
        // client has written "quit" => close bufferedReader and exit
         br.close();
         System.exit(0);
        }
        catch (RemoteException re) 
        {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
            System.exit(0);
        }
        catch (IOException io) 
        {
            System.out.println();
            System.out.println("IOException");
            System.out.println(io);
            System.exit(0);
        }
    }

    @Override
    public void callBack(String s) throws RemoteException {
        System.out.println(s);
    }

    class failureDetectorTask extends TimerTask 
    {
       
        @Override
        public void run() 
        { 
            try 
            {
                auctionSystem.pingServer();
            } 
            catch (RemoteException ex) 
            {
                System.out.println();
                System.out.println("Server is down");
                System.out.println(ex);
                System.exit(0);
            }
        }
    }
}
