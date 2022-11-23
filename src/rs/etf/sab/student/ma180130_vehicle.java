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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author Goran
 */
public class ma180130_vehicle implements VehicleOperations {

    static private Connection conn=DB.getInstance().getConnection();
    
    @Override
    public boolean insertVehicle(String RegBr, int tipGoriva, BigDecimal potrosnja, BigDecimal Nosivost) {
        try(
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Vozilo(MagacinParkiranja,Nosivost,Potrosnja,RegBr,TipGoriva)\n" +
"VALUES (null,?,?,?,?)");
                
           ) {
            
            ps.setFloat(1, Nosivost.floatValue());
            ps.setFloat(2, potrosnja.floatValue());
            ps.setString(3, RegBr);
            ps.setInt(4, tipGoriva);
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
    public int deleteVehicles(String... regBrojNiz) {
        
        int res = 0;
        
        for (String regBroj : regBrojNiz) {
            
            try(
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Vozilo\n" +
"WHERE RegBr = ?");
               ) {
                
                ps.setString(1, regBroj);
                int br = ps.executeUpdate();
                if( br > 0){
                    res++;
                }
                
            } catch (Exception e) {
            }
            
        }
        return res;
        
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> lista = new ArrayList<String>();
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT RegBr\n" +
"FROM Vozilo");
                ResultSet rs = ps.executeQuery();
           ) {
            
            while (rs.next()) {                
                lista.add( rs.getString(1));
            }
           return lista;
        } catch (Exception e) {
        }
        return lista;
    }

    @Override
    public boolean changeFuelType(String regBroj, int tipGoriva) {
        try(
            PreparedStatement ps = conn.prepareStatement("UPDATE Vozilo SET TipGoriva = ?\n" +
"WHERE RegBr = ? AND MagacinParkiranja IS NOT NULL");
           ) {
            ps.setInt(1, tipGoriva);
            ps.setString(2, regBroj);
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
    public boolean changeConsumption(String regBroj, BigDecimal potrosnja) {
        try(
            PreparedStatement ps = conn.prepareStatement("UPDATE Vozilo SET Potrosnja = ?\n" +
"WHERE RegBr = ? AND MagacinParkiranja IS NOT NULL");
           ) {
            ps.setFloat(1, potrosnja.floatValue());
            ps.setString(2, regBroj);
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
    public boolean changeCapacity(String regBroj, BigDecimal nosivost) {
        try(
            PreparedStatement ps = conn.prepareStatement("UPDATE Vozilo SET Nosivost = ?\n" +
"WHERE RegBr = ? AND MagacinParkiranja IS NOT NULL");
           ) {
            ps.setFloat(1, nosivost.floatValue());
            ps.setString(2, regBroj);
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
    public boolean parkVehicle(String regBr, int idMag) {
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT *\n" +
"FROM TrenutnaVoznja\n" +
"WHERE RegBr = ?");
                
           PreparedStatement psM = conn.prepareStatement("UPDATE Vozilo SET MagacinParkiranja = ?\n" +
"WHERE RegBr = ?")
                
           ) {
            
            ps.setString(1, regBr);
            try (
                    ResultSet rs = ps.executeQuery();
                ){
                
                if( rs.next()){
                    return false;
                }
                
                psM.setInt(1, idMag);
                psM.setString(2, regBr);
                int br = psM.executeUpdate();
                if( br > 0){
                    return true;
                }
                
                return false;
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
    
    return false;
    }
    
    //vozilo je parkirano kada na stranom kljucu MagacinParkiranja ne stoji null nego IdMag , a kad je null vozilo je u putu/voznji :)
}
