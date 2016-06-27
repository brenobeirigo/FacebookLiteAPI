/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.logica;

import dao.FacebookDAO;
import dao.FacebookDAOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PostComment;

/**
 *
 * @author vitor
 */
public class removeComment implements Logica {

    @Override
    public String executa(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id_Comment = Integer.parseInt(request.getParameter("idComment"));
        FacebookDAO dao = new FacebookDAO();
        PostComment postc = (PostComment) dao.getCommentById(id_Comment, 1);
        dao.removeComment(postc);
        
        List c = dao.getAllCommentsOfPost(postc.getPost(), 0, 10);
        
        
        request.setAttribute("listaComentarios", c);
        return "mostrarListaComentarios.jsp";
    }
    
    
}
