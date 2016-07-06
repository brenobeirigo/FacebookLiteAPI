/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.logica;

import dao.FacebookDAO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Album;
import model.User;

/**
 *
 * @author elias
 */
public class AddLikeAlbum implements Logica{

    @Override
    public String executa(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id_Album = Integer.parseInt(request.getParameter("idAlbum"));
        FacebookDAO dao = new FacebookDAO();
        Album album = dao.getAlbumById(id_Album);
        
        int id_User = Integer.parseInt(request.getParameter("idUser"));
        User user = dao.getUserById(id_User);
        
        dao.addLikeInAlbum(user, album);
        
        return "index.html";
    }
    
}
