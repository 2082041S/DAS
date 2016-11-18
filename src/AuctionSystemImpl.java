/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package auctionsystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Steghi
 */
public class AuctionSystemImpl implements AuctionSystem
{
    private AtomicLong id = new AtomicLong();
    public long ownerid;
    public Map<Long, AuctionItem> auctions = new HashMap<>(); 
    public final String storageFileLocation = System.getProperty("user.dir") + "\\storage.ser";
    
    public AuctionSystemImpl()
        throws java.rmi.RemoteException 
    {
        super();
    }
    
    @Override
    public long createAuctionItem(String name, long minValue, long closeTime, long ownerid) throws RemoteException 
    {        
        AuctionItem auction = new AuctionItem(name, minValue, closeTime, id.longValue(), ownerid);
        auctions.put(auction.getId(), auction);
        id.addAndGet(1);
        return auction.getId();
    }

    @Override
    /*
    Client bids a price against an auction.
    Bid must be bigger than the highest bid (auction.getMinValue())
    Return the minimum bid price if bid failed, -2 if auction is closed;  -1 if it succeeded
    */
    public String bid(long id, long price,long ownerid) throws RemoteException 
    {
        AuctionItem auction = auctions.get(id);
        if (auction == null)
        {
            return "Auction with id " + id + " does not exist";
        }
        if (auction.hasClosed())
        {
            return "Auction has closed";
        }
        if (auction.getMinValue() >= price)
        {
            return "Your bid is too small. You need to bid more than "+price;
        }
        synchronized(this)
        {
        updateBid(id, price, ownerid);
        }

        return "You have succesfully made a bid";
    }
    
    public void updateBid(long id, long price, long ownerid) throws RemoteException 
    {
        Bid bid = new Bid(price, ownerid);
        AuctionItem auction = auctions.get(id);
        auction.addBid(bid);
        
        //updates auctionID with auction contatining the bid
        auctions.put(id, auction);
        
        System.out.println("Bid received from OwnerID "+ auction.getHighestBid().getOwnerid() + " for " + auction.getHighestBid().getPrice() + 
                "£ for item " + auction.getId() + " with name " + auction.getName());
        System.out.println("Bidders: " + auction.getBidders());       
    }

    @Override
    public List<String> listAvailableAuctionItems() throws RemoteException 
    {
        List<String> auction_list = new ArrayList<>();
        for (AuctionItem auction : auctions.values())
        {
            String openOrClosed = auction.hasClosed()?"CLOSED":"OPEN";
            auction_list.add("Auction " + auction.getId()+
                    " with name " + auction.getName() +
                    " and price " + auction.getMinValue() +
                    "£ " + openOrClosed);
        }
        return auction_list;
    }

    @Override
    public long getNextOwnerID() throws RemoteException {
        return ownerid++;
    }

    @Override
    public AuctionItemIntf getAuctionByID(long id) throws RemoteException 
    {
        if (auctions !=null)
        {
            return auctions.get(id);
        }
        else
        {
            return null;
        }
    }
        

    @Override
    public String restoreAuctions() throws RemoteException 
    {
        List<Long> auctionIDList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();       
        sb.append("\n");
        List<AuctionItem> storedAuctions  = getStoredAuctions();
        
        if (storedAuctions.isEmpty())
        {
            sb.append("Storage file is empty. Bootstraping 10 auctions for clients to bid on.");
            populateStoredAuctions(10);   
        }
        else
        {                    
            for (AuctionItem auction : getStoredAuctions())
            {        
                AuctionItem restoredAuction = new AuctionItem(auction.getName(), auction.getMinValue(), auction.getCloseTime(),auction.getId(), auction.getOwnerid());
                auctions.put(restoredAuction.getId(), restoredAuction);
                sb.append("Auction ").append(restoredAuction.getId()).
                        append(": with name'").append(restoredAuction.getName()).
                        append(" ' and price '").append(restoredAuction.getMinValue()).
                        append(" £' and close time '").append(restoredAuction.getCloseTime()).
                        append(" ' has been restored\n");
            }
            
            for (long auctionID : auctions.keySet())
            {
                if (id.longValue() < auctionID)
                {
                    id.set(auctionID);
                }
            }
            id.addAndGet(1);
            
        }
        
        return sb.toString();
    }
    
    // Warning is surpressed because we are certain that we are reading List<AuctionItem> Object (line:198)
    @SuppressWarnings("unchecked")
    public List<AuctionItem> getStoredAuctions() throws RemoteException 
    {
        List<AuctionItem> storedAuctions = new ArrayList<>();
        try  
        {
            
            FileInputStream fileIn = new FileInputStream(storageFileLocation);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            
            storedAuctions = (List<AuctionItem>) in.readObject();
            
            in.close();
            fileIn.close();

        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.out.println ("Failed to restore auctions");
        }
        return storedAuctions;
    }
    
    /*
    Creates given amunt of auctions to be stored eachone closing 20 seconds after the next one
    -1 ownerID to show they are bootstraped from the server
    */
    @Override
    public void populateStoredAuctions(long numberOfAuctions) throws RemoteException 
    {
        //dummy owner
        long dummyOwnerid = -1;
        for (long auctionID=0; auctionID< numberOfAuctions; auctionID++)
        {
            long closeTime = 10 + 20 * auctionID;
            createAuctionItem("auction" + auctionID, auctionID , closeTime, dummyOwnerid );
        }
        
    }  

    @Override
    public void pingServer() throws RemoteException {}

    @Override
    public String storeAllAuctions() throws RemoteException 
    {
        StringBuilder auctionsStoredSB = new StringBuilder();
        if (auctions == null || auctions.isEmpty())
        {
            return "There are no auctions to be stored";
        }
        
        try
        {
            FileOutputStream fout = new FileOutputStream(storageFileLocation);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            
            List<AuctionItem> items = new ArrayList<>(auctions.values());
            oos.writeObject(items);
            
            for (AuctionItem auction : auctions.values())
            {               
                auctionsStoredSB.append("Auction ").append(auction.getId()).append(" has been permanently stored\n");
            }
            
            oos.close();
            System.out.println("Storage Complete");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return ex.getMessage();
        }
        
        return auctionsStoredSB.toString();
    }
 
    @Override
    public void removeAuction(long id) 
    {
        System.out.println("Auction " + id + " expired and as a result has been removed");
        auctions.remove(id);
    }


    

    
}
