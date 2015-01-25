/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package t3si_serviciu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Ervin
 */
public class T3SI_Serviciu {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            ServerSocket ser = new ServerSocket(7654);
            Socket sock = ser.accept();
            //citim de la client
            BufferedReader ed = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String tmp = ed.readLine();
            
            String[] messages = tmp.split(",");
            
            String mesaj1Criptat = messages[0];
            String mesaj2Criptat = messages[1];
            String KPrimit = messages[2];
            String Kst = messages[3];
            
            String mesaj1 = AES.symmetricDecrypt(mesaj1Criptat,Kst);
            String[] data = mesaj1.split(",");
            
            String K = data[0];
            String IdentitateUserM1 = data[1];
            String L = data[2];
            
            String mesaj2 = AES.symmetricDecrypt(mesaj2Criptat, K);
            String[] data2 = mesaj2.split(",");
            String IdentitateUserM2 = data2[0];
            String timeStamp = data2[1].substring(0,data2[1].length()/2);
            
            String[] LtimeAsNumberArrays = L.split(":");
            String[] timeStampAsNUmberArrays = timeStamp.split(":");
            
            String StringLtimeAsNumber = LtimeAsNumberArrays[0] + LtimeAsNumberArrays[1] + LtimeAsNumberArrays[2];
            
            String StringTimeStampAsNumber = timeStampAsNUmberArrays[0] + timeStampAsNUmberArrays[1] + timeStampAsNUmberArrays[2];
            
            int LtimeAsNumber = Integer.parseInt(StringLtimeAsNumber);
            int LtimeAnswer = Integer.parseInt(StringTimeStampAsNumber.substring(0,StringTimeStampAsNumber.length()-2));
            
            int TimeStampAsNumber = Integer.parseInt(StringTimeStampAsNumber);
            
            if(IdentitateUserM1.equals(IdentitateUserM2))
                if(LtimeAnswer < LtimeAsNumber ){
                String mesajFinal = timeStamp + "," + L;
                String mesajFinalCriptat = AES.symmetricEncrypt(mesajFinal, K);
                try{
                                  Socket socket = new Socket("localhost",9876);
                                  PrintStream ps = new PrintStream(socket.getOutputStream());
                                  ps.println(mesajFinalCriptat);
                                }
                                catch(Exception ex){
                                    ex.printStackTrace();
                                }
            }
            
            
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
}
