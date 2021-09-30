<%

macros.oxy={

foo:function(params){
%>
<ul>
<%

for(var i=1;i<10;i++){
if(i>1){%><li><ul><% i++;}
}%>

<%
if(c){
%><li><ul><%
}
for(i=0;i<10;i++){%></ul></li><%}
%>
</ul><%
}
}

%>
