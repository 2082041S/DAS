/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package auctionsystem;

import static java.lang.Math.abs;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Steghi
 */
public class AuctionSystemImpl implements AuctionSystem
{
   
    List<AuctionItem> auctionItems = new ArrayList<>();
    List<AuctionItem> closedItems = new ArrayList<>();
    List<AuctionItem> expiredItems = new ArrayList<>();
    List<Long> availableItems = new ArrayList<>();
    // Implementations must have an explicit constructor
    // in order to declare the RemoteException exception
    private static final float EXPIRATION_TIME = 100000;
    private long id;
    public long ownerid;

    public AuctionSystemImpl()
        throws java.rmi.RemoteException 
    {
        super();
    }
    
    @Override
    public long createAuctionItem(String name, long minValue, long closeTime, long ownerid) throws RemoteException 
    {
        id++;
        AuctionItem auction = new AuctionItem(name, minValue, closeTime, id, ownerid);
        AuctionSystemServer.addAuction(auction);
        return auction.getId();
    }

    @Override
    /*
    Client bids a price against an auction.
    Bid must be bigger than the highest bid (auction.getMinValue())
    Return the minimum bid price if bid failed, -2 if auction is closed;  -1 if it succeeded
    */
    public long bid(long id, long price,long ownerid) throws RemoteException 
    {
        AuctionItem auction = AuctionSystemServer.getAuctionById(id);
        if (auction == null  || auction.getMinValue() >= price )
            return auction.getMinValue();
        AuctionSystemServer.updateBid(id, price, ownerid);
        if (auction.hasClosed())
        {
            return -2;
        }
        return -1;
    }

    @Override
    public List<String> listAvailableAuctionItems() throws RemoteException 
    {
        
        Map<Long, AuctionItem> auctions = AuctionSystemServer.getAvailableItems();
        List<String> auction_list = new ArrayList<>();
        for (AuctionItem auction : auctions.values())
        {
            if (!auction.hasClosed())
            {
            auction_list.add("id: " + auction.getId()+
                    " name: " + auction.getName() +
                    " minValue " + auction.getMinValue());
            }
        }
        return auction_list;
    }

    @Override
    public long getNextOwnerID() throws RemoteException {
        return ownerid++;
    }

    @Override
    public AuctionItemIntf GetAuctionByID(long id) throws RemoteException 
    {
        Map<Long, AuctionItem> auctions = AuctionSystemServer.getAvailableItems();
        return auctions.get(id);
    }
    
}
