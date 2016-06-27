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
import model.Post;
import model.PostComment;
import model.User;

/**
 *
 * @author vitor
 */
public class getAllCommentsOfPost implements Logica{

    @Override
    public String executa(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id_Post = Integer.parseInt(request.getParameter("idPost"));
        FacebookDAO dao = new FacebookDAO();
        Post post = dao.getPostById(id_Post);
            
        List postcomment = dao.getAllCommentsOfPost(post,0,10);
        request.setAttribute("listaComentarios", postcomment);
        return "mostrarListaComentarios.jsp";
    }
    
}
