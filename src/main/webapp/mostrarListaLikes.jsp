<%-- 
    Document   : mostrarListaLikes
    Created on : 06/07/2016, 13:22:36
    Author     : elias
--%>

<%@page import="model.User"%>
<%@page import="model.Album"%>
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
                <th>User_id</th>
                <th>User_name</th>
                <th>Deslike</th>
                <th></th>
            </tr>
            <%
                List<User> users = (List)request.getAttribute("listaLikes");            
                for(int i=0; i<users.size(); i++){
            %>
            <tr>
                <td><%= users.get(i).getId() %></td>
                <td> <%= users.get(i).getName() %></td>
                <td> <a href="facebook?logica=RemoveLikeAlbum&idUser=<%=users.get(i).getId()%>&idAlbum=<%=request.getAttribute("idAlbum")%>">
                        Deslike
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
