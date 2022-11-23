/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.DriveOperation;

/**
 *
 * @author Goran
 */
public class ma180130_drive implements DriveOperation {

    static private Connection conn=DB.getInstance().getConnection();
    
    @Override
    public boolean planingDrive(String korIme) {
        try(
                PreparedStatement ps1 = conn.prepareStatement("SELECT *\n" +
"FROM TrenutnaVoznja\n" +
"WHERE KorIme = ?");
                PreparedStatement ps2 = conn.prepareStatement("SELECT A.IdGrad\n" +
"FROM Kurir K1 JOIN Korisnik K2 ON K1.KorIme = K2.KorIme JOIN Adresa A ON K2.Adresa = A.IdAdr\n" +
"WHERE K1.KorIme = ?");
                
                PreparedStatement ps3 = conn.prepareStatement("SELECT V.RegBr AS RegBr, V.Nosivost AS Nosivost, M.IdAdr AS Adr\n" +
"FROM Vozilo V JOIN Magacin M ON V.MagacinParkiranja = M.IdMag JOIN Adresa A ON M.IdAdr = A.IdAdr\n" +
"WHERE A.IdGrad = ?");
                
                PreparedStatement ps4 = conn.prepareStatement("INSERT INTO TrenutnaVoznja(KorIme, RegBr,TrenutnaLokacija,BrKm,PocetnaLokacija,Nosivost,TrTezina)\n" +
"VALUES(?,?,?,0,?,?,0)");
                
                PreparedStatement psU = conn.prepareStatement("UPDATE Vozilo SET MagacinParkiranja = null\n" +
"WHERE RegBr = ?");
                
                PreparedStatement ps5 = conn.prepareStatement("SELECT Z.IdZah AS IdZah, P.Lokacija AS IdAdr, Z.Tezina AS Tezina \n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah JOIN Adresa A ON P.Lokacija = A.IdAdr\n" +
"WHERE P.UMagacinu != 1 AND A.IdGrad = ? AND P.Status = 1");
                
                PreparedStatement ps6 = conn.prepareStatement("SELECT Z.IdZah AS IdZah, P.Lokacija AS IdAdr, Z.Tezina AS Tezina \n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah JOIN Adresa A ON P.Lokacija = A.IdAdr\n" +
"WHERE P.UMagacinu = 1 AND A.IdGrad = ? AND ( P.Status = 1 OR (P.Status = 2 AND P.UMagacinu = 1 ))");
                
                PreparedStatement ps7 = conn.prepareStatement("UPDATE TrenutnaVoznja SET BrKm = ?\n" +
"WHERE RegBr = ? AND KorIme = ?");
                
                PreparedStatement psUpdate = conn.prepareStatement("UPDATE Kurir SET Status = 1\n" +
"WHERE KorIme = ?");
                
                PreparedStatement psSviPaketi = conn.prepareStatement("SELECT IdZah\n" +
"FROM ZahtevPaketa");
                PreparedStatement psDodajMoguce = conn.prepareStatement("INSERT INTO MoguciPaketi(KorIme,IdZah)\n" +
"VALUES(?,?)")
           ) {
            
            ps1.setString(1, korIme);
            ps2.setString(1, korIme);
            psUpdate.setString(1, korIme);
            
            try(
                    ResultSet rs1 = ps1.executeQuery();
                    ResultSet rs2 = ps2.executeQuery();
                    ResultSet rsSviPaketi = psSviPaketi.executeQuery();
               ) {
                
                if( rs1.next()){
                    System.out.println("KURIR OVDE IZASO 1");
                    return false;
                }
                rs2.next();
                int IdGrad = rs2.getInt(1);
                ps3.setInt(1, IdGrad);
                ps5.setInt(1, IdGrad); // dohvatanje svih paketa iz ovog grada
                ps6.setInt(1, IdGrad); // paketi iz magacina
                try(
                        ResultSet rs3 = ps3.executeQuery();
                        ResultSet rs5 = ps5.executeQuery();
                        ResultSet rs6 = ps6.executeQuery();
                        
                   ) {
                    
                    if( rs3.next() == false){
                        System.out.println("KURIR OVDE IZASO 2");
                        return false;
                    }
                    String regBr = rs3.getString(1);
                    double nosivost = rs3.getDouble(2);
                    int pocAdr = rs3.getInt(3);
                    ps4.setString(1, korIme);
                    ps4.setString(2, regBr);
                    ps4.setInt(3, pocAdr);
                    ps4.setInt(4, pocAdr);
                    ps4.setDouble(5, nosivost);
                    
                    psU.setString(1, regBr);
                    ps4.executeUpdate();
                    psU.executeUpdate(); // kreiranje trenutne voznje
                    double ukupnaKm = 0;
                    int brPaketa = 0;
                    int XY[] = dohvatiXYadd(pocAdr);
                    
                    while( rs5.next() && nosivost > 0){
                        int idZah = rs5.getInt(1);
                        int idAdr = rs5.getInt(2);
                        int tezina = rs5.getInt(3);
                        if( tezina <= nosivost){
                            nosivost = nosivost - tezina;
                            int XY2[] = dohvatiXYadd(idAdr);
                            double km = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                            ukupnaKm += km;
                            brPaketa++;
                            dodajPaketZaIsporuku(idZah, regBr, korIme);
                            XY = XY2;
                        }
                    
                    }
                    
                    if( nosivost > 0){
                        
                        while( rs6.next() && nosivost > 0){
                            int idZah = rs6.getInt(1);
                            int idAdr = rs6.getInt(2);
                            int tezina = rs6.getInt(3);
                            if( tezina <= nosivost){
                                nosivost = nosivost - tezina;
                                int XY2[] = dohvatiXYadd(idAdr);
                                double km = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                                ukupnaKm += km;
                                brPaketa++;
                                dodajPaketZaIsporuku(idZah, regBr, korIme);
                                XY = XY2;
                            }
                        }
                    }
                    
                    ps7.setDouble(1, 0);
                    ps7.setString(2, regBr);
                    ps7.setString(3, korIme);
                    
                    ps7.executeUpdate();
                    psUpdate.executeUpdate();
                    
                    while( rsSviPaketi.next()){
                        
                        int idZah = rsSviPaketi.getInt(1);
                        psDodajMoguce.setString(1, korIme);
                        psDodajMoguce.setInt(2, idZah);
                        psDodajMoguce.executeUpdate();
                        
                    }
                    
                    return true;
                    
                } catch (Exception e) {
                    System.out.println("KURIR OVDE IZASO 3");
                }
                
                
            } catch (Exception e) {
                System.out.println("KURIR OVDE IZASO 4");
            }
            
            
        } catch (Exception e) {
            System.out.println("KURIR OVDE IZASO 5");
        }
        return false;
    }

    
    public double euklidksaRazd(double x1, double x2, double y1, double y2){
    
            return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1-y2), 2));
    }
    
    public int[] dohvatiXYadd(int idAdr){
    
         try(
                 PreparedStatement ps = conn.prepareStatement("SELECT X, Y\n" +
"FROM Adresa\n" +
"WHERE IdAdr = ?");
            ) {
            
             ps.setInt(1, idAdr);
             try(
                    ResultSet rs = ps.executeQuery();
                ) {
                 rs.next();
                 int x = rs.getInt(1);
                 int y = rs.getInt(2);
                 int niz[] = {x,y};
                 return niz;
             } catch (Exception e) {
             }
             
        } catch (Exception e) {
        }
         int niz[] = {0};
         return niz;
    }
    
    public void dodajPaketZaIsporuku(int idZah, String RegBr, String KorIme){
        
        try(
            PreparedStatement ps1 = conn.prepareStatement("INSERT INTO PaketiZaIsporuku(KorIme,IdZah)\n" +
"VALUES(?,?)");
           ) {
            
            ps1.setString(1, KorIme);
            ps1.setInt(2, idZah);
            ps1.executeUpdate();
            
        } catch (Exception e) {
        }
        
    }
    
    
    public void updateTrenLokacijuVoznje(String korIme, int idAdr, double BrKm){
    
        try(     
                PreparedStatement ps = conn.prepareStatement("UPDATE TrenutnaVoznja SET BrKm = BrKm + ?, TrenutnaLokacija = ?\n" +
"WHERE KorIme = ?");
           ) {
                ps.setDouble(1, BrKm);
                ps.setInt(2, idAdr);
                ps.setString(3, korIme);
                ps.executeUpdate();
            
        } catch (Exception e) {
        }
    
    }
    
    public void paketUtovari(int idZah,String korIme){
        
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Ponuda SET Status = 2, UMagacinu = 0\n" +
"WHERE IdZah = ?");
                PreparedStatement ps2 = conn.prepareStatement("UPDATE TrenutnaVoznja SET TrTezina = TrTezina + ?\n" +
"WHERE KorIme = ?");
                PreparedStatement psPaket = conn.prepareStatement("SELECT Tezina\n" +
"FROM ZahtevPaketa\n" +
"WHERE IdZah = ?");
           ) {
            
                ps.setInt(1, idZah);
                ps.executeUpdate();
                psPaket.setInt(1, idZah);
                try(
                        ResultSet rs = psPaket.executeQuery();
                   ) {
                    rs.next();
                    double tezina = rs.getInt(1);
                    ps2.setDouble(1, tezina);
                    ps2.setString(2, korIme);
                    ps2.executeUpdate();
                            
                    
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
    }
    
    public void paketUtovariMagacin(int idZah,String korIme){
        
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Ponuda SET Status = 2, UMagacinu = 0\n" +
"WHERE IdZah = ?");
                PreparedStatement ps2 = conn.prepareStatement("UPDATE TrenutnaVoznja SET TrTezina = TrTezina + ?\n" +
"WHERE KorIme = ?");
                PreparedStatement psPaket = conn.prepareStatement("SELECT Tezina\n" +
"FROM ZahtevPaketa\n" +
"WHERE IdZah = ?");
                PreparedStatement psInsert = conn.prepareStatement("INSERT INTO PaketiZaMagacin(IdZah,KorIme)\n" +
"VALUES(?,?)");
           ) {
            
                ps.setInt(1, idZah);
                ps.executeUpdate();
                psPaket.setInt(1, idZah);
                try(
                        ResultSet rs = psPaket.executeQuery();
                   ) {
                    rs.next();
                    double tezina = rs.getInt(1);
                    ps2.setDouble(1, tezina);
                    ps2.setString(2, korIme);
                    ps2.executeUpdate();
                    
                    psInsert.setInt(1, idZah);
                    psInsert.setString(2, korIme);
                    psInsert.executeUpdate();
                    
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
    }
    
    public void paketIstovari(int idZah, String korIme){
        
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Ponuda SET Status = 3 , Lokacija = ?\n" +
"WHERE IdZah = ?");
                PreparedStatement ps2 = conn.prepareStatement("UPDATE TrenutnaVoznja SET TrTezina = TrTezina - ?\n" +
"WHERE KorIme = ?");
                PreparedStatement psPaket = conn.prepareStatement("SELECT Tezina, Na\n" +
"FROM ZahtevPaketa\n" +
"WHERE IdZah = ?");
                PreparedStatement psDelete = conn.prepareStatement("DELETE FROM PaketiZaIsporuku\n" +
"WHERE KorIme = ? AND IdZah = ?");
                
                PreparedStatement psKurirBr = conn.prepareStatement("UPDATE Kurir SET BrPaketaIsporuceno = BrPaketaIsporuceno +1\n" +
"WHERE KorIme = ?");
                
           ) {
                psKurirBr.setString(1, korIme);
                psKurirBr.executeUpdate();
            
                psPaket.setInt(1, idZah);
                psDelete.setString(1, korIme);
                psDelete.setInt(2, idZah);
                try(
                        ResultSet rs = psPaket.executeQuery();
                   ) {
                    rs.next();
                    double tezina = rs.getDouble(1);
                    int lok = rs.getInt(2);
                    ps.setInt(1, lok);
                    ps.setInt(2, idZah);
                    ps.executeUpdate();
                    
                    ps2.setDouble(1, tezina);
                    ps2.setString(2, korIme);
                    ps2.executeUpdate();
                    //psDelete.executeUpdate();
                            
                    
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
    }
    
    public void paketIstovarMagacin(int idZah, int idAdr){
    
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Ponuda SET Status = 2, UMagacinu = 1, Lokacija = ?\n" +
"WHERE IdZah = ?");

           ) {
            
                ps.setInt(1, idAdr);
                ps.setInt(2,idZah);
                ps.executeUpdate();
                
            
        } catch (Exception e) {
        }
    
    }
    
    public double cenaVoznjeIsporuke(String korIme){
        
        double profit = 0;
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT SUM(P2.CenaIsporuke) AS Ukupno\n" +
"FROM PaketiZaIsporuku P JOIN Ponuda P2 ON P.IdZah = P2.IdZah\n" +
"WHERE P.KorIme = ?");
           ) {
            ps.setString(1, korIme);
            try(
                ResultSet rs = ps.executeQuery();
               ) {
                rs.next();
                profit = rs.getDouble(1);
                return profit;
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return profit;
    }
    
    public void updateProfitKurira(String KorIme){
        
        double placeno = cenaVoznjeIsporuke(KorIme);
        
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT BrKm, RegBr\n" +
"FROM TrenutnaVoznja\n" +
"WHERE KorIme = ?");
                PreparedStatement ps2 = conn.prepareStatement("SELECT TipGoriva, Potrosnja\n" +
"FROM Vozilo\n" +
"WHERE RegBr = ?");
                PreparedStatement psU = conn.prepareStatement("UPDATE Kurir SET Profit = Profit + ?\n" +
"WHERE KorIme = ?");
                PreparedStatement psInsert = conn.prepareStatement("INSERT INTO SveVoznje(BrKm,KorIme,RegBr)\n" +
"VALUES(?, ?, ?)");
                
           ){
            
            ps.setString(1, KorIme);
            double brKm;
            String regBr;
            try(
                ResultSet rs = ps.executeQuery();
               ) {
                rs.next();
                brKm = rs.getDouble(1);
                System.out.println("KM:" + brKm);
                regBr = rs.getString(2);
                ps2.setString(1, regBr);
                
                try(
                        ResultSet rs2 = ps2.executeQuery();
                   ) {
                    rs2.next();
                    int tipGoriva = rs2.getInt(1);
                    double potrosnja = rs2.getDouble(2);
                    double cena = 0;
                    switch(tipGoriva) {
                            case 0:
                                cena = brKm * potrosnja * 15;
                            break;
                            case 1:
                                cena = brKm  * potrosnja * 32;
                            break;
                            case 2:
                                cena = brKm  * potrosnja * 36;
                            break;
                            default:
                            // code block
                    }
                    double profit = placeno - cena;
                    psU.setDouble(1, profit);
                    psU.setString(2, KorIme);
                    psU.executeUpdate();
                    
                    psInsert.setDouble(1, brKm);
                    psInsert.setString(2, KorIme);
                    psInsert.setString(3, regBr);
                    psInsert.executeUpdate();
                    
                } catch (Exception e) {
                }
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
    
    }
    
    public void obrisiPrenose(String KorIme){
        
        try(
                PreparedStatement ps1 = conn.prepareStatement("DELETE FROM PaketiZaMagacin\n" +
"WHERE KorIme = ?");
                PreparedStatement ps2 = conn.prepareStatement("DELETE FROM PaketiZaIsporuku\n" +
"WHERE KorIme = ?");
                PreparedStatement ps3 = conn.prepareStatement("DELETE FROM TrenutnaVoznja\n" +
"WHERE KorIme = ?");
           ) {
            
            ps1.setString(1, KorIme);
            ps2.setString(1, KorIme);
            ps3.setString(1, KorIme);
            ps1.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
        } catch (Exception e) {
        }
    
    }
    
    @Override
    public int nextStop(String korIme) {
        
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT TrenutnaLokacija, TrTezina, Nosivost, PocetnaLokacija\n" +
"FROM TrenutnaVoznja\n" +
"WHERE KorIme = ?");
                
                PreparedStatement psU = conn.prepareStatement("SELECT Z.IdZah, Z.Sa, Pon.UMagacinu \n" +
"FROM TrenutnaVoznja T JOIN PaketiZaIsporuku P ON T.KorIme = P.KorIme JOIN ZahtevPaketa Z ON Z.IdZah = P.IdZah JOIN Ponuda Pon ON Pon.IdZah = Z.IdZah\n" +
"WHERE T.KorIme = ? AND (Pon.Status = 1 OR (Pon.Status = 2 AND Pon.UMagacinu = 1) ) ORDER BY Pon.Status, Z.IdZah");
                
                PreparedStatement ps2 = conn.prepareStatement("SELECT Z.IdZah, Z.Na\n" +
"FROM TrenutnaVoznja T JOIN PaketiZaIsporuku P ON T.KorIme = P.KorIme JOIN ZahtevPaketa Z ON Z.IdZah = P.IdZah JOIN Ponuda Pon ON Pon.IdZah = Z.IdZah\n" +
"WHERE T.KorIme = ? AND Pon.Status = 2");
                
                // provera da li ima paketa za preuzimanje u datom gradu
                
                PreparedStatement psGrad = conn.prepareStatement("SELECT IdGrad\n" +
"FROM Adresa\n" +
"WHERE IdAdr = ?");
                
                PreparedStatement psGSa = conn.prepareStatement("SELECT Z.IdZah AS IdZah, P.Lokacija AS IdAdr, Z.Tezina AS Tezina \n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah JOIN Adresa A ON P.Lokacija = A.IdAdr JOIN MoguciPaketi M ON M.IdZah = Z.IdZah\n" +
"WHERE P.UMagacinu != 1 AND A.IdGrad = ? AND P.Status = 1 AND (Z.Tezina + ?) <= ? AND M.KorIme = ?");
                
                PreparedStatement psGMag = conn.prepareStatement("SELECT Z.IdZah AS IdZah, P.Lokacija AS IdAdr, Z.Tezina AS Tezina \n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah JOIN Adresa A ON P.Lokacija = A.IdAdr JOIN MoguciPaketi M ON M.IdZah = Z.IdZah\n" +
"WHERE P.UMagacinu = 1 AND A.IdGrad = ? AND (P.Status = 1 OR (P.UMagacinu = 1 AND P.Status = 2) ) AND (Z.Tezina + ?) <= ? AND M.KorIme = ?");
                
                PreparedStatement psIstovarMagacin = conn.prepareStatement("SELECT IdZah\n" +
"FROM PaketiZaMagacin\n" +
"WHERE KorIme = ?");
                
           ) {
            
            ps.setString(1, korIme);
            psU.setString(1, korIme);
            ps2.setString(1, korIme);

            try(
                    ResultSet rs = ps.executeQuery();
                    ResultSet rsU = psU.executeQuery();
                    ResultSet rs2 = ps2.executeQuery();
                    
               ) {
                    if( rs.next() == false){
                        return -1;
                    }
                    int trenutnaLok = rs.getInt(1);
                    double TrTezina = rs.getDouble(2);
                    double nosivost = rs.getDouble(3);
                    int pocetnaLok = rs.getInt(4);
                    
                    psGrad.setInt(1, trenutnaLok);
                    if( rsU.next()){
                        
                        int idZah = rsU.getInt(1);
                        int idAdr = rsU.getInt(2);
                        int UMagacinu = rsU.getInt(3);
                        
                        int[] XY = dohvatiXYadd(trenutnaLok);
                        int[] XY2 = dohvatiXYadd(idAdr);
                        double brKm = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                        paketUtovari(idZah,korIme);
                        updateTrenLokacijuVoznje(korIme, idAdr, brKm);

                        if( UMagacinu == 1){
                            updateTrenLokacijuVoznje(korIme, pocetnaLok, 0);
                            while(rsU.next()){
                                idZah = rsU.getInt(1);
                                idAdr = rsU.getInt(2);
                                paketUtovari(idZah,korIme);
                            }
                        
                        }
                        
                        return -2;
                    
                    }else{
                        
                        try(
                                ResultSet rsGrad = psGrad.executeQuery();
                           ) {
                            rsGrad.next();
                            int idGrad = rsGrad.getInt(1);
                            psGSa.setInt(1, idGrad);
                            psGSa.setDouble(2, TrTezina);
                            psGSa.setDouble(3, nosivost);
                            psGSa.setString(4, korIme);
                            psGMag.setInt(1, idGrad);
                            psGMag.setDouble(2, TrTezina);
                            psGMag.setDouble(3, nosivost);
                            psGMag.setString(4, korIme);
                            
                            ResultSet rsGsa = psGSa.executeQuery();
                            ResultSet rsGMag = psGMag.executeQuery();
                            
                            if( rsGsa.next()){
                                int idZah = rsGsa.getInt(1);
                                int idAdr = rsGsa.getInt(2);
                                paketUtovariMagacin(idZah,korIme);
                                int[] XY = dohvatiXYadd(trenutnaLok);
                                int[] XY2 = dohvatiXYadd(idAdr);
                                double brKm = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                                updateTrenLokacijuVoznje(korIme, idAdr, brKm);
                                
                                rsGsa.close();
                                rsGMag.close();
                                return -2;
                            }
                            if( rsGMag.next()){
                                int idZah = rsGMag.getInt(1);
                                int idAdr = rsGMag.getInt(2);
                                double tezina = rsGMag.getDouble(3);
                                paketUtovariMagacin(idZah, korIme);
                                TrTezina += tezina;
                                int[] XY = dohvatiXYadd(trenutnaLok);
                                int[] XY2 = dohvatiXYadd(idAdr);
                                trenutnaLok = idAdr;
                                double brKm = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                                updateTrenLokacijuVoznje(korIme, idAdr, brKm);
                                
                                while( rsGMag.next()){
                                    idZah = rsGMag.getInt(1);
                                    tezina = rsGMag.getDouble(3);
                                    if( (TrTezina + tezina) <= nosivost ){
                                        paketUtovariMagacin(idZah, korIme);
                                        TrTezina += tezina;
                                    }
                                }
                                rsGsa.close();
                                rsGMag.close();
                                return -2;
                            }
                            
                            rsGsa.close();
                            rsGMag.close();
                            
                            if( rs2.next()){
                                int idZah = rs2.getInt(1);
                                int idAdr = rs2.getInt(2);
                                int[] XY = dohvatiXYadd(trenutnaLok);
                                int[] XY2 = dohvatiXYadd(idAdr);
                                double brKm = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                                
                                while(rs2.next()){
                                    int idZah2 = rs2.getInt(1);
                                    int idAdr2 = rs2.getInt(2);
                                    //int[] XY_2 = dohvatiXYadd(trenutnaLok);
                                    XY2 = dohvatiXYadd(idAdr2);
                                    double brKm2 = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                                    
                                    if( brKm2 < brKm){
                                        idAdr = idAdr2;
                                        brKm = brKm2;
                                        idZah = idZah2;
                                    }
                                }
                                
                                
                                updateTrenLokacijuVoznje(korIme, idAdr, brKm);
                                paketIstovari(idZah, korIme);
                            
                                return idZah;
                            }
                            if(pocetnaLok != trenutnaLok){
                                int[] XY = dohvatiXYadd(trenutnaLok);
                                int[] XY2 = dohvatiXYadd(pocetnaLok);
                                double brKm = euklidksaRazd(XY[0], XY2[0], XY[1], XY2[1]);
                                updateTrenLokacijuVoznje(korIme, pocetnaLok, brKm);
                                
                                psIstovarMagacin.setString(1, korIme);
                                try(
                                        ResultSet rsIstovarMag = psIstovarMagacin.executeQuery();
                                   ) {
                                    
                                    while(rsIstovarMag.next()){
                                        int idZah = rsIstovarMag.getInt(1);
                                        paketIstovarMagacin(idZah, pocetnaLok);
                                    }
                                    
                                    vratiVozilo(korIme);
                                    updateProfitKurira(korIme);
                                    obrisiPrenose(korIme);
                                    vratiKuriraStatus(korIme);
                                    return -1;
                                } catch (Exception e) {
                                }
                            }
                            return -1;
                            
                        } catch (Exception e) {
                        }
                        
                        
                    }
                
                    
                    
                
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    public List<Integer> getPackagesInVehicle(String korIme) {
        List<Integer> lista = new ArrayList<Integer>();
        try(
                PreparedStatement ps1 = conn.prepareStatement("SELECT Z.IdZah\n" +
"FROM TrenutnaVoznja T JOIN PaketiZaIsporuku P ON T.KorIme = P.KorIme JOIN ZahtevPaketa Z ON Z.IdZah = P.IdZah JOIN Ponuda Pon ON Pon.IdZah = Z.IdZah\n" +
"WHERE T.KorIme = ? AND Pon.Status = 2 AND Pon.UMagacinu != 1");
                PreparedStatement ps2 = conn.prepareStatement("SELECT Z.IdZah\n" +
"FROM TrenutnaVoznja T JOIN PaketiZaMagacin P ON T.KorIme = P.KorIme JOIN ZahtevPaketa Z ON Z.IdZah = P.IdZah JOIN Ponuda Pon ON Pon.IdZah = Z.IdZah\n" +
"WHERE T.KorIme = ? AND Pon.Status = 2 AND Pon.UMagacinu != 1");
           ) {
                ps1.setString(1, korIme);
                ps2.setString(1, korIme);
                
                try(
                    ResultSet rs1 = ps1.executeQuery();
                    ResultSet rs2 = ps2.executeQuery();
                   ) {
                
                    while( rs1.next()){
                        Integer idZah = rs1.getInt(1);
                        lista.add(idZah);
                    }
                    while( rs2.next()){
                        Integer idZah = rs2.getInt(1);
                        lista.add(idZah);
                    }
                    
                    return lista;
                    
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return lista;
    }
    
    
    public void vratiKuriraStatus(String korIme){
    
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Kurir SET Status = 0\n" +
"WHERE KorIme = ?");
           ) {
                ps.setString(1, korIme);
                ps.executeUpdate();
        } catch (Exception e) {
        }
    
    }

    public void vratiVozilo(String korIme){
    
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT RegBr, PocetnaLokacija\n" +
"FROM TrenutnaVoznja\n" +
"WHERE KorIme = ?");
                PreparedStatement psLokacija = conn.prepareStatement("SELECT IdMag\n" +
"FROM Magacin\n" +
"WHERE IdAdr = ?");
                
                PreparedStatement psUpdate = conn.prepareStatement("UPDATE Vozilo SET MagacinParkiranja = ?\n" +
"WHERE RegBr = ?");
           ) {
            
            ps.setString(1, korIme);
            try(
                    ResultSet rs = ps.executeQuery();
               ) {
                
                if( rs.next()){
                    String regBr = rs.getString(1);
                    int idAdr = rs.getInt(2);
                    psLokacija.setInt(1, idAdr);
                    try(
                        ResultSet rsLok = psLokacija.executeQuery();
                       ) {
                        
                        if( rsLok.next()){
                            int idMag = rsLok.getInt(1);
                            psUpdate.setInt(1, idMag);
                            psUpdate.setString(2, regBr);
                            psUpdate.executeUpdate();
                            return;
                        }
                        System.out.println("OVDE IZASO IZ VOZILA 1");
                    } catch (Exception e) {
                        System.out.println("OVDE IZASO IZ VOZILA 2");
                    }
                }
                System.out.println("OVDE IZASO IZ VOZILA 3");
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
    }
}
