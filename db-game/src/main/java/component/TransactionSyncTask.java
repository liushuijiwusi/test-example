package component;

public interface TransactionSyncTask {

    void afterCommit();

    default void afterRollback(){}
}
