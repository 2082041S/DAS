import java.io.Serializable;

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
