/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package auctionsystem;

import java.util.List;

/*
	Code: Calculator Interface	calculator.java
	

	The calculator interface provides a description of the 5 remote 
	arithmetic methods available as part of the service provided
	by the remote object calculatorimpl. Note carefully that the interface
	extends remote and each methods throws a remote exception.
*/
public interface AuctionSystem extends java.rmi.Remote
{
    
    public long createAuctionItem(String name, long minValue, long closeTime, long ownerid)
            throws java.rmi.RemoteException;
    
    public long bid(long id, long price, long ownerid) 
            throws java.rmi.RemoteException;

    public List<String> listAvailableAuctionItems()
            throws java.rmi.RemoteException;
    
    public AuctionItemIntf GetAuctionByID(long id)
            throws java.rmi.RemoteException;

    public long getNextOwnerID()
            throws java.rmi.RemoteException;
}
