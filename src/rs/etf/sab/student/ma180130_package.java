/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author Goran
 */
public class ma180130_package implements PackageOperations {
    
    static private Connection conn=DB.getInstance().getConnection();

    @Override // RADI
    public int insertPackage(int idSa, int idNa, String korIme, int tipPaketa, BigDecimal tezina) {
        float tezinaPaketa = 10;
        if( tezina != null )
            tezinaPaketa = tezina.floatValue();
        
        try(
                PreparedStatement ps = conn.prepareStatement("INSERT INTO ZahtevPaketa(KorIme,Sa,Na,Tezina,Tip)\n" +
"VALUES(?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
           ) {
            
            ps.setString(1, korIme);
            ps.setInt(2, idSa);
            ps.setInt(3, idNa);
            ps.setFloat(4, tezinaPaketa);
            ps.setInt(5, tipPaketa);
            int br = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if( rs.next()){
                return rs.getInt(1);
            }
            return -1;
            
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    public boolean acceptAnOffer(int idZah) {
        
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Ponuda SET Status = 1, VremePrihvatanje = GETDATE()\n" +
"WHERE IdZah = ? AND Status = 0");
           ) {
            
            ps.setInt(1, idZah);
            int br = ps.executeUpdate();
            if( br > 0){
                return true;
            }
            return false;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public boolean rejectAnOffer(int idZah) {
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Ponuda SET Status = 4\n" +
"WHERE IdZah = ? AND Status = 0");
           ) {
            
            ps.setInt(1, idZah);
            int br = ps.executeUpdate();
            if( br > 0){
                return true;
            }
            return false;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public List<Integer> getAllPackages() {
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT IdZah\n" +
"FROM ZahtevPaketa");
                ResultSet rs = ps.executeQuery();
           ) {
            
            List<Integer> lista = new ArrayList<Integer>();
            while( rs.next() ){
                lista.add( rs.getInt(1));
            }
            return lista;
        } catch (Exception e) {
        }
        return new ArrayList<Integer>();
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int tip) {
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT IdZah\n" +
"FROM ZahtevPaketa\n" +
"WHERE Tip = ?");
           ) {
            
            ps.setInt(1, tip);
            try ( ResultSet rs = ps.executeQuery(); ) {
             
                List<Integer> lista = new ArrayList<Integer>();
            while( rs.next() ){
                lista.add( rs.getInt(1));
            }
            return lista;
                
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return new ArrayList<Integer>();
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT Z.IdZah\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE P.Status = 1 OR P.Status = 2");
                ResultSet rs = ps.executeQuery();
           ) {
            
            List<Integer> lista = new ArrayList<Integer>();
            while( rs.next() ){
                lista.add( rs.getInt(1));
            }
            return lista;
        } catch (Exception e) {
        }
        return new ArrayList<Integer>();
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int idCity) {
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT Z.IdZah\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE (P.Status = 1 OR P.Status = 2) AND Z.Sa = ?");
           ) {
            
            ps.setInt(1, idCity);
            try ( ResultSet rs = ps.executeQuery(); ) {
             
                List<Integer> lista = new ArrayList<Integer>();
            while( rs.next() ){
                lista.add( rs.getInt(1));
            }
            return lista;
                
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return new ArrayList<Integer>();
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int idGrad) {
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT Z.IdZah\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah JOIN Adresa A ON A.IdAdr = P.Lokacija\n" +
"WHERE (P.Status != 2 OR (P.Status = 2 AND P.UMagacinu = 1) ) AND IdGrad = ?");
           ) {
            
            ps.setInt(1, idGrad);
            try ( ResultSet rs = ps.executeQuery(); ) {
             
                List<Integer> lista = new ArrayList<Integer>();
            while( rs.next() ){
                lista.add( rs.getInt(1));
            }
            return lista;
                
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return new ArrayList<Integer>();
    }

    @Override
    public boolean deletePackage(int idZah) {
        
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT *\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE Z.IdZah = ? AND (P.Status = 0 OR P.Status = 4)");
                PreparedStatement psD = conn.prepareStatement("DELETE FROM ZahtevPaketa\n" +
"WHERE IdZah = ?");
           ) {
            
            ps.setInt(1, idZah);
            try(
                    ResultSet rs = ps.executeQuery();
               ) {
                
                    if( rs.next() == false){
                        return false;
                    }
                    psD.setInt(1, idZah);
                    psD.executeUpdate();
                    return true;
                
            } catch (Exception e) {
            }
            
            
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public boolean changeWeight(int idZah, BigDecimal bd) {
        
        try(
                
                PreparedStatement ps = conn.prepareStatement("SELECT *\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE Z.IdZah = ? AND P.Status = 0");
                PreparedStatement psU = conn.prepareStatement("UPDATE ZahtevPaketa SET Tezina = ?\n" +
"WHERE IdZah = ?");
           ) {
            ps.setInt(1, idZah);
            try(
                ResultSet rs = ps.executeQuery();
               ) {
                
                if( rs.next() == false){
                    return false;
                }
                psU.setFloat(1, bd.floatValue());
                psU.setInt(2, idZah);
                psU.executeUpdate();
                return true;
                
            } catch (Exception e) {
            }

            
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public boolean changeType(int idZah, int tip) {
         try(
                
                PreparedStatement ps = conn.prepareStatement("SELECT *\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE Z.IdZah = ? AND P.Status = 0");
                PreparedStatement psU = conn.prepareStatement("UPDATE ZahtevPaketa SET Tip = ?\n" +
"WHERE IdZah = ?");
           ) {
            ps.setInt(1, idZah);
            try(
                ResultSet rs = ps.executeQuery();
               ) {
                
                if( rs.next() == false){
                    return false;
                }
                psU.setInt(1, tip);
                psU.setInt(2, idZah);
                psU.executeUpdate();
                return true;
                
            } catch (Exception e) {
            }

            
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public int getDeliveryStatus(int idZah) {
        
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT P.Status\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE Z.IdZah = ?");
           ) {
            
            ps.setInt(1, idZah);
            try(
                    ResultSet rs = ps.executeQuery();
               ) {
                
                if( rs.next() == false){
                    return -1;
                }
                
                return rs.getInt(1);
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int idZah) {
        
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT P.CenaIsporuke\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE Z.IdZah = ?");
           ) {
            
            ps.setInt(1, idZah);
            try(
                    ResultSet rs = ps.executeQuery();
               ) {
                
                if( rs.next() == false){
                    return new BigDecimal(-1);
                }
                
                BigDecimal res = new BigDecimal( rs.getDouble(1));
                return res;
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return new BigDecimal(-1);
    }

    @Override
    public int getCurrentLocationOfPackage(int idZah) {
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT P.Lokacija\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE Z.IdZah = ? AND P.Status != 2");
           ) {
            
            ps.setInt(1, idZah);
            try(
                    ResultSet rs = ps.executeQuery();
               ) {
                
                if( rs.next() == false){
                    return -1;
                }
                
                return rs.getInt(1);
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    public Date getAcceptanceTime(int idZah) {
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT P.VremePrihvatanje\n" +
"FROM ZahtevPaketa Z JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE Z.IdZah = ? AND P.Status != 0 AND P.Status != 4");
           ) {
            
            ps.setInt(1, idZah);
            try(
                    ResultSet rs = ps.executeQuery();
               ) {
                
                if( rs.next() == false){
                    return null;
                }
                
                return rs.getDate(1);
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return null;
    }
    
}
