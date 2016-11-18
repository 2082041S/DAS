/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Steghi
 */
import java.rmi.*;

public interface AuctionItemIntf extends Remote
{
    public void registerClient(ClientIntf clientRef, long ownerid) throws java.rmi.RemoteException;   
}
