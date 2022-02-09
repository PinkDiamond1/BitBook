package de.cotto.bitbook.backend.transaction.blockchair;

import de.cotto.bitbook.backend.model.AddressTransactions;
import de.cotto.bitbook.backend.transaction.TransactionsRequestKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static de.cotto.bitbook.backend.model.AddressFixtures.ADDRESS;
import static de.cotto.bitbook.backend.model.AddressTransactionsFixtures.ADDRESS_TRANSACTIONS;
import static de.cotto.bitbook.backend.model.AddressTransactionsFixtures.ADDRESS_TRANSACTIONS_UPDATED;
import static de.cotto.bitbook.backend.model.AddressTransactionsFixtures.LAST_CHECKED_AT_BLOCK_HEIGHT;
import static de.cotto.bitbook.backend.transaction.blockchair.BlockchairAddressTransactionsFixtures.BLOCKCHAIR_ADDRESS_DETAILS;
import static de.cotto.bitbook.backend.transaction.blockchair.BlockchairAddressTransactionsFixtures.BLOCKCHAIR_ADDRESS_UPDATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockchairAddressTransactionsProviderTest {

    @InjectMocks
    private BlockchairAddressTransactionsProvider provider;

    @Mock
    private BlockchairClient blockchairClient;

    @Test
    void getAddressDetails() {
        when(blockchairClient.getAddressDetails(ADDRESS))
                .thenReturn(Optional.of(BLOCKCHAIR_ADDRESS_DETAILS));
        Optional<AddressTransactions> addressTransactions =
                provider.get(new TransactionsRequestKey(ADDRESS, LAST_CHECKED_AT_BLOCK_HEIGHT));
        assertThat(addressTransactions).contains(ADDRESS_TRANSACTIONS);
    }

    @Test
    void getUpdates() {
        when(blockchairClient.getAddressDetails(ADDRESS)).thenReturn(Optional.of(BLOCKCHAIR_ADDRESS_UPDATED));
        TransactionsRequestKey transactionsRequestKey = new TransactionsRequestKey(
                ADDRESS_TRANSACTIONS,
                ADDRESS_TRANSACTIONS_UPDATED.getLastCheckedAtBlockHeight()
        );
        Optional<AddressTransactions> updated =
                provider.get(transactionsRequestKey);
        assertThat(updated).contains(ADDRESS_TRANSACTIONS_UPDATED);
    }

    @Test
    void getUpdates_no_update_returned() {
        when(blockchairClient.getAddressDetails(ADDRESS)).thenReturn(Optional.empty());

        Optional<AddressTransactions> updated =
                provider.get(new TransactionsRequestKey(ADDRESS_TRANSACTIONS, LAST_CHECKED_AT_BLOCK_HEIGHT));

        assertThat(updated).contains(ADDRESS_TRANSACTIONS);
    }

    @Test
    void getName() {
        assertThat(provider.getName()).isEqualTo("BlockchairAddressTransactionsProvider");
    }
}