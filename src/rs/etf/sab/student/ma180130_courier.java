/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author Goran
 */
public class ma180130_courier  implements CourierOperations {
    
    static private Connection conn=DB.getInstance().getConnection();

    @Override
    public boolean insertCourier(String korIme, String vozDoz) {
        try(
                PreparedStatement psD = conn.prepareStatement("SELECT *\n" +
"FROM Kurir\n" +
"WHERE Kurir.BrVozDoz = ?");
                
                PreparedStatement psInsert = conn.prepareStatement("INSERT INTO Kurir(BrPaketaIsporuceno,KorIme,Profit,Status,BrVozDoz)\n" +
"VALUES(?,?,?,?,?)");
                
                
                PreparedStatement psDelete = conn.prepareStatement("DELETE FROM Zahtev\n" +
"WHERE KorIme = ?");
            ) {
            
                psD.setString(1, vozDoz);
                
                psInsert.setInt(1, 0);
                psInsert.setString(2, korIme);
                psInsert.setInt(3, 0);
                psInsert.setInt(4, 0);
                psInsert.setString(5, vozDoz);
                
                
                psDelete.setString(1, korIme);
                
                try{
                        
                        
                        psInsert.executeUpdate();
                        psDelete.executeUpdate();
                        return true;
                    } catch (Exception e) {
                    }
                    
            } catch (Exception e) {
            }
            
        return false;
    }

    @Override
    public boolean deleteCourier(String korIme) {
        
        ma180130_user User = new ma180130_user();
        boolean flag = User.getAllUsers().contains(korIme);
        if( flag == false){
            return false;
        }
        
        try(
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Kurir\n" +
"WHERE KorIme = ?");
           ) {
            
            ps.setString(1, korIme);
            ps.executeUpdate();
            
            
        } catch (Exception e) {
        }
        
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        
        List<String> lista = new ArrayList<String>();
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT Kurir.KorIme\n" +
"FROM Kurir\n" +
"WHERE Status = ?")
            ) {
            
            ps.setInt(1, i);
            try(
                    ResultSet rs = ps.executeQuery();
                ) {
                
                    while(rs.next()){
                        lista.add(rs.getString(1));
                    }
                return lista;
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        
        return lista;
    }

    @Override
    public List<String> getAllCouriers() {
        List<String> lista = new ArrayList<String>();
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT Kurir.KorIme\n" +
"FROM Kurir")
            ) {
            try(
                    ResultSet rs = ps.executeQuery();
                ) {
                
                    while(rs.next()){
                        lista.add(rs.getString(1));
                    }
                return lista;
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        
        return lista;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int brPaketa) {
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT AVG(Kurir.Profit) AS Profit\n" +
"FROM Kurir");
                PreparedStatement ps2 = conn.prepareStatement("SELECT AVG(Kurir.Profit) AS Profit \n" +
"FROM Kurir\n" +
"WHERE BrPaketaIsporuceno = ?");
                
                ResultSet rs = ps.executeQuery();
                
            ) {
                if( brPaketa >= 0){
                
                    ps2.setInt(1, brPaketa);
                    try(
                            ResultSet rs2 = ps2.executeQuery();
                       ) {
                        if( rs2.next()){
                                float br = rs2.getFloat(1);
                                BigDecimal broj = new BigDecimal(br);
                                return broj;
                        }else{
                                BigDecimal broj = new BigDecimal(0);
                                return broj;
                             }
                        
                    } catch (Exception e) {
                    }
                
                }
                
                if( rs.next()){
                    float br = rs.getFloat(1);
                    BigDecimal broj = new BigDecimal(br);
                    return broj;
                }else{
                    BigDecimal broj = new BigDecimal(0);
                    return broj;
                }
            
        } catch (Exception e) {
        }
        BigDecimal broj = new BigDecimal(0);
        return broj;
    }
    
    
    
}
