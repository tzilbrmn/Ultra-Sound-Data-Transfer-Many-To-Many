package ReceiverPackage;

/**********************************************************************************************
 * Interface: CallBack
 * Description:
 * functions:
 *      void onBufferAvailable(byte[] buffer)
 *      void setBufferSize(int size)
 **********************************************************************************************/
public interface CallBack {

    //TODO fares: change functions description
    //Called when recorder finishes recording one byte array
    void onBufferAvailable(byte[] buffer);

    //Set size of byte arrays that recorder will produce
    void setBufferSize(int size);
}
