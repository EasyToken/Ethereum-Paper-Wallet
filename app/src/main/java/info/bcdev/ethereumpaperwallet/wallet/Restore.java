package info.bcdev.ethereumpaperwallet.wallet;

import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.crypto.Credentials;


import info.bcdev.librarysdkew.GetCredentials;

public class Restore {

    public Credentials Get(Boolean checked, String restorevalue, String password){
        if (checked) {
            return new GetCredentials().FromPrivatKey(restorevalue);
        } else {
            try {
                return new GetCredentials().FromSeed(restorevalue, password);
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
