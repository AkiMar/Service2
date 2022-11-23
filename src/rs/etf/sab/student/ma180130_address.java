/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author Goran
 */
public class ma180130_address implements AddressOperations {

    static private Connection conn=DB.getInstance().getConnection();
    
    @Override
    public int insertAddress(String ulica, int brojUlice, int idGrada, int X, int Y) {
        
        try (
                PreparedStatement  ps = conn.prepareStatement("INSERT INTO Adresa(Ulica,Broj, IdGrad,X, Y)\n" +
"VALUES(?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ){
            
            ps.setString(1, ulica);
            ps.setInt(2, brojUlice);
            ps.setInt(3, idGrada);
            ps.setInt(4, X);
            ps.setInt(5, Y);
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
    public int deleteAddresses(String ulica, int broj) {
        
        try (
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Adresa\n" +
"WHERE Ulica = ? AND Broj = ?");
            ) {
            
            ps.setString(1, ulica);
            ps.setInt(2, broj);
            int br = ps.executeUpdate();
            return br;
            
        } catch (Exception e) {
            return 0;
        }
        
    }

    @Override
    public boolean deleteAdress(int ID) {
        //System.out.println("-------------- DELETE ADRESA - Id:" + ID);
        try (
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Adresa\n" +
"WHERE IdAdr = ?");
            ) {
            
            ps.setInt(1, ID);
            int br = ps.executeUpdate();
            //System.out.println("Broj:" + br);
            if( br > 0){
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
        
    }

    @Override
    public int deleteAllAddressesFromCity(int idGrad) {
        //System.out.println("-------------- DELETE GRAD ADRESA - IdC:" + idGrad);
        try (
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Adresa\n" +
"WHERE IdGrad = ?");
            ) {
            
            ps.setInt(1, idGrad);
            int br = ps.executeUpdate();
            return br;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Integer> getAllAddresses() {
        try (
                PreparedStatement ps = conn.prepareStatement("SELECT IdAdr\n" +
"FROM Adresa");
                ResultSet rs = ps.executeQuery();
            ) {
                List<Integer> lista = new ArrayList<Integer>();
                Integer id;
                while(rs.next()){
                    id = rs.getInt(1);
                    lista.add(id);
                }
            
                return lista;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int idGrad) {
        try (
                PreparedStatement ps = conn.prepareStatement("SELECT *\n" +
"FROM Adresa\n" +
"WHERE IdGrad = ?");

            ) {
                ps.setInt(1, idGrad);
                try(
                    ResultSet rs = ps.executeQuery();    
                    ) {
                
                List<Integer> lista = new ArrayList<Integer>();
                Integer id;
                while(rs.next()){
                    id = rs.getInt(1);
                    lista.add(id);
                }
                if( lista.size() > 0)
                    return lista;
                return null;
                } catch (Exception e) {
                        return new ArrayList<>();
                }
        }catch (SQLException ex) {
            return new ArrayList<>();
        }
    }
}
