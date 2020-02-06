package ClientDatabase;

import Clients.Client;

import java.util.Optional;

/**
 * Interface for accessing the client storage. Does not validate permissions
 */
public interface ClientStorageAPI {
    /**
     * Queries client from the client storage
     * @param clientId Client ID to search for
     * @return Client matching the given ID if one was found
     */
    Optional<Client> queryClient(String clientId);

    /**
     * Removes existing client from storage
     * @param clientId Client ID to remove
     * @return True if a client was removed, false if none was removed
     */
    boolean removeClient(String clientId);

    /**
     * Adds a new client to the storage
     * @param client Client to add
     * @return True if the client was added, false if not
     */
    boolean addClient(Client client);
}
