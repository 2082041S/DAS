
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionItem extends UnicastRemoteObject implements  AuctionItemIntf
{

    private long id = 0;
    private final String name;
    private long minValue;
    private final long EXPIRITYTIME = 60;
    private final long closeTime;
    private final long ownerid;
    private List<Long> bidders = new ArrayList<>();
    private Bid highestBid = null;
    private boolean isClosed = false;
    private boolean isExpired = false;
    private Map<Long,ClientIntf> clientsPartakingInTheAuction= new HashMap<>();

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
    public void registerClient(ClientIntf clientRef , long ownerid) throws RemoteException 
    {
        //System.out.println ("Client " + ownerid +  " registered");
        if (clientsPartakingInTheAuction ==null || !clientsPartakingInTheAuction.values().contains(clientRef))
        {
            clientsPartakingInTheAuction.put(ownerid, clientRef);
        }
    }



    class CloseTask extends TimerTask 
    {
        public void run() 
        {
            List<Long> clientsToRemove = new ArrayList<>();
            for (Map.Entry<Long, ClientIntf> client : clientsPartakingInTheAuction.entrySet())
            {
                ClientIntf clientRef = client.getValue();
                try {
                    clientRef.callBack("Auction " + id + ": " + name + " has closed");
                    
                    if (highestBid != null)
                    {
                        clientRef.callBack("Your id is: " + client.getKey());
                        clientRef.callBack("Client with id " + highestBid.getOwnerid() + 
                                " has won the auction for the price of " + highestBid.getPrice() + "£");
                    }
                    else
                    {
                        clientRef.callBack("Nobody bided on this auction");
                    }
                } catch (RemoteException ex) {    
                    //Client has disconnected
                    //clientsToRemove.add(client.getKey());
                    System.out.println ("Client " + client.getKey() + " is down");
                }
            }
            
//            for (long clientid : clientsToRemove)
//            {
//                clientsPartakingInTheAuction.remove(clientid);
//            }
            
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
            try 
            {
                Server.removeAuction(id);
            } 
            catch (RemoteException ex) 
            {
                System.out.println("Failed to remove auction " + id + " which just expired");
            }
            isExpired = true;
        }
    }
}    

