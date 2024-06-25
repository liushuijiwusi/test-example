package component;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionSynchronizationHelper {

    public static void doAfterCompletion(TransactionSyncTask transactionSyncTask) {

        if (TransactionSynchronizationManager.isActualTransactionActive()) {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {

                    TransactionSynchronization.super.afterCompletion(status);

                    if (status == STATUS_COMMITTED) {

                        transactionSyncTask.afterCommit();

                    } else if (status == STATUS_ROLLED_BACK) {

                        transactionSyncTask.afterRollback();
                    }
                }
            });
        }
    }
}
