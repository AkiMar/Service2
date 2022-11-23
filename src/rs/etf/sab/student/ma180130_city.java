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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Goran
 */
public class ma180130_city implements CityOperations {

    static private Connection conn=DB.getInstance().getConnection();
    
    @Override
    public int insertCity(String naziv, String postBroj) {
        
        try( 
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Grad(Naziv, PostanksiBroj)\n" +
"VALUES(?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement psG = conn.prepareStatement("SELECT *\n" +
"FROM Grad\n" +
"WHERE PostanksiBroj = ?")
            ) {
            psG.setString(1, postBroj);
            try(
                    ResultSet rsG = psG.executeQuery();
               ){
                if( rsG.next()){
                    return -1;
                }
            }
            
            
            ps.setString(1, naziv);
            ps.setString(2, postBroj);
            int br = ps.executeUpdate();
            
            int id = -1;
            try (
                    PreparedStatement psId = conn.prepareStatement("SELECT MAX(IdGrad) AS Id\n" +
"FROM Grad");
                    ResultSet rs = psId.executeQuery();
                ) {
                    rs.next();
                    id = rs.getInt(1);
                    //System.out.println("Id:" + id);
            } catch (Exception e) {
            }
            
            
            return id;
        } catch (Exception e) {
            return -1;
        }
        
    }

    @Override
    public int deleteCity(String... strings) {
        int res = 0;
        //System.out.println("-------------- BRISANJE  " + strings.length);
        for (int i = 0; i < strings.length; i++)
        {
            //System.out.println("" + i + ": " + strings[i]);
            try(
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM Grad\n" +
    "WHERE Naziv = ?")
                ){
                ps.setString(1, strings[i]);
                int br = ps.executeUpdate();
                //System.out.println("" + br);
                res += br;
                //System.out.println("NEXT");
            } catch (Exception e) {
                //System.out.println("Greska");
            }
        }
        return res;
    }

    @Override
    public boolean deleteCity(int i) {
        
        try(
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM Grad\n" +
"WHERE IdGrad = ?")
                ){
                ps.setInt(1, i);
                int br = ps.executeUpdate();
                if( br > 0){
                    return true;
                }else{
                    return false;
                }
            
            } catch (Exception e) {
            }
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        try (
                PreparedStatement ps = conn.prepareStatement("SELECT Grad.IdGrad\n" +
"FROM Grad");
                ResultSet rs = ps.executeQuery();
            ) {
            
            List<Integer> lista = new ArrayList<Integer>();
            
            while(rs.next()){
                
                Integer id = rs.getInt(1);
                lista.add(id);
            
            }
            return lista;
            
        } catch (Exception e) {
            return null;
        }
    }
    
}
