package spine.communication.emu;

public abstract interface SocketMessageListener {
	public abstract void messageReceived(int arg0,com.tilab.gal.Message msg);
	
}
