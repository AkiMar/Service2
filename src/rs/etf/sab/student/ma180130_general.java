/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author Goran
 */
public class ma180130_general implements GeneralOperations {

    static private Connection conn=DB.getInstance().getConnection();
    
    @Override
    public void eraseAll() {
        
        try (
                PreparedStatement psSveVoznje = conn.prepareStatement("DELETE FROM SveVoznje\n" +
"WHERE 0=0");
                
                PreparedStatement psPaketiMag = conn.prepareStatement("DELETE FROM PaketiZaMagacin\n" +
"WHERE 0 = 0");
                PreparedStatement psPaketiIsp = conn.prepareStatement("DELETE FROM PaketiZaIsporuku\n" +
"WHERE 0=0");
                PreparedStatement psTrenutnaVoz = conn.prepareStatement("DELETE FROM TrenutnaVoznja\n" +
"WHERE 0=0");
                
                
                PreparedStatement psPonuda = conn.prepareStatement("DELETE FROM Ponuda\n" +
"WHERE 0=0");
        
                //PreparedStatement psPaket = conn.prepareStatement("DELETE FROM ZahtevPaketa\n" +
//"WHERE 0=0");   
        
                PreparedStatement psVozilo = conn.prepareStatement("DELETE FROM Vozilo\n" +
"WHERE 0=0");
                
                PreparedStatement psMagacin = conn.prepareStatement("DELETE FROM Magacin\n" +
"WHERE 0=0");
                
                PreparedStatement psZahtev = conn.prepareStatement("DELETE FROM Zahtev\n" +
"WHERE 0=0");
                PreparedStatement psKurir = conn.prepareStatement("DELETE FROM Kurir\n" +
"WHERE 0 = 0");
                PreparedStatement psKupac = conn.prepareStatement("DELETE FROM Kupac\n" +
"WHERE 0 = 0");
                PreparedStatement psAdmin = conn.prepareStatement("DELETE FROM Administrator\n" +
"WHERE 0 = 0");
                PreparedStatement psK = conn.prepareStatement("DELETE FROM Korisnik\n" +
"WHERE 0 = 0");
                PreparedStatement psA = conn.prepareStatement("DELETE FROM Adresa\n" +
"WHERE 0 = 0");
                PreparedStatement psG = conn.prepareStatement("DELETE FROM Grad\n" +
"WHERE 0 = 0");
            ) {
            
            psSveVoznje.executeUpdate();
            psPaketiMag.executeUpdate();
            psPaketiIsp.executeUpdate();
            psTrenutnaVoz.executeUpdate();
            psPonuda.executeUpdate();
            //psPaket.executeUpdate();
            psVozilo.executeUpdate();
            psMagacin.executeUpdate();
            psZahtev.executeUpdate();
            psKupac.executeUpdate();
            psKurir.executeUpdate();
            psAdmin.executeUpdate();
            psK.executeUpdate();
            psA.executeUpdate();
            psG.executeUpdate();
        } catch (Exception e) {
            System.err.println("Greska prilikom brisanja");
        }
        
    }
    
}
