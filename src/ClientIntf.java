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
public interface ClientIntf  extends Remote
{
   public void callBack(String s) throws java.rmi.RemoteException;
}
