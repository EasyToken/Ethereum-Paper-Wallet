package info.bcdev.librarysdkew.wallet;

import android.os.AsyncTask;

import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import info.bcdev.librarysdkew.interfaces.callback.CBSendingEthereum;
import info.bcdev.librarysdkew.smartcontract.TokenERC20;

public class Sending {

    private Credentials mCredentials;
    private Web3j mWeb3j;
    private String fromAddress;
    private String mValueGasPrice;
    private String mValueGasLimit;

    private CBSendingEthereum cbSendingEthereum;

    public Sending(Web3j web3j, Credentials credentials, String valueGasPrice, String valueGasLimit){
        mWeb3j = web3j;
        mCredentials = credentials;
        fromAddress = credentials.getAddress();
        mValueGasPrice = valueGasPrice;
        mValueGasLimit = valueGasLimit;
    }

    private BigInteger getNonce() throws ExecutionException, InterruptedException {
        EthGetTransactionCount ethGetTransactionCount = mWeb3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    private BigInteger getGasPrice(){
        return BigInteger.valueOf(Long.valueOf(mValueGasPrice));
    }

    private BigInteger getGasLimit(){
        return BigInteger.valueOf(Long.valueOf(mValueGasLimit));
    }

    public void sendEther(String toAddress, String valueAmmount) {
        new SendEthereum().execute(toAddress, valueAmmount);
    }

    public String sendToken(String smartContractAddress, String toAddress, String valueAmmount) throws Exception {

        BigInteger ammount = Convert.toWei(valueAmmount, Convert.Unit.ETHER).toBigInteger();

        TokenERC20 token = TokenERC20.load(smartContractAddress, mWeb3j, mCredentials, getGasPrice(), getGasLimit());

        return token.transfer(toAddress, ammount).send().getTransactionHash();
    }

    private class SendEthereum extends AsyncTask<String,Void,EthSendTransaction> {

        @Override
        protected EthSendTransaction doInBackground(String... values) {
            BigInteger ammount = Convert.toWei(values[1], Convert.Unit.ETHER).toBigInteger();
            try {

                RawTransaction rawTransaction = RawTransaction.createEtherTransaction(getNonce(), getGasPrice(), getGasLimit(), values[0], ammount);

                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, mCredentials);
                String hexValue = "0x"+ Hex.toHexString(signedMessage);

                return mWeb3j.ethSendRawTransaction(hexValue.toString()).sendAsync().get();

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(EthSendTransaction result) {
            super.onPostExecute(result);
            cbSendingEthereum.backSendEthereum(result);
        }
    }

    public void registerCallBack(CBSendingEthereum cbSendingEthereum){
        this.cbSendingEthereum = cbSendingEthereum;
    }

}
