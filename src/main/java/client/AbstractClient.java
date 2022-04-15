package client;

import java.rmi.RemoteException;


// not using for the moment, can refactor if needed

public abstract class AbstractClient implements Client{
    protected State status = State.IDLE;

    @Override
    public boolean isBusy() throws RemoteException {
        return this.status == State.BUSY;
    }
}
