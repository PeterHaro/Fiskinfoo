package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


/**
 * Created by erlendstav on 09/05/2018.
 */

public class BarentswatchResultReceiver extends ResultReceiver {

        Receiver receiver;

        public BarentswatchResultReceiver(Handler handler) {
            super(handler);
        }

        public void setReceiver(Receiver receiver) {
            this.receiver = receiver;
        }


        public interface Receiver {
            public void onBarentswatchResultReceived(int resultCode, Bundle resultData);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (receiver != null) {
                receiver.onBarentswatchResultReceived(resultCode, resultData);
            }
        }

}
