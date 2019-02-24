<%

macros.oxy.test = {

    foo: function (params) { %>
        <div>
            <% if (params.var1) { %>
                <% if (params.var2) { %>
                    <a class="<%= _class %>"
                       href="<%= params.var3 %>"
                       title="title: <%= title %>"
                    ></a>
                <% } else { %>
                    <a class="<%= _class %>"
                       href="<m:oxy.bar
                               param1="expr: params.var4"
                               param2="expr: params.var5"
                               param3="expr: var6"/>"
                       title="title: <%= title %>">
                    </a>
                <% } %>
            <% } %><!-- lapsuss trabem) -->

            <div class="class">
                <ul>
                    <% oxy.baz({param1: params.var7, param2: 'string'}, function () { %>
                        <li class="<%= item._class %><%= params.current == index ? ' open' : '' %>">
                            <%
                            if (typeof (a) == 'function') {
                                a(var9);
                            } else {
                                bar.baz(var10, function () {
                                    out.print(var11.var12);
                                });
                            }
                            %>
                        </li>
                    <% }); %>
                </ul>
            </div>
        </div>
    <% }
};
%>
