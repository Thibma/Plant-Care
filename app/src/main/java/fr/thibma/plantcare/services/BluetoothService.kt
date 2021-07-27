package fr.thibma.plantcare.services

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1

class BluetoothService(private val handler: Handler) {

    inner class ConnectedThread(private val socket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = socket.inputStream
        private val mmOutStream: OutputStream = socket.outputStream
        private val buffer: ByteArray = ByteArray(1024)

        override fun run() {
            super.run()
            var numBytes: Int

            while(true) {
                numBytes = try {
                    mmInStream.read(buffer)
                } catch (e: IOException) {
                    Log.d("READ SOCKET", "Input stream was disconnected", e)
                    break
                }

                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1, buffer
                )
                readMsg.sendToTarget()
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e("WRITE SOCKET", "Error occured when sending data", e)
            }

            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, buffer
            )
            writtenMsg.sendToTarget()
        }

        fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e("CLOSE SOCKET", "Failed", e)
            }
        }

    }

}
