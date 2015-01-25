/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package t3si_server;

/**
 *
 * @author Ervin
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;


public class T3SI_Server {

    
    public static void main(String[] args) {
        String numeServiciu,numeUser,n1Primit,permisiune;
        permisiune = "NU";
        try
        {
            ServerSocket ser = new ServerSocket(5432);
            Socket sock = ser.accept();
            //citim de la client
            BufferedReader ed = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String tmp = ed.readLine();
            //tokenizam ce am primit
             String[] tokens = tmp.split(Pattern.quote(","));  
             System.out.println("[Server]Nume user: "+tokens[0]);
             System.out.println("[Server]Nume serviciu: "+tokens[1]);
             System.out.println("[Server]Nonce: "+tokens[2]);

             
             numeUser = tokens[0];
             numeServiciu = tokens[1];
             n1Primit = tokens[2];
            
             //deschidem fisierul cu permisiuni
            BufferedReader br = new BufferedReader(new FileReader("permisiuni.txt"));
            String line;
            while ((line = br.readLine()) != null) {
               String fileNumeUser, fileNumeServiciu,filePermisiune;
                String[] fileTokens = line.split("-");
                fileNumeUser = fileTokens[0];
                fileNumeServiciu = fileTokens[1];
                filePermisiune = fileTokens[2];
                
                                
                if(fileNumeUser.equals(numeUser) && fileNumeServiciu.equals(numeServiciu)){
                    if(filePermisiune.equals("DA")){
                        permisiune = "DA";
                    }
                }
               
               
            }
            br.close();
           
            if(permisiune.equals("DA")){
                
                String K = AES.generareK(24);
                
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                String timpCurent = dateFormat.format(cal.getTime());
                
                String[] timeTokens = timpCurent.split(":");
                
                int ora = Integer.parseInt(timeTokens[0]);
                ora += 2; //2 ore durata cheie viata
                
                String L = ora + timpCurent.substring(2);
                
                
                String mesaj1 = "";
                String mesaj1CriptatAES ;

                //KUT
                String Kut = AES.generareK(24);
                mesaj1 = K+ "," + n1Primit + "," + L + "," + numeServiciu;
                mesaj1CriptatAES = AES.symmetricEncrypt(mesaj1,Kut);
                
                
                String mesaj2 = "";
                String mesaj2CriptatAES ;
                
    
                //KST
                String Kst = AES.generareK(24);
                mesaj2 = K+ "," + numeUser + "," + L;
                mesaj2CriptatAES = AES.symmetricEncrypt(mesaj2,Kst);
                PrintStream pr = new PrintStream(sock.getOutputStream());
                
             //trimitem raspunsul inapoi, criptat
             pr.println(mesaj1CriptatAES+","+mesaj2CriptatAES+","+Kut+","+Kst);
            
                
            }
            else{
                PrintStream pr = new PrintStream(sock.getOutputStream());  
             
                pr.println("Eroare");

            }
            
             
            
             
        }
      catch(Exception ex){
          ex.printStackTrace();
      }
    }
    
}
