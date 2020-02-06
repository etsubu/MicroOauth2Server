package ClientDatabase;

import Clients.Client;

import java.util.HashMap;
import java.util.Optional;

/**
 * Small in memory client storage which does not sync to disk. Used for testing
 * @author etsubu
 */
public class SimpleClientStorage implements ClientStorageAPI {
    private final HashMap<String, Client> clients;

    /**
     * Initializes SimpleClientStorage
     */
    public SimpleClientStorage() {
        this.clients = new HashMap<>();
    }

    public boolean addClient(Client client) {
        synchronized (clients) {
            clients.put(client.getClientId(), client);
            return true;
        }
    }

    public boolean removeClient(String clientId) {
        synchronized (clients) {
            clients.remove(clientId);
            return true;
        }
    }

    public Optional<Client> queryClient(String clientId) {
        synchronized (clients) {
            return Optional.ofNullable(clients.get(clientId));
        }
    }
}
