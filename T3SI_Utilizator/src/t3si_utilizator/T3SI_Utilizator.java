/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package t3si_utilizator;

/**
 *
 * @author Ervin
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


public class T3SI_Utilizator {

     static private String n1Trimis;

            
   static public void generareNonce(){
        n1Trimis = AES.generareCheieRandom(10);
        
    }
   
    public static void main(String[] args) {
        
        try
        {
            Socket sock = new Socket("localhost",5432);
            
            PrintStream pr = new PrintStream(sock.getOutputStream());
            
            System.out.print("Introduceti [UserName],[SeviceName] : ");
            generareNonce();
            
            InputStreamReader rd = new InputStreamReader(System.in);
            BufferedReader ed = new BufferedReader(rd);
            
            String temp = ed.readLine();
            temp = temp.concat(","+n1Trimis);
            pr.println(temp);
            
            String[] sentData = temp.split(",");
            String numeUser = sentData[0];
            String numeServiciuTrimis = sentData[1];
            
          
            
                BufferedReader gt = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                String tm = gt.readLine();
               
                if(!tm.equals("Eroare")){
                        String StringMesaj1Criptat,StringMesaj2Criptat,mesaj1;

                        String[] messages = tm.split(",");
                        StringMesaj1Criptat =  messages[0];
                        StringMesaj2Criptat = messages[1];

                        String kut = messages[2];  
                        String kst = messages[3];  
                                     
                        
                        //decriptam ce am primit
                        mesaj1 = AES.symmetricDecrypt(StringMesaj1Criptat, kut);
            //            mesaj2 = AES.symmetricDecrypt(StringMesaj2Criptat,kst );

                        String[] data = mesaj1.split(",");

                        String KPrimit = data[0];
                        String n1Primit = data[1];
                        String LPrimit = data[2];
                        String numeServiciuPrimit = data[3];
                        
                        //verificam pasul 3)
                        if(n1Trimis.equals(n1Primit) && numeServiciuTrimis.equals(numeServiciuPrimit)){
                            System.out.println("[Check]N1(nonce) este identic!");

                            System.out.println("[Client] K = "+KPrimit);
                            System.out.println("[Client] n1 = "+n1Primit);
                            System.out.println("[Client] L = "+LPrimit);
                            System.out.println("[Client] ServiceName = "+numeServiciuPrimit);
                            
                            
                            String Mesaj1Criptat,Mesaj2,Mesaj2Criptat;

                            Mesaj1Criptat = StringMesaj2Criptat;

                            //Generate timestamp
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            Calendar cal = Calendar.getInstance();
                            String currentTimestamp = dateFormat.format(cal.getTime());

                            Mesaj2 = numeUser + "," + currentTimestamp + LPrimit;

                            Mesaj2Criptat = AES.symmetricEncrypt(Mesaj2, KPrimit);

                        
                                try{
                                  Socket socket = new Socket("localhost",7654);
                                  PrintStream ps = new PrintStream(socket.getOutputStream());
                                  ps.println(Mesaj1Criptat+","+Mesaj2Criptat+","+KPrimit+","+kst);
                                }
                                catch(Exception ex){
                                    ex.printStackTrace();
                                }
                                
                                
                        //raspunsul FINAL de la SERVICIU
                        ServerSocket ser = new ServerSocket(9876);
                        Socket socket = ser.accept();
                        //citim de la client
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String encryptedMessage = br.readLine();
                        String decryptedMessage = AES.symmetricDecrypt(encryptedMessage, KPrimit);
                        String[] dataRecieved = decryptedMessage.split(",");

                            System.out.println("[Serviciu Answer] Timestamp : " + dataRecieved[0] );  
                            System.out.println("[Serviciu Answer] L-1 : " + dataRecieved[1]);
                                
                }
                        else{
                            System.out.println("[Client] N1 trimis nu coincide cu N1 primit sau Numele Serviciului Trimis nu coincide cu Numele Serviciului Primit ");
                        }
                       
                        
                        
                }
                else {
                    System.out.println("[Client] Nu aveti drepturile pentru accesarea serviciului "+numeServiciuTrimis);
                }
                

            
               
        }     
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
}
