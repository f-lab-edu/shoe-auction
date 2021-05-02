package com.flab.shoeauction.common.db;

import static com.flab.shoeauction.common.utils.constants.DataSourceConstants.MASTER;
import static com.flab.shoeauction.common.utils.constants.DataSourceConstants.SLAVE;
import static org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return isCurrentTransactionReadOnly() ? SLAVE : MASTER;
    }
}