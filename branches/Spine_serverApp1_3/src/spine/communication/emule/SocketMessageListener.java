package spine.communication.emule;

public abstract interface SocketMessageListener {
	public abstract void messageReceived(int arg0,com.tilab.gal.Message msg);
	
}
