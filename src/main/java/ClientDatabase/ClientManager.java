package ClientDatabase;

public class ClientManager {
    private ClientStorageAPI clientStorage;

    public ClientManager() {
        this.clientStorage = new SimpleClientStorage();
    }


}
