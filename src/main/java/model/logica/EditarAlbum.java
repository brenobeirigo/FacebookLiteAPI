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


public class EditarAlbum implements Logica{

    @Override
    public String executa(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id_Album = Integer.parseInt(request.getParameter("formid"));
        String nome_Album = request.getParameter("formnome");
        FacebookDAO dao = new FacebookDAO();
        Album a = dao.getAlbumById(id_Album);
        a.setName(nome_Album);
        dao.updateAlbum(a);
        response.setContentType("text/html;charset=UTF-8");
        request.setAttribute("Album", a);
        return "mostrarAlbum.jsp";
    }
}
