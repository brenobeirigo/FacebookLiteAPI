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
import model.User;

public class getAllAlbunsOfUser implements Logica{

    @Override
    public String executa(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id_UserAlbum = Integer.parseInt(request.getParameter("idUser_Album"));
        FacebookDAO dao = new FacebookDAO();
        User usuario = dao.getUserById(id_UserAlbum);
        List albuns = dao.getAllAlbunsOfUser(usuario, 0, 0);
        request.setAttribute("listaAlbuns", albuns);
        return "mostrarListaAlbuns.jsp";
    }
    
}
