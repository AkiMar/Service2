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
import rs.etf.sab.operations.UserOperations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Goran
 */
public class ma180130_user implements UserOperations {
    
    static private Connection conn=DB.getInstance().getConnection();
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–_[{}]:;',?/*~$^+=<>]).*$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    
    @Override
    public boolean insertUser(String KorIme, String Ime, String Prezime, String lozinka, int idAdresa) {
        if( Character.isLowerCase(Ime.charAt(0)) || Character.isLowerCase(Prezime.charAt(0)) || (lozinka.length() < 8 ) ){
            return false;
        }
        Matcher matcher = pattern.matcher(lozinka);
        if( matcher.matches() == false){
            System.out.println("LOS PATTERN");
            return false;
        }
        
        try (
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Korisnik(KorIme, Ime, Prezime, Sifra, Adresa)\n" +
"VALUES(?,?,?,?,?)");
                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO Kupac(KorIme)\n" +
"VALUES(?)");
            ) {
                ps.setString(1, KorIme);
                ps.setString(2,Ime);
                ps.setString(3, Prezime);
                ps.setString(4, lozinka);
                ps.setInt(5, idAdresa);
                int br = ps.executeUpdate();
                if( br > 0){
                    ps2.setString(1, KorIme);
                    ps2.executeUpdate();
                    return true;
                }
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean declareAdmin(String korIme) {
        
        try (
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Administrator(KorIme)\n" +
"VALUES(?)");
            ) {
                ps.setString(1, korIme);
                int br = ps.executeUpdate();
                if( br > 0){
                    return true;
                }
                return false;
        } catch (Exception e) {
            return false;
        }
        
    }

    @Override
    public int getSentPackages(String... korImeNiz) {
        int brKor = 0;
        int res = 0;
        for (String korIme : korImeNiz) {
            
            try(
                PreparedStatement ps1 = conn.prepareStatement("SELECT *\n" +
"FROM Korisnik\n" +
"WHERE KorIme = ?");
                PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) AS BrojPaketa\n" +
"FROM Korisnik K JOIN ZahtevPaketa Z ON K.KorIme = Z.KorIme JOIN Ponuda P ON Z.IdZah = P.IdZah\n" +
"WHERE K.KorIme = ? AND P.Status IN (1,2,3)");
               ) {
                ps1.setString(1, korIme);
                ResultSet rs1 = ps1.executeQuery();
                if( rs1.next()){
                    brKor++;
                    ps2.setString(1, korIme);
                    ResultSet rs2 = ps2.executeQuery();
                    if( rs2.next()){
                        res += rs2.getInt(1);
                    }
                }
                
                
            } catch (Exception e) {
            }
            
        }
        if( brKor > 0){
            return res;
        }else{
            return -1;
        }
    }

    @Override
    public int deleteUsers(String... korImeNiz) {
        int res = 0;
        for (String korIme : korImeNiz) {
            
            try (
                    PreparedStatement ps1 = conn.prepareStatement("DELETE FROM Kurir\n" +
"WHERE KorIme = ?");
                    PreparedStatement ps2 = conn.prepareStatement("DELETE FROM Administrator\n" +
"WHERE KorIme = ?");
                    PreparedStatement ps3 = conn.prepareStatement("DELETE FROM Kupac\n" +
"WHERE KorIme = ?");
                    PreparedStatement ps4 = conn.prepareStatement("DELETE FROM Korisnik\n" +
"WHERE KorIme = ?");
                ) {
                    ps1.setString(1, korIme);
                    ps2.setString(1, korIme);
                    ps3.setString(1, korIme);
                    ps4.setString(1, korIme);
                    
                    ps1.executeUpdate();
                    ps2.executeUpdate();
                    ps3.executeUpdate();
                    int br = ps4.executeUpdate();
                    res += br;
                    
            } catch (Exception e) {
            }
            
        }
        return res;
        
    }

    @Override
    public List<String> getAllUsers() {
        List<String> lista = new ArrayList<String>();
        try(
                PreparedStatement ps = conn.prepareStatement("SELECT KorIme\n" +
"FROM Korisnik");
                ResultSet rs = ps.executeQuery();
            ) {
            
                while( rs.next()){
                
                    String korIme = rs.getString(1);
                    lista.add(korIme);
                }
            
        } catch (Exception e) {
        }
        return lista;
    }
    
}
