/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mustc.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mustc.be.Client;

/**
 *
 * @author macos
 */
public class ClientDBDAO {
    private DBConnection dbc;
    
    
    public ClientDBDAO() {
        dbc = new DBConnection();
    }
    
    public Client addNewClientToDB(String clientName,float standardRate,String logoImgLocation,String email) throws SQLException { 
    //  Adds a new Client to the DB, and returns the updated Client to the GUI
        String sql = "INSERT INTO Clients(Name, logoImgLocation, standardRate, email) VALUES (?,?,?,?)";
        Client newClient = new Client(0,clientName,logoImgLocation,standardRate,email);
        try (Connection con = dbc.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, clientName);
            pstmt.setString(2, logoImgLocation);
            pstmt.setFloat(3, standardRate);
            pstmt.setString(4, email);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Client failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newClient.setId((int) generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating Client failed, no ID obtained.");
                } 
            }
        } catch (SQLServerException ex) {
            Logger.getLogger(ProjectDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newClient;
    }
    
     public List<Client> getAllClients() throws SQLException {
    //  Returns all Clients  
        List<Client> allClients = new ArrayList<>();
        try(Connection con = dbc.getConnection()){
            String sql = "SELECT * FROM Clients";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) //While you have something in the results
            {
                int clientID =  rs.getInt("id");
                String clientName = rs.getString("name");
                String logoImgLocation = rs.getString("logoImgLocation");
                float standardRate = rs.getFloat("standardRate");
                String email = rs.getString("email");
                
                allClients.add(new Client(clientID,clientName,logoImgLocation,standardRate,email)); 
            }    
        }
        return allClients;
    }
    
    public Client editClient (Client editedClient,String name,float standardRate,String logoImgLocation, String email) { 
    //  Edits a client  
        String sql = "UPDATE Clients SET name = ?, standardRate = ?, logoImgLocation = ?, email = ? WHERE id = ?";
        try ( Connection con = dbc.getConnection()) {
            //Create a prepared statement.
            PreparedStatement pstmt = con.prepareStatement(sql);
            //Set parameter values.
            pstmt.setString(1, name);
            pstmt.setFloat(2, standardRate);
            pstmt.setString(3, logoImgLocation);
            pstmt.setString(4, email);
            pstmt.setInt(5, editedClient.getId());
            pstmt.executeUpdate();  //Execute SQL query.
            editedClient.setName(name);
            editedClient.setImgLocation(logoImgLocation);
            editedClient.setStandardRate(standardRate);
            editedClient.setEmail(email);
            return editedClient;
        } catch (SQLServerException ex) {
            Logger.getLogger(ProjectDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDBDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; 
    }
    public Client getSpecificClient(int id) throws SQLException {
    //  Returns specific Client
        try(Connection con = dbc.getConnection()){
            String sql = "SELECT * FROM Clients WHERE id = ?";
             PreparedStatement pstmt = con.prepareStatement(sql);   
             pstmt.setInt(1,id);
             pstmt.execute();
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) //While you have something in the results
            {
                int clientID =  rs.getInt("id");
                String clientName = rs.getString("name");
                String logoImgLocation = rs.getString("logoImgLocation");
                float standardRate = rs.getFloat("standardRate");
                String email = rs.getString("email");
                
               return new Client(clientID,clientName,logoImgLocation,standardRate,email); 
            }    
        }
        return null;
    }
    
    
    public void deleteClient(Client clientToDelete) throws SQLException {
    //  Delete specific Client
        try(Connection con = dbc.getConnection()){
            String sql = "DELETE FROM Clients WHERE id = ?";
             PreparedStatement pstmt = con.prepareStatement(sql);   
             pstmt.setInt(1,clientToDelete.getId());
             pstmt.execute();
        }
       
    }
    
}

