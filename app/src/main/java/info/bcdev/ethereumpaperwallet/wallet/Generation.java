package info.bcdev.ethereumpaperwallet.wallet;

import info.bcdev.librarysdkew.interfaces.callback.CBBip44;
import info.bcdev.librarysdkew.wallet.generate.Bip44;

public class Generation{

    private String mPasswordwallet;

    public Generation(String password){
        mPasswordwallet = password;
    }

    public void getBip44(CBBip44 cbBip44){
        Bip44 bip44 = new Bip44();
        bip44.registerCallBack(cbBip44);
        bip44.execute(mPasswordwallet);
        //return new Bip44().Generation(mPasswordwallet);
    }



}
