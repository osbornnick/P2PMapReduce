package Client;

import java.rmi.RemoteException;

public abstract class AbstractClient implements Client{
    protected State status = State.IDLE;

    @Override
    public boolean isBusy() throws RemoteException {
        return this.status == State.BUSY;
    }
}
