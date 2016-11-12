
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package auctionsystem;

/**
 *
 * @author Steghi
 */
public class AuctionItem extends UnicastRemoteObject implements  AuctionItemIntf
{

    private long id = 0;
    private final String name;
    private long minValue;
    private final long EXPIRITYTIME = 50;
    private final long closeTime;
    private final long ownerid;
    private List<Long> bidders = new ArrayList<>();
    private Bid highestBid = null;
    private boolean isClosed = false;
    private boolean isExpired = false;
    private List<AuctionSystemClientIntf> callbacks= new ArrayList<>();

    public boolean hasClosed() {
        return isClosed == true;
    }

    public boolean hasExpired() {
        return isExpired == true;
    }
    
    
    public List<Long> getBidders() 
    {
        return bidders;
    }

    public Bid getHighestBid() 
    {
        return highestBid;
    }

    public long getOwnerid() 
    {
        return ownerid;
    }
    
    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public long getMinValue()
    {
        return minValue;
    }


    public long getCloseTime()
    {
        return closeTime;
    }
    
    public void addBid(Bid bid)
    {
        if(!bidders.contains(bid.getOwnerid()))
        {
            bidders.add(bid.getOwnerid());
        }
        if (highestBid == null || bid.getPrice() > highestBid.getPrice())
        {
            highestBid = bid;
            minValue = bid.getPrice();
        }
    }
    
    public AuctionItem(String name, long minValue, long closeTime, long id, long ownerid) 
            throws java.rmi.RemoteException
    {
        super();
        this.ownerid = ownerid;
        this.id = id;
        this.name = name;
        this.minValue = minValue;
        this.closeTime = closeTime;
        
        Timer closeTimer = new Timer();
        closeTimer.schedule(new CloseTask(), closeTime *1000); 
    
    }   

    @Override
    public void registerClient(AuctionSystemClientIntf clientRef) throws RemoteException {
        System.out.println ("Client Registered");
        if (callbacks ==null || !callbacks.contains(clientRef))
        {
            callbacks.add(clientRef);
        }
    }



    class CloseTask extends TimerTask 
    {
        public void run() 
        {
            for (AuctionSystemClientIntf client : callbacks)
            {
                try {
                    client.callBack("Auction " + id + " of owner: "+ ownerid +" has closed");
                    
                    if (highestBid != null)
                    {
                        client.callBack("Winner is " + highestBid.getOwnerid() + " for the price of " + highestBid.getPrice());
                    }
                    else
                    {
                        client.callBack("Nobody bided on this auction");
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
            isClosed = true;
            Timer expiryTimer = new Timer();
            expiryTimer.schedule(new ExpiryTask(), EXPIRITYTIME * 1000);
        }
    }

    class ExpiryTask extends TimerTask 
    {
        @Override
        public void run() 
        {
            AuctionSystemServer.removeAuction(id);
            isExpired = true;
        }
    }
}    

