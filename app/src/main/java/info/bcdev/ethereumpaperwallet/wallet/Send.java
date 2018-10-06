package info.bcdev.ethereumpaperwallet.wallet;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;


import info.bcdev.librarysdkew.interfaces.callback.CBSendingEthereum;
import info.bcdev.librarysdkew.wallet.Sending;

public class Send {

    private Web3j mWeb3j;
    private Credentials mCredentials;
    private String mValueGasPrice;
    private String mValueGasLimit;

    public Send(Web3j web3j, Credentials credentials, String valueGasPrice, String valueGasLimit){
        mWeb3j = web3j;
        mCredentials = credentials;
        mValueGasPrice = valueGasPrice;
        mValueGasLimit = valueGasLimit;
    }

    public void SendEther(String toAddress, String valueAmmount, CBSendingEthereum cbSendingEthereum){
        Sending sending = new Sending(mWeb3j, mCredentials, mValueGasPrice, mValueGasLimit);
        sending.registerCallBack(cbSendingEthereum);
        sending.sendEther(toAddress, valueAmmount);
    }
}
