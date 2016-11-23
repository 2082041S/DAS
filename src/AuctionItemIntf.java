import java.rmi.*;

public interface AuctionItemIntf extends Remote
{
    public void registerClient(ClientIntf clientRef, long ownerid) throws java.rmi.RemoteException;   
}
