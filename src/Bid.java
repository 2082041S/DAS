
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Steghi
 */
public class Bid implements Serializable
{
    
    public long getPrice() {
        return price;
    }

    public long getOwnerid() {
        return ownerid;
    }
    private final long price;
    private final long ownerid;
    
    public Bid(long price, long ownerid)
    {

        this.price = price;
        this.ownerid = ownerid;
    }
    
}
