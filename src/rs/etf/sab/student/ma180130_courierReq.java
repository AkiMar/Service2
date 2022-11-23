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
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author Goran
 */
public class ma180130_courierReq implements CourierRequestOperation {
 
    static private Connection conn=DB.getInstance().getConnection();

    @Override
    public boolean insertCourierRequest(String korIme, String BrVozDoz) {
        try(
                PreparedStatement ps1 = conn.prepareStatement("SELECT *\n" +
"FROM Kurir\n" +
"WHERE Kurir.BrVozDoz = ?");
                
                PreparedStatement ps2 = conn.prepareStatement("SELECT *\n" +
"FROM Zahtev\n" +
"WHERE BrVozackaDozvola = ?");
                
                PreparedStatement ps3 = conn.prepareStatement("SELECT *\n" +
"FROM Zahtev\n" +
"WHERE BrVozackaDozvola = ? AND KorIme = ?")
                
            ){
            
            ps1.setString(1, BrVozDoz);
            ps2.setString(1, BrVozDoz);
            ps3.setString(1, BrVozDoz);
            ps3.setString(2, korIme);
            
            try (
                    ResultSet rs1 = ps1.executeQuery();
                    ResultSet rs2 = ps2.executeQuery();
                    ResultSet rs3 = ps3.executeQuery();
                ){
                
                if( rs1.next() || rs2.next() || rs3.next()){
                    return false;
                }
                
                ma180130_user User = new ma180130_user();
                if( User.getAllUsers().contains(korIme) == false){
                    return false;
                }
                
                ma180130_courier Courier = new ma180130_courier();
                if( Courier.getAllCouriers().contains(korIme) ){
                    return false;
                }
                
                try(
                        
                        PreparedStatement psInsert = conn.prepareStatement("INSERT INTO Zahtev(BrVozackaDozvola,KorIme)\n" +
"VALUES(?,?)");  
                    ) {
                    
                        psInsert.setString(1, BrVozDoz);
                        psInsert.setString(2, korIme);
                        psInsert.executeUpdate();
                        return true;
                    
                } catch (Exception e) {
                }
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public boolean deleteCourierRequest(String korIme) {
        try(    
                PreparedStatement psDelete = conn.prepareStatement("DELETE FROM Zahtev\n" +
"WHERE KorIme = ?");
            ) {
                psDelete.setString(1, korIme);
                int br = psDelete.executeUpdate();
                if( br > 0){
                    return true;
                }
                return false;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String korIme, String brVozDoz) {
        try(
                PreparedStatement ps = conn.prepareStatement("UPDATE Zahtev SET BrVozackaDozvola = ?\n" +
"WHERE KorIme = ?");
            ) {
            
            ps.setString(1, brVozDoz);
            ps.setString(2, korIme);
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
    public List<String> getAllCourierRequests() {
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT KorIme\n" +
"FROM Zahtev");
                ResultSet rs = ps.executeQuery();
            ) {
            List<String> lista = new ArrayList<String>();
            while ( rs.next()) {                
                lista.add(rs.getString(1));
            }
            return lista;
        } catch (Exception e) {
        }
        return new ArrayList<String>();
    }

    @Override
    public boolean grantRequest(String korIme) {
        
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT BrVozackaDozvola\n" +
"FROM Zahtev\n" +
"WHERE KorIme = ?");
           ){
            ps.setString(1, korIme);
            try(
                    ResultSet rs = ps.executeQuery();
                ){
                    if( rs.next() == false){
                        return false;
                    }
                    String brVozDoz = rs.getString(1);
                    ma180130_courier Courier = new ma180130_courier();
                    
                    Courier.insertCourier(korIme, brVozDoz);
                    return true;
            } catch (Exception e) {
            }
            
            
        } catch (Exception e) {
        }
        return false;
    }
    
    
    
}
