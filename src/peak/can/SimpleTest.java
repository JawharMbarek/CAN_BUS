package peak.can;

import peak.can.basic.*;

import java.util.HashMap;
import java.util.Map;

public class SimpleTest {

    // PCANBasic instance
    private static PCANBasic pcanBasic = null;

    private static TPCANMsg canMessage = null;

    private static TPCANTimestamp rcvTime = null;

    private static TPCANStatus ret;

    // Map to store received messages
    private static HashMap<Integer, TableDataRow> receivedData = new HashMap<Integer, TableDataRow>();

    volatile static ChannelItem channelitem = null;

    // Thread to read CAN messages
    static CANReadThread canReadThread;


    public static void main(String[] args) {

        // Crate New instance of PCANBasic
        pcanBasic = new PCANBasic();

        // JNI Initialization
        pcanBasic.initializeAPI();

//        TPCANHandle pcan_handle = TPCANHandle.PCAN_PCIBUS2;
//        TPCANType pcan_type = TPCANType.PCAN_TYPE_NONE;
//        TPCANBaudrate pcan_baudrate = TPCANBaudrate.PCAN_BAUD_100K;
        TPCANHandle pcan_handle = TPCANHandle.PCAN_PCIBUS1;
        TPCANType pcan_type = TPCANType.PCAN_TYPE_NONE;
        TPCANBaudrate pcan_baudrate = TPCANBaudrate.PCAN_BAUD_1M;

//        for (TPCANType pcan_type : TPCANType.values()) {
//        for (TPCANBaudrate pcan_baudrate : TPCANBaudrate.values()) {
//        for (TPCANHandle pcan_handle : TPCANHandle.values()) {

        //channelitem = new ChannelItem(TPCANHandle.PCAN_USBBUS1, TPCANType.PCAN_TYPE_ISA_SJA);
        channelitem = new ChannelItem(pcan_handle, pcan_type);
        channelitem.setWorking(true);

        TPCANStatus res;
//         res = pcanBasic.Initialize(TPCANHandle.PCAN_USBBUS1, pcan_baudrate, pcan_type, 100, (short) 3);
        res = pcanBasic.Initialize(pcan_handle, pcan_baudrate, pcan_type);
        if (res == TPCANStatus.PCAN_ERROR_OK) {
            System.out.println("NO NO NO NO Error by reading the data \n TPCANType =" + pcan_type +
                    "\n TPCANBaudrate =" + pcan_baudrate + "\n TPCANHandle =" + pcan_handle + "\n" +
                    channelitem.getHandle().toString() + " Successfully initialized");
        } else {
            System.out.println("Error by reading the data");
            new RuntimeException("Error by reading the data");
        }

// Create New CANReadThread with default values
        canReadThread = new CANReadThread(pcanBasic, channelitem, receivedData);

        TPCANStatus status;
        // If isWorking, call SetRcvEvent on current channel
        if (channelitem.getWorking()) {
            status = pcanBasic.SetRcvEvent(channelitem.getHandle());
            if (status != TPCANStatus.PCAN_ERROR_OK) {
                System.out.println("NOT -- TPCANStatus.PCAN_ERROR_OK");
            }
        }
        // Start Timer Thread to read CAN Messages
        canReadThread.start();

        ret = pcanBasic.Read(channelitem.getHandle(), canMessage, null);

        //Process result
        if (ret == TPCANStatus.PCAN_ERROR_OK) {
            for (Map.Entry<Integer, TableDataRow> entry : receivedData.entrySet()) {
                System.out.println(entry.getKey() + "/*** JAWHAR ***/" + entry.getValue());
            }
        }
//        }
//        }
//        }
    }
}
