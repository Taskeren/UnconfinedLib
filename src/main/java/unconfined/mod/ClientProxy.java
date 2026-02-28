package unconfined.mod;

import java.util.function.Supplier;

@SuppressWarnings("unused") // used dynamically
public class ClientProxy extends CommonProxy {
    @Override
    public <T> T runSided(Supplier<T> serverSide, Supplier<T> clientSide) {
        return clientSide.get();
    }
}
