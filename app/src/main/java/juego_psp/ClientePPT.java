package juego_psp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class ClientePPT {
    public static void main(String[] args) {
        String host= "localhost";
        int puerto= 9888;

        boolean conectado= true;

        System.out.println("---------Iniciando partida----------");
        
        // se debe definir en el try-with-resources:
        //socket, printwriter, bufferedreader y scanner 

        try (Socket socket = new Socket(host, puerto)
    
         ){
            
            
        } catch (ConnectException e) {
            System.err.println("Error (de conexión al servidor): "+e.getMessage());
        } catch(IOException e){
            System.err.println("Error (de comunicación"+e.getMessage());
        }
            System.out.println("Partida finalizada????");

        
    }

}
