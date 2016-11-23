import java.util.List;

public interface AuctionSystem extends java.rmi.Remote
{
    
    public long createAuctionItem(String name, long minValue, long closeTime, long ownerid)
            throws java.rmi.RemoteException;
    
    public String bid(long id, long price, long ownerid) 
            throws java.rmi.RemoteException;

    public List<String> listAvailableAuctionItems()
            throws java.rmi.RemoteException;
    
    public AuctionItemIntf getAuctionByID(long id)
            throws java.rmi.RemoteException;

    public long getNextOwnerID()
            throws java.rmi.RemoteException;
    
    public String storeAllAuctions()
            throws java.rmi.RemoteException;
    
    public String restoreAuctions()
            throws java.rmi.RemoteException;

    public  void pingServer()
                throws java.rmi.RemoteException;

    public void populateStoredAuctions(long i)
            throws java.rmi.RemoteException;
    
    public void removeAuction(long id)
            throws java.rmi.RemoteException;
    
}
