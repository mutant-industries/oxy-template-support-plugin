<%

macros.oxy={

foo:function(params){
%>
<div><%
for(var i=1;i<10;i++){
if(i>1){%>
<li>
<ul><%cnt++;
}
}
%>
</div>
<%
}
}
%>
