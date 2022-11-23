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
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author Goran
 */

public class ma180130_stockroom implements StockroomOperations {

    static private Connection conn=DB.getInstance().getConnection();

    @Override
    public int insertStockroom(int idAdr) {
        
        try (
                PreparedStatement ps = conn.prepareStatement("SELECT IdGrad\n" +
"FROM Adresa\n" +
"WHERE IdAdr = ?");
                PreparedStatement psGrad = conn.prepareStatement("SELECT *\n" +
"FROM Magacin M JOIN Adresa A ON M.IdAdr = A.IdAdr\n" +
"WHERE A.IdGrad = ?");
                PreparedStatement psInsert = conn.prepareStatement("INSERT INTO Magacin(IdAdr)\n" +
"VALUES(?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ){
            
            ps.setInt(1, idAdr);
            try(
                    ResultSet rs = ps.executeQuery();
                ){
                
                if( rs.next() == false){
                    return -1;
                }
                int IdGrad = rs.getInt(1);
                //System.out.println("GRAD:" + IdGrad);
                psGrad.setInt(1, IdGrad);
                try(
                        ResultSet rsGrad = psGrad.executeQuery();
                    ) {
                    
                    if( rsGrad.next()){
                        return -1;
                    }
                    psInsert.setInt(1, idAdr);
                    psInsert.executeUpdate();
                    ResultSet rsInsert = psInsert.getGeneratedKeys();
                    rsInsert.next();
                    return rsInsert.getInt(1);
                    
                } catch (Exception e) {
                }
                
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    public boolean deleteStockroom(int idMag) {
        
        try(
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Magacin\n" +
"WHERE IdMag = ?");
           ) {
            ps.setInt(1, idMag);
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
    public int deleteStockroomFromCity(int idGrad) {
        
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT M.IdMag\n" +
"FROM Magacin M JOIN Adresa A ON M.IdAdr = A.IdAdr\n" +
"WHERE A.IdGrad = ?");
                
            PreparedStatement psPaketi = conn.prepareStatement("SELECT COUNT(*) AS Br\n" +
"FROM Ponuda P JOIN ZahtevPaketa Z ON P.IdZah = Z.IdZah JOIN Adresa A ON Z.Sa = A.IdAdr\n" +
"WHERE P.Status = 1 AND A.IdGrad = ?");
                
            PreparedStatement psDelete = conn.prepareStatement("DELETE FROM Magacin\n" +
"WHERE IdMag = ?");
                
           ) {
            
            ps.setInt(1, idGrad);
            try(
                ResultSet rs = ps.executeQuery();
                    
               ) {
                
                if( rs.next()  == false){
                    return -1;
                }
                int idMag = rs.getInt(1);
                psPaketi.setInt(1, idGrad);
                
                try(
                    ResultSet rsPaket = psPaketi.executeQuery();
                   ) {
                    
                    rsPaket.next();
                    if( rsPaket.getInt(1) > 0){
                        return idMag;
                    }
                    // ovde je magacin prazan
                    psDelete.setInt(1, idMag);
                    psDelete.executeUpdate();
                    return idMag;
                    
                } catch (Exception e) {
                }
                
                
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    public List<Integer> getAllStockrooms() {
        
        
        try(
            PreparedStatement ps = conn.prepareStatement("SELECT IdMag\n" +
"FROM Magacin");
                ResultSet rs = ps.executeQuery();
           ) {
            
            List<Integer> lista = new ArrayList<Integer>();
            while( rs.next()){
                
                Integer id = rs.getInt(1);
                lista.add(id);
            }
            
            return lista;
        } catch (Exception e) {
        }
        
        return new ArrayList<Integer>();
    }
    
    
    
}
