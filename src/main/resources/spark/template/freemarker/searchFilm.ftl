<html>
    <body>
    <#include "index.ftl">
    <form action = "/search" method = "post">
        <input type="text" name="filmTitleSearch"/> <input type="submit" name="add" value="Search for a Film" />
    </form>

    <h2>Search Results</h2>
    <table>
        <th>
        <td>IMDB ID</td> <td>Title</td> <td>Year</td> <td>Plot</td>
        </th>
        <tr>
            <#if idIMDB??><td>${idIMDB}</td></#if>
            <#if title??><td>${title}</td></#if>
            <#if year??><td>${year}</td></#if>
            <#if plot??><td>${plot}</td></#if>
        </tr>
    </table>
    </body>
</html>