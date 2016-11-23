import java.rmi.*;

public interface ClientIntf  extends Remote
{
   public void callBack(String s) throws java.rmi.RemoteException;
}
