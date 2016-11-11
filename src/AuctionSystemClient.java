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

public class AuctionSystemClient extends UnicastRemoteObject implements AuctionSystemClientIntf,Runnable
{
    private static long ownerid;
    public AuctionSystemClient() throws RemoteException 
    {
        super();
    }

    
    public static void main(String[] args) 
    { 
    String reg_host = "localhost";
    int reg_port = 1099;

    if (args.length == 1) 
    {
     reg_port = Integer.parseInt(args[0]);
   } else if (args.length == 2) 
   {
     reg_host = args[0];
     reg_port = Integer.parseInt(args[1]);
   }
    try 
    {
        // Create the reference to the remote object through the remiregistry			
        AuctionSystem a = (AuctionSystem)
                       //Naming.lookup("rmi://localhost/CalculatorService");
                       Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/AuctionService");

        AuctionItemIntf auctionIntf = (AuctionItemIntf) Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/AuctionService");
        
        ownerid = a.getNextOwnerID();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        System.out.println("Please choose one of the following actions: createAuction, bid, listAvailableAuctions");
        System.out.println("In order to create an auction type in: createAuction <name> <minValue> <closeTime>");
        System.out.println("In order to place a bid type in: bid <auctionID> <value>");
        System.out.println("In order to check all available auctions type in: listAvailableAuctions");
        System.out.println("If you want to quit type in: quit " );
        while (!(line = br.readLine()).startsWith("quit"))
        {
            String[] words = line.split(" ");
            String command = words[0];
            switch (command)
            {
                case "createAuction":
                    String name = words[1];

                    long minValue = Long.parseLong(words[2]);
                    long closeTime = Long.parseLong(words[3]);
                    if (minValue >=0 && closeTime >= 0)
                    {
                        long id = a.createAuctionItem(name, minValue, closeTime,ownerid);
                        System.out.println("You have succesfully created an auction. The ID of the auction is "+ id);
                        auctionIntf.registerClient(new AuctionSystemClient());
                    }
                    else
                    {
                        System.out.println("The minimum value and the close time need to be positive. Please try again.");
                    }
                    break;

                case "bid":                      
                    long auctionID = Long.parseLong(words[1]);
                    long value = Long.parseLong(words[2]);
                    long price = a.bid(auctionID, value, ownerid);
                    if (price == -1)
                    {
                        System.out.println("You have succesfully made a bid");                        
                        auctionIntf.registerClient(new AuctionSystemClient());
                    }
                    else
                    {
                        System.out.println("Auction ID is wrong or your bid is too small. You need to bid more than "+price);
                    }
                    break;

                case "listAvailableItems":
                    System.out.println("Here are the available auctions:");
                    List<String> auctions = a.listAvailableAuctionItems();
                    for (String auction : auctions)
                        System.out.println(auction);

                    break;

                default:

            }
        }  
         br.close();
        }
        // Catch the exceptions that may occur - rubbish URL, Remote exception
	// Not bound exception or the arithmetic exception that may occur in 
	// one of the methods creates an arithmetic error (e.g. divide by zero)
	catch (MalformedURLException murle) 
        {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        }
        catch (RemoteException re) 
        {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }
        catch (NotBoundException nbe) 
        {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }
        catch (java.lang.ArithmeticException ae) 
        {
            System.out.println();
            System.out.println("java.lang.ArithmeticException");
            System.out.println(ae);
        }  
        catch (IOException io) 
        {
            System.out.println();
            System.out.println("IOException");
            System.out.println(io);
        }
    }


    @Override
    public void run() 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void callBack(String s) throws RemoteException {
        System.out.println(s);
    }
}
