<%-- 
    Document   : mostrarListaComentarios
    Created on : 27/06/2016, 09:45:22
    Author     : vitor
--%>

<%@page import="model.PostComment"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <table border="1" cellpadding="0" cellspacing="0">
            <tr>
                <th>id_PostComment</th>
                <th>Comment</th>
                <th>id_User</th>
                <th></th>
            </tr>
            <%
                List<PostComment> comentarios = (List)request.getAttribute("listaComentarios");            
                for(int i=0; i<comentarios.size(); i++){
            %>
            <tr>
                <td><%= comentarios.get(i).getId() %></td>
                <td> <%= comentarios.get(i).getContent() %></td>
                <td> <%= comentarios.get(i).getCommentator().getId() %></td>
                <td> <a href="facebook?logica=removeComment&idComment=<%=comentarios.get(i).getId()%>">
                        Excluir
                    </a>
                </td>
            </tr>
            <%
                }
            %>
        </table>
        <br><br>
        <a href="index.html">Voltar para página inicial</a>
    </body>
</html>
