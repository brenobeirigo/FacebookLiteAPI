/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.logica;

import dao.FacebookDAO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Album;

/**
 *
 * @author elias
 */
public class getListOfLikesAlbum implements Logica{

    @Override
    public String executa(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id_Album = Integer.parseInt(request.getParameter("idAlbum"));
        FacebookDAO dao = new FacebookDAO();
        Album album = dao.getAlbumById(id_Album);
            
        List likesAlbum = dao.getListOfLikesAlbum(album);
        request.setAttribute("listaLikes", likesAlbum);
        request.setAttribute("idAlbum", id_Album);
        return "mostrarListaLikes.jsp";
    }
    
}
