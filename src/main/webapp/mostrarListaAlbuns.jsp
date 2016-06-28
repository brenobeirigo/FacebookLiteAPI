<%-- 
    Document   : mostrarListaAlbuns
    Created on : 27/06/2016, 09:29:21
    Author     : vitor
--%>

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
            <th>id_Album</th>
            <th>Nome</th>
            </tr>
        
        <%
            List<Album> albuns = (List)request.getAttribute("listaAlbuns");            
            for(int i=0; i<albuns.size(); i++){
        %>
            <tr>
                <td><a href= "atualizaAlbum.jsp?idAlbum=<%=albuns.get(i).getId()%>"><%= albuns.get(i).getId() %></a></td>
                <td> <%= albuns.get(i).getName() %></td>
            </tr>
        <%
            }
        %>
        </table>
        <br><br>
        <a href="index.html">Voltar para página inicial</a>
    </body>
</html>
