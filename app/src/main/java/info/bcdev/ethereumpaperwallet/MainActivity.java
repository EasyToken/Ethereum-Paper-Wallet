package info.bcdev.ethereumpaperwallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import info.bcdev.ethereumpaperwallet.wallet.Generation;
import info.bcdev.ethereumpaperwallet.wallet.Restore;
import info.bcdev.ethereumpaperwallet.wallet.Send;
import info.bcdev.librarysdkew.interfaces.callback.CBBip44;
import info.bcdev.librarysdkew.interfaces.callback.CBSendingEthereum;
import info.bcdev.librarysdkew.utils.InfoDialog;
import info.bcdev.librarysdkew.utils.ToastMsg;
import info.bcdev.librarysdkew.utils.identicon.BlockiesIdenticon;
import info.bcdev.librarysdkew.utils.identicon.Identicon;
import info.bcdev.librarysdkew.utils.qr.ScanIntegrator;
import info.bcdev.librarysdkew.wallet.Balance;
import info.bcdev.librarysdkew.web3j.Initiate;

public class MainActivity extends AppCompatActivity implements CBBip44, CBSendingEthereum {

    private TextView mAddress, mBalance;
    private EditText mPassword, mRestorevalue, mToaddress, mGasPrice, mGasLimit, mAmmount;

    private Switch btn_switch;

    private Boolean mChecked = false;

    private IntentIntegrator qrScan;

    private String mQRScanActive;

    private BlockiesIdenticon mIdenticonaddress, mIdenticontoaddress;

    private Web3j mWeb3j;
    private Credentials mCredentials;

    private ToastMsg toastMsg;

    private InfoDialog infoDialog;

    private String mUrlNode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadLogo();

        qrScan = new IntentIntegrator(this);

        mAddress = (TextView) findViewById(R.id.address);
        mBalance = (TextView) findViewById(R.id.balance);
        mPassword = (EditText) findViewById(R.id.password);
        mRestorevalue = (EditText) findViewById(R.id.restorevalue);
        mToaddress = (EditText) findViewById(R.id.toaddress);
        mAmmount = (EditText) findViewById(R.id.ammount);

        mGasPrice = (EditText) findViewById(R.id.gasprce);
        mGasLimit = (EditText) findViewById(R.id.gaslimit);

        mIdenticonaddress = (BlockiesIdenticon) findViewById(R.id.identiconaddress);
        mIdenticontoaddress = (BlockiesIdenticon) findViewById(R.id.identicontoaddress);

        btn_switch = (Switch) findViewById(R.id.btn_switch);
        btn_switch.setChecked(false);

        toastMsg = new ToastMsg();

        onCheckedListener();

        setWeb3j();

        infoDialog = new InfoDialog(this);

    }

    private void LoadLogo(){
        // Logo in ActionBar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setIcon(R.mipmap.ic_launcher);
    }

    public void onClick(View view){

        switch (view.getId()) {
            case R.id.btn_copy_address:
                copyToClipBoard(getAddress());
                return;
            case R.id.btn_qrscan_value:
                startScan("value");
                break;
            case R.id.btn_genwallet:
                Generate();
                return;
            case R.id.btn_recovery:
                Restore();
                return;
            case R.id.btn_qrscan_toaddress:
                startScan("toaddress");
                break;
            case R.id.btn_send:
                SendEthereum(getToAddress(), getAmmount());
                break;
            case R.id.btn_copy_value:
                copyToClipBoard(getRestoreValue());
                break;
        }
    }

    private void copyToClipBoard(String value){
        if (value != null) {
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("copytoclipboard", value);
            clipboard.setPrimaryClip(clip);
            toastMsg.Short(this, "Copied to the clipboard");
        } else {
            toastMsg.Short(this, "Value is null");
        }
    }

    private void onCheckedListener(){
        btn_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mChecked = true;
                    btn_switch.setText("Private Key");
                }else{
                    mChecked = false;
                    btn_switch.setText("Seed Phrase");
                }
            }
        });
    }

    private void setAddress(String address){
        new Identicon(mIdenticonaddress,address);
        mAddress.setText(address);
    }

    private String getAddress(){
        return mAddress.getText().toString();
    }

    private void getBalance(){
        try {
            mBalance.setText(new Balance(mWeb3j,mCredentials.getAddress()).getInEther().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void setToAddress(String address){
        new Identicon(mIdenticontoaddress,address);
        mToaddress.setText(address);
    }

    private String getToAddress(){
        return mToaddress.getText().toString();
    }

    private String getAmmount(){
        return mAmmount.getText().toString();
    }

    private String getPassword(){
        return mPassword.getText().toString();
    }

    private void setRestoreValue(String value){
        mRestorevalue.setText(value);
    }

    private String getRestoreValue(){
        return mRestorevalue.getText().toString();
    }

    private String getGasPrice(){
        if (mGasPrice != null){
            return mGasPrice.getText().toString();
        } else {
            toastMsg.Short(this,"GasPrice not specified");
            return "4";
        }
    }

    private String getGasLimit(){
        if (mGasLimit != null){
            return mGasLimit.getText().toString();
        } else {
            toastMsg.Short(this,"GasLimit not specified");
            return "21000";
        }
    }

    private void setCredentials(Credentials credentials){
        mCredentials = credentials;
    }

    private Credentials getCredentials(){
        return mCredentials;
    }

    private void setWeb3j(){
        mWeb3j = new Initiate(mUrlNode).sWeb3jInstance;
    }

    private Web3j getWeb3j(){
        return mWeb3j;
    }

    private void Generate(){
        infoDialog.Get("Generate Wallet", "Please wait few seconds");
        new Generation(getPassword()).getBip44(this);
    }

    private void Restore(){
        if (!mRestorevalue.getText().equals("")) {
            setCredentials(new Restore().Get(
                    mChecked,
                    mRestorevalue.getText().toString(),
                    getPassword()));
            setAddress(mCredentials.getAddress());
            getBalance();
        } else{
            toastMsg.Short(this,"Wallet restore complete");
        }
    }

    private void SendEthereum(String toaddress, String ammount){
        if (CheckingSendValues()) {
            infoDialog.Get("Ethereum Transaction", "Please wait few seconds");
            Send send = new Send(getWeb3j(), getCredentials(), getGasPrice(), getGasLimit());
            send.SendEther(getToAddress(), getAmmount(),this);
        }

    }

    private Boolean CheckingSendValues(){
        if (mWeb3j.netVersion() == null){
            toastMsg.Short(this, "Network Ethereum not available");
        } else {
            if(getCredentials() == null){
                toastMsg.Short(this, "Please load Ethereum wallet");
            }else{
                if(getToAddress().length() == 0){
                    toastMsg.Short(this, "Enter Address To...");
                }else{
                    if(getAmmount().length() == 0){
                        toastMsg.Short(this, "Amount not filled");
                    }else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void startScan(String a){
        mQRScanActive = a;
        new ScanIntegrator(this).startScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = ScanIntegrator.sIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                if (mQRScanActive.equals("toaddress")){
                    setToAddress(result.getContents());
                } else if(mQRScanActive.equals("value")) {
                    mRestorevalue.setText(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void backGeneration(Map<String,String> result, Credentials credentials) {
        mCredentials = credentials;
        if(mChecked){
            setRestoreValue(result.get("privatekey"));
            setAddress(result.get("address"));
        }else{
            setRestoreValue(result.get("seedcode"));
            setAddress(result.get("address"));
        }
        result.clear();
        infoDialog.Dismiss();
    }

    @Override
    public void backSendEthereum(EthSendTransaction result){
        infoDialog.Dismiss();
        toastMsg.Long(this, "Hash transaction: " + result.getTransactionHash());
        getBalance();
    }

}
